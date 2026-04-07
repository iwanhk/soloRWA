package cc.bamboo.module.project.dal.dataobject.projectinfo;

import cc.bamboo.framework.tenant.core.db.TenantBaseDO;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.*;
import cc.bamboo.framework.mybatis.core.dataobject.BaseDO;

/**
 * 项目核心表（基础+状态） DO
 *
 * @author Swolf
 */
@TableName("biz_project_info")
@KeySequence("biz_project_info_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectInfoDO extends TenantBaseDO {

    /**
     * 项目ID
     */
    @TableId
    private Long projectId;
    /**
     * 项目名称
     */
    private String projectName;
    /**
     * 项目类
     *
     * 枚举 {@link TODO biz_project_type 对应的类}
     */
    private Integer projectType;
    /**
     * 资产类型
     *
     * 枚举 {@link TODO biz_asset_type 对应的类}
     */
    private Integer assetType;
    /**
     * 发行商
     */
    private Long publisherUserId;
    /**
     * 发行商
     */
    private String publisherCompanyName;
    /**
     * 发行数量(份)
     */
    private Integer issueQuantity;
    /**
     * 发行单价(元)
     */
    private BigDecimal issueUnitPrice;
    /**
     * 剩余数量(份)【高频更新】
     */
    private Integer remainingQuantity;

    private Integer salesQuantity;
    /**
     * 预期年化收益
     */
    private BigDecimal expectedAnnualReturn;
    /**
     * 起购量
     */
    private Integer minimumPurchase;
    /**
     * 发行链ID（关联chain_manage）
     */
    private Long issueChainId;

    /**
     * 锁定期-开始
     */
    private LocalDate lockStartTime;
    /**
     * 锁定期-结束
     */
    private LocalDate lockEndTime;

    /**
     * 运行状态：0-未运行 1-待运行审核 2-运行中 3-已结束
     * 
     * @see cc.bamboo.module.project.enums.ProjectStatusEnum
     */
    private Integer projectStatus;
    /**
     * 项目介绍（图文）
     */
    private String projectIntro;
    /**
     * 项目资料URL
     */
    private String projectFileUrls;

    private String redemptionRules;
    /**
     * 提前赎回手续费配置
     */
    private String earlyRedemptionFeeJson;
    /**
     * 项目图片URL
     */
    private String projectImageUrls;
    /**
     * 项目视频URL
     */
    private String projectVideoUrl;

    /**
     * 视频封面URL
     */
    private String cover;

    /**
     * 审核人ID（关联用户表）
     */
    private Long auditUserId;

    /**
     * 审核人名称
     */
    private String auditUserName;

    /**
     * 链合约状态
     */
    private Integer chainStatus;
    /**
     * tokensid
     */
    private Long chainTokensId;

    private String chainTokenAddress;
    /**
     * 审核备注（如审核未通过原因）
     */
    private String auditRemark;

    /**
     * 出售状态：0下架 1上架
     */
    private Integer sellStatus;

    /**
     * 审核状态：0-草稿 1-待上线审核 2-上线通过(募资中) 3-上线拒绝
     * 
     * @see cc.bamboo.module.project.enums.AuditStatusEnum
     */
    private Integer auditStatus;

    /**
     * 基金时长（月），以30天为一月计算
     */
    private Integer duration;

    private String earningCurrency;

    private String investmentCurrency;

    private String purchaseInstructions;

    private String dividendInstructions;

    private String language;

    private String projectJson;

    private Integer version;

    /**
     * 最新编辑的审核状态：0-待提交 1-待审核 2-审核通过 3-审核不通过
     */
    private Integer editStatus;

    /**
     * 最新快照版本号
     */
    private Integer editVersion;

    /**
     * 项目配置类型 0 挖矿 1 基金
     */
    private Integer projectConfigType;
}