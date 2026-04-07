package cc.bamboo.module.user.controller.app.userchain;

import cc.bamboo.framework.common.pojo.CommonResult;
import cc.bamboo.module.user.controller.app.userchain.vo.AppAddressReqVO;
import cc.bamboo.module.user.controller.app.userchain.vo.AppBindAddressReqVO;
import cc.bamboo.module.user.controller.app.userchain.vo.AppChainRespVO;
import cc.bamboo.module.user.controller.app.userchain.vo.AppUnBindAddressReqVO;
import cc.bamboo.module.user.controller.app.userchain.vo.AppUserChainRespVO;
import cc.bamboo.module.user.controller.app.userinfo.vo.AppUserInfoRespVO;
import cc.bamboo.module.user.service.userchain.UserChainService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.validation.Valid;

import java.util.List;

import static cc.bamboo.framework.common.pojo.CommonResult.success;
import static cc.bamboo.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

/**
 * @author Hus
 * @version 1.0
 * @date 2026/1/9 10:37
 * @description
 */
@Tag(name = "用户 APP - 链管理")
@RestController
@RequestMapping("/user/chain")
@Validated
@Slf4j
public class AppUserChainController {

    @Resource
    private UserChainService userChainService;

    @GetMapping("/list")
    @Operation(summary = "获取用户链列表")
    public CommonResult<List<AppUserChainRespVO>> getChainList() {
        return success(userChainService.getUserChain());
    }

    // 获取用户链地址
    @GetMapping("/get")
    @Operation(summary = "获取用户链地址")
    public CommonResult<AppUserChainRespVO> getUserChain(Long userChainId) {
        return success(userChainService.getUserChain(userChainId));
    }

    // 创建签名
    @GetMapping("/sign")
    @Operation(summary = "创建签名")
    public CommonResult<String> createSign(String chainAddress) {
        return success(userChainService.generateSignData(chainAddress));
    }

    // 绑定链地址
    @PostMapping("/bind")
    @Operation(summary = "绑定链地址")
    public CommonResult<Long> bindAddress(@RequestBody @Valid AppBindAddressReqVO repVO) {
        return success(userChainService.bindAddress(repVO));
    }

    // 解绑链地址
    @PostMapping("/unbind")
    @Operation(summary = "解绑链地址")
    public CommonResult unBindAddress(@RequestBody @Valid AppUnBindAddressReqVO repVO) {
        userChainService.unBindAddress(repVO);
        return success("success");
    }

    @GetMapping("/chain-list")
    @Operation(summary = "获取可用链列表")
    @PermitAll
    public CommonResult<List<AppChainRespVO>> getActiveChainList() {
        return success(userChainService.getActiveChainList());
    }

}
