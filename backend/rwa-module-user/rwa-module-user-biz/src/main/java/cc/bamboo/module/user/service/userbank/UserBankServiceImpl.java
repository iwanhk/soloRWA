package cc.bamboo.module.user.service.userbank;

import cc.bamboo.module.user.enums.UserAuditStatusEnum;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import cc.bamboo.module.user.controller.admin.userbank.vo.*;
import cc.bamboo.module.user.controller.app.userinfo.vo.AppUserBankUpdateReqVO;
import cc.bamboo.module.user.dal.dataobject.userbank.UserBankDO;
import cc.bamboo.framework.common.pojo.PageResult;
import cc.bamboo.framework.common.pojo.PageParam;
import cc.bamboo.framework.common.util.object.BeanUtils;

import cc.bamboo.module.user.dal.mysql.userbank.UserBankMapper;

import static cc.bamboo.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cc.bamboo.module.user.enums.ErrorCodeConstants.*;

/**
 * 用户银行卡信息 Service 实现类
 *
 * @author Swolf
 */
@Service
@Validated
public class UserBankServiceImpl implements UserBankService {

    @Resource
    private UserBankMapper bankMapper;

    @Override
    public Long createBank(UserBankSaveReqVO createReqVO) {
        // 插入
        UserBankDO bank = BeanUtils.toBean(createReqVO, UserBankDO.class);
        bankMapper.insert(bank);
        // 返回
        return bank.getId();
    }

    @Override
    public void updateBank(UserBankSaveReqVO updateReqVO) {
        // 校验存在
        validateBankExists(updateReqVO.getId());
        // 更新
        UserBankDO updateObj = BeanUtils.toBean(updateReqVO, UserBankDO.class);
        bankMapper.updateById(updateObj);
    }

    @Override
    public void deleteBank(Long id) {
        // 校验存在
        validateBankExists(id);
        // 删除
        bankMapper.deleteById(id);
    }

    private void validateBankExists(Long id) {
        if (bankMapper.selectById(id) == null) {
            throw exception(BANK_NOT_EXISTS);
        }
    }

    @Override
    public UserBankDO getBank(Long id) {
        return bankMapper.selectById(id);
    }

    @Override
    public PageResult<UserBankDO> getBankPage(UserBankPageReqVO pageReqVO) {
        return bankMapper.selectPage(pageReqVO);
    }

    @Override
    public void updateBankInfo(Long userId, AppUserBankUpdateReqVO reqVO) {
        UserBankDO bank = bankMapper.selectById(reqVO.getId());
        if (bank != null && !bank.getUserId().equals(userId)) {
            throw exception(BANK_NOT_EXISTS);
        }
        String oldJson = JSON.toJSONString(bank);
        // Update
        UserBankDO updateObj = UserBankDO.builder()
                .id(bank.getId())
                .bankAccountName(reqVO.getBankAccountName())
                .bankAccount(reqVO.getBankAccount())
                .bankName(reqVO.getBankName())
                .bankBranch(reqVO.getBankBranch())
                .auditStatus(UserAuditStatusEnum.PENDING.getStatus()) // Under Review
                .auditRemark("") // Clear remark
                .oldBank(oldJson)
                .build();
        bankMapper.updateById(updateObj);
    }

    @Override
    public Long auditBank(UserBankAuditReqVO auditReqVO) {
        // 1. 校验银行卡存在
        UserBankDO bank = bankMapper.selectById(auditReqVO.getId());
        if (bank == null) {
            throw exception(BANK_NOT_EXISTS);
        }

        // 2. 校验审核状态是否有效（只能是通过或不通过）
        if (!Objects.equals(auditReqVO.getAuditStatus(), UserAuditStatusEnum.APPROVED.getStatus())
                && !Objects.equals(auditReqVO.getAuditStatus(), UserAuditStatusEnum.REJECTED.getStatus())) {
            throw exception(BANK_AUDIT_STATUS_INVALID);
        }
        // 如果是审核通过直接更新
        if (Objects.equals(auditReqVO.getAuditStatus(), UserAuditStatusEnum.APPROVED.getStatus())) {
            // 3. 更新审核状态
            UserBankDO updateObj = UserBankDO.builder()
                    .id(auditReqVO.getId())
                    .auditStatus(auditReqVO.getAuditStatus())
                    .auditRemark(auditReqVO.getAuditRemark())
                    .build();
            bankMapper.updateById(updateObj);
        } else if (Objects.equals(auditReqVO.getAuditStatus(), UserAuditStatusEnum.REJECTED.getStatus())) {
            // 如果不通过则回滚到之前的状态和信息
            /*
             * UserBankDO userBankDO = JSONObject.parseObject(bank.getOldBank(),
             * UserBankDO.class);
             * userBankDO.setUpdateTime(LocalDateTime.now());
             */
            UserBankDO updateObj = UserBankDO.builder()
                    .id(auditReqVO.getId())
                    .auditStatus(auditReqVO.getAuditStatus())
                    .auditRemark(auditReqVO.getAuditRemark())
                    .build();
            bankMapper.updateById(updateObj);
        }
        return bank.getUserId();
    }

    @Override
    public Map<Long, Long> getPendingBankApplyMap(Collection<Long> userIds) {
        if (CollUtil.isEmpty(userIds)) {
            return Collections.emptyMap();
        }
        List<UserBankDO> list = bankMapper.selectList(new LambdaQueryWrapper<UserBankDO>()
                .in(UserBankDO::getUserId, userIds)
                .eq(UserBankDO::getAuditStatus, UserAuditStatusEnum.PENDING.getStatus()));

        Map<Long, Long> result = new HashMap<>();
        list.forEach(item -> result.putIfAbsent(item.getUserId(), item.getId()));
        return result;
    }

}