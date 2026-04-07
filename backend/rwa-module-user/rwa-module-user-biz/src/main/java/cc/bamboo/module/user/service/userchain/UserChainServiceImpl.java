package cc.bamboo.module.user.service.userchain;

import cc.bamboo.framework.redis.service.RedisService;
import cc.bamboo.module.chain.util.Web3SignUtils;
import cc.bamboo.module.user.controller.app.userchain.vo.AppBindAddressReqVO;
import cc.bamboo.module.user.controller.app.userchain.vo.AppChainRespVO;
import cc.bamboo.module.user.controller.app.userchain.vo.AppUnBindAddressReqVO;
import cc.bamboo.module.user.controller.app.userchain.vo.AppUserChainRespVO;
import cc.bamboo.module.user.enums.ChainAddressStatusEnum;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;

import java.util.*;
import java.util.concurrent.TimeUnit;

import cc.bamboo.module.user.controller.admin.userchain.vo.*;
import cc.bamboo.module.user.dal.dataobject.userchain.UserChainDO;
import cc.bamboo.framework.common.pojo.PageResult;
import cc.bamboo.framework.common.util.object.BeanUtils;

import cc.bamboo.module.user.dal.mysql.userchain.UserChainMapper;

import static cc.bamboo.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cc.bamboo.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

import static cc.bamboo.module.user.dal.redis.RedisKeyConstants.USER_CHAIN_SIGN_DATA;
import static cc.bamboo.module.user.enums.ErrorCodeConstants.*;

/**
 * 用户链地址表= Service 实现类
 *
 * @author Swolf
 */
@Service
@Validated
@Slf4j
public class UserChainServiceImpl implements UserChainService {

    @Resource
    private UserChainMapper chainMapper;

    @Resource
    private ChainOperationTaskService chainOperationTaskService;

    @Resource
    private RedisService redisService;

    @Value("${rwa.url}")
    private String url;

    @Override
    public Long createChain(UserChainSaveReqVO createReqVO) {
        // 插入
        UserChainDO chain = BeanUtils.toBean(createReqVO, UserChainDO.class);
        chainMapper.insert(chain);
        // 返回
        return chain.getId();
    }

    @Override
    public void updateChain(UserChainSaveReqVO updateReqVO) {
        // 校验存在
        validateChainExists(updateReqVO.getId());
        // 更新
        UserChainDO updateObj = BeanUtils.toBean(updateReqVO, UserChainDO.class);
        chainMapper.updateById(updateObj);
    }

    @Override
    public void deleteChain(Long id) {
        // 校验存在
        validateChainExists(id);
        // 删除
        chainMapper.deleteById(id);
    }

    private void validateChainExists(Long id) {
        if (chainMapper.selectById(id) == null) {
            throw exception(CHAIN_NOT_EXISTS);
        }
    }

    @Override
    public UserChainDO getChain(Long id) {
        return chainMapper.selectById(id);
    }

    @Override
    public PageResult<UserChainDO> getChainPage(UserChainPageReqVO pageReqVO) {
        return chainMapper.selectPage(pageReqVO);
    }

