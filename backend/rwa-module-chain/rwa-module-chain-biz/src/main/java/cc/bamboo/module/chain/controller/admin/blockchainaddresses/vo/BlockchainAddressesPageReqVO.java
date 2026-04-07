package cc.bamboo.module.chain.controller.admin.blockchainaddresses.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cc.bamboo.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cc.bamboo.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 区块链地址分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class BlockchainAddressesPageReqVO extends PageParam {

    @Schema(description = "地址名称", example = "芋艿")
    private String name;

    @Schema(description = "区块链地址")
    private String address;

    @Schema(description = "地址私钥")
    private String privateKey;

    @Schema(description = "地址描述", example = "你猜")
    private String description;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}