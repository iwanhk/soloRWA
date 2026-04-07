package cc.bamboo.module.system.controller.app.dict.vo;

import lombok.Data;

/**
 * @author Hus
 * @version 1.0
 * @date 2026/1/28 10:38
 * @description
 */
@Data
public class AppCommonConfigRespVO {

    /**
     * 参数名称
     */
    private String name;
    /**
     * 参数键名
     */
    private String configKey;
    /**
     * 参数键值
     */
    private String value;
}
