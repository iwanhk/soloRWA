package cc.bamboo.module.system.api.mail.dto;

import lombok.Data;

@Data
public class MailCodeSendReqDTO {
    private String mail;
    private Long userId;
    private Integer userType;
    private String templateCode;
    private Integer scene;
}
