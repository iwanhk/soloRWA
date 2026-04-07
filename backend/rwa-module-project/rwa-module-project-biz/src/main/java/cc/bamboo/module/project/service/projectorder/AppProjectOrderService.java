package cc.bamboo.module.project.service.projectorder;

import cc.bamboo.framework.common.pojo.PageResult;
import cc.bamboo.module.project.controller.app.orderbalance.vo.AppAssetByCurrencyRespVO;
import cc.bamboo.module.project.controller.app.projectorder.vo.*;
import cc.bamboo.module.project.dal.dataobject.projectorder.ProjectOrderDO;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;

/**
 * 用户端 - 项目订单 Service 接口
 *
 * @author Swolf
 */
public interface AppProjectOrderService {

    /**
     * 生成 Web3 签名字符串
     *
     * @param userId       用户ID
     * @param projectId    项目ID
     * @param chainAddress 钱包地址
     * @param quantity     购买数量
     * @return 签名信息（包含签名字符串和订单号）
     */
    AppGenerateSignatureRespVO generateSignature(Long userId, Long projectId, String chainAddress, Integer quantity);

    /**
     * 创建订单（使用乐观锁）
     *
     * @param userId       用户ID
     * @param projectId    项目ID
     * @param chainAddress 钱包地址
     * @param quantity     购买数量
     * @param orderNo      预生成的订单号
     * @return 订单信息
     */
    ProjectOrderDO createOrder(Long userId, Long projectId, String chainAddress, Integer quantity, String orderNo);

    /**
     * 创建订单
     *
     * @param userId
     * @param projectId
     * @param addrssId
     * @param quantity
     * @author: Hus
     * @date: 2026/1/11 12:02
     * @return: ProjectOrderDO
     * @description
     */
    ProjectOrderDO createOrder(Long userId, Long projectId, Long addrssId, Integer quantity);

    /**
     * 支付确认
     *
     * @param userId     用户ID
     */
    void payOrder(Long userId, AppPayOrderReqVO reqVO);

    /**
     * 查询用户的订单列表
     *
     * @param appOrderListReqVO 用户ID
     * @return 订单列表
     */
    PageResult<AppOrderListRespVO> getUserOrderList(AppOrderListReqVO appOrderListReqVO);

    /**
     * 查询用户的订单详情
     *
     * @param userId  用户ID
     * @param orderId 订单ID
     * @return 订单详情
     */
    AppOrderDetailRespVO getUserOrderDetail(Long userId, Long orderId);

    /**
     * 提前赎回
     *
     * @param userId 用户ID
     * @param reqVO  提前赎回请求
     */
    void earlyRedemption(Long userId, @Valid AppEarlyRedemptionReqVO reqVO);

    /**
     * 分红申请
     *
     * @param userId 用户ID
     * @param reqVO  分红申请请求
     */
    void applyDividend(Long userId, @Valid AppDividendReqVO reqVO);

    /**
     * 到期赎回
     *
     * @param userId 用户ID
     * @param reqVO  到期赎回请求
     */
    void maturityRedemption(Long userId, @Valid AppMaturityRedemptionReqVO reqVO);

    /**
     * 计算订单当前可提现余额
     * 可提现余额 = 已解锁的分红收益总和 - 已提取金额
     *
     * @param orderId   订单ID
     * @param projectId 项目ID
     * @param userId    用户ID
     * @return 当前可提现余额
     */
    AppAssetByCurrencyRespVO calculateWithdrawableBalance(Long orderId, Long projectId, Long userId);

    /**
     * 获取用户订单状态统计
     *
     * @param userId 用户ID
     * @return 订单状态统计
     */
    AppOrderStatusCountRespVO getOrderStatusCount(Long userId);

    /**
     * 获取当前订单赎回手续费率
     *
     * @param userId  用户ID
     * @param orderId 订单ID
     * @return 手续费率
     */
    BigDecimal getRedemptionFeeRate(Long userId, Long orderId);

}
