package cc.bamboo.module.user.controller.app.useraudit.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "用户 APP - OCR识别证件 Response VO")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppOcrIdCardRespVO {

    @Schema(description = "姓名", example = "张三")
    private String name;

    @Schema(description = "证件号", example = "110101199001011234")
    private String idCardNo;

    @Schema(description = "有效期", example = "2030-12-31")
    private String validDate;

    @Schema(description = "性别", example = "男")
    private String gender;

    @Schema(description = "民族", example = "汉")
    private String nation;

    @Schema(description = "出生日期", example = "1990-01-01")
    private String birth;

    @Schema(description = "住址", example = "北京市朝阳区")
    private String address;

    @Schema(description = "签发机关", example = "北京市公安局")
    private String authority;

}
