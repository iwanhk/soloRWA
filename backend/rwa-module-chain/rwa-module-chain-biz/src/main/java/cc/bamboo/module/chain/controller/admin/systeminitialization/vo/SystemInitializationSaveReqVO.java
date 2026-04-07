package cc.bamboo.module.chain.controller.admin.systeminitialization.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import javax.validation.constraints.*;

@Schema(description = "管理后台 - 系统初始化新增/修改 Request VO")
@Data
public class SystemInitializationSaveReqVO {

    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "8995")
    private Long id;

    @Schema(description = "初始化步骤名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "初始化步骤名称不能为空")
    private String step;

    @Schema(description = "状态，如pending-待处理，completed-已完成", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @NotEmpty(message = "状态，如pending-待处理，completed-已完成不能为空")
    private String status;

    @Schema(description = "相关合约地址")
    private String contractAddress;

    @Schema(description = "交易哈希")
    private String transactionHash;

    @Schema(description = "区块号")
    private Long blockNumber;

    @Schema(description = "错误信息，若有")
    private String errorMessage;

    @Schema(description = "元数据，JSON格式")
    private String metadata;

}