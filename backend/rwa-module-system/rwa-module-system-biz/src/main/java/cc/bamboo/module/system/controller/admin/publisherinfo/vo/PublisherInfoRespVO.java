package cc.bamboo.module.system.controller.admin.publisherinfo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import com.alibaba.excel.annotation.*;

@Schema(description = "管理后台 - 发行商 Response VO")
@Data
@ExcelIgnoreUnannotated
public class PublisherInfoRespVO {

    @Schema(description = "发行商ID （sys_user.id，发行商对应的用户账号）", requiredMode = Schema.RequiredMode.REQUIRED, example = "1805")
    @ExcelProperty("发行商ID （sys_user.id，发行商对应的用户账号）")
    private Long id;

    @Schema(description = "用户手机号（冗余sys_user.phonenumber）", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("用户手机号（冗余sys_user.phonenumber）")
    private String userPhone;

    @Schema(description = "注册时间（冗余sys_user.create_time）", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("注册时间（冗余sys_user.create_time）")
    private LocalDateTime registerTime;

    @Schema(description = "身份认证状态：0-未认证 1-认证中 2-已认证 3-认证失败", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty("身份认证状态：0-未认证 1-认证中 2-已认证 3-认证失败")
    private Integer identityAuthStatus;

    @Schema(description = "认证身份")
    @ExcelProperty("认证身份")
    private String authIdentity;

    @Schema(description = "公司名称（企业全称，与营业执照一致）", requiredMode = Schema.RequiredMode.REQUIRED, example = "赵六")
    @ExcelProperty("公司名称（企业全称，与营业执照一致）")
    private String companyName;

    @Schema(description = "公司统一社会信用代码（唯一，18位）", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("公司统一社会信用代码（唯一，18位）")
    private String companyCreditCode;

    @Schema(description = "营业执照URL（图片/文件）", example = "https://www.iocoder.cn")
    @ExcelProperty("营业执照URL（图片/文件）")
    private String businessLicenseUrl;

    @Schema(description = "资质文件URL（多个用,分隔）")
    @ExcelProperty("资质文件URL（多个用,分隔）")
    private String qualificationFileUrls;

    @Schema(description = "授权文件URL（多个用,分隔）")
    @ExcelProperty("授权文件URL（多个用,分隔）")
    private String authorizationFileUrls;

    @Schema(description = "身份证姓名", example = "李四")
    @ExcelProperty("身份证姓名")
    private String idCardName;

    @Schema(description = "身份证号")
    @ExcelProperty("身份证号")
    private String idCardNo;

    @Schema(description = "身份证有效期")
    @ExcelProperty("身份证有效期")
    private LocalDate idCardExpireTime;

    @Schema(description = "身份证正面URL", example = "https://www.iocoder.cn")
    @ExcelProperty("身份证正面URL")
    private String idCardFrontUrl;

    @Schema(description = "身份证背面URL", example = "https://www.iocoder.cn")
    @ExcelProperty("身份证背面URL")
    private String idCardBackUrl;

    @Schema(description = "邮箱")
    @ExcelProperty("邮箱")
    private String email;

    @Schema(description = "开户名（与公司名称/法人姓名一致）", example = "张三")
    @ExcelProperty("开户名（与公司名称/法人姓名一致）")
    private String bankAccountName;

    @Schema(description = "银行账户（卡号）", example = "19008")
    @ExcelProperty("银行账户（卡号）")
    private String bankAccount;

    @Schema(description = "开户行", example = "赵六")
    @ExcelProperty("开户行")
    private String bankName;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    private Integer companyType;

}
