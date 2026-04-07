package cc.bamboo.module.user.service.userchain;

import java.util.*;
import javax.validation.*;
import cc.bamboo.module.user.controller.admin.userchain.vo.*;
import cc.bamboo.module.user.controller.app.userchain.vo.AppBindAddressReqVO;
import cc.bamboo.module.user.controller.app.userchain.vo.AppChainRespVO;
import cc.bamboo.module.user.controller.app.userchain.vo.AppUnBindAddressReqVO;
import cc.bamboo.module.user.controller.app.userchain.vo.AppUserChainRespVO;
import cc.bamboo.module.user.dal.dataobject.userchain.UserChainDO;
import cc.bamboo.framework.common.pojo.PageResult;

/**
 * 用户链地址表= Service 接口
 *
 * @author Swolf
 */
public interface UserChainService {

    /**
     * 创建用户链地址表=
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createChain(@Valid UserChainSaveReqVO createReqVO);

    /**
     * 更新用户链地址表=
     *
     * @param updateReqVO 更新信息
     */
    void updateChain(@Valid UserChainSaveReqVO updateReqVO);

    /**
     * 删除用户链地址表=
     *
     * @param id 编号
     */
    void deleteChain(Long id);

    /**
     * 获得用户链地址表=
     *
     * @param id 编号
     * @return 用户链地址表=
     */
    UserChainDO getChain(Long id);

    /**
     * 获得用户链地址表=分页
     *
     * @param pageReqVO 分页查询
     * @return 用户链地址表=分页
     */
    PageResult<UserChainDO> getChainPage(UserChainPageReqVO pageReqVO);

    List<AppUserChainRespVO> getUserChain();

    /**
     * 绑定链地址
     *
     * @param repVO
     * @author: Hus
     * @date: 2026/1/9 16:43
     * @return: void
     * @description
     */
    Long bindAddress(AppBindAddressReqVO repVO);


    Long createAddress(Long userId);

    /**
     * 解绑链地址
     *
     * @param repVO
     * @author: Hus
     * @date: 2026/1/11 10:20
     * @return: void
     * @description
     */
    void unBindAddress(AppUnBindAddressReqVO repVO);

    /**
     * 生成签名数据
     *
     * @param address 链地址
     * @return 签名数据
     */
    String generateSignData(String address);

    /**
     * 获取用户链地址
     *
     * @param userChainId 用户链地址表=编号
     * @return 用户链地址
     */
    AppUserChainRespVO getUserChain(Long userChainId);

    /**
     * 获取可用链列表
     *
     * @return 可用链列表
     */
    List<AppChainRespVO> getActiveChainList();

}