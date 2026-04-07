package cc.bamboo.module.user.controller.admin.noticemessage.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import com.alibaba.excel.annotation.*;
import cc.bamboo.framework.excel.core.annotations.DictFormat;
import cc.bamboo.framework.excel.core.convert.DictConvert;

@Schema(description = "管理后台 - 用户消息 Response VO")
@Data
@ExcelIgnoreUnannotated
public class NoticeMessageRespVO {

    @Schema(description = "用户ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "12139")
    @ExcelProperty("用户ID")
    private Long id;

    @Schema(description = "用户id", requiredMode = Schema.RequiredMode.REQUIRED, example = "15301")
    @ExcelProperty("用户id")
    private Long userId;

    @Schema(description = "消息类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @ExcelProperty(value = "消息类型", converter = DictConvert.class)
    @DictFormat("biz_notice_type") // TODO 代码优化：建议设置到对应的 DictTypeConstants 枚举类中
    private Integer noticeType;


    @Schema(description = "模版编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1040")
    @ExcelProperty("模版编号")
    private Long templateId;

    @Schema(description = "模板编码", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("模板编码")
    private String templateCode;

    @Schema(description = "模版发送人名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "赵六")
    @ExcelProperty("模版发送人名称")
    private String templateNickname;

    @Schema(description = "模版内容", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("模版内容")
    private String templateContent;

    @Schema(description = "模版标题", requiredMode = Schema.RequiredMode.REQUIRED, example = "您的订单已发货")
    @ExcelProperty("模版标题")
    private String templateTitle;



    @Schema(description = "模版类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @ExcelProperty("模版类型")
    private Integer templateType;

    @Schema(description = "模版参数", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("模版参数")
    private String templateParams;

    @Schema(description = "订单id", example = "3333")
    @ExcelProperty("订单id")
    private Long orderId;

    @Schema(description = "消息地址", example = "https://www.iocoder.cn")
    @ExcelProperty("消息地址")
    private String noticeUrl;

    @Schema(description = "是否已读", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty("是否已读")
    private Boolean readStatus;

    @Schema(description = "阅读时间")
    @ExcelProperty("阅读时间")
    private LocalDateTime readTime;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}