package cc.bamboo.module.chain.service.token;

import cc.bamboo.module.chain.service.token.dto.MintTokenReqDTO;
import cc.bamboo.module.chain.service.token.dto.MintTokenRespDTO;

/**
 * Token 发行服务接口
 * 
 * @author Swolf
 */
public interface TokenMintService {
    
    /**
     * 发行 Token
     * 
     * @param reqDTO 发行请求
     * @return 发行结果
     */
    MintTokenRespDTO mint(MintTokenReqDTO reqDTO);
}
