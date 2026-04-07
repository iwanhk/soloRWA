package cc.bamboo.module.user.api.userinfo;

import cc.bamboo.framework.common.pojo.CommonResult;
import cc.bamboo.framework.common.util.object.BeanUtils;
import cc.bamboo.module.user.api.userinfo.dto.UserInfoRespDTO;
import cc.bamboo.module.user.api.userinfo.dto.Verify2FAReqDTO;
import cc.bamboo.module.user.dal.dataobject.userinfo.UserInfoDO;
import cc.bamboo.module.user.dal.mysql.userinfo.UserInfoMapper;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

import java.util.List;

import static cc.bamboo.framework.common.pojo.CommonResult.success;

/**
 * RPC 服务 - 用户信息 API 实现类
 *
 * @author Swolf
 */
@RestController
@Validated
public class UserInfoApiImpl implements UserInfoApi {

    @Resource
    private UserInfoMapper userInfoMapper;

    @Override
    public CommonResult<UserInfoRespDTO> getUserInfo(Long id) {
        UserInfoDO userInfo = userInfoMapper.selectById(id);
        return success(BeanUtils.toBean(userInfo, UserInfoRespDTO.class));
    }

    @Override
    public CommonResult<List<UserInfoRespDTO>> getUserInfoList(List<Long> ids) {
        List<UserInfoDO> userInfoList = userInfoMapper.selectBatchIds(ids);
        return success(BeanUtils.toBean(userInfoList, UserInfoRespDTO.class));
    }

    @Override
    public CommonResult<Boolean> verify2FAIfEnabled(Verify2FAReqDTO reqVO) {
        Long userId = reqVO.getUserId();
        String code = reqVO.getCode();

        // 1. 查询用户信息
        UserInfoDO userInfo = userInfoMapper.selectById(userId);
        if (userInfo == null) {
            return success(false);
        }

        // 2. 检查用户是否开启了2FA
        // 如果未开启2FA（twoFactorAuthStatus != 1 或 twoFactorAuthSecret 为空），直接返回true
        if (userInfo.getTwoFactorAuthStatus() == null
                || userInfo.getTwoFactorAuthStatus() != 1
                || userInfo.getTwoFactorAuthSecret() == null) {
            return success(true); // 未开启2FA，跳过验证
        }

        // 3. 验证2FA码
        // 如果code为空，且已开启2FA，则验证失败
        if (code == null || code.isEmpty()) {
            return success(false);
        }

        // 4. 调用验证工具
        boolean verified = cc.bamboo.module.user.util.TwoFactorAuthUtils.verifyCode(
                userInfo.getTwoFactorAuthSecret(), code);

        return success(verified);
    }

}
