package cc.bamboo.module.project.service.chainoperation;

/**
 * 链操作任务服务接口
 * 负责创建链操作任务并发送消息到 chain 模块
 *
 * @author Swolf
 */
public interface ChainOperationTaskService {

    /**
     * 创建项目部署 Token 任务并发送消息
     *
     * @param projectId 项目ID
     * @param projectName 项目名称
     * @param projectSymbol 项目符号
     * @return 任务编号
     */
    String createDeployTokenTask(Long projectId, String projectName, String projectSymbol);

    /**
     * 创建用户下单任务并发送消息
     *
     * @param orderId 订单ID
     * @param orderNo 订单号
     * @param projectId 项目ID
     * @param projectName 项目名称
     * @param tokenAddress Token 地址
     * @param userId 用户ID
     * @param userAddress 用户钱包地址
     * @param countryCode 国家代码
     * @return 任务编号
     */
    String createAddUserTask(Long orderId, String orderNo, Long projectId, String projectName, 
                            String tokenAddress, Long userId, String userAddress, Integer countryCode);

    /**
     * 创建订单审核通过任务并发送消息
     *
     * @param orderId 订单ID
     * @param orderNo 订单号
     * @param projectId 项目ID
     * @param projectName 项目名称
     * @param tokenAddress Token 地址
     * @param userId 用户ID
     * @param userAddress 用户钱包地址
     * @param mintAmount 铸造数量
     * @return 任务编号
     */
    String createMintTokenTask(Long orderId, String orderNo, Long projectId, String projectName,
                              String tokenAddress, Long userId, String userAddress, String mintAmount);
}
