package cc.bamboo.module.chain.service.contractdeployments;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import cc.bamboo.module.chain.controller.admin.contractdeployments.vo.*;
import cc.bamboo.module.chain.dal.dataobject.contractdeployments.ContractDeploymentsDO;
import cc.bamboo.framework.common.pojo.PageResult;
import cc.bamboo.framework.common.pojo.PageParam;
import cc.bamboo.framework.common.util.object.BeanUtils;

import cc.bamboo.module.chain.dal.mysql.contractdeployments.ContractDeploymentsMapper;

import static cc.bamboo.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cc.bamboo.module.chain.enums.ErrorCodeConstants.*;

/**
 * 合约部署 Service 实现类
 *
 * @author Swolf
 */
@Service
@Validated
public class ContractDeploymentsServiceImpl implements ContractDeploymentsService {

    @Resource
    private ContractDeploymentsMapper contractDeploymentsMapper;

    @Override
    public Long createContractDeployments(ContractDeploymentsSaveReqVO createReqVO) {
        // 插入
        ContractDeploymentsDO contractDeployments = BeanUtils.toBean(createReqVO, ContractDeploymentsDO.class);
        contractDeploymentsMapper.insert(contractDeployments);
        // 返回
        return contractDeployments.getId();
    }

    @Override
    public void updateContractDeployments(ContractDeploymentsSaveReqVO updateReqVO) {
        // 校验存在
        validateContractDeploymentsExists(updateReqVO.getId());
        // 更新
        ContractDeploymentsDO updateObj = BeanUtils.toBean(updateReqVO, ContractDeploymentsDO.class);
        contractDeploymentsMapper.updateById(updateObj);
    }

    @Override
    public void deleteContractDeployments(Long id) {
        // 校验存在
        validateContractDeploymentsExists(id);
        // 删除
        contractDeploymentsMapper.deleteById(id);
    }

    private void validateContractDeploymentsExists(Long id) {
        if (contractDeploymentsMapper.selectById(id) == null) {
            throw exception(CONTRACT_DEPLOYMENTS_NOT_EXISTS);
        }
    }

    @Override
    public ContractDeploymentsDO getContractDeployments(Long id) {
        return contractDeploymentsMapper.selectById(id);
    }

    @Override
    public PageResult<ContractDeploymentsDO> getContractDeploymentsPage(ContractDeploymentsPageReqVO pageReqVO) {
        return contractDeploymentsMapper.selectPage(pageReqVO);
    }

}