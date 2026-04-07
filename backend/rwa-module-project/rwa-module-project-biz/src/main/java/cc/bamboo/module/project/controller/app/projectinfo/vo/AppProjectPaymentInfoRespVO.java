package cc.bamboo.module.project.controller.app.projectinfo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 项目收款信息响应 VO
 */
@Schema(description = "用户 APP - 项目收款信息 Response VO")
@Data
public class AppProjectPaymentInfoRespVO {

    @Schema(description = "开户名", example = "北京XX科技有限公司")
    private String bankAccountName;

    @Schema(description = "银行账号", example = "6222021234567890123")
    private String bankAccount;

    @Schema(description = "开户行", example = "中国工商银行北京分行")
    private String bankName;
}
