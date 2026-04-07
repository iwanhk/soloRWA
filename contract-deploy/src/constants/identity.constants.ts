/**
 * Identity 相关的常量定义
 */

/**
 * Key Purpose 定义
 * 用于 ERC734 标准中的 key purpose
 */
export const enum KeyPurpose {
  MANAGEMENT = 1,  // 管理密钥，可以管理 identity
  ACTION = 2,      // 操作密钥，执行操作
  CLAIM = 3,       // 声明密钥，用于签发 claim
  ENCRYPTION = 4,  // 加密密钥，用于加密数据
}

/**
 * Key Type 定义
 * 用于 ERC734 标准中的 key type
 */
export const enum KeyType {
  ECDSA = 1,  // ECDSA 密钥
  RSA = 2,    // RSA 密钥
}

/**
 * Claim Scheme 定义
 * 用于 ERC735 标准中的 claim scheme
 */
export const enum ClaimScheme {
  ECDSA_SIGNATURE = 1,  // ECDSA 签名方案
}

