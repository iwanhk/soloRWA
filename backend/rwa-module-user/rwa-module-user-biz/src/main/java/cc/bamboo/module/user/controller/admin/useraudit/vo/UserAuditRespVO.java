package cc.bamboo.module.user.controller.admin.useraudit.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import com.alibaba.excel.annotation.*;

@Schema(description = "管理后台 - 用户投资者认证审核 Response VO")
@Data
@ExcelIgnoreUnannotated
public class UserAuditRespVO {

    @Schema(description = "审核记录ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "5850")
    @ExcelProperty("审核记录ID")
    private Long id;

    @Schema(description = "关联用户ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "8299")
    @ExcelProperty("关联用户ID")
    private Long userId;

    @Schema(description = "用户姓名（提交审核时的姓名）", example = "王五")
    @ExcelProperty("用户姓名（提交审核时的姓名）")
    private String realName;

    @Schema(description = "证件号（脱敏存储）")
    @ExcelProperty("证件号（脱敏存储）")
    private String idCard;

    @Schema(description = "证件号有效期")
    @ExcelProperty("证件号有效期")
    private String idCardExpire;

    @Schema(description = "证件号人像面图片地址", example = "https://www.iocoder.cn")
    @ExcelProperty("证件号人像面图片地址")
    private String idCardFrontUrl;

    @Schema(description = "证件号国徽面图片地址", example = "https://www.iocoder.cn")
    @ExcelProperty("证件号国徽面图片地址")
    private String idCardBackUrl;

    @Schema(description = "投资资质图片地址", example = "https://www.iocoder.cn")
    @ExcelProperty("投资资质图片地址")
    private String investmentQualificationUrl;

    @Schema(description = "银行流水单图片地址", example = "https://www.iocoder.cn")
    @ExcelProperty("银行流水单图片地址")
    private String bankFlowUrl;

    @Schema(description = "住址证明图片地址", example = "https://www.iocoder.cn")
    @ExcelProperty("住址证明图片地址")
    private String residenceProofUrl;

    @Schema(description = "关联用户银行卡ID（user_bank_card.id）", example = "6043")
    @ExcelProperty("关联用户银行卡ID（user_bank_card.id）")
    private Long bankCardId;

    @Schema(description = "邮箱")
    @ExcelProperty("邮箱")
    private String email;

    @Schema(description = "联系电话")
    @ExcelProperty("联系电话")
    private String contactPhone;

    @Schema(description = "审核状态：0-待提交 1-待审核 2-审核通过 3-审核驳回", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty("审核状态：0-待提交 1-待审核 2-审核通过 3-审核驳回")
    private Integer auditStatus;

    @Schema(description = "提交版本（用户第N次提交认证）", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("提交版本（用户第N次提交认证）")
    private Integer submitVersion;

    @Schema(description = "审核备注（驳回原因）", example = "你猜")
    @ExcelProperty("审核备注（驳回原因）")
    private String auditRemark;

    @Schema(description = "是否为最新提交记录（1-是 0-否）", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("是否为最新提交记录（1-是 0-否）")
    private Boolean isLatest;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

      /**
     * 银行卡开户名（需与实名一致）
     */
    private String bankAccountName;
    /**
     * 银行卡号
     */
    private String bankAccount;
    /**
     * 开户行
     */
    private String bankName;

}