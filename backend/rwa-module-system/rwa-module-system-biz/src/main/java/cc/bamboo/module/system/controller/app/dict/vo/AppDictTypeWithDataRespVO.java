package cc.bamboo.module.system.controller.app.dict.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Schema(description = "用户 App - 字典类型及数据 Response VO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppDictTypeWithDataRespVO {

    @Schema(description = "字典类型名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "性别")
    private String name;

    @Schema(description = "字典类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "sys_user_sex")
    private String type;

    @Schema(description = "字典数据列表", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<DictDataItem> dataList;

    @Schema(description = "字典数据项")
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DictDataItem {

        @Schema(description = "字典标签", requiredMode = Schema.RequiredMode.REQUIRED, example = "男")
        private String label;

        @Schema(description = "字典值", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
        private String value;

        @Schema(description = "排序", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
        private Integer sort;

    }

}
