package cc.bamboo.module.project.controller.app.orderbalance.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "用户 APP - 资产详情 Request VO")
public class AppAssetDetailReqVO {

    @Schema(description = "项目编号", example = "1024")
    private Long projectId;

    @Schema(description = "订单编号", example = "1024")
    private Long orderId;
}
