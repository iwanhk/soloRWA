package cc.bamboo.module.chain.service.tokens;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import cc.bamboo.module.chain.controller.admin.tokens.vo.*;
import cc.bamboo.module.chain.dal.dataobject.tokens.TokensDO;
import cc.bamboo.framework.common.pojo.PageResult;
import cc.bamboo.framework.common.pojo.PageParam;
import cc.bamboo.framework.common.util.object.BeanUtils;

import cc.bamboo.module.chain.dal.mysql.tokens.TokensMapper;

import static cc.bamboo.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cc.bamboo.module.chain.enums.ErrorCodeConstants.*;

/**
 * 代币 Service 实现类
 *
 * @author Swolf
 */
@Service
@Validated
public class TokensServiceImpl implements TokensService {

    @Resource
    private TokensMapper tokensMapper;

    @Override
    public Long createTokens(TokensSaveReqVO createReqVO) {
        // 插入
        TokensDO tokens = BeanUtils.toBean(createReqVO, TokensDO.class);
        tokensMapper.insert(tokens);
        // 返回
        return tokens.getId();
    }

    @Override
    public void updateTokens(TokensSaveReqVO updateReqVO) {
        // 校验存在
        validateTokensExists(updateReqVO.getId());
        // 更新
        TokensDO updateObj = BeanUtils.toBean(updateReqVO, TokensDO.class);
        tokensMapper.updateById(updateObj);
    }

    @Override
    public void deleteTokens(Long id) {
        // 校验存在
        validateTokensExists(id);
        // 删除
        tokensMapper.deleteById(id);
    }

    private void validateTokensExists(Long id) {
        if (tokensMapper.selectById(id) == null) {
            throw exception(TOKENS_NOT_EXISTS);
        }
    }

    @Override
    public TokensDO getTokens(Long id) {
        return tokensMapper.selectById(id);
    }

    @Override
    public PageResult<TokensDO> getTokensPage(TokensPageReqVO pageReqVO) {
        return tokensMapper.selectPage(pageReqVO);
    }

}