package cc.bamboo.module.project.controller.app.projectinfo.vo;

import cc.bamboo.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户 APP - 项目列表 Response VO
 *
 * @author Swolf
 */
@Schema(description = "用户 APP - 项目列表 Response VO")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppProjectListReqVO extends PageParam {

    @Schema(description = "项目状态：1-未开售 2-出售中 3-已售罄 4-盈利中", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @NotNull(message = "项目状态不能为空")
    private Integer projectStatus;

    private Integer assetType;

}
