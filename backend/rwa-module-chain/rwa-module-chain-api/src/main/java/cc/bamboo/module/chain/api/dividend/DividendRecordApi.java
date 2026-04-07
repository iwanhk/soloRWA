package cc.bamboo.module.chain.api.dividend;

import cc.bamboo.module.chain.api.dividend.dto.DividendRecordReqDTO;
import cc.bamboo.module.chain.api.dividend.dto.DividendRecordRespDTO;
import cc.bamboo.module.chain.enums.ApiConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

/**
 * 分红记录上链 API 接口
 *
 * @author Swolf
 */
@FeignClient(name = ApiConstants.NAME)
@Tag(name = "RPC 服务 - 链服务")
public interface DividendRecordApi {

    /**
     * 记录分红信息上链
     *
     * @param reqDTO 请求参数
     * @return 上链结果
     */
    String PREFIX = ApiConstants.PREFIX + "/chain-dividend";

    @PostMapping(PREFIX + "/record-dividend")
    @Operation(summary = "分红上链")
    DividendRecordRespDTO recordDividend(@Valid DividendRecordReqDTO reqDTO);

}
