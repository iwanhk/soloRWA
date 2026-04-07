package cc.bamboo.module.chain.controller.admin.dividend;

import cc.bamboo.framework.common.pojo.CommonResult;
import cc.bamboo.module.chain.service.dividend.DividendRecordService;
import cc.bamboo.module.chain.api.dividend.dto.DividendRecordReqDTO;
import cc.bamboo.module.chain.api.dividend.dto.DividendRecordRespDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.validation.Valid;

import static cc.bamboo.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 分红记录上链")
@RestController
@RequestMapping("/chain/dividend")
@Validated
public class DividendRecordController {

    @Resource
    private DividendRecordService dividendRecordService;

    @PostMapping("/record")
    @Operation(summary = "记录分红信息上链")
    @PermitAll
    public CommonResult<DividendRecordRespDTO> recordDividend(@Valid @RequestBody DividendRecordReqDTO reqDTO) {
        return success(dividendRecordService.recordDividend(reqDTO));
    }
}
