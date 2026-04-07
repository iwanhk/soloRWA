package cc.bamboo.module.chain.dal.mysql.blockchainaddresses;

import java.util.*;

import cc.bamboo.framework.common.pojo.PageResult;
import cc.bamboo.framework.mybatis.core.query.LambdaQueryWrapperX;
import cc.bamboo.framework.mybatis.core.mapper.BaseMapperX;
import cc.bamboo.module.chain.dal.dataobject.blockchainaddresses.BlockchainAddressesDO;
import org.apache.ibatis.annotations.Mapper;
import cc.bamboo.module.chain.controller.admin.blockchainaddresses.vo.*;

/**
 * 区块链地址 Mapper
 *
 * @author Swolf
 */
@Mapper
public interface BlockchainAddressesMapper extends BaseMapperX<BlockchainAddressesDO> {

    default PageResult<BlockchainAddressesDO> selectPage(BlockchainAddressesPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<BlockchainAddressesDO>()
                .likeIfPresent(BlockchainAddressesDO::getName, reqVO.getName())
                .eqIfPresent(BlockchainAddressesDO::getAddress, reqVO.getAddress())
                .eqIfPresent(BlockchainAddressesDO::getPrivateKey, reqVO.getPrivateKey())
                .eqIfPresent(BlockchainAddressesDO::getDescription, reqVO.getDescription())
                .betweenIfPresent(BlockchainAddressesDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(BlockchainAddressesDO::getId));
    }

}