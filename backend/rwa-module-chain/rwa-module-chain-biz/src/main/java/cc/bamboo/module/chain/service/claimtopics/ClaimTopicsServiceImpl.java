package cc.bamboo.module.chain.service.claimtopics;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import cc.bamboo.module.chain.controller.admin.claimtopics.vo.*;
import cc.bamboo.module.chain.dal.dataobject.claimtopics.ClaimTopicsDO;
import cc.bamboo.framework.common.pojo.PageResult;
import cc.bamboo.framework.common.pojo.PageParam;
import cc.bamboo.framework.common.util.object.BeanUtils;

import cc.bamboo.module.chain.dal.mysql.claimtopics.ClaimTopicsMapper;

import static cc.bamboo.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cc.bamboo.module.chain.enums.ErrorCodeConstants.*;

/**
 * 声明主题 Service 实现类
 *
 * @author Swolf
 */
@Service
@Validated
public class ClaimTopicsServiceImpl implements ClaimTopicsService {

    @Resource
    private ClaimTopicsMapper claimTopicsMapper;

    @Override
    public Long createClaimTopics(ClaimTopicsSaveReqVO createReqVO) {
        // 插入
        ClaimTopicsDO claimTopics = BeanUtils.toBean(createReqVO, ClaimTopicsDO.class);
        claimTopicsMapper.insert(claimTopics);
        // 返回
        return claimTopics.getId();
    }

    @Override
    public void updateClaimTopics(ClaimTopicsSaveReqVO updateReqVO) {
        // 校验存在
        validateClaimTopicsExists(updateReqVO.getId());
        // 更新
        ClaimTopicsDO updateObj = BeanUtils.toBean(updateReqVO, ClaimTopicsDO.class);
        claimTopicsMapper.updateById(updateObj);
    }

    @Override
    public void deleteClaimTopics(Long id) {
        // 校验存在
        validateClaimTopicsExists(id);
        // 删除
        claimTopicsMapper.deleteById(id);
    }

    private void validateClaimTopicsExists(Long id) {
        if (claimTopicsMapper.selectById(id) == null) {
            throw exception(CLAIM_TOPICS_NOT_EXISTS);
        }
    }

    @Override
    public ClaimTopicsDO getClaimTopics(Long id) {
        return claimTopicsMapper.selectById(id);
    }

    @Override
    public PageResult<ClaimTopicsDO> getClaimTopicsPage(ClaimTopicsPageReqVO pageReqVO) {
        return claimTopicsMapper.selectPage(pageReqVO);
    }

}