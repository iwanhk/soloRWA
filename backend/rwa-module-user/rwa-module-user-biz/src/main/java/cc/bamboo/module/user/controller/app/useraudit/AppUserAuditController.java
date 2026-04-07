package cc.bamboo.module.user.controller.app.useraudit;

import cc.bamboo.framework.common.pojo.CommonResult;
import cc.bamboo.module.user.controller.app.useraudit.vo.AppOcrIdCardReqVO;
import cc.bamboo.module.user.controller.app.useraudit.vo.AppOcrIdCardRespVO;
import cc.bamboo.module.user.controller.app.useraudit.vo.AppUserAuditDetailRespVO;
import cc.bamboo.module.user.controller.app.useraudit.vo.AppUserAuditSubmitReqVO;
import cc.bamboo.module.user.service.useraudit.AppUserAuditService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.validation.Valid;

import java.io.IOException;

import static cc.bamboo.framework.common.pojo.CommonResult.success;
import static cc.bamboo.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

/**
 * 用户 APP - 用户认证
 *
 * @author Kiro
 */
@Tag(name = "用户 APP - 用户认证")
@RestController
@RequestMapping("/user/user-audit")
@Validated
@Slf4j
public class AppUserAuditController {

    @Resource
    private AppUserAuditService appUserAuditService;

    @PostMapping("/ocr-idcard")
    @Operation(summary = "OCR识别证件信息")
    public CommonResult<AppOcrIdCardRespVO> ocrIdCard(@Valid AppOcrIdCardReqVO reqVO) throws Exception {
        return success(appUserAuditService.ocrIdCard(reqVO));
    }

    @PostMapping("/submit")
    @Operation(summary = "提交用户认证")
    public CommonResult<Long> submitAudit(@Valid @RequestBody AppUserAuditSubmitReqVO reqVO) throws IOException {
        Long auditId = appUserAuditService.submitAudit(getLoginUserId(), reqVO);
        return success(auditId);
    }

    @GetMapping("/detail")
    @Operation(summary = "获取用户最新认证详情")
    public CommonResult<AppUserAuditDetailRespVO> getLatestAuditDetail() {
        return success(appUserAuditService.getLatestAuditDetail(getLoginUserId()));
    }

     @PostMapping("/upload-file")
     @Operation(summary = "上传文件")
     public CommonResult<String> uploadFile(@RequestParam("file") MultipartFile file)  throws IOException{
         return success(appUserAuditService.uploadFile(file));
     }

}
