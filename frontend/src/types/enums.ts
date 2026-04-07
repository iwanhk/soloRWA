import type {DictValue} from "@/types/common.ts";

export const enum MessageType {
	SYSTEM = 1,
	PERSONAL = 2,
}

export const enum ProjectStatus {
	NOT_STARTED = 1, // 未开售
	ON_SALE = 2, // 出售中
	SOLD_OUT = 3, // 已售罄
	PROFITABLE = 4, // 盈利中
}

export const enum ProjectState {
	SOLD = 0, //已售罄
	SUBSCRIPTION = 1, // 认购期
	LOCK_PERIOD = 2, // 锁定期
	DIVIDEND = 3, // 分红期
	REDEEM = 4, // 赎回期
}

export const enum OrderStatus {
	PENDING_PAYMENT = 0, // 待支付
	AUDITING = 1, // 审核中
	AUDIT_PASSED = 2, // 审核通过
	AUDIT_REJECTED = 3, // 审核未通过
	CANCELLED = 4, // 已取消
	REDEEMED = 5, // 已取消
}

export const enum AuditStatus {
	NOT_SUBMITTED = 0, // 未提交
	PENDING_AUDIT = 1, // 待审核
	AUDIT_PASSED = 2, // 通过
	AUDIT_REJECTED = 3, // 不通过
	PAYED = 4,
}

export const enum TwoFactorAuthStatus {
	DISABLED = 0, // 未开启
	ENABLED = 1, // 已开启
	PENDING_VERIFICATION = 2, // 待验证
	UNBINDING = 3, // 解绑中
}

export const enum ChainStatus {
	BINDING = 0, // 绑定中
	SUCCESS = 1, // 正常
	UNBINDING = 2, // 解绑中
	UNBIND = 3, // 已解绑
}

export const enum DictKey {
	USER_STATUS = 'user_status', // 用户状态
	AUDIT_STATUS = 'audit_status', // 审核状态
	LOGIN_STATUS = 'login_status', // 登录状态
	NOTICE_TYPE = 'biz_notice_type', // 消息类型
	PROJECT_TYPE = 'biz_project_type', // 项目类型
	ASSET_TYPE = 'biz_asset_type', // 资产类型
	PROJECT_STATUS = 'biz_project_status', // 项目状态
	BILL_TYPE = 'biz_bill_type', // 账单类型
	PROJECT_NOTICE_TYPE = 'biz_project_notice_type', // 项目通告类型
	PROJECT_NOTICE_STATUS = 'biz_project_notice_status', // 项目通告状态
	ORDER_STATUS = 'biz_order_status', // 订单状态
	BALANCE_TYPE = 'biz_balance_type', // 资金类型
	COIN_TYPE = 'biz_coin_type', // 币种
	AGREEMENT_TYPE = 'agreement_type', // 协议类型
	PAY_TYPE = 'biz_pay_type', // 支付方式
	INCOME_STATUS = 'biz_income_status', // 收入状态
}

export const enum Language {
	ZH_CN = 'zh-Hans',
	ZH_HK = 'zh-Hant',
	EN_US = 'en',
}

export const enum Currency {
	USD = 'USD',
	CNY = 'CNY',
	HKD = 'HKD',
	USDT = 'USDT'
}
export type CryptoCurrency = 'ETH' | 'BTC';

export type ExtendCurreny = Currency | CryptoCurrency;

export const enum LoginType{
	PASSWORD = 102,
	SMS = 104,
}

export const enum ProtocolKey{
	REGISTER = 'register',//		注册协议
	SERVICE = 'user_service',//		用户服务协议
	PRIVACY = 'privacy_policy',//		隐私保护协议
	BUY = 'buy',//		认购协议
}

export const enum ConfigKey{
	PAYMENT_NOTICE = 'pay_remark',
	PURCHASE_NOTICE = 'purchase_instructions',
}

export const enum AssetStatus {
	CONFIRMING = 1, // 确认中
	APPROVED = 2, // 审核通过
	REJECTED = 3, // 待收益
	PROFITTING = 4, // 收益中
	REDEEMABLE = 5, // 可赎回
	ENDED = 6, // 已结束
}

export const enum ProjectType {
	OPEN = 1, // 开放型
	CLOSED = 2, // 封闭型
}

export const enum AssetType {
	DIGITAL = 1, // 数字资产
	PHYSICAL = 2, // 实体资产
}

export const enum BillType {
	DIVIDEND = 1, // 分红
	MATURITY_REDEMPTION = 2, // 到期赎回
	EARLY_REDEMPTION = 3, // 提前赎回
}

export const enum PayType {
	BANK = 'bank', // 银行转账
}

export type MobileEmail = "Mobile" | 'Email'; 
