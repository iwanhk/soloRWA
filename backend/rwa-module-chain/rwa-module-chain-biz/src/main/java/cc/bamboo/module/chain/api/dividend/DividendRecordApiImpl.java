package cc.bamboo.module.chain.api.dividend;

import cc.bamboo.module.chain.api.dividend.dto.DividendRecordReqDTO;
import cc.bamboo.module.chain.api.dividend.dto.DividendRecordRespDTO;
import cc.bamboo.module.chain.service.dividend.DividendRecordService;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;

/**
 * 分红记录上链 API 实现类
 *
 * @author Swolf
 */
@Service
@Validated
public class DividendRecordApiImpl implements DividendRecordApi {

    @Resource
    private DividendRecordService dividendRecordService;

    @Override
    public DividendRecordRespDTO recordDividend(DividendRecordReqDTO reqDTO) {
        return dividendRecordService.recordDividend(reqDTO);
    }
}
