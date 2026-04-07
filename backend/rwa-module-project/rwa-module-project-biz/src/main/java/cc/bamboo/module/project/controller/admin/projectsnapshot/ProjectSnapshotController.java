package cc.bamboo.module.project.controller.admin.projectsnapshot;

import cc.bamboo.framework.common.pojo.CommonResult;
import cc.bamboo.framework.common.pojo.PageResult;
import cc.bamboo.framework.common.util.object.BeanUtils;
import cc.bamboo.module.project.controller.admin.projectsnapshot.vo.ProjectSnapshotPageReqVO;
import cc.bamboo.module.project.controller.admin.projectsnapshot.vo.ProjectSnapshotRespVO;
import cc.bamboo.module.project.dal.dataobject.projectsnapshot.ProjectSnapshotDO;
import cc.bamboo.module.project.service.projectsnapshot.ProjectSnapshotService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

import static cc.bamboo.framework.common.pojo.CommonResult.success;

/**
 * 项目快照 Controller
 *
 * @author Swolf
 */
@Tag(name = "管理后台 - 项目快照")
@RestController
@RequestMapping("/project/snapshot")
@Validated
public class ProjectSnapshotController {

    @Resource
    private ProjectSnapshotService snapshotService;

    @GetMapping("/latest")
    @Operation(summary = "获取项目最新快照")
    @Parameter(name = "projectId", description = "项目ID", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('project:info:query')")
    public CommonResult<ProjectSnapshotRespVO> getLatestSnapshot(@RequestParam("projectId") Long projectId) {
        ProjectSnapshotDO snapshot = snapshotService.getLatestSnapshot(projectId);
        return success(BeanUtils.toBean(snapshot, ProjectSnapshotRespVO.class));
    }

    @GetMapping("/get")
    @Operation(summary = "获取快照详情")
    @Parameter(name = "id", description = "快照ID", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('project:info:query')")
    public CommonResult<ProjectSnapshotRespVO> getSnapshot(@RequestParam("id") Long id) {
        ProjectSnapshotDO snapshot = snapshotService.getSnapshot(id);
        return success(BeanUtils.toBean(snapshot, ProjectSnapshotRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询项目快照")
    @PreAuthorize("@ss.hasPermission('project:info:query')")
    public CommonResult<PageResult<ProjectSnapshotRespVO>> getSnapshotPage(@Valid ProjectSnapshotPageReqVO pageReqVO) {
        PageResult<ProjectSnapshotDO> pageResult = snapshotService.getSnapshotPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, ProjectSnapshotRespVO.class));
    }

}
