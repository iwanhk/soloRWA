package cc.bamboo.module.user.service.userinfo;

import cc.bamboo.framework.common.exception.ErrorCode;
import cc.bamboo.module.user.enums.F2AEnum;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import cc.bamboo.module.user.controller.admin.userinfo.vo.*;
import cc.bamboo.module.user.dal.dataobject.userinfo.UserInfoDO;
import cc.bamboo.framework.common.pojo.PageResult;
import cc.bamboo.framework.common.pojo.PageParam;
import cc.bamboo.framework.common.util.object.BeanUtils;

import cc.bamboo.module.user.dal.mysql.userinfo.UserInfoMapper;

import static cc.bamboo.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cc.bamboo.module.user.enums.ErrorCodeConstants.*;

/**
 * 用户基础信息 Service 实现类
 *
 * @author Swolf
 */
@Service
@Validated
public class UserInfoServiceImpl implements UserInfoService {

    @Resource
    private UserInfoMapper infoMapper;

    @Resource
    private PasswordEncoder passwordEncoder;

    // 可用数字（0-9排除4、7）
    private static final String VALID_DIGITS = "01235689";
    // 可用字母（大小写混合）
    private static final String VALID_LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    private static final Random RANDOM = new Random();

    @Override
    public Long createInfo(UserInfoSaveReqVO createReqVO) {
        // 插入
        UserInfoDO info = BeanUtils.toBean(createReqVO, UserInfoDO.class);
        // 生成用户昵称
        String nickName = generateNickname();
        info.setNickName(nickName);
        infoMapper.insert(info);
        // 返回
        return info.getId();
    }

    /**
     * 生成固定格式的随机昵称（前4位字母+后4位数字）
     * 
     * @return 8位昵称：4字母 + 4数字（数字不含4、7）
     */
    public static String generateNickname() {
        // 生成前4位字母
        StringBuilder letters = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            int index = RANDOM.nextInt(VALID_LETTERS.length());
            letters.append(VALID_LETTERS.charAt(index));
        }

        // 生成后4位数字（排除4、7）
        StringBuilder digits = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            int index = RANDOM.nextInt(VALID_DIGITS.length());
            digits.append(VALID_DIGITS.charAt(index));
        }

        // 拼接字母和数字，返回最终昵称
        return letters.append(digits).toString();
    }

    @Override
    public void updateInfo(UserInfoSaveReqVO updateReqVO) {
        // 校验存在
        validateInfoExists(updateReqVO.getId());
        // 更新
        UserInfoDO updateObj = BeanUtils.toBean(updateReqVO, UserInfoDO.class);
        infoMapper.updateById(updateObj);
    }

    @Override
    public void updateInfo(UserInfoDO userInfoDO) {
        infoMapper.updateById(userInfoDO);
    }

    @Override
    public void deleteInfo(Long id) {
        // 校验存在
        validateInfoExists(id);
        // 删除
        infoMapper.deleteById(id);
    }

    private void validateInfoExists(Long id) {
        if (infoMapper.selectById(id) == null) {
            throw exception(INFO_NOT_EXISTS);
        }
    }

    @Override
    public UserInfoDO getInfo(Long id) {
        return infoMapper.selectById(id);
    }

    @Override
    public PageResult<UserInfoDO> getInfoPage(UserInfoPageReqVO pageReqVO) {
        return infoMapper.selectPage(pageReqVO);
    }

    @Override
    public UserInfoDO getUserInfoByMobile(String mobile) {
        LambdaQueryWrapper<UserInfoDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserInfoDO::getMobile, mobile);
        queryWrapper.last("limit 1");
        return infoMapper.selectOne(queryWrapper);
    }

    @Override
    public UserInfoDO getUserInfoByEmail(String email) {
        LambdaQueryWrapper<UserInfoDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserInfoDO::getEmail, email);
        queryWrapper.last("limit 1");
        return infoMapper.selectOne(queryWrapper);
    }

    @Override
    public boolean isPasswordMatch(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approve2FAUnbind(AuditF2AReqVO reqVO) {
        // 校验用户是否存在
        UserInfoDO user = infoMapper.selectById(reqVO.getId());
        if (user == null) {
            throw exception(INFO_NOT_EXISTS);
        }

        // 校验2FA状态是否为解绑中
        if (!Objects.equals(user.getTwoFactorAuthStatus(), F2AEnum.UNBINDING.getStatus())) {
            throw exception(new ErrorCode(10008, "2FA不在解绑中状态"));
        }

        // 更新2FA状态
        UserInfoDO updateReqVO = new UserInfoDO();
        updateReqVO.setId(reqVO.getId());
        if (reqVO.isApproved()) {
            // 审核通过：设置为未开启，清空密钥
            updateReqVO.setTwoFactorAuthStatus(F2AEnum.NOT_ENABLED.getStatus()); // 0-未开启
            updateReqVO.setTwoFactorAuthSecret(null);
            updateReqVO.setTwoFactorAuthBindTime(null);
        } else {
            // 审核不通过：恢复为已开启
            updateReqVO.setTwoFactorAuthStatus(F2AEnum.ENABLED.getStatus()); // 1-已开启
        }
        infoMapper.updateById(updateReqVO);
    }

}