package cc.bamboo.module.user.controller.admin.userinfo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 用户基础信息新增/修改 Request VO")
@Data
public class AuditF2AReqVO {

    private boolean approved;

    private String remark;

    private Long id;
}