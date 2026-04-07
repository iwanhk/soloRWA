package cc.bamboo.module.chain.service.identity;

import cc.bamboo.module.chain.service.identity.dto.*;

/**
 * 身份管理服务接口
 * 负责 ClaimIssuer 和 User 的身份管理
 * 
 * @author Swolf
 */
public interface IdentityService {
    
    /**
     * 添加 ClaimIssuer
     * 
     * @param address ClaimIssuer 钱包地址
     * @return 操作结果
     */
    IdentityOperationRespDTO addClaimIssuer(String address);
    
    /**
     * 添加用户
     * 
     * @param address 用户钱包地址
     * @param tokenId 要关联的 Token ID（可选）
     * @param countryCode 国家代码（可选）
     * @return 操作结果
     */
    IdentityOperationRespDTO addUser(Long userId,String address, Long tokenId, Integer countryCode);
    
    /**
     * 关联新钱包到现有身份
     * 
     * @param newWalletAddress 新钱包地址
     * @param oldWalletAddress 旧钱包地址
     * @return 关联结果
     */
    LinkWalletRespDTO linkWallet(String newWalletAddress, String oldWalletAddress);

    /**
     * 绑定钱包地址到现有身份
     *
     * @param newWalletAddress
     * @param userId
     * @author: Hus
     * @date: 2026/1/9 14:48
     * @return: LinkNewWalletRespDTO
     * @description
     */
    LinkNewWalletRespDTO linkWallet(String newWalletAddress, Long userId);


    /**
     *
     *
     * @param walletAddress
     * @author: Hus
     * @date: 2026/1/9 15:54
     * @return: DeleteWalletRespDTO
     * @description
     */
    DeleteWalletRespDTO deleteWallet(String walletAddress);
    /**
     * 签发 Claim
     * 
     * @param reqDTO 签发请求
     * @return 签发结果
     */
    IssueClaimRespDTO issueClaim(IssueClaimReqDTO reqDTO);

     /**
     * 更新用户 Identity 的topicIds
     *
     * @param userId 用户ID
     * @param topicId 新的topicId
     */
    void updateUserIdentityTopicIds(Long userId, Long topicId);
}
