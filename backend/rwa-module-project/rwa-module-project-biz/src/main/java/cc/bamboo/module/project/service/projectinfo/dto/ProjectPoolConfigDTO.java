package cc.bamboo.module.project.service.projectinfo.dto;

import lombok.Data;

/**
 * 项目矿池配置信息 (内部使用，包含解密后的私钥)
 *
 * @author Swolf
 */
@Data
public class ProjectPoolConfigDTO {

    /**
     * 矿池AccessKey
     */
    private String poolAccessKey;

    /**
     * 矿池私钥 (已解密)
     */
    private String poolPrivateKey;

    /**
     * 矿池子账号
     */
    private String poolName;

}
