import {
	AssetStatus,
	AssetType,
	AuditStatus,
	BillType,
	ChainStatus,
	ConfigKey,
	Currency,
	DictKey,
	type ExtendCurreny,
	type CryptoCurrency,
	LoginType,
	MessageType,
	OrderStatus,
	PayType,
	type ProjectStatus,
	ProjectType,
	ProtocolKey,
	TwoFactorAuthStatus
} from "@/types/enums";
import {type DictValue, ReverseBooleanNumber} from "@/types/common";

export interface UserEntity {
	id: number
	mobile: string; // 手机号
	email: string;
	realName: string; // 用户姓名
	nickName: string;
	avatar: string;
	auditStatus: AuditStatus; // 审核状态
	defaultCurrency: Currency; // 币种
	twoFactorAuthStatus: TwoFactorAuthStatus; // 2FA验证状态
	chainCount: number; // 链数量
	orderCount: number; // 有效订单数量
}

export interface MessageEntity{
	id: number; // 消息ID
	noticeType: MessageType; // 消息类型
	templateCode: string; // 模板编码
	templateTitle?: string; // 模板标题
	templateNickname?: string; // 模板发送人名称
	templateContent: string; // 模板内容
	templateType?: number; // 模板类型
	orderId?: number; // 订单ID
	noticeUrl?: string; // 消息地址
	readStatus: boolean; // 是否已读
	readTime?: string; // 阅读时间
	createTime: string; // 创建时间
}

export interface AssetEntity {
	id: number; // 订单ID
	userId: number; // 用户ID
	projectId: number; // 项目ID
	principalAmount: number; // 本金金额(元)
	holdAmount: number; // 持有金额
	buyQuantity: number; // 购买份额
	holdQuantity: number; // 当前持有份额(份)
	totalIncome: number; // 累计总收益(元)
	withdrawnDividend: number; // 已提取分红(元)
	freezeDividend: string; // 冻结的分红(元)
	unwithdrawnDividend: number; // 未提取分红(元)
	totalRedemptionAmount: number; // 累计赎回本金(元)
	projectName: string; // 项目名称
	status: AssetStatus; // 资产状态
	investmentCurrency: Currency;
	earningCurrency: ExtendCurreny,
}

export interface BankCardInfo {
	id: number; // 银行卡ID
	bankAccount: string; // 银行卡号(脱敏)
	bankName: string; // 开户银行
	bankAccountName: string; // 开户名
	auditStatus: AuditStatus,
	auditRemark?: string,
}

export interface UserAuditDetail {
	id: number; // 审核记录ID
	realName: string; // 用户姓名
	idCard: string; // 证件号(脱敏)
	idCardExpire: string; // 证件号有效期
	idCardFrontUrl?: string; // 证件号人像面图片URL
	idCardBackUrl?: string; // 证件号国徽面图片URL
	investmentQualificationUrls?: string[]; // 投资资质图片URL列表
	bankFlowUrls?: string[]; // 银行流水图片URL列表
	residenceProofUrls?: string[]; // 住址证明图片URL列表
	bankCard?: BankCardInfo; // 银行卡信息
	contactPhone: string; // 联系电话
	auditStatus: AuditStatus; // 审核状态
	auditStatusDesc: string; // 审核状态描述
	submitVersion: number; // 提交版本
	auditRemark?: string; // 审核备注(驳回原因)
	createTime: number; // 提交时间
}

export type ExchangeRate = {
	[key in Lowercase<CryptoCurrency>]: number;
} & {
	usd: number; // USDT对美元汇率
	cny: number; // USDT对人民币汇率
	hkd: number; // USDT对港币汇率
};

