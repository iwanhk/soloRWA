package cc.bamboo.module.user.controller.admin.userbank.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import com.alibaba.excel.annotation.*;

@Schema(description = "管理后台 - 用户银行卡信息 Response VO")
@Data
@ExcelIgnoreUnannotated
public class UserBankRespVO {

    @Schema(description = "银行卡记录ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "27765")
    @ExcelProperty("银行卡记录ID")
    private Long id;

    @Schema(description = "关联用户ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "18779")
    @ExcelProperty("关联用户ID")
    private Long userId;

    @Schema(description = "银行卡开户名（需与实名一致）", requiredMode = Schema.RequiredMode.REQUIRED, example = "赵六")
    @ExcelProperty("银行卡开户名（需与实名一致）")
    private String bankAccountName;

    @Schema(description = "银行卡号", requiredMode = Schema.RequiredMode.REQUIRED, example = "30915")
    @ExcelProperty("银行卡号")
    private String bankAccount;

    @Schema(description = "开户行", requiredMode = Schema.RequiredMode.REQUIRED, example = "王五")
    @ExcelProperty("开户行")
    private String bankName;

    @Schema(description = "开户行支行")
    @ExcelProperty("开户行支行")
    private String bankBranch;

    @Schema(description = "是否默认银行卡", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("是否默认银行卡")
    private Boolean isDefault;

    @Schema(description = "审核状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty("审核状态")
    private Integer auditStatus;

    @Schema(description = "审核备注", example = "你说的对")
    @ExcelProperty("审核备注")
    private String auditRemark;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}