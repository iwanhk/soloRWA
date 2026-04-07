package cc.bamboo.module.chain.controller.admin.systeminitialization.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import com.alibaba.excel.annotation.*;

@Schema(description = "管理后台 - 系统初始化 Response VO")
@Data
@ExcelIgnoreUnannotated
public class SystemInitializationRespVO {

    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "8995")
    @ExcelProperty("ID")
    private Long id;

    @Schema(description = "初始化步骤名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("初始化步骤名称")
    private String step;

    @Schema(description = "状态，如pending-待处理，completed-已完成", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @ExcelProperty("状态，如pending-待处理，completed-已完成")
    private String status;

    @Schema(description = "相关合约地址")
    @ExcelProperty("相关合约地址")
    private String contractAddress;

    @Schema(description = "交易哈希")
    @ExcelProperty("交易哈希")
    private String transactionHash;

    @Schema(description = "区块号")
    @ExcelProperty("区块号")
    private Long blockNumber;

    @Schema(description = "错误信息，若有")
    @ExcelProperty("错误信息，若有")
    private String errorMessage;

    @Schema(description = "元数据，JSON格式")
    @ExcelProperty("元数据，JSON格式")
    private String metadata;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}