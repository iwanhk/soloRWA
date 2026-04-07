package cc.bamboo.module.project.controller.admin.projectdividendperiod.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;
import java.util.*;
import javax.validation.constraints.*;

@Schema(description = "管理后台 - 分红周期新增/修改 Request VO")
@Data
public class ProjectDividendPeriodSaveReqVO {

    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "4912")
    private Long id;

    @Schema(description = "项目id", example = "15942")
    private Long projectId;

    @Schema(description = "分红期数")
    private Integer periodSeq;

    @Schema(description = "解锁日期")
    private LocalDate unlockDate;

}