package cc.bamboo.module.chain.controller.admin.claimissueridentities.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cc.bamboo.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cc.bamboo.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 声明发行者身份分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ClaimIssuerIdentitiesPageReqVO extends PageParam {

    @Schema(description = "发行者地址")
    private String address;

    @Schema(description = "发行者合约地址")
    private String contractAddress;

    @Schema(description = "管理密钥地址")
    private String managementKey;

    @Schema(description = "关联的区块链地址ID", example = "16071")
    private Long blockchainAddressId;

    @Schema(description = "盐值，用于加密或哈希计算")
    private String salt;

    @Schema(description = "交易哈希")
    private String transactionHash;

    @Schema(description = "区块号")
    private Long blockNumber;

    @Schema(description = "合约是否已部署，0-未部署，1-已部署")
    private Boolean contractDeployed;

    @Schema(description = "声明密钥是否已设置，0-未设置，1-已设置")
    private Boolean claimKeySetup;

    @Schema(description = "状态，如active-活跃", example = "1")
    private String status;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}