package cc.bamboo.module.user.controller.admin.userchain.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cc.bamboo.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cc.bamboo.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 用户链地址表=分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class UserChainPageReqVO extends PageParam {

    @Schema(description = "用户ID", example = "8226")
    private Long userId;

    @Schema(description = "关联链ID（chain_manage.id）", example = "130")
    private Long chainId;

    @Schema(description = "用户在该链上的地址（如ETH地址：0x...）")
    private String chainAddress;

    @Schema(description = "identity_id", example = "24161")
    private Long identityId;

    @Schema(description = "链上状态", example = "1")
    private Long chainStatus;

    @Schema(description = "是否默认地址：1-是 0-否（同一链下仅1个默认）")
    private Boolean isDefault;

    @Schema(description = "地址备注（如“常用钱包”）", example = "随便")
    private String addressRemark;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}