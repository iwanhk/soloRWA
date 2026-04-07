import type {LabelOptions} from "@/types/common";
import {
	AssetStatus,
	AssetType,
	AuditStatus,
	BillType,
	ChainStatus,
	Currency,
	Language,
	LoginType,
	OrderStatus,
	PayType,
	ProjectState,
	ProjectStatus,
	ProjectType,
	ProtocolKey,
	TwoFactorAuthStatus
} from "@/types/enums";
import i18n from "@/i18n";

export const currencyOptions = computed<LabelOptions<Currency, { unit: string }>>(() => [
	{ value: Currency.USD, label: i18n.global.t('options.currency.usd'), unit:'US$' },
	{ value: Currency.CNY, label: i18n.global.t('options.currency.cny'), unit: 'RMB' },
	{ value: Currency.HKD, label: i18n.global.t('options.currency.hkd'), unit:'HK$' },
	{ value: Currency.USDT, label: i18n.global.t('options.currency.usdt'), unit:'U' }
]);

export const languageOptions = computed<LabelOptions<Language>>(() => [
	{ value: Language.ZH_CN, label: '中文 (简体)' },
	{ value: Language.ZH_HK, label: '中文 (繁体)' },
	{ value: Language.EN_US, label: 'English' }
]);

export const fontScaleOptions = computed<LabelOptions<number>>(() => [
	{ value: 1, label: i18n.global.t('options.fontSize.standard') },
	{ value: 1.25, label: i18n.global.t('options.fontSize.large') },
	{ value: 1.5, label: i18n.global.t('options.fontSize.extraLarge') }
]);

export const chainStatusOptions = computed<LabelOptions<ChainStatus>>(() => [
	{ value: ChainStatus.BINDING, label: i18n.global.t('options.chainStatus.binding') },
	{ value: ChainStatus.SUCCESS, label: i18n.global.t('options.chainStatus.success') },
	{ value: ChainStatus.UNBINDING, label: i18n.global.t('options.chainStatus.unbinding') },
	{ value: ChainStatus.UNBIND, label: i18n.global.t('options.chainStatus.unbind') },
]);

export const orderStatusOptions = computed<LabelOptions<OrderStatus | -1>>(() => [
	{ value: -1 as any, label: i18n.global.t('options.orderStatus.all') },
	{ value: OrderStatus.AUDIT_PASSED, label: i18n.global.t('options.orderStatus.auditPassed') },
	{ value: OrderStatus.PENDING_PAYMENT, label: i18n.global.t('options.orderStatus.pendingPayment') },
	{ value: OrderStatus.AUDITING, label: i18n.global.t('options.orderStatus.auditing') },
	{ value: OrderStatus.AUDIT_REJECTED, label: i18n.global.t('options.orderStatus.auditRejected') },
	{ value: OrderStatus.CANCELLED, label: i18n.global.t('options.orderStatus.cancelled') },
	{ value: OrderStatus.REDEEMED, label: i18n.global.t('options.orderStatus.redeemed') },
]);

export const projectStateOptions = computed<LabelOptions<ProjectState>>(() => [
	{ value: ProjectState.SOLD, label: i18n.global.t('options.projectState.sold') },
	{ value: ProjectState.SUBSCRIPTION, label: i18n.global.t('options.projectState.subscription') },
	{ value: ProjectState.LOCK_PERIOD, label: i18n.global.t('options.projectState.lockPeriod') },
	{ value: ProjectState.DIVIDEND, label: i18n.global.t('options.projectState.dividend') },
	{ value: ProjectState.REDEEM, label: i18n.global.t('options.projectState.redeem') },
]);

export const projectStatusOptions = computed<LabelOptions<ProjectStatus>>(() => [
	{ value: ProjectStatus.NOT_STARTED, label: i18n.global.t('options.projectStatus.notStarted') },
	{ value: ProjectStatus.ON_SALE, label: i18n.global.t('options.projectStatus.onSale') },
	{ value: ProjectStatus.SOLD_OUT, label: i18n.global.t('options.projectStatus.soldOut') },
	{ value: ProjectStatus.PROFITABLE, label: i18n.global.t('options.projectStatus.profitable') },
]);