export interface ProjectEntity {
	projectId: number; // 项目ID
	projectName: string; // 项目名称
	projectType: ProjectType; // 项目类型
	assetType: AssetType; // 资产类型
	publisherUserId: number; // 发行商用户ID
	publisherCompanyName: string; // 发行商公司名称
	createTime: number; // 创建时间
	issueQuantity: number; // 发行数量(份)
	issueUnitPrice: number; // 发行单价(元)
	remainingQuantity: number; // 剩余数量(份)
	expectedAnnualReturn: number; // 预期年化收益
	minimumPurchase: number; // 起购量(份)
	issueChainId: number; // 发行链ID
	lockStartTime: Date; // 锁定期-开始时间
	lockEndTime: Date; // 锁定期-结束时间
	firstDividendDate: Date,
	projectStatus: ProjectStatus; // 项目状态
	projectIntro: string; // 项目介绍（图文）
	subscriptionContractIds: string; // 认购合同ID（多个用逗号分隔）
	dividendContractIds: string; // 分红合同ID（多个用逗号分隔）
	maturityRedemptionContractIds: string; // 到期赎回合同ID（多个用逗号分隔）
	earlyRedemptionContractIds: string; // 提前赎回合同ID（多个用逗号分隔）
	earlyRedemptionFeeJson: string; // 提前赎回手续费配置（JSON格式）
	earlyRedemptionFee: number,
	projectImageUrls: string; // 项目图片URL（多个用逗号分隔）
	projectVideoUrl: string; // 项目视频URL
	redemptionRules: string;
	duration: number; // 基金时长（月），以30天为一月计算
	earningCurrency: ExtendCurreny; // 分红币种
	investmentCurrency: Currency; // 投资币种
	cover: string,
	purchaseInstructions: string,
	dividendInstructions: string,
}

export type SimpleProjectEntity = Pick<ProjectEntity,
	'projectId' | 'projectName' | 'projectType' | 'assetType' | 'publisherCompanyName' |
	'issueQuantity' | 'issueUnitPrice' | 'remainingQuantity' | 'expectedAnnualReturn' |
	'minimumPurchase' | 'projectStatus' | 'projectImageUrls' | 'duration' | 'earningCurrency' | 'investmentCurrency'
>;

export interface ProtocolEntity {
	id: number; // 协议ID
	agreementTitle: string; // 协议名称
	agreementContent: string; // 协议内容
	agreementKey: ProtocolKey,
}

export type SimpleProtocolEntity = Pick<ProtocolEntity, 'id' | 'agreementTitle' | 'agreementKey'>


export interface ProjectNoticeEntity {
	id: number; // 通告ID
	noticeNo: string; // 通告编号（唯一，格式：NOTICE+日期+序号，如NOTICE202512200001）
	noticeTitle: string; // 通告标题
	noticeContent: string; // 通告内容
	attachUrls?: string; // 附件URL（多个用逗号分隔，支持PDF/Word/图片等，如"url1,url2"）
	publishTime: number;
}

export type ProjectOrderEntity = Pick<OrderEntity,
	'id' | 'orderNo' | 'applyDate' | 'orderStatus' | 'projectId' | 'projectName' |
	'subscribeQuantity' | 'price' | 'totalAmount' | 'totalIncome' | 'chainAddress' | 'expireTime' |
	'investmentCurrency' | 'createTime'
>

export type OrderListEntity = Pick<OrderEntity,
	'id' | 'orderNo' | 'applyDate' | 'orderStatus' | 'projectStatus' | 'projectId' | 'projectName' |
	'subscribeQuantity' | 'price' | 'totalAmount' | 'totalIncome' | 'expireTime' | 'auditRemark' |
	'chainAddress' | 'contractNo' | 'payVoucherUrl' | 'payType' | 'confirmPurchaseTime' |
	'bankAccountName'  | 'bankAccount' | 'bankName' | 'investmentCurrency' |'createTime'
>

