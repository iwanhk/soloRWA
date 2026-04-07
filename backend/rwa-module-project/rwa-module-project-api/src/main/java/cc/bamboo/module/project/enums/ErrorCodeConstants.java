package cc.bamboo.module.project.enums;

import cc.bamboo.framework.common.exception.ErrorCode;

/**
 * System 错误码枚举类
 *
 * system 系统，使用 1-002-000-000 段
 */
public interface ErrorCodeConstants {
    // ========== 项目相关错误码 50100-50199 ==========
    ErrorCode PROJECT_NOT_FOUND = new ErrorCode(50100, "项目不存在");
    ErrorCode PROJECT_NOT_ON_SALE = new ErrorCode(50101, "项目未在售");
    ErrorCode PROJECT_SOLD_OUT = new ErrorCode(50102, "项目已售罄");
    ErrorCode INSUFFICIENT_STOCK = new ErrorCode(50103, "库存不足");
    ErrorCode BELOW_MINIMUM_PURCHASE = new ErrorCode(50104, "购买数量低于起购量");
    ErrorCode INVALID_WALLET_ADDRESS = new ErrorCode(50105, "钱包地址格式无效");
    ErrorCode INFO_NOT_EXISTS = new ErrorCode(50106, "项目信息不存在"); // 原描述重复，微调语义区分
    ErrorCode PROJECT_STATUS_NOT_ALLOW_CHANGE_SELL_STATUS = new ErrorCode(50107, "项目状态不允许修改上下架状态（只有未开售和出售中状态才能修改）");
    ErrorCode PUBLISHER_BANK_INFO_NOT_FOUND = new ErrorCode(50108, "发行商银行信息不存在");

    // ========== 订单相关错误码 50200-50299 ==========
    ErrorCode ORDER_NOT_FOUND = new ErrorCode(50200, "订单不存在");
    ErrorCode ORDER_NOT_PENDING = new ErrorCode(50201, "订单状态不是待支付");
    ErrorCode ORDER_EXPIRED = new ErrorCode(50202, "订单已过期");
    ErrorCode ORDER_NOT_BELONG_TO_USER = new ErrorCode(50203, "订单不属于当前用户");
    ErrorCode INVALID_SMS_CODE = new ErrorCode(50204, "验证码错误");
    ErrorCode UPLOAD_VOUCHER_FAILED = new ErrorCode(50205, "支付凭证上传失败");
    ErrorCode ORDER_NOT_UNDER_REVIEW = new ErrorCode(50206, "订单状态不是审核中");
    ErrorCode AUDIT_REMARK_REQUIRED = new ErrorCode(50207, "审核不通过时必须填写原因");
    ErrorCode AUDIT_FILES_REQUIRED = new ErrorCode(50208, "审核通过时必须上传文件");

    // ========== 订单余额相关错误码 50300-50399 ==========
    ErrorCode BALANCE_NOT_FOUND = new ErrorCode(50300, "订单余额不存在");
    ErrorCode BALANCE_NOT_BELONG_TO_USER = new ErrorCode(50301, "订单余额不属于当前用户");
    ErrorCode INSUFFICIENT_BALANCE = new ErrorCode(50302, "余额不足");
    ErrorCode INSUFFICIENT_HOLD_QUANTITY = new ErrorCode(50303, "持有份额不足");
    ErrorCode PROJECT_NOT_MATURED = new ErrorCode(50304, "项目未到期，不能进行到期赎回");
    ErrorCode DIVIDEND_AMOUNT_EXCEEDS_LIMIT = new ErrorCode(50305, "分红金额超过可提取金额");
    ErrorCode NOT_BANK = new ErrorCode(50306, "没有可用银行卡");
    ErrorCode ORDER_BALANCE_DATE_RANGE_TOO_LONG = new ErrorCode(50307, "订单余额查询时间范围不能超过90天");

    // ========== 账单审核相关错误码 50400-50499 ==========
    ErrorCode BILL_NOT_FOUND = new ErrorCode(50400, "账单不存在");
    ErrorCode BILL_NOT_PENDING = new ErrorCode(50401, "账单不是待审核状态");
    ErrorCode BILL_NOT_APPROVED = new ErrorCode(50402, "账单不是审核通过状态");
    ErrorCode BILL_ALREADY_PAID = new ErrorCode(50403, "账单已上传支付凭证");
    ErrorCode AUDIT_REMARK_REQUIRED_FOR_REJECTION = new ErrorCode(50404, "审核不通过时必须填写原因");
    ErrorCode BILL_AUDIT_CONFLICT = new ErrorCode(50405, "账单状态已变更，请刷新后重试");

    // ========== 数据不存在相关错误码 50500-50599（原999统一调整） ==========
    ErrorCode ORDER_NOT_EXISTS = new ErrorCode(50500, "项目认购订单不存在");
    ErrorCode OPERATION_NOT_EXISTS = new ErrorCode(50501, "项目运营统计表不存在");
    ErrorCode BILL_NOT_EXISTS = new ErrorCode(50502, "项目账单管理表不存在");
    ErrorCode NOTICE_NOT_EXISTS = new ErrorCode(50503, "项目通告表（含全局通告）不存在");
    ErrorCode USER_PROJECT_BALANCE_NOT_EXISTS = new ErrorCode(50504, "用户项目余额表（本金/收益汇总）不存在");
    ErrorCode REVENUE_NOT_EXISTS = new ErrorCode(50505, "项目收益不存在");
    ErrorCode ORDER_BALANCE_NOT_EXISTS = new ErrorCode(50506, "用户项目余额表不存在");
    ErrorCode ORDER_BALANCE_LOG_NOT_EXISTS = new ErrorCode(50507, "用户项目余额记录不存在");
    ErrorCode ORDER_DAILY_INCOME_NOT_EXISTS = new ErrorCode(50508, "订单每日收益统计不存在");
    ErrorCode DIVIDEND_PERIOD_NOT_EXISTS = new ErrorCode(50509, "分红周期不存在");
    ErrorCode SYSTEM_COIN_NOT_EXISTS = new ErrorCode(50510, "币种管理不存在");
    ErrorCode MINING_CONFIG_NOT_EXISTS = new ErrorCode(50511, "挖矿项目配置不存在");
    ErrorCode NOTICE_TENANT_NOT_MATCH = new ErrorCode(50512, "发行商与项目不匹配"); // 原50500调整，避免冲突

    // ========== 快照相关错误码 50600-50699 ==========
    ErrorCode SNAPSHOT_PENDING_CANNOT_EDIT = new ErrorCode(50600, "项目有待审核的编辑，请等待审核完成后再编辑");


    // ========== 文件相关错误码（保留原有特殊编码） ==========
    ErrorCode FILE_SIZE_EXCEEDED = new ErrorCode(1004005001, "文件大小超出限制");
    ErrorCode FUND_CONFIG_NOT_EXISTS = new ErrorCode(999, "基金项目配置不存在");
}
