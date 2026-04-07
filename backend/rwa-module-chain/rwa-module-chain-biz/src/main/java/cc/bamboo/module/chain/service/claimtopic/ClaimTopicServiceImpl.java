package cc.bamboo.module.chain.service.claimtopic;

import cc.bamboo.module.chain.dal.dataobject.claimtopics.ClaimTopicsDO;
import cc.bamboo.module.chain.dal.mysql.claimtopics.ClaimTopicsMapper;
import cc.bamboo.module.chain.util.AbiEncoderUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * ClaimTopic 服务实现
 * 
 * @author Swolf
 */
@Service
@Slf4j
public class ClaimTopicServiceImpl implements ClaimTopicService {
    
    @Resource
    private ClaimTopicsMapper claimTopicsMapper;
    
    @Override
    public ClaimTopicsDO createClaimTopic(String name, String value) {
        // 计算 topic 哈希
        String topic = AbiEncoderUtil.keccak256(value);
        
        // 检查是否已存在
        ClaimTopicsDO existing = claimTopicsMapper.selectOne(ClaimTopicsDO::getTopic, topic);
        if (existing != null) {
            throw new RuntimeException("ClaimTopic 已存在，topic: " + topic);
        }
        
        ClaimTopicsDO claimTopic = new ClaimTopicsDO();
        claimTopic.setName(name);
        claimTopic.setValue(value);
        claimTopic.setTopic(topic);
        claimTopicsMapper.insert(claimTopic);
        
        log.info("创建 ClaimTopic，name: {}, value: {}, topic: {}", name, value, topic);
        
        return claimTopic;
    }
    
    @Override
    public List<ClaimTopicsDO> getAllClaimTopics() {
        return claimTopicsMapper.selectList();
    }
    
    @Override
    public ClaimTopicsDO getClaimTopicById(Long id) {
        return claimTopicsMapper.selectById(id);
    }
    
    @Override
    public ClaimTopicsDO getClaimTopicByTopic(String topic) {
        return claimTopicsMapper.selectOne(ClaimTopicsDO::getTopic, topic);
    }
}
