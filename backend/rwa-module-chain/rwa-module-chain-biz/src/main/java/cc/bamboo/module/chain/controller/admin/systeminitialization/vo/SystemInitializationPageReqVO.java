package cc.bamboo.module.chain.controller.admin.systeminitialization.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cc.bamboo.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cc.bamboo.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 系统初始化分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class SystemInitializationPageReqVO extends PageParam {

    @Schema(description = "初始化步骤名称")
    private String step;

    @Schema(description = "状态，如pending-待处理，completed-已完成", example = "2")
    private String status;

    @Schema(description = "相关合约地址")
    private String contractAddress;

    @Schema(description = "交易哈希")
    private String transactionHash;

    @Schema(description = "区块号")
    private Long blockNumber;

    @Schema(description = "错误信息，若有")
    private String errorMessage;

    @Schema(description = "元数据，JSON格式")
    private String metadata;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}