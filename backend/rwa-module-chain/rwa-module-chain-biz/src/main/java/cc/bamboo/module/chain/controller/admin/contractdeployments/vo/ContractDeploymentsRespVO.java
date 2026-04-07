package cc.bamboo.module.chain.controller.admin.contractdeployments.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import com.alibaba.excel.annotation.*;

@Schema(description = "管理后台 - 合约部署 Response VO")
@Data
@ExcelIgnoreUnannotated
public class ContractDeploymentsRespVO {

    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "11213")
    @ExcelProperty("ID")
    private Long id;

    @Schema(description = "合约类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @ExcelProperty("合约类型")
    private String contractType;

    @Schema(description = "部署地址", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("部署地址")
    private String deploymentAddress;

    @Schema(description = "部署者地址", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("部署者地址")
    private String deployerAddress;

    @Schema(description = "部署信息，JSON格式")
    @ExcelProperty("部署信息，JSON格式")
    private String deploymentInfo;

    @Schema(description = "交易哈希")
    @ExcelProperty("交易哈希")
    private String transactionHash;

    @Schema(description = "区块号")
    @ExcelProperty("区块号")
    private Long blockNumber;

    @Schema(description = "状态，如deployed-已部署", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @ExcelProperty("状态，如deployed-已部署")
    private String status;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}