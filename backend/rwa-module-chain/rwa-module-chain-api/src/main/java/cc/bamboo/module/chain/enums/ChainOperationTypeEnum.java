package cc.bamboo.module.chain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 链操作类型枚举
 *
 * @author Swolf
 */
@Getter
@AllArgsConstructor
public enum ChainOperationTypeEnum {

    /**
     * 部署 Token
     */
    DEPLOY_TOKEN(1, "部署Token"),

    /**
     * 添加用户并签发 Claim
     */
    ADD_USER_AND_ISSUE_CLAIM(2, "添加用户并签发Claim"),

    /**
     * 铸造 Token
     */
    MINT_TOKEN(3, "铸造Token");

    /**
     * 类型
     */
    private final Integer type;

    /**
     * 描述
     */
    private final String description;

    /**
     * 根据类型获取枚举
     *
     * @param type 类型
     * @return 枚举
     */
    public static ChainOperationTypeEnum getByType(Integer type) {
        if (type == null) {
            return null;
        }
        for (ChainOperationTypeEnum value : values()) {
            if (value.getType().equals(type)) {
                return value;
            }
        }
        return null;
    }
}
