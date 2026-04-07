package cc.bamboo.module.chain.controller.admin.addressidentities.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import com.alibaba.excel.annotation.*;

@Schema(description = "管理后台 - 地址身份关联 Response VO")
@Data
@ExcelIgnoreUnannotated
public class AddressIdentitiesRespVO {

    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1536")
    @ExcelProperty("ID")
    private Long id;

    @Schema(description = "区块链地址", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("区块链地址")
    private String address;

    @Schema(description = "身份ID，关联到对应的身份表", requiredMode = Schema.RequiredMode.REQUIRED, example = "13618")
    @ExcelProperty("身份ID，关联到对应的身份表")
    private Long identityId;

    @Schema(description = "身份类型，如ClaimIssuer、User等", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty("身份类型，如ClaimIssuer、User等")
    private String type;

    @Schema(description = "合约地址")
    @ExcelProperty("合约地址")
    private String contractAddress;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}