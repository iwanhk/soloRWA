package cc.bamboo.module.user.controller.admin.userinfo;

import cc.bamboo.module.user.enums.notice.NoticeTemplateEnum;
import cc.bamboo.module.user.service.noticemessage.NoticeMessageSendService;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;

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

import cc.bamboo.module.user.controller.admin.userinfo.vo.*;
import cc.bamboo.module.user.dal.dataobject.userinfo.UserInfoDO;
import cc.bamboo.module.user.service.userinfo.UserPurchaseExportService;
import cc.bamboo.module.user.service.userinfo.UserInfoService;
import cc.bamboo.module.user.service.userbank.UserBankService;
import cn.hutool.core.collection.CollUtil;

@Tag(name = "管理后台 - 用户基础信息")
@RestController
@RequestMapping("/user/info")
@Validated
public class UserInfoController {

    @Resource
    private UserInfoService infoService;

    @Resource
    private UserBankService userBankService;

    @Resource
    private NoticeMessageSendService noticeMessageSendService;

    @Resource
    private UserPurchaseExportService userPurchaseExportService;

    @PostMapping("/create")
    @Operation(summary = "创建用户基础信息")
    @PreAuthorize("@ss.hasPermission('user:info:create')")
    public CommonResult<Long> createInfo(@Valid @RequestBody UserInfoSaveReqVO createReqVO) {
        return success(infoService.createInfo(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新用户基础信息")
    @PreAuthorize("@ss.hasPermission('user:info:update')")
    public CommonResult<Boolean> updateInfo(@Valid @RequestBody UserInfoSaveReqVO updateReqVO) {
        infoService.updateInfo(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除用户基础信息")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('user:info:delete')")
    public CommonResult<Boolean> deleteInfo(@RequestParam("id") Long id) {
        infoService.deleteInfo(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得用户基础信息")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('user:info:query')")
    public CommonResult<UserInfoRespVO> getInfo(@RequestParam("id") Long id) {
        UserInfoDO info = infoService.getInfo(id);
        return success(BeanUtils.toBean(info, UserInfoRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得用户基础信息分页")
    @PreAuthorize("@ss.hasPermission('user:info:query')")
    public CommonResult<PageResult<UserInfoRespVO>> getInfoPage(@Valid UserInfoPageReqVO pageReqVO) {
        PageResult<UserInfoDO> pageResult = infoService.getInfoPage(pageReqVO);
        PageResult<UserInfoRespVO> respVOPage = BeanUtils.toBean(pageResult, UserInfoRespVO.class);

        if (CollUtil.isNotEmpty(respVOPage.getList())) {
            Set<Long> userIds = new HashSet<>();
            respVOPage.getList().forEach(item -> userIds.add(item.getId()));
            Map<Long, Long> pendingBankMap = userBankService.getPendingBankApplyMap(userIds);
            respVOPage.getList().forEach(item -> {
                item.setPendingBankApplyId(pendingBankMap.get(item.getId()));
                // 手机号脱敏处理：中间4位替换为****
                if (item.getMobile() != null && item.getMobile().length() >= 11) {
                    String phone = item.getMobile();
                    item.setMobile(phone.substring(0, 3) + "****" + phone.substring(7));
                }
            });
        }
        return success(respVOPage);
    }

    @PostMapping("/approve-2fa-unbind")
    @Operation(summary = "审核2FA解绑请求")
    @PreAuthorize("@ss.hasPermission('user:info:update')")
    public CommonResult<Boolean> approve2FAUnbind(
            @RequestBody AuditF2AReqVO reqVO) {
        infoService.approve2FAUnbind(reqVO);

        // 审核通过，创建用户链地址
        if (reqVO.isApproved()) {
            // 如果用户有链地址，则进行签发
            noticeMessageSendService.sendSingleMessageAsync(reqVO.getId(),
                    NoticeTemplateEnum.F2A_UNBIND_APPROVED.getCode(), null);

        } else {
            Map<String, Object> map = new HashMap<>();
            map.put(PARAM_REASON, reqVO.getRemark());
            noticeMessageSendService.sendSingleMessageAsync(reqVO.getId(),
                    NoticeTemplateEnum.F2A_UNBIND_REJECTED.getCode(), map);
        }
        return success(true);
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出用户基础信息 Excel")
    @PreAuthorize("@ss.hasPermission('user:info:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportInfoExcel(@Valid UserInfoPageReqVO pageReqVO,
            HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<UserInfoDO> list = infoService.getInfoPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "用户基础信息.xls", "数据", UserInfoRespVO.class,
                BeanUtils.toBean(list, UserInfoRespVO.class));
    }

    @GetMapping("/export-purchase-excel")
    @Operation(summary = "导出用户购买信息 Excel")
    @PreAuthorize("@ss.hasPermission('user:info:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportUserPurchaseExcel(@Valid UserInfoPageReqVO pageReqVO,
                                        @RequestParam(value = "userIds", required = false) String userIds,
                                        HttpServletResponse response) throws IOException {
        List<Long> ids = new ArrayList<>();
        if (userIds != null && !userIds.trim().isEmpty()) {
            String[] parts = userIds.split(",");
            for (String part : parts) {
                if (part == null) {
                    continue;
                }
                String trimmed = part.trim();
                if (trimmed.isEmpty()) {
                    continue;
                }
                ids.add(Long.valueOf(trimmed));
            }
        }
        userPurchaseExportService.exportUserPurchaseExcel(pageReqVO, ids, response);
    }

}
