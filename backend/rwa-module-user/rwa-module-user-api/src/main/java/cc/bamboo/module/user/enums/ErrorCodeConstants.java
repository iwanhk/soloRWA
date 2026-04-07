package cc.bamboo.module.user.enums;

import cc.bamboo.framework.common.exception.ErrorCode;

/**
 * System 错误码枚举类
 *
 * system 系统，使用 1-002-000-000 段
 */
public interface ErrorCodeConstants {

    // ========== 审核/认证相关 10000-19999 ==========
    ErrorCode AUDIT_NOT_EXISTS = new ErrorCode(10001, "审核不存在");
    ErrorCode AUDIT_ALREADY_PASSED = new ErrorCode(10002, "用户认证已通过，无法重复提交");
    ErrorCode F2A_ALREADY_BOUND = new ErrorCode(10003, "用户已绑定2FA认证");
    ErrorCode F2A_NOT_INIT = new ErrorCode(10004, "2FA未初始化");
    ErrorCode F2A_VERIFY_CODE_ERROR = new ErrorCode(10005, "2FA验证码错误");
    ErrorCode F2A_NOT_ENABLED = new ErrorCode(10006, "2FA未开启");
    ErrorCode AUTH_MOBILE_NOT_MATCH = new ErrorCode(10007, "手机号不匹配");
    ErrorCode FILE_UPLOAD_ERROR = new ErrorCode(10008, "文件上传失败");
    ErrorCode FILE_SIZE_EXCEEDED = new ErrorCode(10009, "文件大小超出限制");
    ErrorCode AUDIT_NO_REMARK = new ErrorCode(10010, "请填写驳回理由");
    ErrorCode AUDIT_BANK_ACCOUNT_NAME_NOT_MATCH = new ErrorCode(10011, "银行账号名称与证件名称不一致");
    ErrorCode AUTH_LOGIN_BAD_CREDENTIALS = new ErrorCode(10012, "登录失败，账号密码不正确"); // 替代1_004_003_000
    ErrorCode AUTH_LOGIN_USER_DISABLED = new ErrorCode(10013, "登录失败，账号被禁用"); // 替代1_004_003_001
    ErrorCode USER_MOBILE_NOT_EXISTS = new ErrorCode(10014, "用户不存在"); // 替代1_004_003_005（与20001语义重复，统一归至此）
    ErrorCode AUTH_MOBILE_USED = new ErrorCode(10015, "手机号已经被使用"); // 替代1_004_003_007
    ErrorCode AUTH_MOBILE_NOT_BLANK = new ErrorCode(10016, "手机号不能为空"); // 替代1_004_003_008
    ErrorCode AUTH_EMAIL_USED = new ErrorCode(10017, "邮箱已经被使用");
    ErrorCode AUTH_EMAIL_NOT_EXISTS = new ErrorCode(10018, "该邮箱未注册");

    // ========== 用户基础信息 20000-29999 ==========
    ErrorCode INFO_NOT_EXISTS = new ErrorCode(20001, "用户不存在"); // 保留原有编码，语义与10014一致，可按需合并

    // ========== 银行/银行卡相关 30000-39999 ==========
    ErrorCode BANK_NOT_EXISTS = new ErrorCode(30001, "银行不存在");
    ErrorCode BANK_AUDIT_STATUS_INVALID = new ErrorCode(30002, "银行卡审核状态无效");

    // ========== 登录日志相关 40000-49999 ==========
    ErrorCode LOGIN_LOG_NOT_EXISTS = new ErrorCode(40001, "用户登录日志不存在");

    // ========== 消息通知相关 50000-59999 ==========
    ErrorCode NOTICE_TEMPLATE_NOT_EXISTS = new ErrorCode(50001, "消息模板不存在");
    ErrorCode NOTICE_MESSAGE_NOT_EXISTS = new ErrorCode(50002, "用户消息不存在");
    ErrorCode NOTICE_READ_NOT_EXISTS = new ErrorCode(50003, "用户消息已读不存在");

    // ========== 系统协议相关 60000-69999 ==========
    ErrorCode AGREEMENT_NOT_EXISTS = new ErrorCode(60001, "系统协议表不存在");

    // ========== 链地址相关 70000-79999（新增专属段，替代原999/1_004_004_xxx） ==========
    ErrorCode CHAIN_NOT_EXISTS = new ErrorCode(70001, "用户链地址表不存在"); // 替代原999，修正描述笔误（去掉=）
    ErrorCode CHAIN_ADDRESS_NOT_EXISTS = new ErrorCode(70002, "链地址不存在"); // 替代1_004_004_001
    ErrorCode CHAIN_ADDRESS_ALREADY_BOUND = new ErrorCode(70003, "链地址已绑定"); // 替代1_004_004_002
    ErrorCode CHAIN_ADDRESS_NOT_BOUND = new ErrorCode(70004, "链地址未绑定"); // 替代1_004_004_003（原重复编码）
    ErrorCode CHAIN_SIGN_ERROR = new ErrorCode(70005, "签名验证失败"); // 替代1_004_004_004
    ErrorCode CHAIN_ADDRESS_UNBIND_ERROR = new ErrorCode(70006, "该状态无法进行解绑"); // 替代原重复的1_004_004_003
    ErrorCode CHAIN_ADDRESS_STATUS_ERROR = new ErrorCode(70007, "该状态下无法进行操作"); // 替代1_004_004_005

}
