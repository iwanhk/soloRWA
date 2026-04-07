package cc.bamboo.module.project.service.projectbill;

import javax.validation.*;
import cc.bamboo.module.project.controller.admin.projectbill.vo.*;
import cc.bamboo.module.project.controller.app.projectorder.vo.AppProjectBillReqVO;
import cc.bamboo.module.project.controller.app.projectorder.vo.AppProjectBillRespVO;
import cc.bamboo.module.project.dal.dataobject.projectbill.ProjectBillDO;
import cc.bamboo.framework.common.pojo.PageResult;

/**
 * 项目账单管理表 Service 接口
 *
 * @author Swolf
 */
public interface ProjectBillService {

    /**
     * 创建项目账单管理表
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createBill(@Valid ProjectBillSaveReqVO createReqVO);

    /**
     * 更新项目账单管理表
     *
     * @param updateReqVO 更新信息
     */
    void updateBill(@Valid ProjectBillSaveReqVO updateReqVO);

    /**
     * 删除项目账单管理表
     *
     * @param id 编号
     */
    void deleteBill(Long id);

    /**
     * 获得项目账单管理表
     *
     * @param id 编号
     * @return 项目账单管理表
     */
    ProjectBillDO getBill(Long id);

    /**
     * 获得项目账单管理表分页
     *
     * @param pageReqVO 分页查询
     * @return 项目账单管理表分页
     */
    PageResult<ProjectBillDO> getBillPage(ProjectBillPageReqVO pageReqVO);

    /**
     * 获得账单分页(带用户名称)
     *
     * @param pageReqVO 分页查询
     * @return 账单分页
     */
    PageResult<ProjectBillRespVO> getBillPageWithUserName(ProjectBillPageReqVO pageReqVO);

    /**
     * 获得账单详情(带用户名称)
     *
     * @param id 编号
     * @return 账单详情
     */
    ProjectBillRespVO getBillDetail(Long id);

    /**
     * 审核账单
     *
     * @param auditReqVO    审核信息
     * @param auditUserId   审核人ID
     * @param auditUserName 审核人姓名
     */
    void auditBill(@Valid ProjectBillAuditReqVO auditReqVO, Long auditUserId, String auditUserName);

    /**
     * 上传支付凭证
     *
     * @param uploadReqVO 上传信息
     */
    void uploadPayVoucher(@Valid ProjectBillUploadVoucherReqVO uploadReqVO);

    /**
     * 获得账单统计
     *
     * @param reqVO 统计查询
     * @return 统计结果
     */
    ProjectBillStatisticsRespVO getBillStatistics(ProjectBillStatisticsReqVO reqVO);

    /**
     * 获得用户项目账单分页
     *
     * @param pageReqVO 分页查询
     * @return 用户项目账单分页
     */
    PageResult<cc.bamboo.module.project.controller.app.projectorder.vo.AppProjectBillRespVO> getAppBillPage(
            AppProjectBillReqVO pageReqVO);

    /**
     * 获取用户账单状态统计
     *
     * @param userId 用户ID
     * @return 账单状态统计
     */
    cc.bamboo.module.project.controller.app.projectorder.vo.AppBillStatusCountRespVO getBillStatusCount(Long userId);
}
