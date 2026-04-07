package cc.bamboo.module.chain.service.token;

import cc.bamboo.module.chain.config.Web3jConfig;
import cc.bamboo.module.chain.dal.dataobject.blockchainaddresses.BlockchainAddressesDO;
import cc.bamboo.module.chain.dal.dataobject.tokens.TokensDO;
import cc.bamboo.module.chain.dal.mysql.blockchainaddresses.BlockchainAddressesMapper;
import cc.bamboo.module.chain.dal.mysql.tokens.TokensMapper;
import cc.bamboo.module.chain.service.contract.ContractCallService;
import cc.bamboo.module.chain.service.token.dto.MintTokenReqDTO;
import cc.bamboo.module.chain.service.token.dto.MintTokenRespDTO;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import javax.annotation.Resource;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * Token 发行服务实现
 * 
 * @author Swolf
 */
@Service
@Slf4j
public class TokenMintServiceImpl implements TokenMintService {
    
    @Resource
    private Web3j web3j;
    
    @Resource
    private Web3jConfig web3jConfig;
    
    @Resource
    private ContractCallService contractCallService;
    
    @Resource
    private TokensMapper tokensMapper;
    
    @Resource
    private BlockchainAddressesMapper blockchainAddressesMapper;
    
    @Override
    public MintTokenRespDTO mint(MintTokenReqDTO reqDTO) {
        // 获取 Token
        TokensDO token = tokensMapper.selectById(reqDTO.getTokenId());
        if (token == null) {
            throw new RuntimeException("未找到 Token");
        }
        
        if (token.getAddress() == null) {
            throw new RuntimeException("Token 尚未部署");
        }
        
        // 确定使用的 agent 地址
   /*     String agentAddress = reqDTO.getAgentAddress();
        if (agentAddress == null) {
            List<String> tokenAgents = parseStringList(token.getTokenAgents());
            if (tokenAgents.isEmpty()) {
                throw new RuntimeException("未配置 Token 代理");
            }
            agentAddress = tokenAgents.get(0);
        }*/
        
        // 获取 agent 钱包
        Credentials agentCredentials = getDeployerCredentials();
        if (agentCredentials == null) {
            throw new RuntimeException("未找到代理钱包: " );
        }
        
        // 解析发行数量
        BigInteger amount;
        try {
            amount = new BigInteger(reqDTO.getAmount());
        } catch (NumberFormatException e) {
            throw new RuntimeException("无效的发行数量: " + reqDTO.getAmount());
        }
        
        log.info("发行 Token，tokenId: {}, toAddress: {}, amount: {}, agentAddress: {}", 
                reqDTO.getTokenId(), reqDTO.getToAddress(), reqDTO.getAmount(), "agentAddress");
        
        try {
            // 调用 Token.mint() 方法
            TransactionReceipt receipt = contractCallService.mint(
                    agentCredentials,
                    token.getAddress(),
                    reqDTO.getToAddress(),
                    amount
            );
            
            log.info("Token 发行成功，txHash: {}, blockNumber: {}", 
                    receipt.getTransactionHash(), receipt.getBlockNumber());
            
            MintTokenRespDTO result = new MintTokenRespDTO();
            result.setTokenId(reqDTO.getTokenId());
            result.setToAddress(reqDTO.getToAddress());
            result.setAmount(reqDTO.getAmount());
            result.setAgentAddress("agentAddress");
            result.setTransactionHash(receipt.getTransactionHash());
            result.setBlockNumber(receipt.getBlockNumber().longValue());
            
            return result;
            
        } catch (Exception e) {
            log.error("Token 发行失败，tokenId: {}, toAddress: {}, amount: {}", 
                    reqDTO.getTokenId(), reqDTO.getToAddress(), reqDTO.getAmount(), e);
            throw new RuntimeException("Token 发行失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 根据地址获取凭证
     */
    private Credentials getCredentialsByAddress(String address) {
        BlockchainAddressesDO blockchainAddress = blockchainAddressesMapper.selectOne(
                BlockchainAddressesDO::getAddress, address);
        if (blockchainAddress == null) {
            return null;
        }
        return Credentials.create(blockchainAddress.getPrivateKey());
    }

    private Credentials getDeployerCredentials() {
        LambdaQueryWrapper<BlockchainAddressesDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BlockchainAddressesDO::getName, "deployer");
        queryWrapper.last("limit 1");
        BlockchainAddressesDO blockchainAddress = blockchainAddressesMapper.selectOne(queryWrapper);
        if (blockchainAddress == null) {
            return null;
        }
        return Credentials.create(blockchainAddress.getPrivateKey());
    }
    
    /**
     * 解析字符串列表
     */
    private List<String> parseStringList(String json) {
        if (json == null || json.isEmpty()) {
            return new ArrayList<>();
        }
        try {
            return JSONUtil.toList(json, String.class);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
}
