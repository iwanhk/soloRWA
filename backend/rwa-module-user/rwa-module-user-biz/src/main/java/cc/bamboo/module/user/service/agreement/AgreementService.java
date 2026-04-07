package cc.bamboo.module.user.service.agreement;

import java.util.*;
import javax.validation.*;
import cc.bamboo.module.user.controller.admin.agreement.vo.*;
import cc.bamboo.module.user.controller.app.userinfo.vo.AppAgreementListRespVO;
import cc.bamboo.module.user.controller.app.userinfo.vo.AppAgreementRespVO;
import cc.bamboo.module.user.dal.dataobject.agreement.AgreementDO;
import cc.bamboo.framework.common.pojo.PageResult;
import cc.bamboo.framework.common.pojo.PageParam;

/**
 * 系统协议表 Service 接口
 *
 * @author Swolf
 */
public interface AgreementService {

    /**
     * 创建系统协议表
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createAgreement(@Valid AgreementSaveReqVO createReqVO);

    /**
     * 更新系统协议表
     *
     * @param updateReqVO 更新信息
     */
    void updateAgreement(@Valid AgreementSaveReqVO updateReqVO);

    /**
     * 删除系统协议表
     *
     * @param id 编号
     */
    void deleteAgreement(Long id);

    /**
     * 获得系统协议表
     *
     * @param id 编号
     * @return 系统协议表
     */
    AgreementDO getAgreement(Long id);

    /**
     * 获得系统协议表分页
     *
     * @param pageReqVO 分页查询
     * @return 系统协议表分页
     */
    PageResult<AgreementDO> getAgreementPage(AgreementPageReqVO pageReqVO);

     /**
     * 获得系统协议表简单列表，主要用于前端的下拉选择框
     *
     * @return 系统协议表列表
     */
     List<AgreementDO> getAgreementSimple();

    List<AppAgreementListRespVO> getAgreementList();


     /**
     * 获得系统协议表详情
     *
     * @param id 编号
     * @return 系统协议表详情
     */
    AppAgreementRespVO getAgreementDetail(Long id);

    AppAgreementRespVO getAgreementDetailByKey(String agreementKey);
}