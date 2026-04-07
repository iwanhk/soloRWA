package cc.bamboo.module.user.controller.admin.userchain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import com.alibaba.excel.annotation.*;
import cc.bamboo.framework.excel.core.annotations.DictFormat;
import cc.bamboo.framework.excel.core.convert.DictConvert;

@Schema(description = "管理后台 - 用户链地址表= Response VO")
@Data
@ExcelIgnoreUnannotated
public class UserChainRespVO {

    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "17334")
    @ExcelProperty("ID")
    private Long id;

    @Schema(description = "用户ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "8226")
    @ExcelProperty("用户ID")
    private Long userId;

    @Schema(description = "关联链ID（chain_manage.id）", requiredMode = Schema.RequiredMode.REQUIRED, example = "130")
    @ExcelProperty("关联链ID（chain_manage.id）")
    private Long chainId;

    @Schema(description = "用户在该链上的地址（如ETH地址：0x...）", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("用户在该链上的地址（如ETH地址：0x...）")
    private String chainAddress;

    @Schema(description = "identity_id", example = "24161")
    @ExcelProperty("identity_id")
    private Long identityId;

    @Schema(description = "链上状态", example = "1")
    @ExcelProperty(value = "链上状态", converter = DictConvert.class)
    @DictFormat("biz_user_chain_status") // TODO 代码优化：建议设置到对应的 DictTypeConstants 枚举类中
    private Long chainStatus;

    @Schema(description = "是否默认地址：1-是 0-否（同一链下仅1个默认）", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("是否默认地址：1-是 0-否（同一链下仅1个默认）")
    private Boolean isDefault;

    @Schema(description = "地址备注（如“常用钱包”）", example = "随便")
    @ExcelProperty("地址备注（如“常用钱包”）")
    private String addressRemark;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}