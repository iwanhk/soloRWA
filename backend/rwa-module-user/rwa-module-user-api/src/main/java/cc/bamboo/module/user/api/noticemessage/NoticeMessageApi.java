package cc.bamboo.module.user.api.noticemessage;

import cc.bamboo.framework.common.pojo.CommonResult;
import cc.bamboo.module.user.api.noticemessage.dto.NoticeMessageSendReqDTO;
import cc.bamboo.module.user.enums.ApiConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.security.PermitAll;
import javax.validation.Valid;

/**
 * RPC 服务 - 消息发送 API
 *
 * @author Kiro
 */
@FeignClient(name = ApiConstants.NAME)
@Tag(name = "RPC 服务 - 消息发送")
public interface NoticeMessageApi {

    String PREFIX = ApiConstants.PREFIX + "/notice-message";

    /**
     * 异步发送单条消息给指定用户
     *
     * @param reqDTO 消息发送请求
     * @return 成功
     */
    @PostMapping(PREFIX + "/send-single-async")
    @Operation(summary = "异步发送单条消息给指定用户")
    @PermitAll
    CommonResult<Boolean> sendSingleMessageAsync(@RequestBody NoticeMessageSendReqDTO reqDTO);

    /**
     * 异步发送系统消息
     *
     * @param reqDTO 消息发送请求
     * @return 成功
     */
    @PostMapping(PREFIX + "/send-system-async")
    @Operation(summary = "异步发送系统消息")
    @PermitAll
    CommonResult<Boolean> sendSystemMessageAsync(@RequestBody NoticeMessageSendReqDTO reqDTO);

    /**
     * 异步发送订单消息
     *
     * @param reqDTO 消息发送请求
     * @return 成功
     */
    @PostMapping(PREFIX + "/send-order-async")
    @Operation(summary = "异步发送订单消息")
    @PermitAll
    CommonResult<Boolean> sendOrderMessageAsync(@RequestBody NoticeMessageSendReqDTO reqDTO);

}
