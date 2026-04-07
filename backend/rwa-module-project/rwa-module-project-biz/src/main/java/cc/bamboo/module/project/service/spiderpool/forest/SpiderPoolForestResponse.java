package cc.bamboo.module.project.service.spiderpool.forest;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * SpiderPool API 统一响应体
 *
 * @author Swolf
 */
@Data
public class SpiderPoolForestResponse {

    /**
     * 响应码 (字符串 "SUCCESS" 表示成功)
     */
    private int code;

    /**
     * 响应消息
     */
    private String msg;

    /**
     * 时间戳
     */
    private Long t;

    /**
     * 响应数据 (可能是JSON对象、数组或其他类型)
     */
    private Object data;

    /**
     * 判断请求是否成功
     * code = "SUCCESS" 才表示业务成功
     */
    public boolean isSuccess() {
        return code == 200;
    }

}
