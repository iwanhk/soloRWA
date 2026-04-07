package cc.bamboo.module.user.controller.app.userinfo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Schema(description = "用户 APP - 设置默认币种 Request VO")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppAuthSetCurrencyReqVO {

    @Schema(description = "默认币种代码", requiredMode = Schema.RequiredMode.REQUIRED, example = "USD")
    @NotEmpty(message = "币种代码不能为空")
    private String currency;

}
