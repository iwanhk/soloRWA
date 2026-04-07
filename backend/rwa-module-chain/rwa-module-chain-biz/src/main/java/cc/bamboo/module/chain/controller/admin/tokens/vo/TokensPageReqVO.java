package cc.bamboo.module.chain.controller.admin.tokens.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cc.bamboo.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cc.bamboo.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 代币分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class TokensPageReqVO extends PageParam {

    @Schema(description = "代币名称", example = "赵六")
    private String name;

    @Schema(description = "代币符号")
    private String symbol;

    @Schema(description = "小数位数")
    private Long decimals;

    @Schema(description = "代币合约地址")
    private String address;

    @Schema(description = "所有者地址")
    private String ownerAddress;

    @Schema(description = "部署者地址")
    private String deployerAddress;

    @Schema(description = "身份注册表存储ID", example = "8976")
    private Long identityRegistryStorageId;

    @Schema(description = "身份注册表地址")
    private String identityRegistryAddress;

    @Schema(description = "声明主题注册表地址")
    private String claimTopicsRegistryAddress;

    @Schema(description = "可信发行者注册表地址")
    private String trustedIssuersRegistryAddress;

    @Schema(description = "模块化合规合约地址")
    private String modularComplianceAddress;

    @Schema(description = "代币链上ID地址")
    private String tokenOnchainIdAddress;

    @Schema(description = "交易哈希")
    private String transactionHash;

    @Schema(description = "区块号")
    private Long blockNumber;

    @Schema(description = "状态，如pending-待处理，deployed-已部署", example = "2")
    private String status;

    @Schema(description = "盐值，用于加密或哈希计算")
    private String salt;

    @Schema(description = "代币代理，JSON格式")
    private String tokenAgents;

    @Schema(description = "声明主题，JSON格式")
    private String claimTopics;

    @Schema(description = "发行者，JSON格式")
    private String issuers;

    @Schema(description = "发行者声明，JSON格式")
    private String issuerClaims;

    @Schema(description = "部署信息，JSON格式")
    private String deploymentInfo;

    @Schema(description = "错误信息，若有")
    private String errorMessage;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}