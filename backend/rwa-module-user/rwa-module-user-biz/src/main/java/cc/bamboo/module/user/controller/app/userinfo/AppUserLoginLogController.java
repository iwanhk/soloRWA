package cc.bamboo.module.user.controller.app.userinfo;

import cc.bamboo.framework.common.pojo.CommonResult;
import cc.bamboo.framework.common.pojo.PageResult;
import cc.bamboo.framework.common.util.object.BeanUtils;
import cc.bamboo.module.user.controller.admin.userloginlog.vo.UserLoginLogPageReqVO;
import cc.bamboo.module.user.controller.app.userinfo.vo.AppUserLoginLogPageReqVO;
import cc.bamboo.module.user.controller.app.userinfo.vo.AppUserLoginLogRespVO;
import cc.bamboo.module.user.dal.dataobject.userloginlog.UserLoginLogDO;
import cc.bamboo.module.user.service.userloginlog.UserLoginLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

import static cc.bamboo.framework.common.pojo.CommonResult.success;
import static cc.bamboo.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

@Tag(name = "用户 APP - 登录日志")
@RestController
@RequestMapping("/user/login-log")
@Validated
public class AppUserLoginLogController {

    @Resource
    private UserLoginLogService userLoginLogService;

    @GetMapping("/page")
    @Operation(summary = "获得登录日志分页")
    public CommonResult<PageResult<AppUserLoginLogRespVO>> getLoginLogPage(@Valid AppUserLoginLogPageReqVO pageReqVO) {
        UserLoginLogPageReqVO reqVO = BeanUtils.toBean(pageReqVO, UserLoginLogPageReqVO.class);
        reqVO.setUserId(getLoginUserId());
        PageResult<UserLoginLogDO> pageResult = userLoginLogService.getLoginLogPage(reqVO);
        return success(BeanUtils.toBean(pageResult, AppUserLoginLogRespVO.class));
    }
}
