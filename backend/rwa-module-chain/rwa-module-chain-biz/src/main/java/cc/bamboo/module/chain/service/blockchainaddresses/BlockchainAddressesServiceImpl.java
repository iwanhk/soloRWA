package cc.bamboo.module.chain.service.blockchainaddresses;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import cc.bamboo.module.chain.controller.admin.blockchainaddresses.vo.*;
import cc.bamboo.module.chain.dal.dataobject.blockchainaddresses.BlockchainAddressesDO;
import cc.bamboo.framework.common.pojo.PageResult;
import cc.bamboo.framework.common.pojo.PageParam;
import cc.bamboo.framework.common.util.object.BeanUtils;

import cc.bamboo.module.chain.dal.mysql.blockchainaddresses.BlockchainAddressesMapper;

import static cc.bamboo.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cc.bamboo.module.chain.enums.ErrorCodeConstants.*;

/**
 * 区块链地址 Service 实现类
 *
 * @author Swolf
 */
@Service
@Validated
public class BlockchainAddressesServiceImpl implements BlockchainAddressesService {

    @Resource
    private BlockchainAddressesMapper blockchainAddressesMapper;

    @Override
    public Long createBlockchainAddresses(BlockchainAddressesSaveReqVO createReqVO) {
        // 插入
        BlockchainAddressesDO blockchainAddresses = BeanUtils.toBean(createReqVO, BlockchainAddressesDO.class);
        blockchainAddressesMapper.insert(blockchainAddresses);
        // 返回
        return blockchainAddresses.getId();
    }

    @Override
    public void updateBlockchainAddresses(BlockchainAddressesSaveReqVO updateReqVO) {
        // 校验存在
        validateBlockchainAddressesExists(updateReqVO.getId());
        // 更新
        BlockchainAddressesDO updateObj = BeanUtils.toBean(updateReqVO, BlockchainAddressesDO.class);
        blockchainAddressesMapper.updateById(updateObj);
    }

    @Override
    public void deleteBlockchainAddresses(Long id) {
        // 校验存在
        validateBlockchainAddressesExists(id);
        // 删除
        blockchainAddressesMapper.deleteById(id);
    }

    private void validateBlockchainAddressesExists(Long id) {
        if (blockchainAddressesMapper.selectById(id) == null) {
            throw exception(BLOCKCHAIN_ADDRESSES_NOT_EXISTS);
        }
    }

    @Override
    public BlockchainAddressesDO getBlockchainAddresses(Long id) {
        return blockchainAddressesMapper.selectById(id);
    }

    @Override
    public PageResult<BlockchainAddressesDO> getBlockchainAddressesPage(BlockchainAddressesPageReqVO pageReqVO) {
        return blockchainAddressesMapper.selectPage(pageReqVO);
    }

}