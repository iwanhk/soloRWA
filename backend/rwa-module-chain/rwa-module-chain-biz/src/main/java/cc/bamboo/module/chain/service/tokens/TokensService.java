package cc.bamboo.module.chain.service.tokens;

import java.util.*;
import javax.validation.*;
import cc.bamboo.module.chain.controller.admin.tokens.vo.*;
import cc.bamboo.module.chain.dal.dataobject.tokens.TokensDO;
import cc.bamboo.framework.common.pojo.PageResult;
import cc.bamboo.framework.common.pojo.PageParam;

/**
 * 代币 Service 接口
 *
 * @author Swolf
 */
public interface TokensService {

    /**
     * 创建代币
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createTokens(@Valid TokensSaveReqVO createReqVO);

    /**
     * 更新代币
     *
     * @param updateReqVO 更新信息
     */
    void updateTokens(@Valid TokensSaveReqVO updateReqVO);

    /**
     * 删除代币
     *
     * @param id 编号
     */
    void deleteTokens(Long id);

    /**
     * 获得代币
     *
     * @param id 编号
     * @return 代币
     */
    TokensDO getTokens(Long id);

    /**
     * 获得代币分页
     *
     * @param pageReqVO 分页查询
     * @return 代币分页
     */
    PageResult<TokensDO> getTokensPage(TokensPageReqVO pageReqVO);

}