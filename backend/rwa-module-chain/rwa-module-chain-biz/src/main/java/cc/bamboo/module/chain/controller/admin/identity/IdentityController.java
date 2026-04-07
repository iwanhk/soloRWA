package cc.bamboo.module.chain.controller.admin.identity;

import cc.bamboo.framework.common.pojo.CommonResult;
import cc.bamboo.module.chain.controller.admin.identity.vo.*;
import cc.bamboo.module.chain.service.identity.IdentityService;
import cc.bamboo.module.chain.service.identity.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;

import static cc.bamboo.framework.common.pojo.CommonResult.success;

/**
 * 身份管理 Controller
 * 
 * @author Swolf
 */
@Tag(name = "管理后台 - 身份管理")
@RestController
@RequestMapping("/chain/identity")
@Validated
public class IdentityController {
    
    @Resource
    private IdentityService identityService;
    
    @PostMapping("/claim-issuer")
    @Operation(summary = "添加 ClaimIssuer")
    public CommonResult<IdentityOperationRespDTO> addClaimIssuer(@RequestBody @Validated AddClaimIssuerReqVO reqVO) {
        return success(identityService.addClaimIssuer(reqVO.getAddress()));
    }
    
    @PostMapping("/user")
    @Operation(summary = "添加用户")
    @PermitAll
    public CommonResult<IdentityOperationRespDTO> addUser(@RequestBody @Validated AddUserReqVO reqVO) {
        return success(identityService.addUser(reqVO.getUserId(),reqVO.getAddress(), reqVO.getTokenId(), reqVO.getCountryCode()));
    }
    
    @PostMapping("/link-wallet")
    @Operation(summary = "关联新钱包到现有身份")
    public CommonResult<LinkWalletRespDTO> linkWallet(@RequestBody @Validated LinkWalletReqVO reqVO) {
        return success(identityService.linkWallet(reqVO.getNewWalletAddress(), reqVO.getOldWalletAddress()));
    }

    @PostMapping("/link-new-wallet")
    @Operation(summary = "关联新钱包到现有身份")
    @PermitAll
    public CommonResult<LinkNewWalletRespDTO> linkNewWallet(@RequestBody @Validated LinkWalletReqVO reqVO) {
        return success(identityService.linkWallet(reqVO.getNewWalletAddress(), reqVO.getId()));
    }

    @PostMapping("/delete-wallet")
    @Operation(summary = "关联新钱包到现有身份")
    @PermitAll
    public CommonResult<DeleteWalletRespDTO> deleteWallet(@RequestBody @Validated LinkWalletReqVO reqVO) {
        return success(identityService.deleteWallet(reqVO.getNewWalletAddress()));
    }
    
    @PostMapping("/{claimIssuerId}/issue-claim")
    @Operation(summary = "签发 Claim")
    @PermitAll
    public CommonResult<IssueClaimRespDTO> issueClaim(
            @PathVariable Long claimIssuerId,
            @RequestBody @Validated IssueClaimReqVO reqVO) {
        IssueClaimReqDTO reqDTO = new IssueClaimReqDTO();
        reqDTO.setClaimIssuerId(claimIssuerId);
        reqDTO.setUserId(reqVO.getUserId());
        reqDTO.setTopic(reqVO.getTopic());
        reqDTO.setData(reqVO.getData());
        reqDTO.setUri(reqVO.getUri());
        reqDTO.setScheme(reqVO.getScheme());
        return success(identityService.issueClaim(reqDTO));
    }
}
