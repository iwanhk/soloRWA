package cc.bamboo.module.project.controller.admin.systemcoin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import com.alibaba.excel.annotation.*;
import cc.bamboo.framework.excel.core.annotations.DictFormat;
import cc.bamboo.framework.excel.core.convert.DictConvert;

@Schema(description = "管理后台 - 币种管理 Response VO")
@Data
@ExcelIgnoreUnannotated
public class SystemCoinRespVO {

    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "24207")
    @ExcelProperty("ID")
    private Long id;

    @Schema(description = "币种标识", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("币种标识")
    private String coinCode;

    @Schema(description = "币种名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "张三")
    @ExcelProperty("币种名称")
    private String coinName;

    @Schema(description = "英文名称")
    @ExcelProperty("英文名称")
    private String coinNameEn;

    @Schema(description = "币种类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @ExcelProperty(value = "币种类型", converter = DictConvert.class)
    @DictFormat("biz_coin_type") // TODO 代码优化：建议设置到对应的 DictTypeConstants 枚举类中
    private Integer coinType;

    @Schema(description = "显示符号")
    @ExcelProperty("显示符号")
    private String symbol;

    @Schema(description = "币安交易对符号(如BTCUSDT)")
    @ExcelProperty("币安交易对符号(如BTCUSDT)")
    private String binanceSymbol;

    @Schema(description = "矿池币种符号(如btc)")
    @ExcelProperty("矿池币种符号(如btc)")
    private String poolSymbol;

    @Schema(description = "币种图标", example = "https://www.iocoder.cn")
    @ExcelProperty("币种图标")
    private String iconUrl;

    @Schema(description = "小数精度", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("小数精度")
    private Integer decimals;

    @Schema(description = "排序(越小越前)", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("排序(越小越前)")
    private Integer sort;

    @Schema(description = "状态:0-禁用 1-启用", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty("状态:0-禁用 1-启用")
    private Integer status;

    @Schema(description = "备注", example = "随便")
    @ExcelProperty("备注")
    private String remark;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}