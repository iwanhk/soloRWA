package cc.bamboo.framework.tenant.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Collections;
import java.util.Set;

/**
 * 多租户配置
 *
 * @author 芋道源码
 */
@ConfigurationProperties(prefix = "rwa.tenant")
@Data
public class TenantProperties {

    /**
     * 租户是否开启
     */
    private static final Boolean ENABLE_DEFAULT = true;

    /**
     * 是否开启
     */
    private Boolean enable = ENABLE_DEFAULT;

    /**
     * 需要忽略多租户的请求
     *
     * 默认情况下，每个请求需要带上 tenant-id 的请求头。但是，部分请求是无需带上的，例如说短信回调、支付回调等 Open API！
     */
    private Set<String> ignoreUrls = Collections.emptySet();

    /**
     * 需要忽略多租户的表
     *
     * 即默认所有表都开启多租户的功能，所以记得添加对应的 tenant_id 字段哟
     */
    private Set<String> ignoreTables = Collections.emptySet();

    /**
     * 超级租户（租户ID=1）可以跨租户查询的表
     *
     * 只有在这个列表中的表，租户1才能查看所有租户的数据
     * 例如：biz_project_info, biz_order 等业务表
     */
    private Set<String> superTenantTables = Collections.emptySet();

    /**
     * APP端请求忽略多租户的表
     *
     * 在APP端请求时，这些表不会添加租户过滤条件
     * 适用于：商品列表、项目列表等需要用户查看所有租户数据的场景
     * 例如：biz_project_info, biz_product 等面向C端用户的表
     */
    private Set<String> appIgnoreTables = Collections.emptySet();

    /**
     * 需要忽略多租户的 Spring Cache 缓存
     *
     * 即默认所有缓存都开启多租户的功能，所以记得添加对应的 tenant_id 字段哟
     */
    private Set<String> ignoreCaches = Collections.emptySet();

}
