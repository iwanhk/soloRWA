package cc.bamboo.module.project.api.projectinfo;

import cc.bamboo.framework.common.pojo.CommonResult;
import cc.bamboo.module.project.enums.ApiConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = ApiConstants.NAME)
@Tag(name = "RPC 服务 - 项目信息")
public interface ProjectInfoApi {

    String PREFIX = ApiConstants.PREFIX + "/project-info";

    @PostMapping(PREFIX + "/update-chain-info")
    @Operation(summary = "更新项目链上信息")
    CommonResult<Boolean> updateProjectChainInfo(
            @RequestParam("projectId") @Parameter(name = "projectId", description = "项目ID") Long projectId,
            @RequestParam("chainStatus") @Parameter(name = "chainStatus", description = "链合约状态") Integer chainStatus,
            @RequestParam("chainTokensId") @Parameter(name = "chainTokensId", description = "链上 Token 主键ID") Long chainTokensId,
            @RequestParam("chainTokenAddress") @Parameter(name = "chainTokenAddress", description = "链上 Token 合约地址") String chainTokenAddress);
}

