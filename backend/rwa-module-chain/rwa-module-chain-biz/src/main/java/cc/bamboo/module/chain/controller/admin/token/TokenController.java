package cc.bamboo.module.chain.controller.admin.token;

import cc.bamboo.framework.common.pojo.CommonResult;
import cc.bamboo.module.chain.controller.admin.token.vo.MintTokenReqVO;
import cc.bamboo.module.chain.service.token.TokenMintService;
import cc.bamboo.module.chain.service.token.dto.MintTokenReqDTO;
import cc.bamboo.module.chain.service.token.dto.MintTokenRespDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;

import static cc.bamboo.framework.common.pojo.CommonResult.success;

/**
 * Token 管理 Controller
 * 
 * @author Swolf
 */
@Tag(name = "管理后台 - Token 管理")
@RestController
@RequestMapping("/chain/token")
@Validated
public class TokenController {
    
    @Resource
    private TokenMintService tokenMintService;
    
    @PostMapping("/{tokenId}/mint")
    @Operation(summary = "发行 Token")
    @PermitAll
    public CommonResult<MintTokenRespDTO> mint(
            @PathVariable Long tokenId,
            @RequestBody @Validated MintTokenReqVO reqVO) {
        MintTokenReqDTO reqDTO = new MintTokenReqDTO();
        reqDTO.setTokenId(tokenId);
        reqDTO.setToAddress(reqVO.getToAddress());
        reqDTO.setAmount(reqVO.getAmount());
        reqDTO.setAgentAddress(reqVO.getAgentAddress());
        return success(tokenMintService.mint(reqDTO));
    }
}
