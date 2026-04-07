package cc.bamboo.module.project.controller.admin.systemcoin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import javax.validation.constraints.*;

@Schema(description = "管理后台 - 币种管理新增/修改 Request VO")
@Data
public class SystemCoinSaveReqVO {

    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "24207")
    private Long id;

    @Schema(description = "币种标识", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "币种标识不能为空")
    private String coinCode;

    @Schema(description = "币种名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "张三")
    @NotEmpty(message = "币种名称不能为空")
    private String coinName;

    @Schema(description = "英文名称")
    private String coinNameEn;

    @Schema(description = "币种类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @NotNull(message = "币种类型不能为空")
    private Integer coinType;

    @Schema(description = "显示符号")
    private String symbol;

    @Schema(description = "币安交易对符号(如BTCUSDT)")
    private String binanceSymbol;

    @Schema(description = "矿池币种符号(如btc)")
    private String poolSymbol;

    @Schema(description = "币种图标", example = "https://www.iocoder.cn")
    private String iconUrl;

    @Schema(description = "小数精度", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "小数精度不能为空")
    private Integer decimals;

    @Schema(description = "排序(越小越前)", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "排序(越小越前)不能为空")
    private Integer sort;

    @Schema(description = "状态:0-禁用 1-启用", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "状态:0-禁用 1-启用不能为空")
    private Integer status;

    @Schema(description = "备注", example = "随便")
    private String remark;

}