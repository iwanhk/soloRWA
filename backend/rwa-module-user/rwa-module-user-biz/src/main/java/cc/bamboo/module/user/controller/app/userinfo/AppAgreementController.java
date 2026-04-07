package cc.bamboo.module.user.controller.app.userinfo;

import cc.bamboo.framework.common.pojo.CommonResult;
import cc.bamboo.framework.common.util.object.BeanUtils;
import cc.bamboo.module.user.controller.app.userinfo.vo.AppAgreementListRespVO;
import cc.bamboo.module.user.controller.app.userinfo.vo.AppAgreementRespVO;
import cc.bamboo.module.user.dal.dataobject.agreement.AgreementDO;
import cc.bamboo.module.user.service.agreement.AgreementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import java.util.List;

import static cc.bamboo.framework.common.pojo.CommonResult.success;

@Tag(name = "用户 APP - 协议")
@RestController
@RequestMapping("/user/agreement")
@Validated
public class AppAgreementController {

    @Resource
    private AgreementService agreementService;

    @GetMapping("/list")
    @Operation(summary = "获得协议列表")
    @PermitAll
    public CommonResult<List<AppAgreementListRespVO>> getAgreementList() {
        // Reuse getAgreementSimple as it fetches all agreements without pagination
        List<AppAgreementListRespVO> list = agreementService.getAgreementList();
        return success(list);
    }

    @GetMapping("/detail")
    @Operation(summary = "获得协议详情")
    @PermitAll
    public CommonResult<AppAgreementRespVO> getAgreementDetail(@RequestParam Long id) {
        AppAgreementRespVO detail = agreementService.getAgreementDetail(id);
        return success(detail);
    }

    @GetMapping("/detail-by-key")
    @Operation(summary = "根据协议键值获得协议详情")
    @PermitAll
    public CommonResult<AppAgreementRespVO> getAgreementDetailByKey(@RequestParam String agreementKey) {
        AppAgreementRespVO detail = agreementService.getAgreementDetailByKey(agreementKey);
        return success(detail);
    }
}
