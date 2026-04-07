package cc.bamboo.module.chain.controller.admin.identityregistrystorages.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import com.alibaba.excel.annotation.*;

@Schema(description = "管理后台 - 身份注册表存储 Response VO")
@Data
@ExcelIgnoreUnannotated
public class IdentityRegistryStoragesRespVO {

    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "19084")
    @ExcelProperty("ID")
    private Long id;

    @Schema(description = "存储合约地址", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("存储合约地址")
    private String address;

    @Schema(description = "部署者地址", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("部署者地址")
    private String deployerAddress;

    @Schema(description = "交易哈希")
    @ExcelProperty("交易哈希")
    private String transactionHash;

    @Schema(description = "区块号")
    @ExcelProperty("区块号")
    private Long blockNumber;

    @Schema(description = "绑定的代币数量", requiredMode = Schema.RequiredMode.REQUIRED, example = "25752")
    @ExcelProperty("绑定的代币数量")
    private Long boundTokenCount;

    @Schema(description = "状态，如deployed-已部署，active-活跃", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @ExcelProperty("状态，如deployed-已部署，active-活跃")
    private String status;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}