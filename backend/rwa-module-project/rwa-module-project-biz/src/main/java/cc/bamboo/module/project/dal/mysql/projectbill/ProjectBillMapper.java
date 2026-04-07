package cc.bamboo.module.project.dal.mysql.projectbill;

import java.util.*;

import cc.bamboo.framework.common.pojo.PageResult;
import cc.bamboo.framework.mybatis.core.query.LambdaQueryWrapperX;
import cc.bamboo.framework.mybatis.core.mapper.BaseMapperX;
import cc.bamboo.module.project.dal.dataobject.projectbill.ProjectBillDO;
import cc.bamboo.module.project.enums.AuditStatusEnum;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.apache.ibatis.annotations.Mapper;
import cc.bamboo.module.project.controller.admin.projectbill.vo.*;

/**
 * 项目账单管理表 Mapper
 *
 * @author Swolf
 */
@Mapper
public interface ProjectBillMapper extends BaseMapperX<ProjectBillDO> {

        default PageResult<ProjectBillDO> selectPage(ProjectBillPageReqVO reqVO) {
                return selectPage(reqVO, new LambdaQueryWrapperX<ProjectBillDO>()
                                .eqIfPresent(ProjectBillDO::getBillNo, reqVO.getBillNo())
                                .eqIfPresent(ProjectBillDO::getBillType, reqVO.getBillType())
                                .betweenIfPresent(ProjectBillDO::getApplyTime, reqVO.getApplyTime())
                                .eqIfPresent(ProjectBillDO::getUserId, reqVO.getUserId())
                                .eqIfPresent(ProjectBillDO::getProjectId, reqVO.getProjectId())
                                .likeIfPresent(ProjectBillDO::getProjectName, reqVO.getProjectName())
                                .eqIfPresent(ProjectBillDO::getBankAccountName, reqVO.getPayee())
                                .eqIfPresent(ProjectBillDO::getBankAccount, reqVO.getBankAccount())
                                .likeIfPresent(ProjectBillDO::getBankName, reqVO.getBankName())
                                .eqIfPresent(ProjectBillDO::getBillAmount, reqVO.getBillAmount())
                                .eqIfPresent(ProjectBillDO::getAuditStatus, reqVO.getAuditStatus())
                                .eqIfPresent(ProjectBillDO::getAuditUserId, reqVO.getAuditUserId())
                                .likeIfPresent(ProjectBillDO::getAuditUserName, reqVO.getAuditUserName())
                                .betweenIfPresent(ProjectBillDO::getAuditTime, reqVO.getAuditTime())
                                .eqIfPresent(ProjectBillDO::getAuditRemark, reqVO.getAuditRemark())
                                .eqIfPresent(ProjectBillDO::getPayVoucherUrl, reqVO.getPayVoucherUrl())
                                .betweenIfPresent(ProjectBillDO::getPayTime, reqVO.getPayTime())
                                .betweenIfPresent(ProjectBillDO::getCreateTime, reqVO.getCreateTime())
                                .orderByDesc(ProjectBillDO::getId));
        }

        /**
         * 使用乐观锁更新账单审核状态
         * 
         * @param id            账单ID
         * @param auditStatus   审核状态
         * @param auditUserId   审核人ID
         * @param auditUserName 审核人姓名
         * @param auditTime     审核时间
         * @param auditRemark   审核备注
         * @param payVoucherUrl 支付凭证URL(可选)
         * @param payTime       支付时间(可选)
         * @return 更新行数
         */
        @org.apache.ibatis.annotations.Update("UPDATE biz_project_bill SET " +
                        "audit_status = #{auditStatus}, " +
                        "audit_user_id = #{auditUserId}, " +
                        "audit_user_name = #{auditUserName}, " +
                        "audit_time = #{auditTime}, " +
                        "audit_remark = #{auditRemark}, " +
                        "actual_amount = #{actualAmount}, " +
                        "pay_voucher_url = #{payVoucherUrl}, " +
                        "pay_time = #{payTime}, " +
                        "arrival_day = #{arrivalDay}, " +
                        "update_time = NOW() " +
                        "WHERE id = #{id} AND audit_status = 1 AND deleted = 0")
        int updateAuditStatus(@org.apache.ibatis.annotations.Param("id") Long id,
                        @org.apache.ibatis.annotations.Param("auditStatus") Integer auditStatus,
                        @org.apache.ibatis.annotations.Param("auditUserId") Long auditUserId,
                        @org.apache.ibatis.annotations.Param("auditUserName") String auditUserName,
                        @org.apache.ibatis.annotations.Param("auditTime") java.time.LocalDateTime auditTime,
                        @org.apache.ibatis.annotations.Param("auditRemark") String auditRemark,
                        @org.apache.ibatis.annotations.Param("actualAmount") java.math.BigDecimal actualAmount,
                        @org.apache.ibatis.annotations.Param("payVoucherUrl") String payVoucherUrl,
                        @org.apache.ibatis.annotations.Param("payTime") java.time.LocalDateTime payTime,
                        @org.apache.ibatis.annotations.Param("arrivalDay") Integer day);

