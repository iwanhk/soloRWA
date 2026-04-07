package cc.bamboo.module.project.controller.app.orderbalance.vo;

import cc.bamboo.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户项目余额表 DO
 *
 * @author Swolf
 */
@Data
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppProjectOrderBalanceRespVO{

    /**
     * 记录ID
     */
    @Schema(description = "记录ID")
    private Long id;
    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    private Long userId;
    /**
     * 项目ID
     */
    @Schema(description = "项目ID")
    private Long projectId;

    /**
     * 本金金额(元)
     */
    @Schema(description = "本金金额(元)")
    private BigDecimal principalAmount;
    /**
     * 持有金额
     */
    @Schema(description = "持有金额")
    private BigDecimal holdAmount;
    /**
     * 购买份额
     */
    @Schema(description = "购买份额")
    private Integer buyQuantity;
    /**
     * 当前持有份额(份)（赎回后扣减）
     */
    @Schema(description = "当前持有份额(份)（赎回后扣减）")
    private Integer holdQuantity;
    /**
     * 累计总收益(元)（含未提取）
     */
    @Schema(description = "累计总收益(元)（含未提取）")
    private BigDecimal totalIncome;
    /**
     * 已提取分红(元)
     */
    @Schema(description = "已提取分红(元)")
    private BigDecimal withdrawnDividend;

    /**
     * 未提取分红(元)（=总收益-已提取）
     */
    @Schema(description = "未提取分红(元)（=总收益-已提取）")
    private BigDecimal unwithdrawnDividend;
    /**
     * 累计赎回本金(元)（赎回时累加）
     */
    @Schema(description = "累计赎回本金(元)")
    private BigDecimal totalRedemptionAmount;
    /**
     * 项目名称
     */
    @Schema(description = "项目名称")
    private String projectName;


    private Integer status;

    @Schema(description = "投资币种")
    private String investmentCurrency;

    @Schema(description = "收益币种")
    private String earningCurrency;

}