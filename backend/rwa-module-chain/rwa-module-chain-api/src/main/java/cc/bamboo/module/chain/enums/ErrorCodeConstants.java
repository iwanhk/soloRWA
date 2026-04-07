package cc.bamboo.module.chain.enums;


import cc.bamboo.framework.common.exception.ErrorCode;

/**
 * System 错误码枚举类
 *
 * system 系统，使用 1-002-000-000 段
 */
public interface ErrorCodeConstants {

    ErrorCode TOKENS_NOT_EXISTS = new ErrorCode(999, "代币不存在");

    ErrorCode USER_IDENTITIES_NOT_EXISTS = new ErrorCode(999, "用户身份不存在");

    ErrorCode SYSTEM_INITIALIZATION_NOT_EXISTS = new ErrorCode(999, "系统初始化不存在");

    ErrorCode CONTRACT_DEPLOYMENTS_NOT_EXISTS = new ErrorCode(999, "合约部署不存在");

    ErrorCode IDENTITY_REGISTRY_STORAGES_NOT_EXISTS = new ErrorCode(999, "身份注册表存储不存在");

    ErrorCode CLAIM_TOPICS_NOT_EXISTS = new ErrorCode(999, "声明主题不存在");

    ErrorCode ADDRESS_IDENTITIES_NOT_EXISTS = new ErrorCode(999, "地址身份关联不存在");

    ErrorCode CLAIM_ISSUER_IDENTITIES_NOT_EXISTS = new ErrorCode(999, "声明发行者身份不存在");

    ErrorCode BLOCKCHAIN_ADDRESSES_NOT_EXISTS = new ErrorCode(999, "区块链地址不存在");
    ErrorCode CHAIN_NOT_EXISTS = new ErrorCode(999, "区块链不存在");
}
