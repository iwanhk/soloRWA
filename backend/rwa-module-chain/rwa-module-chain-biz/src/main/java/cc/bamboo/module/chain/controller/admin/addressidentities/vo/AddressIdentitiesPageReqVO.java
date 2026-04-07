package cc.bamboo.module.chain.controller.admin.addressidentities.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cc.bamboo.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cc.bamboo.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 地址身份关联分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class AddressIdentitiesPageReqVO extends PageParam {

    @Schema(description = "区块链地址")
    private String address;

    @Schema(description = "身份ID，关联到对应的身份表", example = "13618")
    private Long identityId;

    @Schema(description = "身份类型，如ClaimIssuer、User等", example = "1")
    private String type;

    @Schema(description = "合约地址")
    private String contractAddress;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}