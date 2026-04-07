package cc.bamboo.module.project.service.projectorder;

import javax.validation.*;
import cc.bamboo.module.project.controller.admin.projectorder.vo.*;
import cc.bamboo.module.project.controller.app.projectorder.vo.*;
import cc.bamboo.module.project.dal.dataobject.projectorder.ProjectOrderDO;
import cc.bamboo.framework.common.pojo.PageResult;

import java.math.BigDecimal;

/**
 * 项目认购订单 Service 接口
 *
 * @author Swolf
 */
public interface ProjectOrderService {

    /**
     * 创建项目认购订单
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createOrder(@Valid ProjectOrderSaveReqVO createReqVO);

    /**
     * 更新项目认购订单
     *
     * @param updateReqVO 更新信息
     */
    void updateOrder(@Valid ProjectOrderSaveReqVO updateReqVO);

    /**
     * 删除项目认购订单
     *
     * @param id 编号
     */
    void deleteOrder(Long id);

    /**
     * 获得项目认购订单
     *
     * @param id 编号
     * @return 项目认购订单
     */
    ProjectOrderRespVO getOrder(Long id);

    /**
     * 获得项目认购订单分页
     *
     * @param pageReqVO 分页查询
     * @return 项目认购订单分页
     */
    PageResult<ProjectOrderRespVO> getOrderPage(ProjectOrderPageReqVO pageReqVO);

    /**
     * 审核项目订单
     *
     * @param auditReqVO 审核信息
     * @param auditUserId 审核人ID
     * @param auditUserName 审核人名称
     */
    void auditOrder(@Valid ProjectOrderAuditReqVO auditReqVO, Long auditUserId, String auditUserName);


    /**
     * 发送铸造 Token 消息
     *
     * @param orderId 订单ID
     */
    void sendMintToken(Long orderId);
    /**
     * 提前赎回
     *
     * @param userId 用户ID
     * @param reqVO 提前赎回请求
     */
    void earlyRedemption(Long userId, @Valid AppEarlyRedemptionReqVO reqVO);

    /**
     * 分红申请
     *
     * @param userId 用户ID
     * @param reqVO 分红申请请求
     */
    void applyDividend(Long userId, @Valid AppDividendReqVO reqVO);

    /**
     * 到期赎回
     *
     * @param userId 用户ID
     * @param reqVO 到期赎回请求
     */
    void maturityRedemption(Long userId, @Valid AppMaturityRedemptionReqVO reqVO);

    /**
     * 取消未支付订单并恢复库存
     * 由RabbitMQ延时消息触发
     *
     * @param orderId 订单ID
     * @param projectId 项目ID
     * @param quantity 购买数量（用于恢复库存）
     */
    void cancelUnpaidOrder(Long orderId, Long projectId, Integer quantity);

    BigDecimal calculateEarlyRedemptionFeeRate(String feeJson, long holdDays);
}