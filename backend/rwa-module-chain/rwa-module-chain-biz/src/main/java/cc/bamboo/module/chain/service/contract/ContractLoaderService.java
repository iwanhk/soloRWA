package cc.bamboo.module.chain.service.contract;

import cc.bamboo.module.chain.config.ContractConfig;
import cn.hutool.core.io.IoUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 合约加载服务
 * 负责加载合约的 ABI 和 Bytecode
 * 
 * @author Swolf
 */
@Service
@Slf4j
public class ContractLoaderService {
    
    @Resource
    private ContractConfig contractConfig;
    
    /**
     * ABI 缓存
     */
    private final ConcurrentHashMap<String, String> abiCache = new ConcurrentHashMap<>();
    
    /**
     * Bytecode 缓存
     */
    private final ConcurrentHashMap<String, String> bytecodeCache = new ConcurrentHashMap<>();
    
    /**
     * 加载合约 ABI
     * 
     * @param contractName 合约名称
     * @return ABI JSON 字符串
     */
    public String loadAbi(String contractName) {
        return abiCache.computeIfAbsent(contractName, name -> {
            try {
                String path = "contracts/abi/" + name + ".json";
                ClassPathResource resource = new ClassPathResource(path);
                try (InputStream is = resource.getInputStream()) {
                    String content = IoUtil.read(is, StandardCharsets.UTF_8);
                    // 如果是完整的合约 JSON（包含 abi 和 bytecode），提取 abi 部分
                    if (content.contains("\"abi\"")) {
                        JSONObject json = JSONUtil.parseObj(content);
                        return json.getJSONArray("abi").toString();
                    }
                    return content;
                }
            } catch (IOException e) {
                log.error("加载合约 ABI 失败: {}", name, e);
                throw new RuntimeException("加载合约 ABI 失败: " + name, e);
            }
        });
    }
    
    /**
     * 加载合约 Bytecode
     * 
     * @param contractName 合约名称
     * @return Bytecode 十六进制字符串
     */
    public String loadBytecode(String contractName) {
        return bytecodeCache.computeIfAbsent(contractName, name -> {
            try {
                String path = "contracts/abi/" + name + ".json";
                ClassPathResource resource = new ClassPathResource(path);
                try (InputStream is = resource.getInputStream()) {
                    String content = IoUtil.read(is, StandardCharsets.UTF_8);
                    JSONObject json = JSONUtil.parseObj(content);
                    String bytecode = json.getStr("bytecode");
                    if (bytecode == null) {
                        bytecode = json.getStr("bin");
                    }
                    if (bytecode == null) {
                        throw new RuntimeException("合约 " + name + " 没有 bytecode");
                    }
                    // 确保以 0x 开头
                    if (!bytecode.startsWith("0x")) {
                        bytecode = "0x" + bytecode;
                    }
                    return bytecode;
                }
            } catch (IOException e) {
                log.error("加载合约 Bytecode 失败: {}", name, e);
                throw new RuntimeException("加载合约 Bytecode 失败: " + name, e);
            }
        });
    }
    
    /**
     * 加载完整的合约 JSON
     * 
     * @param contractName 合约名称
     * @return 合约 JSON 对象
     */
    public JSONObject loadContract(String contractName) {
        try {
            String path = "contracts/abi/" + contractName + ".json";
            ClassPathResource resource = new ClassPathResource(path);
            try (InputStream is = resource.getInputStream()) {
                String content = IoUtil.read(is, StandardCharsets.UTF_8);
                return JSONUtil.parseObj(content);
            }
        } catch (IOException e) {
            log.error("加载合约失败: {}", contractName, e);
            throw new RuntimeException("加载合约失败: " + contractName, e);
        }
    }
    
    /**
     * 清除缓存
     */
    public void clearCache() {
        abiCache.clear();
        bytecodeCache.clear();
    }
}
