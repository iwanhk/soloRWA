package cc.bamboo.module.project.enums;

import cc.bamboo.framework.common.enums.RpcConstants;

public class ApiConstants {

    public static final String NAME = "project-server";

    public static final String PREFIX = RpcConstants.RPC_API_PREFIX + "/project";

    public static final String VERSION = "1.0.0";

    public static final String ASSET_DETAIL_KEY = "order:asset:detail:%s:%s";

    public static final String ASSET_DETAIL_KEY_ALL = "order:asset:detail:all:%s";

    public static final String COIN_RATE_KEY = "coin:rate:%s";



    /**
     * 资产类型：开放型基金
     */
    public static final Integer ASSET_TYPE_OPEN_FUND = 1;

    /**
     * 资产类型：封闭型基金
     */
    public static final Integer ASSET_TYPE_CLOSED_FUND = 2;

    /**
     * 配置类型：挖矿
     */
    public static final Integer PROJECT_CONFIG_TYPE_MINING = 0;

    /**
     * 配置类型：基金
     */
    public static final Integer PROJECT_CONFIG_TYPE_FUND = 1;

    /**
     * 上架状态：已上架
     */
    public static final Integer SELL_STATUS_ON_SALE = 1;

    public static final Integer SELL_STATUS_NOT_SALE = 0;
}