export const twoFactorAuthStatusOptions = computed<LabelOptions<TwoFactorAuthStatus>>(() => [
	{ value: TwoFactorAuthStatus.DISABLED, label: i18n.global.t('options.twoFactorAuthStatus.disabled') },
	{ value: TwoFactorAuthStatus.ENABLED, label: i18n.global.t('options.twoFactorAuthStatus.enabled') },
	{ value: TwoFactorAuthStatus.PENDING_VERIFICATION, label: i18n.global.t('options.twoFactorAuthStatus.pendingVerification') },
	{ value: TwoFactorAuthStatus.UNBINDING, label: i18n.global.t('options.twoFactorAuthStatus.unbinding') },
]);

export const auditStatusOptions = computed<LabelOptions<AuditStatus | -1>>(() => [
	{ label: i18n.global.t('options.auditStatus.all'), value: -1 },
	{ label: i18n.global.t('options.auditStatus.pendingAudit'), value: AuditStatus.PENDING_AUDIT },
	{ label: i18n.global.t('options.auditStatus.auditPassed'), value: AuditStatus.AUDIT_PASSED },
	{ label: i18n.global.t('options.auditStatus.auditRejected'), value: AuditStatus.AUDIT_REJECTED },
	{ label: i18n.global.t('options.auditStatus.payed'), value: AuditStatus.PAYED},
]);

export const loginTypeOptions = computed<LabelOptions<LoginType>>(() => [
	{ value: LoginType.PASSWORD, label: i18n.global.t('options.loginType.password') },
	{ value: LoginType.SMS, label: i18n.global.t('options.loginType.sms') },
]);

export const agreementKeyOptions = computed<LabelOptions<ProtocolKey>>(() => [
	{ value: ProtocolKey.REGISTER, label: i18n.global.t('options.agreementKey.register') },
	{ value: ProtocolKey.SERVICE, label: i18n.global.t('options.agreementKey.service') },
	{ value: ProtocolKey.PRIVACY, label: i18n.global.t('options.agreementKey.privacy') },
	{ value: ProtocolKey.BUY, label: i18n.global.t('options.agreementKey.buy') },
]);

export const assetStatusOptions = computed<LabelOptions<AssetStatus>>(() => [
	{ value: AssetStatus.CONFIRMING, label: i18n.global.t('options.assetStatus.confirming') },
	{ value: AssetStatus.APPROVED, label: i18n.global.t('options.assetStatus.approved') },
	{ value: AssetStatus.REJECTED, label: i18n.global.t('options.assetStatus.rejected') },
	{ value: AssetStatus.PROFITTING, label: i18n.global.t('options.assetStatus.profitting') },
	{ value: AssetStatus.REDEEMABLE, label: i18n.global.t('options.assetStatus.redeemable') },
	{ value: AssetStatus.ENDED, label: i18n.global.t('options.assetStatus.ended') },
]);

export const projectTypeOptions = computed<LabelOptions<ProjectType>>(() => [
	{ value: ProjectType.OPEN, label: i18n.global.t('options.projectType.open') },
	{ value: ProjectType.CLOSED, label: i18n.global.t('options.projectType.closed') },
]);

export const assetTypeOptions = computed<LabelOptions<AssetType>>(() => [
	{ value: AssetType.DIGITAL, label: i18n.global.t('options.assetType.digital') },
	{ value: AssetType.PHYSICAL, label: i18n.global.t('options.assetType.physical') },
]);

export const billTypeOptions = computed<LabelOptions<BillType>>(() => [
	{ value: BillType.DIVIDEND, label: i18n.global.t('options.billType.dividend') },
	{ value: BillType.MATURITY_REDEMPTION, label: i18n.global.t('options.billType.maturityRedemption') },
	{ value: BillType.EARLY_REDEMPTION, label: i18n.global.t('options.billType.earlyRedemption') },
]);

export const payTypeOptions = computed<LabelOptions<PayType>>(() => [
	{ value: PayType.BANK, label: i18n.global.t('options.payType.bank') },
]);

export const defaultPayType: PayType = PayType.BANK;

export const projectTypeOpen: ProjectType = ProjectType.OPEN;
export const projectTypeClosed: ProjectType = ProjectType.CLOSED;
