package cc.bamboo.module.chain.service.claimtopics;

import java.util.*;
import javax.validation.*;
import cc.bamboo.module.chain.controller.admin.claimtopics.vo.*;
import cc.bamboo.module.chain.dal.dataobject.claimtopics.ClaimTopicsDO;
import cc.bamboo.framework.common.pojo.PageResult;
import cc.bamboo.framework.common.pojo.PageParam;

/**
 * 声明主题 Service 接口
 *
 * @author Swolf
 */
public interface ClaimTopicsService {

    /**
     * 创建声明主题
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createClaimTopics(@Valid ClaimTopicsSaveReqVO createReqVO);

    /**
     * 更新声明主题
     *
     * @param updateReqVO 更新信息
     */
    void updateClaimTopics(@Valid ClaimTopicsSaveReqVO updateReqVO);

    /**
     * 删除声明主题
     *
     * @param id 编号
     */
    void deleteClaimTopics(Long id);

    /**
     * 获得声明主题
     *
     * @param id 编号
     * @return 声明主题
     */
    ClaimTopicsDO getClaimTopics(Long id);

    /**
     * 获得声明主题分页
     *
     * @param pageReqVO 分页查询
     * @return 声明主题分页
     */
    PageResult<ClaimTopicsDO> getClaimTopicsPage(ClaimTopicsPageReqVO pageReqVO);

}