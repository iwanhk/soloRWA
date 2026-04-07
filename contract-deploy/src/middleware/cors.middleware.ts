import { Middleware } from '@midwayjs/decorator';
import { Context, NextFunction } from '@midwayjs/koa';

/**
 * CORS 中间件
 */
@Middleware()
export class CorsMiddleware {
  resolve() {
    return async (ctx: Context, next: NextFunction) => {
      const origin = ctx.get('origin');
      
      // 允许的源列表
      const allowedOrigins = [
        origin
      ];

      // 检查是否允许该源
      if (allowedOrigins.includes(origin)) {
        ctx.set('Access-Control-Allow-Origin', origin);
      }

      // 设置 CORS 响应头
      ctx.set('Access-Control-Allow-Methods', 'GET, POST, PUT, DELETE, PATCH, OPTIONS');
      ctx.set('Access-Control-Allow-Headers', 'Content-Type, Authorization, X-Requested-With');
      ctx.set('Access-Control-Allow-Credentials', 'true');
      ctx.set('Access-Control-Max-Age', '86400');

      // 处理 OPTIONS 请求
      if (ctx.method === 'OPTIONS') {
        ctx.status = 204;
        return;
      }

      await next();
    };
  }
}

