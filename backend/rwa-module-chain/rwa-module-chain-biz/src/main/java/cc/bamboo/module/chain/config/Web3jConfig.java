package cc.bamboo.module.chain.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

/**
 * Web3j 配置类
 * 
 * @author Swolf
 */
@Configuration
@ConfigurationProperties(prefix = "blockchain")
@Data
public class Web3jConfig {
    
    /**
     * 区块链 RPC URL
     */
    private String rpcUrl = "http://localhost:8545";
    
    /**
     * 链 ID
     */
    private Long chainId = 1337L;
    
    /**
     * Gas Price (wei)
     */
    private Long gasPrice = 20000000000L;
    
    /**
     * Gas Limit
     */
    private Long gasLimit = 5000000L;
    
    /**
     * 创建 Web3j 实例
     */
    @Bean
    public Web3j web3j() {
        return Web3j.build(new HttpService(rpcUrl));
    }
}