        /**
         * 使用乐观锁更新支付凭证
         * 
         * @param id            账单ID
         * @param payVoucherUrl 支付凭证URL
         * @param payTime       支付时间
         * @return 更新行数
         */
        @org.apache.ibatis.annotations.Update("UPDATE biz_project_bill SET " +
                        "pay_voucher_url = #{payVoucherUrl}, " +
                        "pay_time = #{payTime}, " +
                        "audit_status = 4, " +
                        "update_time = NOW() " +
                        "WHERE id = #{id} AND audit_status = 2 AND (pay_voucher_url IS NULL OR pay_voucher_url = '') AND deleted = 0")
        int updatePayVoucher(@org.apache.ibatis.annotations.Param("id") Long id,
                        @org.apache.ibatis.annotations.Param("payVoucherUrl") String payVoucherUrl,
                        @org.apache.ibatis.annotations.Param("payTime") java.time.LocalDateTime payTime);

        /**
         * 统计待审核账单数量
         * 
         * @param startTime 开始时间(可选)
         * @param endTime   结束时间(可选)
         * @return 待审核数量
         */
        default Long countPending(java.time.LocalDateTime startTime, java.time.LocalDateTime endTime) {
                return selectCount(new LambdaQueryWrapperX<ProjectBillDO>()
                                .eq(ProjectBillDO::getAuditStatus, 1)
                                .betweenIfPresent(ProjectBillDO::getApplyTime,
                                                new java.time.LocalDateTime[] { startTime, endTime }));
        }

        /**
         * 统计已审核未支付账单数量
         * 
         * @param startTime 开始时间(可选)
         * @param endTime   结束时间(可选)
         * @return 已审核未支付数量
         */
        default Long countApprovedUnpaid(java.time.LocalDateTime startTime, java.time.LocalDateTime endTime) {
                return selectCount(new LambdaQueryWrapperX<ProjectBillDO>()
                                .eq(ProjectBillDO::getAuditStatus, 2)
                                .and(w -> w.isNull(ProjectBillDO::getPayVoucherUrl).or()
                                                .eq(ProjectBillDO::getPayVoucherUrl, "")));
        }

        /**
         * 统计已支付账单数量
         * 
         * @param startTime 开始时间(可选)
         * @param endTime   结束时间(可选)
         * @return 已支付数量
         */
        default Long countPaid(java.time.LocalDateTime startTime, java.time.LocalDateTime endTime) {
                return selectCount(new LambdaQueryWrapperX<ProjectBillDO>()
                                .eq(ProjectBillDO::getAuditStatus, 2)
                                .isNotNull(ProjectBillDO::getPayVoucherUrl)
                                .ne(ProjectBillDO::getPayVoucherUrl, ""));
        }

        /**
         * 统计分红总金额
         * 
         * @param startTime 开始时间(可选)
         * @param endTime   结束时间(可选)
         * @return 分红总金额
         */
        default java.math.BigDecimal sumDividendAmount(java.time.LocalDateTime startTime,
                        java.time.LocalDateTime endTime) {
                List<ProjectBillDO> list = selectList(new LambdaQueryWrapperX<ProjectBillDO>()
                                .eq(ProjectBillDO::getBillType, 1)
                                .eq(ProjectBillDO::getAuditStatus, 2)
                                .betweenIfPresent(ProjectBillDO::getApplyTime,
                                                new java.time.LocalDateTime[] { startTime, endTime }));
                return list.stream()
                                .map(ProjectBillDO::getBillAmount)
                                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
        }

        /**
         * 统计赎回总金额(到期赎回+提前赎回)
         * 
         * @param startTime 开始时间(可选)
         * @param endTime   结束时间(可选)
         * @return 赎回总金额
         */
        default java.math.BigDecimal sumRedemptionAmount(java.time.LocalDateTime startTime,
                        java.time.LocalDateTime endTime) {
                List<ProjectBillDO> list = selectList(new LambdaQueryWrapperX<ProjectBillDO>()
                                .in(ProjectBillDO::getBillType, java.util.Arrays.asList(2, 3))
                                .eq(ProjectBillDO::getAuditStatus, 2)
                                .betweenIfPresent(ProjectBillDO::getApplyTime,
                                                new java.time.LocalDateTime[] { startTime, endTime }));
                return list.stream()
                                .map(ProjectBillDO::getBillAmount)
                                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
        }

        /**
         * 统计待审核账单数量(所有租户)
         * 
         * @return 待审核账单数
         */

        default Long countPendingAudit() {
                LambdaQueryWrapper<ProjectBillDO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
                lambdaQueryWrapper.eq(ProjectBillDO::getAuditStatus, AuditStatusEnum.PENDING.getStatus());
                return selectCount(lambdaQueryWrapper);
        }

        /**
         * 统计待审核账单数量(按租户)
         * 
         * @param tenantId 租户ID
         * @return 待审核账单数
         */
        default Long countPendingAuditByTenant(){
            LambdaQueryWrapper<ProjectBillDO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(ProjectBillDO::getAuditStatus, AuditStatusEnum.PENDING.getStatus());
            return selectCount(lambdaQueryWrapper);
        }
}