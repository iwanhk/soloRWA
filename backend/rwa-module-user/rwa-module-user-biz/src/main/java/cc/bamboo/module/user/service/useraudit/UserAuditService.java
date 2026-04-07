package cc.bamboo.module.user.service.useraudit;

import java.util.*;
import javax.validation.*;
import cc.bamboo.module.user.controller.admin.useraudit.vo.*;
import cc.bamboo.module.user.dal.dataobject.useraudit.UserAuditDO;
import cc.bamboo.framework.common.pojo.PageResult;
import cc.bamboo.framework.common.pojo.PageParam;

/**
 * 用户投资者认证审核 Service 接口
 *
 * @author Swolf
 */
public interface UserAuditService {

    /**
     * 创建用户投资者认证审核
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createAudit(@Valid UserAuditSaveReqVO createReqVO);

    /**
     * 更新用户投资者认证审核
     *
     * @param updateReqVO 更新信息
     */
    void updateAudit(@Valid UserAuditSaveReqVO updateReqVO);

    /**
     * 删除用户投资者认证审核
     *
     * @param id 编号
     */
    void deleteAudit(Long id);

    /**
     * 获得用户投资者认证审核
     *
     * @param id 编号
     * @return 用户投资者认证审核
     */
    UserAuditDO getAudit(Long id);

    /**
     * 获得用户投资者认证审核分页
     *
     * @param pageReqVO 分页查询
     * @return 用户投资者认证审核分页
     */
    PageResult<UserAuditDO> getAuditPage(UserAuditPageReqVO pageReqVO);

    /**
     * 审核用户认证
     *
     * @param reviewReqVO 审核信息
     */
    Long reviewAudit(@Valid UserAuditReviewReqVO reviewReqVO);

}