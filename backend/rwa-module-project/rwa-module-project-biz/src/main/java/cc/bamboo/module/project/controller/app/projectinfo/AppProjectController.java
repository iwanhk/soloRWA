package cc.bamboo.module.project.controller.app.projectinfo;

import cc.bamboo.framework.common.pojo.CommonResult;
import cc.bamboo.framework.common.pojo.PageResult;
import cc.bamboo.framework.common.util.object.BeanUtils;
import cc.bamboo.framework.tenant.core.aop.TenantIgnore;
import cc.bamboo.module.project.controller.app.projectinfo.vo.*;
import cc.bamboo.module.project.dal.dataobject.projectinfo.ProjectInfoDO;
import cc.bamboo.module.project.dal.mysql.projectinfo.ProjectInfoMapper;
import cc.bamboo.module.project.service.projectinfo.AppProjectService;
import cc.bamboo.module.project.service.projectnotice.ProjectNoticeService;
import cc.bamboo.module.project.service.projectorder.ProjectOrderService;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static cc.bamboo.framework.common.pojo.CommonResult.success;

/**
 * 用户 APP - 项目管理
 *
 * @author Swolf
 */
@Tag(name = "用户 APP - 项目管理")
@RestController
@RequestMapping("/project/info")
@Validated
@Slf4j
public class AppProjectController {

    @Resource
    private AppProjectService appProjectService;

    @Resource
    private ProjectNoticeService projectNoticeService;

    @Resource
    private ProjectInfoMapper projectInfoMapper;

    @Resource
    private ProjectOrderService projectOrderService;

    @GetMapping("/detail")
    @Operation(summary = "获取项目详情")
    @Parameter(name = "projectId", description = "项目ID", required = true, example = "1")
    @PermitAll
    @TenantIgnore
    public CommonResult<AppProjectDetailRespVO> getProjectDetail(
            @RequestParam("projectId") @NotNull(message = "项目ID不能为空") Long id) {

        return success(appProjectService.getAppProjectDetail(id));
    }

    @GetMapping("/detail-file")
    @Operation(summary = "获取项目文件")
    @Parameter(name = "projectId", description = "项目ID", required = true, example = "1")
    @TenantIgnore
    public CommonResult<String> getProjectFileUrls(
            @RequestParam("projectId") @NotNull(message = "项目ID不能为空") Long id) {
        return success(appProjectService.getProjectFileUrls(id));
    }


    @GetMapping("/list")
    @Operation(summary = "查询项目列表")
    @PermitAll
    @TenantIgnore
    public CommonResult<PageResult<AppProjectListRespVO>> getOnSaleProjectList(@Valid AppProjectListReqVO reqVO) {
        return success(appProjectService.getProjectList(reqVO));
    }

    @GetMapping("/noticeList")
    @Operation(summary = "查询公告")
    @TenantIgnore
    public CommonResult<PageResult<AppProjectNoticeRespVO>> noticeList(@Valid AppProjectNoticePageReqVO pageReqVO) {
        return success(projectNoticeService.getAppNoticePage(pageReqVO));
    }

    @GetMapping("/payment-info")
    @Operation(summary = "获取项目收款信息", description = "根据项目ID查询发行商的银行收款信息")
    @Parameter(name = "projectId", description = "项目ID", required = true, example = "1")
    @PermitAll
    @TenantIgnore
    public CommonResult<AppProjectPaymentInfoRespVO> getPaymentInfo(
            @RequestParam("projectId") @NotNull(message = "项目ID不能为空") Long projectId) {

        log.info("[getPaymentInfo] 查询项目收款信息，项目ID: {}", projectId);

        // 1. 查询项目信息获取tenant_id
        ProjectInfoDO project = appProjectService.getProjectDetail(projectId);
        if (project == null) {
            log.warn("[getPaymentInfo] 项目不存在，项目ID: {}", projectId);
            return success(null);
        }

        Long tenantId = project.getTenantId();
        if (tenantId == null) {
            log.warn("[getPaymentInfo] 项目无租户信息，项目ID: {}", projectId);
            return success(null);
        }

        // 2. 通过tenant_id直接查询biz_publisher_info表获取银行信息
        AppProjectPaymentInfoRespVO respVO = projectInfoMapper.getPublisherBankInfoByTenantId(tenantId);
        if (respVO == null) {
            log.warn("[getPaymentInfo] 发行商银行信息不存在，租户ID: {}", tenantId);
            return success(null);
        }

        log.info("[getPaymentInfo] 查询成功，项目ID: {}, 开户名: {}", projectId, respVO.getBankAccountName());

        return success(respVO);
    }

    @GetMapping("/asset-type-count")
    @Operation(summary = "获取项目资产类型统计")
    @PermitAll
    @TenantIgnore
    public CommonResult<List<AssetTypeCountRespVO>> countProjectByAssetType() {
        return success(appProjectService.countProjectByAssetType());
    }

}
