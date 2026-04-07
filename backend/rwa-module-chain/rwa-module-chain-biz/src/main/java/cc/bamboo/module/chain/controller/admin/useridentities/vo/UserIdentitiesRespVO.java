package cc.bamboo.module.chain.controller.admin.useridentities.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import com.alibaba.excel.annotation.*;

@Schema(description = "管理后台 - 用户身份 Response VO")
@Data
@ExcelIgnoreUnannotated
public class UserIdentitiesRespVO {

    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "6150")
    @ExcelProperty("ID")
    private Long id;

    @Schema(description = "用户地址", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("用户地址")
    private String address;

    @Schema(description = "用户合约地址")
    @ExcelProperty("用户合约地址")
    private String contractAddress;

    @Schema(description = "管理密钥地址", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("管理密钥地址")
    private String managementKey;

    @Schema(description = "关联的区块链地址ID", example = "22391")
    @ExcelProperty("关联的区块链地址ID")
    private Long blockchainAddressId;

    @Schema(description = "盐值，用于加密或哈希计算")
    @ExcelProperty("盐值，用于加密或哈希计算")
    private String salt;

    @Schema(description = "交易哈希")
    @ExcelProperty("交易哈希")
    private String transactionHash;

    @Schema(description = "区块号")
    @ExcelProperty("区块号")
    private Long blockNumber;

    @Schema(description = "合约是否已部署，0-未部署，1-已部署", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("合约是否已部署，0-未部署，1-已部署")
    private Boolean contractDeployed;

    @Schema(description = "声明密钥是否已设置，0-未设置，1-已设置", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("声明密钥是否已设置，0-未设置，1-已设置")
    private Boolean claimKeySetup;

    @Schema(description = "国家代码")
    @ExcelProperty("国家代码")
    private Long countryCode;

    @Schema(description = "关联的代币ID，JSON格式")
    @ExcelProperty("关联的代币ID，JSON格式")
    private String associatedTokenIds;

    @Schema(description = "待处理的代币ID，JSON格式")
    @ExcelProperty("待处理的代币ID，JSON格式")
    private String pendingTokenIds;

    @Schema(description = "状态，如active-活跃", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @ExcelProperty("状态，如active-活跃")
    private String status;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}