package cc.bamboo.module.user.enums.notice;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Hus
 * @version 1.0
 * @date 2026/1/16 11:21
 * @description
 */
@Getter
public enum NoticeTemplateEnum {
    // 12 申请分红-通过
    DIVIDENDS_APPROVED("申请分红-通过", "dividends_approved", 2),
    // 13 新用户注册通知
    USER_REGISTER("新用户注册通知", "user_register", 2),
    // 14 身份认证结果通知-通过
    AUTH_APPROVED("身份认证结果通知-通过", "auth_approved", 2),
    // 15 身份认证结果通知-未通过
    AUTH_REJECTED("身份认证结果通知-未通过", "auth_rejected", 2),
    // 16 项目到达分红期通知
    PROJECT_DIVIDEND_ARRIVE("项目到达分红期通知", "project_dividend_arrive", 2),
    // 17 申请分红-未通过
    DIVIDENDS_REJECTED("申请分红-未通过", "dividends_rejected", 2),
    // 18 项目到期可赎回通知
    PROJECT_REDEMPTION_ARRIVE("项目到期可赎回通知", "project_redemption_arrive", 2),
    // 19 项目到期赎回申请-通过
    REDEMPTION_DUE_APPROVED("项目到期赎回申请-通过", "redemption_due_approved", 2),
    // 20 项目到期赎回申请-未通过
    REDEMPTION_DUE_REJECTED("项目到期赎回申请-未通过", "redemption_due_rejected", 2),
    // 21 提前赎回申请-通过
    REDEMPTION_ADVANCE_APPROVED("提前赎回申请-通过", "redemption_advance_approved", 2),
    // 22 提前赎回申请-未通过
    REDEMPTION_ADVANCE_REJECTED("提前赎回申请-未通过", "redemption_advance_rejected", 2),
    // 银行卡审核不通过
    // 23 银行卡审核不通过
    BANK_REJECTED("银行卡审核不通过", "bank_rejected", 2),
    // 24 银行卡审核通过
    BANK_APPROVED("银行卡审核通过", "bank_approved", 2),
    // 25 f2a解绑审核不通过
    F2A_UNBIND_REJECTED("f2a解绑审核不通过", "f2a_rejected", 2),
    // 26 f2a解绑审核通过
    F2A_UNBIND_APPROVED("f2a解绑审核通过", "f2a_approved", 2),
    ;
    /**
     * 模板名称 对应表字段 name
     */
    private final String name;

    /**
     * 模板编码 对应表字段 code
     */
    private final String code;

    /**
     * 模板类型 对应表字段 type (固定值 4)
     */
    private final Integer type;

    // 枚举构造方法
    NoticeTemplateEnum(String name, String code, Integer type) {
        this.name = name;
        this.code = code;
        this.type = type;
    }

    // 常用工具方法：根据code获取枚举（业务开发高频使用）
    public static NoticeTemplateEnum getByCode(String code) {
        for (NoticeTemplateEnum templateEnum : values()) {
            if (templateEnum.getCode().equals(code)) {
                return templateEnum;
            }
        }
        return null;
    }
}
