package cc.bamboo.module.user.controller.admin.userinfo.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 用户购买信息导出 Response VO")
@Data
@ExcelIgnoreUnannotated
public class UserPurchaseExportRespVO {

    @Schema(description = "用户名")
    @ExcelProperty("用户名")
    private String username;

    @Schema(description = "手机号（脱敏）")
    @ExcelProperty("手机号")
    private String mobile;

    @Schema(description = "注册时间")
    @ExcelProperty("注册时间")
    @ColumnWidth(22)
    private LocalDateTime registerTime;

    @Schema(description = "购买项目及金额")
    @ExcelProperty("购买项目及金额")
    private String purchaseProjects;
}
