package cc.bamboo.module.chain.controller.admin.blockchainaddresses.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import com.alibaba.excel.annotation.*;

@Schema(description = "管理后台 - 区块链地址 Response VO")
@Data
@ExcelIgnoreUnannotated
public class BlockchainAddressesRespVO {

    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1999")
    @ExcelProperty("ID")
    private Long id;

    @Schema(description = "地址名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "芋艿")
    @ExcelProperty("地址名称")
    private String name;

    @Schema(description = "区块链地址", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("区块链地址")
    private String address;

    @Schema(description = "地址私钥", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("地址私钥")
    private String privateKey;

    @Schema(description = "地址描述", example = "你猜")
    @ExcelProperty("地址描述")
    private String description;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}