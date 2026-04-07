package cc.bamboo.module.user.service.userchain;


import cc.bamboo.module.user.dal.dataobject.userinfo.UserInfoDO;
import cc.bamboo.module.user.dal.mysql.userchain.UserChainMapper;
import cc.bamboo.module.user.dal.mysql.userinfo.UserInfoMapper;
import cc.bamboo.module.user.enums.UserAuditStatusEnum;
import cc.bamboo.module.user.mq.message.RemoveAddressMessage;
import cc.bamboo.module.user.mq.message.UserClaimMessage;
import cc.bamboo.module.user.mq.message.UserCreateMessage;
import cc.bamboo.module.user.mq.message.UserLinkAddressMessage;
import cc.bamboo.module.user.mq.producer.ClaimProducer;
import cc.bamboo.module.user.mq.producer.RemoveAddressProducer;
import cc.bamboo.module.user.mq.producer.UserIdentityCreateProducer;
import cc.bamboo.module.user.mq.producer.UserLinkWalletProducer;
import cn.hutool.core.util.IdUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * 链操作任务服务实现
 * 负责创建链操作任务并发送消息到 chain 模块
 *
 * @author Swolf
 */
@Service
@Slf4j
public class ChainOperationTaskServiceImpl implements ChainOperationTaskService {

    @Resource
    private UserIdentityCreateProducer userIdentityCreateProducer;

    @Resource
    private UserLinkWalletProducer  userLinkWalletProducer;

    @Resource
    private ClaimProducer ClaimProducer;

    @Resource
    private RemoveAddressProducer removeAddressProducer;

    @Resource
    private UserChainMapper userChainMapper;

    @Resource
    private UserInfoMapper  userInfoMapper;


    /**
     * 生成任务编号
     * 格式：前缀_时间戳_随机数
     *
     * @param prefix 前缀
     * @return 任务编号
     */
    private String generateTaskNo(String prefix) {
        return prefix + "_" + System.currentTimeMillis() + "_" + IdUtil.fastSimpleUUID().substring(0, 8);
    }

    @Override
    public String createAddUserTask(Long userId, Long userChainId, String userAddress, Integer countryCode) {
        // 生成任务编号
        String taskNo = generateTaskNo("ADDUSER");

        log.info("[ChainOperationTaskService] 创建添加用户任务，taskNo: {}, 用户ID: {}",
                taskNo,  userId);

        try {
            // 构建消息
            UserCreateMessage message = new UserCreateMessage();
            message.setTaskNo(taskNo);
            message.setUserChainId(userChainId);
            message.setUserId(userId);
            message.setUserAddress(userAddress);
            message.setCountryCode(countryCode);
            message.setRetryCount(0);

            // 发送消息到 chain 模块
            userIdentityCreateProducer.sendUserCreateMessage(message);

            log.info("[ChainOperationTaskService] 添加用户任务创建成功，taskNo: {}, ",
                    taskNo);

            return taskNo;

        } catch (Exception e) {
            log.error("[ChainOperationTaskService] 添加用户任务创建失败，taskNo: {}，userId: {},  错误: {}",
                    taskNo,userId, e.getMessage(), e);
            throw e;
        }



    }

    @Override
    public String createLinkWalletTask(Long userId, Long userChainId, String userAddress) {
        // 生成任务编号
        String taskNo = generateTaskNo("LINKWALLET");

        log.info("[ChainOperationTaskService] 创建添加用户任务，taskNo: {}, 用户ID: {}",
                taskNo,  userId);

        try {
            // 构建消息
            UserLinkAddressMessage message = new UserLinkAddressMessage();
            message.setTaskNo(taskNo);
            message.setUserChainId(userChainId);
            message.setUserId(userId);
            message.setUserAddress(userAddress);
            message.setRetryCount(0);

            // 发送消息到 chain 模块
            userLinkWalletProducer.sendUserLinkAddressMessage(message);

            log.info("[ChainOperationTaskService] 新增钱包任务创建成功，taskNo: {}, ",
                    taskNo);

            return taskNo;

        } catch (Exception e) {
            log.error("[ChainOperationTaskService] 新增钱包任务创建失败，taskNo: {},userAddress: {}, 错误: {}",
                    taskNo,userAddress, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public String claimTask(Long userId) {

        Long id = userChainMapper.selectUserIdentity(userId);
        if(id == null){
            log.error("用户未绑定链地址，无法签发 Claim，userId: {}", userId);
            return null;
        }
        // 生成任务编号
        String taskNo = generateTaskNo("CLAIM");

        log.info("[ChainOperationTaskService] 创建签发任务，taskNo: {}, 用户ID: {}",
                taskNo,  userId);

        try {
            // 构建消息
            UserClaimMessage message = new UserClaimMessage();
            message.setTaskNo(taskNo);
            message.setUserId(userId);
            message.setRetryCount(0);

            // 发送消息到 chain 模块
            ClaimProducer.sendUserClaimMessage(message);

            log.info("[ChainOperationTaskService] 创建签发任务创建成功，taskNo: {}, ",
                    taskNo);

            return taskNo;

        } catch (Exception e) {
            log.error("[ChainOperationTaskService] 创建签发任务创建失败，taskNo: {},userId:{} 错误: {}",
                    taskNo,userId, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public String deleteAddress(Long userId, Long userChainId, String address) {
        // 生成任务编号
        String taskNo = generateTaskNo("REMOVE");

        log.info("[ChainOperationTaskService] 创建删除钱包任务，taskNo: {}, 用户ID: {}",
                taskNo,  userId);

        try {
            // 构建消息
            RemoveAddressMessage message = new RemoveAddressMessage();
            message.setTaskNo(taskNo);
            message.setUserChainId(userChainId);
            message.setUserId(userId);
            message.setUserAddress(address);
            message.setRetryCount(0);

            // 发送消息到 chain 模块
            removeAddressProducer.sendRemoveAddressMessage(message);

            log.info("[ChainOperationTaskService] 删除钱包任务创建成功，taskNo: {}, ",
                    taskNo);

            return taskNo;

        } catch (Exception e) {
            log.error("[ChainOperationTaskService] 删除钱包任务创建失败，taskNo: {},address: {} 错误: {}",
                    taskNo,address, e.getMessage(), e);
            throw e;
        }
    }

}
