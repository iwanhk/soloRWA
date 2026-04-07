package cc.bamboo.module.user.controller.app.useraudit.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Hus
 * @version 1.0
 * @date 2026/1/6 18:01
 * @description
 */

@Schema(description = "银行卡信息")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppBankCardInfoRespVO {
    @Schema(description = "银行卡ID", example = "1")
    private Long id;

    private String bankAccountName;
    /**
     * 银行卡号
     */
    private String bankAccount;
    /**
     * 开户行
     */
    private String bankName;

    @Schema(description = "审核状态：1-待审核 2正常 3-审核驳回", example = "1")
    private Integer auditStatus;

    private String auditRemark;
}
