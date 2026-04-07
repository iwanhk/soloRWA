package cc.bamboo.module.user.service.userbank;

import java.util.*;
import javax.validation.*;
import cc.bamboo.module.user.controller.admin.userbank.vo.*;
import cc.bamboo.module.user.controller.app.userinfo.vo.AppUserBankUpdateReqVO;
import cc.bamboo.module.user.dal.dataobject.userbank.UserBankDO;
import cc.bamboo.framework.common.pojo.PageResult;
import cc.bamboo.framework.common.pojo.PageParam;

/**
 * 用户银行卡信息 Service 接口
 *
 * @author Swolf
 */
public interface UserBankService {

    /**
     * 创建用户银行卡信息
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createBank(@Valid UserBankSaveReqVO createReqVO);

    /**
     * 更新用户银行卡信息
     *
     * @param updateReqVO 更新信息
     */
    void updateBank(@Valid UserBankSaveReqVO updateReqVO);

    /**
     * 删除用户银行卡信息
     *
     * @param id 编号
     */
    void deleteBank(Long id);

    /**
     * 获得用户银行卡信息
     *
     * @param id 编号
     * @return 用户银行卡信息
     */
    UserBankDO getBank(Long id);

    /**
     * 获得用户银行卡信息分页
     *
     * @param pageReqVO 分页查询
     * @return 用户银行卡信息分页
     */
    PageResult<UserBankDO> getBankPage(UserBankPageReqVO pageReqVO);

    /**
     * 更新用户银行卡信息（App端）
     * 更新后状态变为审核中
     *
     * @param userId 用户ID
     * @param reqVO  更新信息
     */
    void updateBankInfo(Long userId, @Valid AppUserBankUpdateReqVO reqVO);

    /**
     * 审核用户银行卡
     *
     * @param auditReqVO 审核信息
     */
    Long auditBank(@Valid UserBankAuditReqVO auditReqVO);

    /**
     * 获取用户待审核的银行卡申请
     *
     * @param userIds 用户ID列表
     * @return 用户ID -> 银行卡ID
     */
    Map<Long, Long> getPendingBankApplyMap(Collection<Long> userIds);

}