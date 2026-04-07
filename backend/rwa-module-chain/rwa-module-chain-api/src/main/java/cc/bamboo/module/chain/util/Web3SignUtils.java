package cc.bamboo.module.chain.util;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.IdcardUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.web3j.crypto.Keys;
import org.web3j.crypto.Sign;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.regex.Pattern;

/**
 * Web3 签名工具类
 * 用于生成 Web3 钱包签名字符串和验证钱包地址格式
 *
 * @author Swolf
 */
@Slf4j
public class Web3SignUtils {

    /**
     * 以太坊钱包地址正则表达式
     * 格式：0x 开头 + 40位十六进制字符
     */
    private static final Pattern WALLET_ADDRESS_PATTERN = Pattern.compile("^0x[0-9a-fA-F]{40}$");

    /**
     * 生成 Web3 签名字符串
     *
     * @param chainAddress 钱包地址
     * @param url 订单号
     * @return 签名字符串
     */
    public static String generateSignatureData(String chainAddress, String url) {
        // 拼接签名原文：你现在正在请求：{url}，并且需要对该请求进行签名，请确保请求的完整性和安全性。
        String snowflakeId = IdUtil.getSnowflakeNextIdStr();
        String signData = "Welcome to SOLO!\n" +
                "\n" +
                "Click \"Sign\" to sign in. No password needed!\n" +
                "This request will not trigger a blockchain transaction or cost any gas fees.\n" +
                "\n" +
                "\n" +
                "I accept the SOLO of Service: " +url+"\n" +
                "\n" +
                "Wallet address:\n" + chainAddress +
                "\n" +
                "Nonce:\n" + snowflakeId;
        return signData;
    }

    /**
     * 验证钱包地址格式是否正确
     * 标准以太坊地址格式：0x + 40位十六进制字符
     *
     * @param walletAddress 钱包地址
     * @return true-格式正确，false-格式错误
     */
    public static boolean validateWalletAddress(String walletAddress) {
        if (StrUtil.isBlank(walletAddress)) {
            log.warn("[validateWalletAddress] 钱包地址为空");
            return false;
        }
        
        boolean isValid = WALLET_ADDRESS_PATTERN.matcher(walletAddress).matches();
        
        if (!isValid) {
            log.warn("[validateWalletAddress] 钱包地址格式无效: {}", walletAddress);
        }
        
        return isValid;
    }

    /**
     * 验证 Web3 签名
     * 验证签名是否由指定地址对指定消息签署
     *
     * @param address   钱包地址
     * @param message   原始内容（未加前缀的字符串）
     * @param signature 签名结果（16进制字符串，0x开头或不带开头，长度130或132）
     * @return true-验证通过
     */
   public static boolean verifySignature(String address, String message, String signature) {

        // 1. 入参空值校验
        if (StrUtil.isBlank(address) || StrUtil.isBlank(message) || StrUtil.isBlank(signature)) {
            log.warn("[verifySignature] 验签失败：入参为空，address={}, message={}, signature={}", address, message, signature);
            return false;
        }

        try {
            // 2. 处理签名字符串，移除 0x 前缀
            String cleanSignature = Numeric.cleanHexPrefix(signature);
            // 修复BUG3：签名长度校验放宽，只要>=130即可（兼容补0的签名）
            if (cleanSignature.length() < 130) {
                log.warn("[verifySignature] 签名长度无效，长度={}, 签名={}", cleanSignature.length(), signature);
                return false;
            }

            // 3. 解析签名数据 (R, S, V) 转字节数组
            byte[] signatureBytes = Numeric.hexStringToByteArray(cleanSignature);
            byte v = signatureBytes[64];
            // ============ 修复BUG1：完整的V值标准化处理，兼容所有以太坊规范 ============
            if (v == 0 || v == 1) {
                // 最主流场景：原始0/1 转为以太坊标准27/28
                v += 27;
            } else if (v > 30) {
                // 兼容EIP-155 链ID签名：v = 链ID*2 +35/36 → 还原为27/28
                v = (byte) (v - 2 * 4 - 1);
            }
            // ======================================================================

            byte[] r = Arrays.copyOfRange(signatureBytes, 0, 32);
            byte[] s = Arrays.copyOfRange(signatureBytes, 32, 64);
            Sign.SignatureData signatureData = new Sign.SignatureData(v, r, s);

            // 4. 恢复公钥：修复BUG2，强制指定 UTF-8 编码，杜绝乱码导致的验签失败
            BigInteger publicKey = Sign.signedPrefixedMessageToKey(message.getBytes(StandardCharsets.UTF_8), signatureData);

            // ============ 规避风险1：公钥空值判断，防止空指针异常 ============
            if (publicKey == null) {
                log.warn("[verifySignature] 验签失败：公钥恢复为空，address={}, message={}", address, message);
                return false;
            }
            // =================================================================

            // 5. 从公钥获取地址（固定带0x前缀）
            String recoveredAddress = "0x" + Keys.getAddress(publicKey);

            // ============ 规避风险2：标准化处理入参地址，兼容带/不带0x前缀 ============
            String standardAddress = address.startsWith("0x") ? address : "0x" + address;
            // ======================================================================

            // 6. 比较地址（忽略大小写，以太坊地址大小写不敏感）
            boolean match = standardAddress.equalsIgnoreCase(recoveredAddress);
            if (!match) {
                log.warn("[verifySignature] 签名验证失败: 期望地址={}, 恢复地址={}, 原文={}", standardAddress, recoveredAddress, message);
            } else {
                log.info("[verifySignature] 签名验证成功: 地址={}, 原文={}", standardAddress, message);
            }
            return match;

        } catch (Exception e) {
            log.error("[verifySignature] 签名验证异常，address={}, message={}, signature={}", address, message, signature, e);
            return false;
        }
    }

}
