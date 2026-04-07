package cc.bamboo.module.chain.service.blockchainaddresses;

import java.util.*;
import javax.validation.*;
import cc.bamboo.module.chain.controller.admin.blockchainaddresses.vo.*;
import cc.bamboo.module.chain.dal.dataobject.blockchainaddresses.BlockchainAddressesDO;
import cc.bamboo.framework.common.pojo.PageResult;
import cc.bamboo.framework.common.pojo.PageParam;

/**
 * 区块链地址 Service 接口
 *
 * @author Swolf
 */
public interface BlockchainAddressesService {

    /**
     * 创建区块链地址
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createBlockchainAddresses(@Valid BlockchainAddressesSaveReqVO createReqVO);

    /**
     * 更新区块链地址
     *
     * @param updateReqVO 更新信息
     */
    void updateBlockchainAddresses(@Valid BlockchainAddressesSaveReqVO updateReqVO);

    /**
     * 删除区块链地址
     *
     * @param id 编号
     */
    void deleteBlockchainAddresses(Long id);

    /**
     * 获得区块链地址
     *
     * @param id 编号
     * @return 区块链地址
     */
    BlockchainAddressesDO getBlockchainAddresses(Long id);

    /**
     * 获得区块链地址分页
     *
     * @param pageReqVO 分页查询
     * @return 区块链地址分页
     */
    PageResult<BlockchainAddressesDO> getBlockchainAddressesPage(BlockchainAddressesPageReqVO pageReqVO);

}