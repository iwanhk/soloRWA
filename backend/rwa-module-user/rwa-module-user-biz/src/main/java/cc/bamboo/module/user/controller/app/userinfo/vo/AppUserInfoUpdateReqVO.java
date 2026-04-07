package cc.bamboo.module.user.controller.app.userinfo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

@Schema(description = "用户 APP - 用户基础信息修改 Request VO")
@Data
public class AppUserInfoUpdateReqVO {

    @Schema(description = "用户昵称", example = "芋艿")
    @Length(max = 30, message = "用户昵称长度不能超过 30 个字符")
    private String nickName;

    @Schema(description = "用户头像文件")
    private MultipartFile avatarFile;

}
