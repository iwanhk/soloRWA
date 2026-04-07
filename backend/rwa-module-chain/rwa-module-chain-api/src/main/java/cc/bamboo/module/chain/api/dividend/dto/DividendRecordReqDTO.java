package cc.bamboo.module.chain.api.dividend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;

/**
 * 分红记录上链请求 DTO
 *
 * @author Swolf
 */
@Data
@Schema(description = "RPC 服务 - 分红记录上链 Request DTO")
public class DividendRecordReqDTO implements Serializable {

    @Schema(description = "分红日期 (格式: \"2024-01-15\")", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDate date;

    @Schema(description = "项目ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1001")
    private Long projectId;

    @Schema(description = "分红金额", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigInteger amount;

    @Schema(description = "分红币种", requiredMode = Schema.RequiredMode.REQUIRED, example = "USDT")
    private String currency;

    @Schema(description = "分红地址列表", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<String> addresses;

    @Schema(description = "每个地址的份数", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<BigInteger> shares;
}
