package cc.bamboo.framework.tenant.core.web;

import cc.bamboo.framework.tenant.core.context.TenantContextHolder;
import cc.bamboo.framework.web.core.util.WebFrameworkUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 多租户 Context Web 过滤器
 * 将请求 Header 中的 tenant-id 解析出来，添加到 {@link TenantContextHolder} 中，这样后续的 DB
 * 等操作，可以获得到租户编号。
 *
 * @author 芋道源码
 */
public class TenantContextWebFilter extends OncePerRequestFilter {

    /**
     * APP端API的URL前缀
     */
    private static final String APP_API_PREFIX = "/app-api/";

    private static final String RPC_API_PREFIX = "/rpc-api/";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        // 设置租户ID
        Long tenantId = WebFrameworkUtils.getTenantId(request);
        if (tenantId != null) {
            TenantContextHolder.setTenantId(tenantId);
        }

        // 判断是否为APP端请求
        String requestURI = request.getRequestURI();
        System.out.println("requestURI=============:"+requestURI);
        if (requestURI != null && (requestURI.contains(APP_API_PREFIX) || requestURI.contains(RPC_API_PREFIX))) {
            TenantContextHolder.setAppRequest(true);
        }


        try {
            chain.doFilter(request, response);
        } finally {
            // 清理
            TenantContextHolder.clear();
        }
    }

}
