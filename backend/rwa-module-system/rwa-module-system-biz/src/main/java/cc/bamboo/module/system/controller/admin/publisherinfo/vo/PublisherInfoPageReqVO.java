package cc.bamboo.module.system.controller.admin.publisherinfo.vo;

import lombok.*;

import java.time.LocalDate;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cc.bamboo.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cc.bamboo.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 发行商分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class PublisherInfoPageReqVO extends PageParam {

    @Schema(description = "用户手机号（冗余sys_user.phonenumber）")
    private String userPhone;

    @Schema(description = "注册时间（冗余sys_user.create_time）")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] registerTime;

    @Schema(description = "身份认证状态：0-未认证 1-认证中 2-已认证 3-认证失败", example = "1")
    private Integer identityAuthStatus;

    @Schema(description = "认证身份（如“企业法人”“经办人”）")
    private String authIdentity;

    @Schema(description = "公司名称（企业全称，与营业执照一致）", example = "赵六")
    private String companyName;

    @Schema(description = "公司统一社会信用代码（唯一，18位）")
    private String companyCreditCode;

    @Schema(description = "营业执照URL（图片/文件）", example = "https://www.iocoder.cn")
    private String businessLicenseUrl;

    @Schema(description = "资质文件URL（多个用,分隔）")
    private String qualificationFileUrls;

    @Schema(description = "授权文件URL（多个用,分隔）")
    private String authorizationFileUrls;

    @Schema(description = "身份证姓名", example = "李四")
    private String idCardName;

    @Schema(description = "身份证号")
    private String idCardNo;

    @Schema(description = "身份证有效期")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDate[] idCardExpireTime;

    @Schema(description = "身份证正面URL", example = "https://www.iocoder.cn")
    private String idCardFrontUrl;

    @Schema(description = "身份证背面URL", example = "https://www.iocoder.cn")
    private String idCardBackUrl;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "开户名（与公司名称/法人姓名一致）", example = "张三")
    private String bankAccountName;

    @Schema(description = "银行账户（卡号）", example = "19008")
    private String bankAccount;

    @Schema(description = "开户行", example = "赵六")
    private String bankName;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}