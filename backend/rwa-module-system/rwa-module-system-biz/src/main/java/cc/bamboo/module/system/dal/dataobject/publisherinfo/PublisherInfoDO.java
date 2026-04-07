package cc.bamboo.module.system.dal.dataobject.publisherinfo;

import cc.bamboo.framework.tenant.core.db.TenantBaseDO;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cc.bamboo.framework.mybatis.core.dataobject.BaseDO;

/**
 * 发行商 DO
 *
 * @author Swolf
 */
@TableName("biz_publisher_info")
@KeySequence("biz_publisher_info_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PublisherInfoDO extends TenantBaseDO {

    /**
     * 发行商ID （sys_user.id，发行商对应的用户账号）
     */
    @TableId
    private Long id;
    /**
     * 用户手机号（冗余sys_user.phonenumber）
     */
    private String userPhone;
    /**
     * 注册时间（冗余sys_user.create_time）
     */
    private LocalDateTime registerTime;
    /**
     * 身份认证状态：0-未认证 1-认证中 2-已认证 3-认证失败
     */
    private Integer identityAuthStatus;
    /**
     * 认证身份（如“企业法人”“经办人”）
     */
    private String authIdentity;
    /**
     * 公司名称（企业全称，与营业执照一致）
     */
    private String companyName;
    /**
     * 公司统一社会信用代码（唯一，18位）
     */
    private String companyCreditCode;
    /**
     * 营业执照URL（图片/文件）
     */
    private String businessLicenseUrl;
    /**
     * 资质文件URL（多个用,分隔）
     */
    private String qualificationFileUrls;
    /**
     * 授权文件URL（多个用,分隔）
     */
    private String authorizationFileUrls;
    /**
     * 身份证姓名
     */
    private String idCardName;
    /**
     * 身份证号
     */
    private String idCardNo;
    /**
     * 身份证有效期
     */
    private LocalDate idCardExpireTime;
    /**
     * 身份证正面URL
     */
    private String idCardFrontUrl;
    /**
     * 身份证背面URL
     */
    private String idCardBackUrl;
    /**
     * 邮箱
     */
    private String email;
    /**
     * 开户名（与公司名称/法人姓名一致）
     */
    private String bankAccountName;
    /**
     * 银行账户（卡号）
     */
    private String bankAccount;
    /**
     * 开户行
     */
    private String bankName;

    private Integer companyType;

}