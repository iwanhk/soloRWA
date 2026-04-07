package cc.bamboo.framework.tenant.core.context;

import cn.hutool.core.util.StrUtil;
import cc.bamboo.framework.common.enums.DocumentEnum;
import com.alibaba.ttl.TransmittableThreadLocal;

/**
 * 多租户上下文 Holder
 *
 * @author 芋道源码
 */
public class TenantContextHolder {

    /**
     * 当前租户编号
     */
    private static final ThreadLocal<Long> TENANT_ID = new TransmittableThreadLocal<>();

    /**
     * 是否忽略租户
     */
    private static final ThreadLocal<Boolean> IGNORE = new TransmittableThreadLocal<>();

    /**
     * 是否为APP端请求（APP端对指定表不做租户过滤）
     */
    private static final ThreadLocal<Boolean> APP_REQUEST = new TransmittableThreadLocal<>();

    /**
     * 获得租户编号
     *
     * @return 租户编号
     */
    public static Long getTenantId() {
        return TENANT_ID.get();
    }

    /**
     * 获得租户编号。如果不存在，则抛出 NullPointerException 异常
     *
     * @return 租户编号
     */
    public static Long getRequiredTenantId() {
        Long tenantId = getTenantId();
        if (tenantId == null) {
            throw new NullPointerException("TenantContextHolder 不存在租户编号！可参考文档："
                    + DocumentEnum.TENANT.getUrl());
        }
        return tenantId;
    }

    public static void setTenantId(Long tenantId) {
        TENANT_ID.set(tenantId);
    }

    public static void setIgnore(Boolean ignore) {
        IGNORE.set(ignore);
    }

    /**
     * 当前是否忽略租户
     *
     * @return 是否忽略
     */
    public static boolean isIgnore() {
        return Boolean.TRUE.equals(IGNORE.get());
    }

    /**
     * 设置是否为APP端请求
     */
    public static void setAppRequest(Boolean appRequest) {
        APP_REQUEST.set(appRequest);
    }

    /**
     * 当前是否为APP端请求
     *
     * @return 是否为APP端请求
     */
    public static boolean isAppRequest() {
        return Boolean.TRUE.equals(APP_REQUEST.get());
    }

    public static void clear() {
        TENANT_ID.remove();
        IGNORE.remove();
        APP_REQUEST.remove();
    }

}
