package cc.bamboo.module.project.controller.app.projectorder.vo;

import cc.bamboo.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 项目账单管理表 DO
 *
 * @author Swolf
 */
@Data
public class AppProjectBillReqVO extends PageParam {


    /**
     * 账单类型：1-分红 2-到期赎回 3-提前赎回
     */
    @Schema(description = "账单类型：1-分红 2-到期赎回 3-提前赎回")
    private Integer billType;

    private Long orderId;


    private Long projectId;

    @Schema(description = "审核状态：1-待审核 2-审核通过 3-审核不通过")
    private Integer auditStatus;

}