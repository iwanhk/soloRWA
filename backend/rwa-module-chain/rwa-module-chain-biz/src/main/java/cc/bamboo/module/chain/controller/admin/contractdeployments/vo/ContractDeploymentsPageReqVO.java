package cc.bamboo.module.chain.controller.admin.contractdeployments.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cc.bamboo.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cc.bamboo.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 合约部署分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ContractDeploymentsPageReqVO extends PageParam {

    @Schema(description = "合约类型", example = "2")
    private String contractType;

    @Schema(description = "部署地址")
    private String deploymentAddress;

    @Schema(description = "部署者地址")
    private String deployerAddress;

    @Schema(description = "部署信息，JSON格式")
    private String deploymentInfo;

    @Schema(description = "交易哈希")
    private String transactionHash;

    @Schema(description = "区块号")
    private Long blockNumber;

    @Schema(description = "状态，如deployed-已部署", example = "2")
    private String status;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}