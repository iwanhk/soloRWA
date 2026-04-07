package cc.bamboo.module.project.mq.message;

import lombok.Data;

import java.io.Serializable;

/**
 * 项目审核通过消息
 * 用于通知 chain 模块为项目部署 Token
 *
 * @author Swolf
 */
@Data
public class ProjectAuditPassMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 任务编号（幂等性标识）
     */
    private String taskNo;

    /**
     * 项目ID
     */
    private Long projectId;

    /**
     * 项目名称
     */
    private String projectName;

    /**
     * 项目符号（简称）
     */
    private String projectSymbol;

    /**
     * 当前重试次数
     */
    private Integer retryCount;

}
