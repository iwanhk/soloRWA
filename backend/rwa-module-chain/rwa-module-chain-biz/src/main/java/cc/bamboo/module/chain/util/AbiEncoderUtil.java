package cc.bamboo.module.chain.util;

import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeEncoder;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.DynamicBytes;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Hash;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

/**
 * ABI 编码工具类
 * 提供 Solidity ABI 编码和哈希计算功能
 * 
 * @author Swolf
 */
public class AbiEncoderUtil {
    
    private AbiEncoderUtil() {
        // 工具类不允许实例化
    }
    
    /**
     * 编码单个地址
     * 
     * @param address 以太坊地址
     * @return 编码后的十六进制字符串
     */
    public static String encodeAddress(String address) {
        return TypeEncoder.encode(new Address(address));
    }
    
    /**
     * 计算地址的 keccak256 哈希
     * 用于计算 managementKey
     * 
     * @param address 以太坊地址
     * @return 哈希值（bytes32）
     */
    public static String hashAddress(String address) {
        String encoded = encodeAddress(address);
        return Hash.sha3("0x" + encoded);
    }
    
    /**
     * 编码地址和 uint256
     * 
     * @param address 以太坊地址
     * @param value uint256 值
     * @return 编码后的十六进制字符串
     */
    public static String encodeAddressAndUint256(String address, BigInteger value) {
        String addressEncoded = TypeEncoder.encode(new Address(address));
        String uint256Encoded = TypeEncoder.encode(new Uint256(value));
        return "0x" + addressEncoded + uint256Encoded;
    }
    
    /**
     * 编码地址、uint256 和 bytes
     * 用于 Claim 数据编码
     * 
     * @param address 以太坊地址（Identity 合约地址）
     * @param topic 声明主题（uint256）
     * @param data 声明数据（bytes）
     * @return 编码后的十六进制字符串
     */
    public static String encodeAddressUint256Bytes(String address, BigInteger topic, byte[] data) {
        String addressEncoded = TypeEncoder.encode(new Address(address));
        String uint256Encoded = TypeEncoder.encode(new Uint256(topic));
        // 计算偏移量：address(32) + uint256(32) + offset(32) = 96 (0x60)
        String offsetEncoded = TypeEncoder.encode(new Uint256(BigInteger.valueOf(96)));

        // 编码动态bytes数据
        String bytesEncoded = TypeEncoder.encode(new DynamicBytes(data));

        // 完整编码：固定部分 + 偏移量 + 动态数据
        return "0x" + addressEncoded + uint256Encoded + offsetEncoded + bytesEncoded;
    }
    
    /**
     * 计算地址、uint256、bytes 的 keccak256 哈希
     * 用于计算 Claim 数据哈希
     * 
     * @param address 以太坊地址（Identity 合约地址）
     * @param topic 声明主题（uint256）
     * @param data 声明数据（bytes）
     * @return 哈希值（bytes32）
     */
    public static String hashAddressUint256Bytes(String address, BigInteger topic, byte[] data) {
        String encoded = encodeAddressUint256Bytes(address, topic, data);
        return Hash.sha3(encoded);
    }
    
    /**
     * 计算地址、uint256、bytes 的 keccak256 哈希（字符串版本）
     * 
     * @param address 以太坊地址（Identity 合约地址）
     * @param topicHex 声明主题（十六进制字符串）
     * @param dataHex 声明数据（十六进制字符串）
     * @return 哈希值（bytes32）
     */
    public static String hashAddressUint256Bytes(String address, String topicHex, String dataHex) {
        BigInteger topic = Numeric.toBigInt(topicHex);
        byte[] data = Numeric.hexStringToByteArray(dataHex);
        return hashAddressUint256Bytes(address, topic, data);
    }
    
    /**
     * 通用编码方法
     * 
     * @param types 类型列表
     * @return 编码后的十六进制字符串
     */
    public static String encode(List<Type> types) {
        StringBuilder result = new StringBuilder();
        for (Type type : types) {
            result.append(TypeEncoder.encode(type));
        }
        return "0x" + result;
    }
    
    /**
     * 通用哈希方法
     * 
     * @param types 类型列表
     * @return 哈希值（bytes32）
     */
    public static String hash(List<Type> types) {
        String encoded = encode(types);
        return Hash.sha3(encoded);
    }
    
    /**
     * 计算字符串的 keccak256 哈希
     * 用于计算 ClaimTopic
     * 
     * @param value 字符串值
     * @return 哈希值（bytes32）
     */
    public static String keccak256(String value) {
        return Hash.sha3String(value);
    }
    
    /**
     * 编码构造函数参数
     * 
     * @param types 参数类型列表
     * @return 编码后的十六进制字符串
     */
    public static String encodeConstructor(List<Type> types) {
        return FunctionEncoder.encodeConstructor(types);
    }
    
    /**
     * 将十六进制字符串转换为 BigInteger
     * 
     * @param hex 十六进制字符串
     * @return BigInteger
     */
    public static BigInteger hexToBigInteger(String hex) {
        return Numeric.toBigInt(hex);
    }
    
    /**
     * 将 BigInteger 转换为十六进制字符串
     * 
     * @param value BigInteger
     * @return 十六进制字符串（带 0x 前缀）
     */
    public static String bigIntegerToHex(BigInteger value) {
        return Numeric.toHexStringWithPrefix(value);
    }
}
