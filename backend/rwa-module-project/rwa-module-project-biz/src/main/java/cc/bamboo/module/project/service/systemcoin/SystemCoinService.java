package cc.bamboo.module.project.service.systemcoin;

import java.util.*;
import javax.validation.*;
import cc.bamboo.module.project.controller.admin.systemcoin.vo.*;
import cc.bamboo.module.project.dal.dataobject.systemcoin.SystemCoinDO;
import cc.bamboo.framework.common.pojo.PageResult;
import cc.bamboo.framework.common.pojo.PageParam;

/**
 * 币种管理 Service 接口
 *
 * @author swolf
 */
public interface SystemCoinService {

    /**
     * 创建币种管理
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createSystemCoin(@Valid SystemCoinSaveReqVO createReqVO);

    /**
     * 更新币种管理
     *
     * @param updateReqVO 更新信息
     */
    void updateSystemCoin(@Valid SystemCoinSaveReqVO updateReqVO);

    /**
     * 删除币种管理
     *
     * @param id 编号
     */
    void deleteSystemCoin(Long id);

    /**
     * 获得币种管理
     *
     * @param id 编号
     * @return 币种管理
     */
    SystemCoinDO getSystemCoin(Long id);

    /**
     * 获得币种管理分页
     *
     * @param pageReqVO 分页查询
     * @return 币种管理分页
     */
    PageResult<SystemCoinDO> getSystemCoinPage(SystemCoinPageReqVO pageReqVO);

    /**
     * 根据币种类型获取币种列表（下拉用）
     *
     * @param coinType 币种类型：1-数字货币 2-法币，为null时返回全部
     * @return 币种列表
     */
    List<SystemCoinDO> getSystemCoinListByType(Integer coinType);

}