import { Middleware } from '@midwayjs/decorator';
import { Context, NextFunction } from '@midwayjs/koa';
import { ResponseUtil } from '../utils/response';

/**
 * 全局错误处理中间件
 */
@Middleware()
export class ErrorMiddleware {
  resolve() {
    return async (ctx: Context, next: NextFunction) => {
      try {
        await next();
      } catch (error) {
        console.error('Error in ErrorMiddleware:', error);
        // 处理错误
        const message = error.message || 'Internal Server Error';
        const statusCode = typeof error.code === 'number' ? error.code : 500;

        ctx.body = ResponseUtil.error(statusCode, message);
        ctx.status = statusCode;
      }
    };
  }
}

