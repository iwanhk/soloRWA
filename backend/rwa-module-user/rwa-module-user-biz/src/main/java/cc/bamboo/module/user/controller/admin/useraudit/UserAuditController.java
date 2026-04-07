package cc.bamboo.module.user.controller.admin.useraudit;

import cc.bamboo.module.user.dal.dataobject.userbank.UserBankDO;
import cc.bamboo.module.user.enums.UserAuditStatusEnum;
import cc.bamboo.module.user.enums.notice.NoticeTemplateEnum;
import cc.bamboo.module.user.service.noticemessage.NoticeMessageSendService;
import cc.bamboo.module.user.service.userchain.ChainOperationTaskService;
import lombok.extern.slf4j.Slf4j;
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
import static cc.bamboo.module.user.enums.ApiConstants.PARAM_REASON;

import cc.bamboo.module.user.controller.admin.useraudit.vo.*;
import cc.bamboo.module.user.dal.dataobject.useraudit.UserAuditDO;
import cc.bamboo.module.user.service.useraudit.UserAuditService;
import cc.bamboo.module.user.service.userbank.UserBankService;

@Tag(name = "管理后台 - 用户投资者认证审核")
@RestController
@RequestMapping("/user/audit")
@Validated
@Slf4j
public class UserAuditController {

    @Resource
    private UserAuditService auditService;

    @Resource
    private UserBankService userBankService;

    @Resource
    private ChainOperationTaskService chainOperationTaskService;

    @Resource
    private NoticeMessageSendService noticeMessageSendService;

    @PostMapping("/create")
    @Operation(summary = "创建用户投资者认证审核")
    @PreAuthorize("@ss.hasPermission('user:audit:create')")
    public CommonResult<Long> createAudit(@Valid @RequestBody UserAuditSaveReqVO createReqVO) {
        return success(auditService.createAudit(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新用户投资者认证审核")
    @PreAuthorize("@ss.hasPermission('user:audit:update')")
    public CommonResult<Boolean> updateAudit(@Valid @RequestBody UserAuditSaveReqVO updateReqVO) {
        auditService.updateAudit(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除用户投资者认证审核")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('user:audit:delete')")
    public CommonResult<Boolean> deleteAudit(@RequestParam("id") Long id) {
        auditService.deleteAudit(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得用户投资者认证审核")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('user:audit:query')")
    public CommonResult<UserAuditRespVO> getAudit(@RequestParam("id") Long id) {
        UserAuditDO audit = auditService.getAudit(id);
        UserAuditRespVO respVO = BeanUtils.toBean(audit, UserAuditRespVO.class);
        // 填充银行卡信息
        UserBankDO bank = userBankService.getBank(audit.getUserId());
        if (bank != null) {
            respVO.setBankAccountName(bank.getBankAccountName());
            respVO.setBankAccount(bank.getBankAccount());
            respVO.setBankName(bank.getBankName());
        }
        return success(respVO);
    }

    @GetMapping("/page")
    @Operation(summary = "获得用户投资者认证审核分页")
    @PreAuthorize("@ss.hasPermission('user:audit:query')")
    public CommonResult<PageResult<UserAuditRespVO>> getAuditPage(@Valid UserAuditPageReqVO pageReqVO) {
        PageResult<UserAuditDO> pageResult = auditService.getAuditPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, UserAuditRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出用户投资者认证审核 Excel")
    @PreAuthorize("@ss.hasPermission('user:audit:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportAuditExcel(@Valid UserAuditPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<UserAuditDO> list = auditService.getAuditPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "用户投资者认证审核.xls", "数据", UserAuditRespVO.class,
                        BeanUtils.toBean(list, UserAuditRespVO.class));
    }

    @PutMapping("/review")
    @Operation(summary = "审核用户认证")
    @PreAuthorize("@ss.hasPermission('user:audit:review')")
    public CommonResult<Boolean> reviewAudit(@Valid @RequestBody UserAuditReviewReqVO reviewReqVO) {
        Long userId = auditService.reviewAudit(reviewReqVO);
        // 审核通过，创建用户链地址
        if (Objects.equals(reviewReqVO.getAuditStatus(), UserAuditStatusEnum.APPROVED.getStatus())) {
            //如果用户有链地址，则进行签发
            noticeMessageSendService.sendSingleMessageAsync(userId, NoticeTemplateEnum.AUTH_APPROVED.getCode(),null);
            try {
                String taskNo = chainOperationTaskService.claimTask(
                        userId
                );
                log.info("[claimTask] 添加签发任务创建成功， taskNo: {}",  taskNo);
            } catch (Exception e) {
                log.error("[claimTask] 添加签发任务创建失败， 错误: {}",  e.getMessage(), e);
                // 注意：这里不抛出异常，因为订单已经创建成功，任务创建失败可以后续手动处理
            }
        }else{
            Map<String, Object> map = new HashMap<>();
            map.put(PARAM_REASON,reviewReqVO.getAuditRemark());
            noticeMessageSendService.sendSingleMessageAsync(userId, NoticeTemplateEnum.AUTH_REJECTED.getCode(),map);
        }
        return success(true);
    }

}