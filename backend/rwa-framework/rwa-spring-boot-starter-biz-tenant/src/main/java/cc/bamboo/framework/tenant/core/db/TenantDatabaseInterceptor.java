package cc.bamboo.framework.tenant.core.db;

import cn.hutool.core.collection.CollUtil;
import cc.bamboo.framework.tenant.config.TenantProperties;
import cc.bamboo.framework.tenant.core.context.TenantContextHolder;
import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;

import java.util.HashSet;
import java.util.Set;

/**
 * 基于 MyBatis Plus 多租户的功能，实现 DB 层面的多租户的功能
 *
 * @author 芋道源码
 */
public class TenantDatabaseInterceptor implements TenantLineHandler {

    private final Set<String> ignoreTables = new HashSet<>();

    /**
     * 超级租户（租户1）可以跨租户查询的表
     * 只有在这个列表中的表，租户1才能查看所有租户的数据
     */
    private final Set<String> superTenantTables = new HashSet<>();

    /**
     * APP端请求忽略多租户的表
     * 在APP端请求时，这些表不会添加租户过滤条件
     */
    private final Set<String> appIgnoreTables = new HashSet<>();

    public TenantDatabaseInterceptor(TenantProperties properties) {
        // 不同 DB 下，大小写的习惯不同，所以需要都添加进去
        properties.getIgnoreTables().forEach(table -> {
            ignoreTables.add(table.toLowerCase());
            ignoreTables.add(table.toUpperCase());
        });
        // 在 OracleKeyGenerator 中，生成主键时，会查询这个表，查询这个表后，会自动拼接 TENANT_ID 导致报错
        ignoreTables.add("DUAL");

        // 超级租户可跨租户查询的表
        properties.getSuperTenantTables().forEach(table -> {
            superTenantTables.add(table.toLowerCase());
            superTenantTables.add(table.toUpperCase());
        });

        // APP端忽略多租户的表
        properties.getAppIgnoreTables().forEach(table -> {
            appIgnoreTables.add(table.toLowerCase());
            appIgnoreTables.add(table.toUpperCase());
        });
    }

    @Override
    public Expression getTenantId() {
        return new LongValue(TenantContextHolder.getRequiredTenantId());
    }

    @Override
    public boolean ignoreTable(String tableName) {
        // 情况一：全局忽略多租户
        if (TenantContextHolder.isIgnore()) {
            return true;
        }
        // 情况二：忽略多租户的表（配置中指定的表不需要租户隔离）
        if (CollUtil.contains(ignoreTables, tableName)) {
            return true;
        }
        // 情况三：租户1（超级租户）对于指定表不添加租户条件，可查看所有数据
        Long tenantId = TenantContextHolder.getTenantId();
        if (tenantId != null && 1L == tenantId
                && CollUtil.contains(superTenantTables, tableName)) {
            return true;
        }
        // 情况四：APP端请求对于指定表不添加租户条件，可查看所有数据
        if (TenantContextHolder.isAppRequest()
               /* && CollUtil.contains(appIgnoreTables, tableName)*/) {
            return true;
        }
        return false;
    }

}
