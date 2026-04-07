package cc.bamboo.module.chain.service.contractdeployments;

import java.util.*;
import javax.validation.*;
import cc.bamboo.module.chain.controller.admin.contractdeployments.vo.*;
import cc.bamboo.module.chain.dal.dataobject.contractdeployments.ContractDeploymentsDO;
import cc.bamboo.framework.common.pojo.PageResult;
import cc.bamboo.framework.common.pojo.PageParam;

/**
 * 合约部署 Service 接口
 *
 * @author Swolf
 */
public interface ContractDeploymentsService {

    /**
     * 创建合约部署
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createContractDeployments(@Valid ContractDeploymentsSaveReqVO createReqVO);

    /**
     * 更新合约部署
     *
     * @param updateReqVO 更新信息
     */
    void updateContractDeployments(@Valid ContractDeploymentsSaveReqVO updateReqVO);

    /**
     * 删除合约部署
     *
     * @param id 编号
     */
    void deleteContractDeployments(Long id);

    /**
     * 获得合约部署
     *
     * @param id 编号
     * @return 合约部署
     */
    ContractDeploymentsDO getContractDeployments(Long id);

    /**
     * 获得合约部署分页
     *
     * @param pageReqVO 分页查询
     * @return 合约部署分页
     */
    PageResult<ContractDeploymentsDO> getContractDeploymentsPage(ContractDeploymentsPageReqVO pageReqVO);

}