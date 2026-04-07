package cc.bamboo.module.user.service.statistics;

import cc.bamboo.module.user.controller.admin.statistics.vo.UserAuditStatisticsRespVO;
import cc.bamboo.module.user.dal.mysql.userbank.UserBankMapper;
import cc.bamboo.module.user.dal.mysql.userinfo.UserInfoMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 用户审核统计 Service 实现类
 *
 * @author Swolf
 */
@Service
@Slf4j
public class UserAuditStatisticsServiceImpl implements UserAuditStatisticsService {

    @Resource
    private UserInfoMapper userInfoMapper;

    @Resource
    private UserBankMapper userBankMapper;

    @Override
    public UserAuditStatisticsRespVO getUserAuditStatistics() {
        log.info("[getUserAuditStatistics] 开始获取用户审核统计");

        // 统计待审核用户数
        Long userAuditCount = userInfoMapper.countPendingAudit();

        // 统计待审核银行卡数(仅已通过用户审核的)
        Long bankCardAuditCount = userBankMapper.countPendingAuditForApprovedUsers();

        UserAuditStatisticsRespVO result = UserAuditStatisticsRespVO.builder()
                .userAuditCount(userAuditCount)
                .bankCardAuditCount(bankCardAuditCount)
                .build();

        log.info("[getUserAuditStatistics] 用户审核统计: {}", result);
        return result;
    }

}
