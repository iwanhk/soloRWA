package cc.bamboo.module.user.service.userchain;

/**
 * 链操作任务服务接口
 * 负责创建链操作任务并发送消息到 chain 模块
 *
 * @author Swolf
 */
public interface ChainOperationTaskService {

    /**
     * 创建用户identity
     *

     * @param userId 用户ID
     * @param userChainId 用户ID
     * @param userAddress 用户钱包地址
     * @param countryCode 国家代码
     * @return 任务编号
     */
    String createAddUserTask(Long userId, Long userChainId, String userAddress, Integer countryCode);

    /**
     * 新加地址
     * @param userId 用户ID
     * @param userChainId 用户ID
     * @param userAddress 用户钱包地址
     * @return 任务编号
     */
    String createLinkWalletTask(Long userId, Long userChainId, String userAddress);

    /**
     * 发送签发消息
     *
     * @param userId
     * @author: Hus
     * @date: 2026/1/9 17:07
     * @return: String
     * @description
     */
    String claimTask(Long userId);

    /**
     * 删除地址
     *
     * @param userId
     * @param userChainId
     * @param address
     * @author: Hus
     * @date: 2026/1/9 17:11
     * @return: String
     * @description
     */
    String deleteAddress(Long userId, Long userChainId,String address);
}
