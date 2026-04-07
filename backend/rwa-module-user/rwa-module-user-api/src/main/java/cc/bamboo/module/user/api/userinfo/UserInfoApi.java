package cc.bamboo.module.user.api.userinfo;

import cc.bamboo.framework.common.pojo.CommonResult;
import cc.bamboo.module.user.api.userinfo.dto.UserInfoRespDTO;
import cc.bamboo.module.user.api.userinfo.dto.Verify2FAReqDTO;
import cc.bamboo.module.user.enums.ApiConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * RPC 服务 - 用户信息 API
 *
 * @author Swolf
 */
@FeignClient(name = ApiConstants.NAME)
@Tag(name = "RPC 服务 - 用户信息")
public interface UserInfoApi {

    String PREFIX = ApiConstants.PREFIX + "/user-info";

    /**
     * 通过用户 ID 查询用户信息
     *
     * @param id 用户ID
     * @return 用户信息
     */
    @GetMapping(PREFIX + "/get")
    @Operation(summary = "通过用户 ID 查询用户信息")
    @Parameter(name = "id", description = "用户编号", example = "1", required = true)
    CommonResult<UserInfoRespDTO> getUserInfo(@RequestParam("id") Long id);

    @GetMapping(PREFIX + "/getList")
    @Operation(summary = "通过用户 ID 查询用户信息")
    @Parameter(name = "id", description = "用户编号", example = "1", required = true)
    CommonResult<List<UserInfoRespDTO>> getUserInfoList(@RequestParam("ids") List<Long> ids);

    /**
     * 验证2FA码（如果用户开启了2FA）
     * 如果用户未开启2FA，则直接返回true
     *
     * @return 验证结果
     */
    @PostMapping(PREFIX + "/verify2fa")
    @Operation(summary = "验证2FA码（如果用户开启了2FA）")
    CommonResult<Boolean> verify2FAIfEnabled(@RequestBody Verify2FAReqDTO reqVO);
}
