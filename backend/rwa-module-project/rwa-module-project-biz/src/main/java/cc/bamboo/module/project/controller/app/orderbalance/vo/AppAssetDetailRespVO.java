package cc.bamboo.module.project.controller.app.orderbalance.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Schema(description = "用户 APP - 资产详情 Response VO")
public class AppAssetDetailRespVO {

    @Schema(description = "持有金额", requiredMode = Schema.RequiredMode.REQUIRED, example = "10000.00")
    private List<AppAssetByCurrencyRespVO> totalAssets;


    @Schema(description = "累计总收益", requiredMode = Schema.RequiredMode.REQUIRED, example = "500.00")
    private List<AppAssetByCurrencyRespVO> totalIncome;


    @Schema(description = "今日收益", requiredMode = Schema.RequiredMode.REQUIRED, example = "10.00")
    private List<AppAssetByCurrencyRespVO> todayIncome;


    @Schema(description = "可提现余额", requiredMode = Schema.RequiredMode.REQUIRED, example = "500.00")
    private AppAssetByCurrencyRespVO withdrawableBalance;

}
