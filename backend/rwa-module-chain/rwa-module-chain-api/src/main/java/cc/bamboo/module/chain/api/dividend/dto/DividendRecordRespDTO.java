package cc.bamboo.module.chain.api.dividend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * 分红记录上链响应 DTO
 *
 * @author Swolf
 */
@Data
@Schema(description = "RPC 服务 - 分红记录上链 Response DTO")
public class DividendRecordRespDTO implements Serializable {

    @Schema(description = "交易哈希")
    private String transactionHash;

    @Schema(description = "区块号")
    private Long blockNumber;

    @Schema(description = "是否成功")
    private Boolean success;

    @Schema(description = "错误信息")
    private String errorMessage;

    @Schema(description = "项目ID")
    private Long projectId;

    @Schema(description = "分红日期")
    private LocalDate date;
}
