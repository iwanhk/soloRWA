package cc.bamboo.module.user.controller.app.useraudit.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "用户 APP - 用户认证详情 Response VO")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppUserAuditDetailRespVO {

    @Schema(description = "审核记录ID", example = "1")
    private Long id;

    @Schema(description = "用户姓名", example = "张三")
    private String realName;

    @Schema(description = "证件号(脱敏)", example = "110101********1234")
    private String idCard;

    @Schema(description = "证件号有效期", example = "2030-12-31")
    private String idCardExpire;

    @Schema(description = "证件号人像面图片URL", example = "https://example.com/front.jpg")
    private String idCardFrontUrl;

    @Schema(description = "证件号国徽面图片URL", example = "https://example.com/back.jpg")
    private String idCardBackUrl;

    @Schema(description = "投资资质图片URL列表", example = "[\"https://example.com/1.jpg\"]")
    private List<String> investmentQualificationUrls;

    @Schema(description = "银行流水图片URL列表", example = "[\"https://example.com/1.jpg\"]")
    private List<String> bankFlowUrls;

    @Schema(description = "住址证明图片URL列表", example = "[\"https://example.com/1.jpg\"]")
    private List<String> residenceProofUrls;

    @Schema(description = "银行卡信息")
    private AppBankCardInfoRespVO bankCard;

    @Schema(description = "邮箱", example = "user@example.com")
    private String email;

    @Schema(description = "联系电话", example = "13800138000")
    private String contactPhone;

    @Schema(description = "审核状态：0-待提交 1-待审核 2-审核通过 3-审核驳回", example = "1")
    private Integer auditStatus;

    @Schema(description = "审核状态描述", example = "待审核")
    private String auditStatusDesc;

    @Schema(description = "提交版本", example = "1")
    private Integer submitVersion;

    @Schema(description = "审核备注(驳回原因)", example = "证件照片不清晰")
    private String auditRemark;

    @Schema(description = "提交时间")
    private LocalDateTime createTime;



}
