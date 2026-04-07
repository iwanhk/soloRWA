package cc.bamboo.module.project.controller.admin.systemcoin.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cc.bamboo.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cc.bamboo.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 币种管理分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class SystemCoinPageReqVO extends PageParam {

    @Schema(description = "币种标识")
    private String coinCode;

    @Schema(description = "币种名称", example = "张三")
    private String coinName;

    @Schema(description = "英文名称")
    private String coinNameEn;

    @Schema(description = "币种类型", example = "2")
    private Integer coinType;

    @Schema(description = "显示符号")
    private String symbol;

    @Schema(description = "币安交易对符号(如BTCUSDT)")
    private String binanceSymbol;

    @Schema(description = "矿池币种符号(如btc)")
    private String poolSymbol;

    @Schema(description = "币种图标", example = "https://www.iocoder.cn")
    private String iconUrl;

    @Schema(description = "小数精度")
    private Integer decimals;

    @Schema(description = "排序(越小越前)")
    private Integer sort;

    @Schema(description = "状态:0-禁用 1-启用", example = "1")
    private Integer status;

    @Schema(description = "备注", example = "随便")
    private String remark;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}