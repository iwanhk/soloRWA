/**
 * 标准响应格式
 */
export interface StandardResponse<T = any> {
  code: number;
  data: T | null;
  message: string;
}

/**
 * 响应工具类
 */
export class ResponseUtil {
  /**
   * 成功响应
   * @param data 响应数据
   * @param message 成功消息
   */
  static success<T>(data: T, message: string = 'Success'): StandardResponse<T> {
    return {
      code: 0,
      data,
      message,
    };
  }

  /**
   * 错误响应
   * @param code 错误代码
   * @param message 错误消息
   * @param data 错误数据（可选）
   */
  static error<T = null>(
    code: number = 1,
    message: string = 'Error',
    data: T | null = null
  ): StandardResponse<T> {
    return {
      code,
      data,
      message,
    };
  }

  /**
   * 参数验证错误
   */
  static validationError(message: string = 'Validation Error'): StandardResponse {
    return this.error(400, message);
  }

  /**
   * 未找到错误
   */
  static notFound(message: string = 'Not Found'): StandardResponse {
    return this.error(404, message);
  }

  /**
   * 服务器错误
   */
  static serverError(message: string = 'Internal Server Error'): StandardResponse {
    return this.error(500, message);
  }
}

