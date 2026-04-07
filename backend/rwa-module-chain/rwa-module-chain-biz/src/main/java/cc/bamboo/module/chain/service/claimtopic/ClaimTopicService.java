package cc.bamboo.module.chain.service.claimtopic;

import cc.bamboo.module.chain.dal.dataobject.claimtopics.ClaimTopicsDO;

import java.util.List;

/**
 * ClaimTopic 服务接口
 * 
 * @author Swolf
 */
public interface ClaimTopicService {
    
    /**
     * 创建 ClaimTopic
     * 
     * @param name 主题名称
     * @param value 主题值
     * @return 创建的 ClaimTopic
     */
    ClaimTopicsDO createClaimTopic(String name, String value);
    
    /**
     * 获取所有 ClaimTopic
     * 
     * @return ClaimTopic 列表
     */
    List<ClaimTopicsDO> getAllClaimTopics();
    
    /**
     * 根据 ID 获取 ClaimTopic
     * 
     * @param id ID
     * @return ClaimTopic
     */
    ClaimTopicsDO getClaimTopicById(Long id);
    
    /**
     * 根据 topic 哈希获取 ClaimTopic
     * 
     * @param topic topic 哈希
     * @return ClaimTopic
     */
    ClaimTopicsDO getClaimTopicByTopic(String topic);
}
