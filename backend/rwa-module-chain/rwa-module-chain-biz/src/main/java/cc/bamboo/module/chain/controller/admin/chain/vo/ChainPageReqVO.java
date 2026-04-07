package cc.bamboo.module.chain.controller.admin.chain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import javax.validation.constraints.*;
import cc.bamboo.framework.common.pojo.PageParam;

@Schema(description = "管理后台 - 区块链信息分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ChainPageReqVO extends PageParam {

    @Schema(description = "链名称")
    private String name;

    @Schema(description = "状态：0-开启，1-关闭")
    private Integer status;

    private Integer chainId;

}