export interface OrderEntity {
	id: number; // 订单ID
	orderNo: string; // 订单号
	applyDate: number; // 申请日期
	userId: number; // 用户ID
	orderStatus: OrderStatus; // 订单状态
	status: AssetStatus,
	projectStatus: ProjectStatus;
	projectId: number; // 项目ID
	projectName: string; // 项目名称
	subscribeQuantity: number; // 申购份额(份)
	price: number; // 单价
	totalAmount: number; // 金额
	totalIncome: number; // 收益
	confirmPurchaseTime?: string; // 确认时间
	payType?: PayType; // 支付方式
	chainAddress: string; // 链地址
	contractNo?: string; // 合同号
	payVoucherUrl?: string; // 支付凭证URL
	auditTime?: string; // 审核时间
	auditUserId?: number; // 审核人ID
	auditUserName?: string; // 审核人名称
	auditRemark?: string; // 审核备注
	cancelTime?: string; // 取消时间
	cancelReason?: string; // 取消原因
	expireTime: number; // 订单过期时间
	bankAccountName:string,
	bankAccount: string,
	bankName: string,
	withdrawableBalance: number,
	investmentCurrency: Currency,
	earningCurrency: ExtendCurreny,
	holdQuantity: number,
	holdAmount: number,
	createTime: number,
	auditFiles: string,
}

export interface ChainAddressEntity {
	id: number,
	chainAddress: string; // 链地址
	chainStatus: ChainStatus; // 链状态
}

export interface ChainEntity {
	id: number; // 主键ID
	name: string; // 链名称
	chainId: number; // 链ID
	rpcUrl: string; // RPC URL
	browserUrl: string; // 浏览器 URL
	status: number; // 状态：0-开启，1-关闭
	sort: number; // 排序
}

export interface DictItemEntity {
	label: string; // 字典标签
	value: string; // 字典值
	sort: number; // 排序
}

export interface DictEntity {
	name: string; // 字典类型名称
	type:DictKey; // 字典类型
	dataList: DictItemEntity[]; // 字典数据列表
}

export interface LoginHistoryEntity {
	id: number; // 日志ID
	loginTime: string; // 登录时间
	loginIp: string; // 登录IP
	loginCity: string; // 登录地点
	deviceInfo: string; // 设备信息
	loginStatus: ReverseBooleanNumber; // 登录状态
	loginType: LoginType,
}

export interface DailyProfitEntity {
	date: string; // 日期
	dailyTotalIncome: CurrencyInfo; // 日总收益
}

export interface OrderProfitEntity {
	projectId: number; // 项目ID
	projectName: string; // 项目名称
	orderId: number; // 订单ID
	incomeDate: string; // 收益日期
	orderDailyIncome: number; // 订单日收益
	incomeRate: number;
	investmentCurrency: Currency,
	earningCurrency: ExtendCurreny,
	holdAmount: number,
}

export interface BillEntity {
	id: number; // 账单ID
	billNo: string; // 流水号（唯一，如：BILL202512160001）
	billType: BillType; // 账单类型
	orderId: number; // 订单ID
	orderNo: string; // 订单号
	applyTime: string; // 申请时间
	projectId: number; // 项目ID
	projectName: string; // 所属项目名称（冗余）
	payee: string; // 收款方（用户/企业名称）
	bankAccount: string; // 收款账户（银行卡号/链地址）
	bankName: string; // 开户行（如"中国工商银行XX支行"）
	billAmount: number; // 账单金额（元）
	billCoin: string;
	actualAmount: number; // 实际到账金额（元）
	actualCoin: string;
	auditStatus: AuditStatus; // 审核状态：1-待审核 2-审核通过 3-审核不通过
	auditTime: string; // 审核时间
	auditRemark: string; // 审核备注（审核不通过原因）
	quantity: number; // 份额
}

export interface CommonConfigEntity {
	name: string; // 参数名称
	configKey: ConfigKey; // 参数键名
	value: string; // 参数键值
}

export interface CurrencyInfo {
	coinCode: ExtendCurreny,
	total: number
}