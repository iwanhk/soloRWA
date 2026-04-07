package cc.bamboo.module.user.controller.admin.userbank;

import cc.bamboo.module.user.enums.UserAuditStatusEnum;
import cc.bamboo.module.user.enums.notice.NoticeTemplateEnum;
import cc.bamboo.module.user.service.noticemessage.NoticeMessageSendService;
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

import cc.bamboo.module.user.controller.admin.userbank.vo.*;
import cc.bamboo.module.user.dal.dataobject.userbank.UserBankDO;
import cc.bamboo.module.user.service.userbank.UserBankService;

@Tag(name = "管理后台 - 用户银行卡信息")
@RestController
@RequestMapping("/user/bank")
@Validated
public class UserBankController {

    @Resource
    private UserBankService bankService;

    @Resource
    private NoticeMessageSendService noticeMessageSendService;

    @PostMapping("/create")
    @Operation(summary = "创建用户银行卡信息")
    @PreAuthorize("@ss.hasPermission('user:bank:create')")
    public CommonResult<Long> createBank(@Valid @RequestBody UserBankSaveReqVO createReqVO) {
        return success(bankService.createBank(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新用户银行卡信息")
    @PreAuthorize("@ss.hasPermission('user:bank:update')")
    public CommonResult<Boolean> updateBank(@Valid @RequestBody UserBankSaveReqVO updateReqVO) {
        bankService.updateBank(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除用户银行卡信息")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('user:bank:delete')")
    public CommonResult<Boolean> deleteBank(@RequestParam("id") Long id) {
        bankService.deleteBank(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得用户银行卡信息")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('user:bank:query')")
    public CommonResult<UserBankRespVO> getBank(@RequestParam("id") Long id) {
        UserBankDO bank = bankService.getBank(id);
        return success(BeanUtils.toBean(bank, UserBankRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得用户银行卡信息分页")
    @PreAuthorize("@ss.hasPermission('user:bank:query')")
    public CommonResult<PageResult<UserBankRespVO>> getBankPage(@Valid UserBankPageReqVO pageReqVO) {
        PageResult<UserBankDO> pageResult = bankService.getBankPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, UserBankRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出用户银行卡信息 Excel")
    @PreAuthorize("@ss.hasPermission('user:bank:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportBankExcel(@Valid UserBankPageReqVO pageReqVO,
            HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<UserBankDO> list = bankService.getBankPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "用户银行卡信息.xls", "数据", UserBankRespVO.class,
                BeanUtils.toBean(list, UserBankRespVO.class));
    }

    @PutMapping("/audit")
    @Operation(summary = "审核用户银行卡")
    @PreAuthorize("@ss.hasPermission('user:bank:audit')")
    public CommonResult<Boolean> auditBank(@Valid @RequestBody UserBankAuditReqVO auditReqVO) {
       Long userId = bankService.auditBank(auditReqVO);
        if(Objects.equals(auditReqVO.getAuditStatus(), UserAuditStatusEnum.APPROVED.getStatus())){
            noticeMessageSendService.sendSingleMessageAsync(userId, NoticeTemplateEnum.BANK_APPROVED.getCode(),null);
        }else{
            Map<String, Object> map = new HashMap<>();
            map.put(PARAM_REASON,auditReqVO.getAuditRemark());
            noticeMessageSendService.sendSingleMessageAsync(userId, NoticeTemplateEnum.BANK_REJECTED.getCode(),map);
        }
        return success(true);
    }

}