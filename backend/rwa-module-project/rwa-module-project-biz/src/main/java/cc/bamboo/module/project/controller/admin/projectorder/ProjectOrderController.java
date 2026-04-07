package cc.bamboo.module.project.controller.admin.projectorder;

import cc.bamboo.framework.security.core.util.SecurityFrameworkUtils;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;

import javax.validation.constraints.*;
import javax.validation.*;
import javax.servlet.http.*;
import java.util.*;
import java.io.IOException;

import cc.bamboo.framework.common.pojo.PageParam;
import cc.bamboo.framework.common.pojo.PageResult;
import cc.bamboo.framework.common.pojo.CommonResult;
import cc.bamboo.framework.common.util.object.BeanUtils;
import static cc.bamboo.framework.common.pojo.CommonResult.success;

import cc.bamboo.framework.excel.core.util.ExcelUtils;

import cc.bamboo.framework.apilog.core.annotation.ApiAccessLog;
import static cc.bamboo.framework.apilog.core.enums.OperateTypeEnum.*;

import cc.bamboo.module.project.controller.admin.projectorder.vo.*;
import cc.bamboo.module.project.dal.dataobject.projectorder.ProjectOrderDO;
import cc.bamboo.module.project.service.projectorder.ProjectOrderService;

import cc.bamboo.framework.security.core.LoginUser;
import static cc.bamboo.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;
import static cc.bamboo.framework.security.core.util.SecurityFrameworkUtils.getLoginUser;

@Tag(name = "管理后台 - 项目认购订单")
@RestController
@RequestMapping("/project/order")
@Validated
public class ProjectOrderController {

    @Resource
    private ProjectOrderService orderService;

    @PostMapping("/create")
    @Operation(summary = "创建项目认购订单")
    @PreAuthorize("@ss.hasPermission('project:order:create')")
    public CommonResult<Long> createOrder(@Valid @RequestBody ProjectOrderSaveReqVO createReqVO) {
        return success(orderService.createOrder(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新项目认购订单")
    @PreAuthorize("@ss.hasPermission('project:order:update')")
    public CommonResult<Boolean> updateOrder(@Valid @RequestBody ProjectOrderSaveReqVO updateReqVO) {
        orderService.updateOrder(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除项目认购订单")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('project:order:delete')")
    public CommonResult<Boolean> deleteOrder(@RequestParam("id") Long id) {
        orderService.deleteOrder(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得项目认购订单")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('project:order:query')")
    public CommonResult<ProjectOrderRespVO> getOrder(@RequestParam("id") Long id) {
        return success(orderService.getOrder(id));
    }

    @GetMapping("/page")
    @Operation(summary = "获得项目认购订单分页")
    @PreAuthorize("@ss.hasPermission('project:order:query')")
    public CommonResult<PageResult<ProjectOrderRespVO>> getOrderPage(@Valid ProjectOrderPageReqVO pageReqVO) {
        PageResult<ProjectOrderRespVO> pageResult = orderService.getOrderPage(pageReqVO);
        return success(pageResult);
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出项目认购订单 Excel")
    @PreAuthorize("@ss.hasPermission('project:order:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportOrderExcel(@Valid ProjectOrderPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<ProjectOrderRespVO> list = orderService.getOrderPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "项目认购订单.xls", "数据", ProjectOrderRespVO.class,
                        BeanUtils.toBean(list, ProjectOrderRespVO.class));
    }

    @PutMapping("/audit")
    @Operation(summary = "审核项目订单")
    @PreAuthorize("@ss.hasPermission('project:order:audit')")
    @ApiAccessLog(operateType = UPDATE)
    public CommonResult<Boolean> auditOrder(@Valid @RequestBody ProjectOrderAuditReqVO auditReqVO) {
        // 获取当前登录用户信息
        Long auditUserId = getLoginUserId();
        // 从用户信息中获取昵称作为审核人名称
        String auditUserName = SecurityFrameworkUtils.getLoginUserNickname();
        
        // 执行审核
        orderService.auditOrder(auditReqVO, auditUserId, auditUserName);
        if(auditReqVO.getApproved()){
            // 发送铸造 Token 消息
            orderService.sendMintToken(auditReqVO.getId());
        }
        return success(true);
    }

}