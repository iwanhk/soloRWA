package cc.bamboo.module.project.api.projectinfo;

import cc.bamboo.framework.common.pojo.CommonResult;
import cc.bamboo.module.project.dal.mysql.projectinfo.ProjectInfoMapper;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

import static cc.bamboo.framework.common.pojo.CommonResult.success;

@RestController
@Validated
public class ProjectInfoApiImpl implements ProjectInfoApi {

    @Resource
    private ProjectInfoMapper projectInfoMapper;

    @Override
    public CommonResult<Boolean> updateProjectChainInfo(Long projectId, Integer chainStatus, Long chainTokensId, String chainTokenAddress) {
        projectInfoMapper.updateChainInfo(projectId, chainStatus, chainTokensId, chainTokenAddress);
        return success(true);
    }
}

