package cc.bamboo.module.project.api.purchase;

import cc.bamboo.framework.common.pojo.CommonResult;
import cc.bamboo.module.project.api.purchase.dto.UserPurchaseSummaryRespDTO;
import cc.bamboo.module.project.enums.ApiConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collection;
import java.util.List;

@FeignClient(name = ApiConstants.NAME)
@Tag(name = "RPC 服务 - 用户购买汇总")
public interface UserPurchaseSummaryApi {

    String PREFIX = ApiConstants.PREFIX + "/purchase-summary";

    @GetMapping(PREFIX + "/list-by-user-ids")
    @Operation(summary = "按用户 ID 列表查询购买项目及金额汇总")
    CommonResult<List<UserPurchaseSummaryRespDTO>> getPurchaseSummaryList(
            @RequestParam("userIds") @Parameter(name = "userIds", description = "用户编号数组", example = "1,2") Collection<Long> userIds);
}

