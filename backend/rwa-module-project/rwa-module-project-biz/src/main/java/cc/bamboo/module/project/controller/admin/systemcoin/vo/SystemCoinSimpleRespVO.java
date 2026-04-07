package cc.bamboo.module.project.controller.admin.systemcoin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 币种简单 Response VO")
@Data
public class SystemCoinSimpleRespVO {

    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "币种标识", requiredMode = Schema.RequiredMode.REQUIRED, example = "BTC")
    private String coinCode;

    @Schema(description = "币种名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "比特币")
    private String coinName;

    @Schema(description = "币种类型:1-数字货币 2-法币", example = "1")
    private Integer coinType;

    @Schema(description = "显示符号", example = "₿")
    private String symbol;

    @Schema(description = "币种图标", example = "https://...")
    private String iconUrl;

}
