package cc.bamboo.module.user.util;

import cn.hutool.core.codec.Base32;
import cn.hutool.core.util.RandomUtil;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;

/**
 * 2FA (Two-Factor Authentication) 工具类
 * 基于 TOTP (Time-based One-Time Password) 算法实现
 *
 * @author Kiro
 */
@Slf4j
public class TwoFactorAuthUtils {

    private static final int SECRET_SIZE = 20; // 密钥长度(字节)
    private static final int WINDOW_SIZE = 3; // 时间窗口大小(允许前后各3个时间窗口)
    private static final int TIME_STEP = 30; // 时间步长(秒)
    private static final String CRYPTO = "HmacSHA1";

    /**
     * 生成随机密钥(Base32格式)
     *
     * @return Base32格式的密钥
     */
    public static String generateSecretKey() {
        byte[] buffer = RandomUtil.randomBytes(SECRET_SIZE);
        return Base32.encode(buffer);
    }

    /**
     * 生成二维码URL
     *
     * @param secret 密钥
     * @param account 账号(通常是用户手机号或邮箱)
     * @param issuer 发行者(应用名称)
     * @return 二维码URL
     */
    public static String generateQRCodeUrl(String secret, String account, String issuer) {
        return String.format("otpauth://totp/%s:%s?secret=%s&issuer=%s",
                issuer, account, secret, issuer);
    }

    /**
     * 验证2FA验证码
     *
     * @param secret 密钥(Base32格式)
     * @param code 用户输入的验证码
     * @return 是否验证通过
     */
    public static boolean verifyCode(String secret, String code) {
        try {
            long currentTime = Instant.now().getEpochSecond() / TIME_STEP;
            
            // 检查当前时间窗口及前后窗口
            for (int i = -WINDOW_SIZE; i <= WINDOW_SIZE; i++) {
                String generatedCode = generateCode(secret, currentTime + i);
                if (generatedCode.equals(code)) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            log.error("验证2FA验证码失败", e);
            return false;
        }
    }

    /**
     * 生成指定时间的验证码
     *
     * @param secret 密钥(Base32格式)
     * @param timeIndex 时间索引
     * @return 6位验证码
     */
    private static String generateCode(String secret, long timeIndex) throws NoSuchAlgorithmException, InvalidKeyException {
        byte[] key = Base32.decode(secret);
        byte[] data = ByteBuffer.allocate(8).putLong(timeIndex).array();

        Mac mac = Mac.getInstance(CRYPTO);
        mac.init(new SecretKeySpec(key, CRYPTO));
        byte[] hash = mac.doFinal(data);

        int offset = hash[hash.length - 1] & 0xF;
        long truncatedHash = 0;
        for (int i = 0; i < 4; i++) {
            truncatedHash <<= 8;
            truncatedHash |= (hash[offset + i] & 0xFF);
        }

        truncatedHash &= 0x7FFFFFFF;
        truncatedHash %= 1000000;

        return String.format("%06d", truncatedHash);
    }

    /**
     * 生成当前时间的验证码(用于测试)
     *
     * @param secret 密钥(Base32格式)
     * @return 6位验证码
     */
    public static String generateCurrentCode(String secret) {
        try {
            long currentTime = Instant.now().getEpochSecond() / TIME_STEP;
            return generateCode(secret, currentTime);
        } catch (Exception e) {
            log.error("生成2FA验证码失败", e);
            return null;
        }
    }
}
