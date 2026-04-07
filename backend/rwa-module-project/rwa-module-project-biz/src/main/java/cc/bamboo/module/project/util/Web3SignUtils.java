package cc.bamboo.module.project.util;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import lombok.extern.slf4j.Slf4j;

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
     * 使用 SHA-256 算法对钱包地址、订单号和购买数量进行哈希
     *
     * @param chainAddress 钱包地址
     * @param orderNo 订单号
     * @param quantity 购买数量
     * @return 签名字符串（十六进制格式）
     */
    public static String generateSignature(String chainAddress, String orderNo, Integer quantity) {
        // 拼接签名原文：钱包地址 + 订单号 + 购买数量
        String signData = chainAddress + orderNo + quantity;
        
        // 使用 SHA-256 生成签名
        String signature = DigestUtil.sha256Hex(signData);
        
        log.debug("[generateSignature] 生成签名成功，钱包地址: {}, 订单号: {}, 购买数量: {}, 签名: {}", 
                chainAddress, orderNo, quantity, signature);
        
        return "0x" + signature;
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

}
