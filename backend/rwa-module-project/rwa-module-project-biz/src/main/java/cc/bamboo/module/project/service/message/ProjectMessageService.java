package cc.bamboo.module.project.service.message;

import java.util.Map;

/**
 * 项目模块 - 消息发送 Service 接口
 *
 * @author Kiro
 */
public interface ProjectMessageService {

    /**
     * 异步发送单条消息给指定用户
     *
     * @param userId         用户ID
     * @param templateCode   模板编码
     * @param templateParams 模板参数
     */
    void sendSingleMessageAsync(Long userId, String templateCode, Map<String, Object> templateParams);

    /**
     * 异步发送系统消息
     *
     * @param templateCode   模板编码
     * @param templateParams 模板参数
     */
    void sendSystemMessageAsync(String templateCode, Map<String, Object> templateParams);

    /**
     * 异步发送订单消息
     *
     * @param userId         用户ID
     * @param orderId        订单ID
     * @param templateCode   模板编码
     * @param templateParams 模板参数
     */
    void sendOrderMessageAsync(Long userId, Long orderId, String templateCode, Map<String, Object> templateParams);

}
