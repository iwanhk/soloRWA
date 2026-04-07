package cc.bamboo.module.user.controller.admin.noticetemplate.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Schema(description = "管理后台 - 消息模板新增/修改 Request VO")
@Data
public class NoticeTemplateSaveReqVO {

    @Schema(description = "主键", example = "1024")
    private Long id;

    @Schema(description = "模板名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "审核通过通知")
    @NotEmpty(message = "模板名称不能为空")
    private String name;

    @Schema(description = "模板编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "AUDIT_PASS")
    @NotEmpty(message = "模板编码不能为空")
    private String code;

    @Schema(description = "发送人名称", example = "系统管理员")
    private String nickname;

    @Schema(description = "模板内容", requiredMode = Schema.RequiredMode.REQUIRED, example = "您的认证已通过")
    @NotEmpty(message = "模板内容不能为空")
    private String content;

    @Schema(description = "模板标题", requiredMode = Schema.RequiredMode.REQUIRED, example = "您的认证已通过")
    @NotEmpty(message = "模板标题不能为空")
    private String title;



    @Schema(description = "类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer type;

    @Schema(description = "参数数组", example = "[\"userName\", \"auditTime\"]")
    private String params;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "状态不能为空")
    private Integer status;

    @Schema(description = "备注", example = "用于用户认证通过后的通知")
    private String remark;

}
