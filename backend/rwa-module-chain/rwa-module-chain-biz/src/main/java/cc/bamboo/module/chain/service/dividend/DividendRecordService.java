package cc.bamboo.module.chain.service.dividend;

import cc.bamboo.module.chain.api.dividend.dto.DividendRecordReqDTO;
import cc.bamboo.module.chain.api.dividend.dto.DividendRecordRespDTO;

/**
 * 分红记录上链服务接口
 *
 * @author Swolf
 */
public interface DividendRecordService {

    /**
     * 记录分红信息到链上
     *
     * @param reqDTO 分红记录请求
     * @return 上链结果
     */
    DividendRecordRespDTO recordDividend(DividendRecordReqDTO reqDTO);
}