    @Override
    public List<AppUserChainRespVO> getUserChain() {
        LambdaQueryWrapper<UserChainDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserChainDO::getUserId, getLoginUserId());
        queryWrapper.ne(UserChainDO::getChainStatus, ChainAddressStatusEnum.UNBIND.getStatus());
        List<UserChainDO> userChainDOS = chainMapper.selectList(queryWrapper);
        return BeanUtils.toBean(userChainDOS, AppUserChainRespVO.class);
    }

    @Override
    public Long bindAddress(AppBindAddressReqVO repVO) {
        // 校验sign

        Long userId = getLoginUserId();
        validateSign(userId, repVO.getSign(), repVO.getChainAddress());
        //查看该链地址是否已经绑定
        LambdaQueryWrapper<UserChainDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserChainDO::getChainAddress, repVO.getChainAddress());
        queryWrapper.ne(UserChainDO::getChainStatus, ChainAddressStatusEnum.UNBIND.getStatus());
        Long count = chainMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw exception(CHAIN_ADDRESS_ALREADY_BOUND);
        }
        // 查看有没有已经绑定的user_identities,如果没有则是创建，如果有则是绑定
        Long userIdentityId = chainMapper.selectUserIdentity(userId);
        UserChainDO userChainDO = new UserChainDO();
        userChainDO.setUserId(userId);
        userChainDO.setChainAddress(repVO.getChainAddress());
        userChainDO.setChainStatus(ChainAddressStatusEnum.BINDING.getStatus());
        userChainDO.setIdentityId(userIdentityId);
        chainMapper.insert(userChainDO);

        if (userIdentityId == null) {
            createIdentity(userId, userChainDO.getId(), repVO.getChainAddress());
        } else {
            linkWallet(userId, userChainDO.getId(), repVO.getChainAddress());
        }
        return userChainDO.getId();
    }

    @Override
    public void unBindAddress(AppUnBindAddressReqVO reqVO) {
        Long userId = getLoginUserId();
        // 查询addressId
        UserChainDO userChainDO = chainMapper.selectById(reqVO.getAddressId());
        if (userChainDO == null || !Objects.equals(userChainDO.getUserId(), userId)) {
            throw exception(CHAIN_ADDRESS_NOT_EXISTS);
        }
        // 校验链地址状态
        if (!Objects.equals(userChainDO.getChainStatus(), ChainAddressStatusEnum.SUCCESS.getStatus()) &&
                !Objects.equals(userChainDO.getChainStatus(), ChainAddressStatusEnum.UNBIND_FAIL.getStatus())) {
            throw exception(CHAIN_ADDRESS_UNBIND_ERROR);
        }
        // 校验sign
        //validateSign(userId, reqVO.getSign(), userChainDO.getChainAddress());

        // 更新user_chain_status
        LambdaUpdateWrapper<UserChainDO> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(UserChainDO::getChainStatus, userChainDO.getChainStatus());
        updateWrapper.eq(UserChainDO::getId, reqVO.getAddressId());
        updateWrapper.set(UserChainDO::getChainStatus, ChainAddressStatusEnum.UNBINDING.getStatus());
        int updateCount = chainMapper.update(null, updateWrapper);
        if (updateCount == 0) {
            throw exception(CHAIN_ADDRESS_UNBIND_ERROR);
        }
        try {
            String taskNo = chainOperationTaskService.deleteAddress(
                    userId,
                    reqVO.getAddressId(),
                    userChainDO.getChainAddress());

        } catch (Exception e) {
            log.error("[deleteAddress] 解绑任务创建失败， 错误: {}", e.getMessage(), e);
            // 注意：这里不抛出异常，因为订单已经创建成功，任务创建失败可以后续手动处理
            // 更新状态为解绑失败
            updateWrapper.set(UserChainDO::getChainStatus, ChainAddressStatusEnum.UNBIND_FAIL.getStatus());
            chainMapper.update(null, updateWrapper);

        }

    }

    @Override
    public String generateSignData(String address) {
        Long userId = getLoginUserId();
        String message = Web3SignUtils.generateSignatureData(address, url);
        // 10. 缓存签名数据
        redisService.setCacheObject(
                String.format(USER_CHAIN_SIGN_DATA, userId, address),
                message,
                5L,
                TimeUnit.MINUTES);
        return message;
    }

    @Override
    public AppUserChainRespVO getUserChain(Long userChainId) {
        UserChainDO userChainDO = chainMapper.selectById(userChainId);
        if (userChainDO == null || !Objects.equals(userChainDO.getUserId(), getLoginUserId())) {
            throw exception(CHAIN_ADDRESS_NOT_EXISTS);
        }
        AppUserChainRespVO respVO = BeanUtils.toBean(userChainDO, AppUserChainRespVO.class);
        return respVO;
    }

    public void createIdentity(Long userId, Long userChainId, String userAddress) {
        // 9. 创建添加用户任务（添加用户到白名单 ）
        try {
            String taskNo = chainOperationTaskService.createAddUserTask(
                    userId,
                    userChainId,
                    userAddress,
                    156);

            log.info("[createIdentity] 添加用户任务创建成功， taskNo: {}", taskNo);

        } catch (Exception e) {
            log.error("[createIdentity] 添加用户任务创建失败， 错误: {}", e.getMessage(), e);
            // 注意：这里不抛出异常，因为订单已经创建成功，任务创建失败可以后续手动处理
            LambdaUpdateWrapper<UserChainDO> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(UserChainDO::getId, userChainId);
            updateWrapper.set(UserChainDO::getChainStatus, ChainAddressStatusEnum.BIND_FAIL.getStatus());
            chainMapper.update(null, updateWrapper);
        }
    }

    public void linkWallet(Long userId, Long userChainId, String userAddress) {
        // 9. 创建添加用户任务（添加用户到白名单 + 签发 Claim）
        try {
            String taskNo = chainOperationTaskService.createLinkWalletTask(
                    userId,
                    userChainId,
                    userAddress);

        } catch (Exception e) {
            log.error("[linkWallet] 添加用户任务创建失败， 错误: {}", e.getMessage(), e);
            // 注意：这里不抛出异常，因为订单已经创建成功，任务创建失败可以后续手动处理
            LambdaUpdateWrapper<UserChainDO> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(UserChainDO::getId, userChainId);
            updateWrapper.set(UserChainDO::getChainStatus, ChainAddressStatusEnum.BIND_FAIL.getStatus());
            chainMapper.update(null, updateWrapper);
        }
    }

    // 验证签名
    public void validateSign(Long userId, String sign, String address) {
        // 首先验证是否有这个签名数据
        String cacheSign = redisService.getCacheObject(String.format(USER_CHAIN_SIGN_DATA, userId, address));
        if (cacheSign == null) {
            throw exception(CHAIN_SIGN_ERROR);
        }
        boolean result = Web3SignUtils.verifySignature(address, cacheSign, sign);
        if (!result) {
            throw exception(CHAIN_SIGN_ERROR);
        } else {
            // 删除缓存
            redisService.deleteObject(String.format(USER_CHAIN_SIGN_DATA, userId, address));
        }
    }

    @Override
    public List<AppChainRespVO> getActiveChainList() {
        return chainMapper.selectActiveChainList();
    }

}