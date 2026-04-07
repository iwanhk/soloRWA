package cc.bamboo.module.chain.service.chain;

import java.util.*;
import javax.validation.*;
import cc.bamboo.module.chain.controller.admin.chain.vo.*;
import cc.bamboo.module.chain.dal.dataobject.chain.ChainDO;
import cc.bamboo.framework.common.pojo.PageResult;

/**
 * 区块链信息 Service 接口
 *
 * @author Trae
 */
public interface ChainService {

    /**
     * 创建区块链信息
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createChain(@Valid ChainSaveReqVO createReqVO);

    /**
     * 更新区块链信息
     *
     * @param updateReqVO 更新信息
     */
    void updateChain(@Valid ChainSaveReqVO updateReqVO);

    /**
     * 删除区块链信息
     *
     * @param id 编号
     */
    void deleteChain(Long id);

    /**
     * 获得区块链信息
     *
     * @param id 编号
     * @return 区块链信息
     */
    ChainDO getChain(Long id);

    /**
     * 获得区块链信息分页
     *
     * @param pageReqVO 分页查询
     * @return 区块链信息分页
     */
    PageResult<ChainDO> getChainPage(ChainPageReqVO pageReqVO);

    /**
     * 获得开启状态的区块链列表
     *
     * @return 区块链列表
     */
    List<ChainDO> getActiveChainList();

}
