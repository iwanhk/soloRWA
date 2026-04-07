package cc.bamboo.module.project.dal.mysql.projectorder;

import java.util.*;

import cc.bamboo.framework.common.pojo.PageResult;
import cc.bamboo.framework.mybatis.core.query.LambdaQueryWrapperX;
import cc.bamboo.framework.mybatis.core.mapper.BaseMapperX;
import cc.bamboo.module.project.api.purchase.dto.UserPurchaseProjectAmountRespDTO;
import cc.bamboo.module.project.dal.dataobject.projectorder.ProjectOrderDO;
import cc.bamboo.module.project.enums.OrderStatusEnum;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import cc.bamboo.module.project.controller.admin.projectorder.vo.*;

/**
 * 项目认购订单 Mapper
 *
 * @author Swolf
 */
@Mapper
public interface ProjectOrderMapper extends BaseMapperX<ProjectOrderDO> {

    default PageResult<ProjectOrderDO> selectPage(ProjectOrderPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ProjectOrderDO>()
                .eqIfPresent(ProjectOrderDO::getOrderNo, reqVO.getOrderNo())
                .betweenIfPresent(ProjectOrderDO::getApplyDate, reqVO.getApplyDate())
                .eqIfPresent(ProjectOrderDO::getUserId, reqVO.getUserId())
                .eqIfPresent(ProjectOrderDO::getOrderStatus, reqVO.getOrderStatus())
                .eqIfPresent(ProjectOrderDO::getProjectId, reqVO.getProjectId())
                .likeIfPresent(ProjectOrderDO::getProjectName, reqVO.getProjectName())
                .eqIfPresent(ProjectOrderDO::getSubscribeQuantity, reqVO.getSubscribeQuantity())
                .eqIfPresent(ProjectOrderDO::getPrice, reqVO.getPrice())
                .eqIfPresent(ProjectOrderDO::getTotalAmount, reqVO.getTotalAmount())
                .betweenIfPresent(ProjectOrderDO::getConfirmPurchaseTime, reqVO.getConfirmPurchaseTime())
                .eqIfPresent(ProjectOrderDO::getPayType, reqVO.getPayType())
                .eqIfPresent(ProjectOrderDO::getChainAddress, reqVO.getChainAddress())
                .eqIfPresent(ProjectOrderDO::getContractNo, reqVO.getContractNo())
                .eqIfPresent(ProjectOrderDO::getPayVoucherUrl, reqVO.getPayVoucherUrl())
                .betweenIfPresent(ProjectOrderDO::getAuditTime, reqVO.getAuditTime())
                .eqIfPresent(ProjectOrderDO::getAuditUserId, reqVO.getAuditUserId())
                .likeIfPresent(ProjectOrderDO::getAuditUserName, reqVO.getAuditUserName())
                .eqIfPresent(ProjectOrderDO::getAuditRemark, reqVO.getAuditRemark())
                .betweenIfPresent(ProjectOrderDO::getCancelTime, reqVO.getCancelTime())
                .eqIfPresent(ProjectOrderDO::getCancelReason, reqVO.getCancelReason())
                .betweenIfPresent(ProjectOrderDO::getExpireTime, reqVO.getExpireTime())
                .betweenIfPresent(ProjectOrderDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(ProjectOrderDO::getId));
    }

    default long getProjectOrderCountByProjectId(Long projectId, Long userId) {
        return selectCount(new LambdaQueryWrapperX<ProjectOrderDO>()
                .eq(ProjectOrderDO::getProjectId, projectId)
                .eq(ProjectOrderDO::getUserId, userId)
                .in(ProjectOrderDO::getOrderStatus,
                        Arrays.asList(OrderStatusEnum.APPROVED.getStatus(), OrderStatusEnum.ENDED.getStatus())));
    }

    /**
     * 统计待审核的订单数量
     * 
     * @return 待审核订单数
     */

    default Long countOrderAudit(){
        LambdaQueryWrapper<ProjectOrderDO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ProjectOrderDO::getOrderStatus, OrderStatusEnum.UNDER_REVIEW.getStatus());
        return selectCount(lambdaQueryWrapper);
    }

    @Select({
            "<script>",
            "SELECT user_id AS userId, project_id AS projectId, project_name AS projectName, investment_currency AS investmentCurrency, COALESCE(SUM(total_amount), 0) AS amount",
            "FROM biz_project_order",
            "WHERE deleted = 0",
            "AND order_status IN (#{approvedStatus}, #{endedStatus})",
            "AND user_id IN",
            "<foreach collection='userIds' item='id' open='(' separator=',' close=')'>",
            "#{id}",
            "</foreach>",
            "GROUP BY user_id, project_id, project_name, investment_currency",
            "</script>"
    })
    List<UserPurchaseProjectAmountRespDTO> selectUserPurchaseAmountByUserIds(
            @Param("userIds") Collection<Long> userIds,
            @Param("approvedStatus") Integer approvedStatus,
            @Param("endedStatus") Integer endedStatus);

}
