import { Body, Controller, Post, Inject } from '@midwayjs/decorator';
import { ContractCallService } from '../service/contract-call.service';
import { CONTRACT_TYPES } from '../service/trex-deploy.service';
import { ResponseUtil } from '../utils/response';

@Controller('/api/contract-call')
export class ContractCallController {
  @Inject()
  contractCallService: ContractCallService;

  /**
   * 调用合约方法
   * POST /api/contract-call
   * Body: {
   *   contractAddress: string,      // 合约地址
   *   contractType: string,         // 合约类型（来自 CONTRACT_TYPES）
   *   methodName: string,           // 方法名
   *   params?: any[],               // 方法参数数组
   *   callerAddress?: string,       // 调用者地址（用于状态改变的方法）
   *   gas?: string,                 // gas price (wei)，可选
   *   gasLimit?: string             // gas limit，可选
   * }
   */
  @Post('/')
  async callContract(
    @Body('contractAddress') contractAddress: string,
    @Body('contractType') contractType: string,
    @Body('methodName') methodName: string,
    @Body('params') params?: any[],
    @Body('callerAddress') callerAddress?: string,
    @Body('gas') gas?: string,
    @Body('gasLimit') gasLimit?: string
  ) {
    try {
      // 参数验证
      if (!contractAddress || !contractType || !methodName) {
        return ResponseUtil.validationError(
          'contractAddress, contractType, and methodName are required'
        );
      }

      // 验证 contractType 是否在 CONTRACT_TYPES 中
      const validTypes = Object.values(CONTRACT_TYPES);
      if (!validTypes.includes(contractType as any)) {
        return ResponseUtil.validationError(
          `contractType must be one of CONTRACT_TYPES: ${validTypes.join(', ')}`
        );
      }

      // 调用合约方法
      const result = await this.contractCallService.callContract(
        contractAddress,
        contractType,
        methodName,
        params || [],
        callerAddress,
        gas,
        gasLimit
      );

      return ResponseUtil.success(result, 'Contract call executed successfully');
    } catch (error) {
      if (error.message.includes('Invalid')) {
        return ResponseUtil.validationError(error.message);
      }
      if (error.message.includes('Failed to call')) {
        return ResponseUtil.error(400, error.message);
      }
      throw error;
    }
  }

  /**
   * 估算 gas 费用
   * POST /api/contract-call/estimate-gas
   * Body: {
   *   contractAddress: string,      // 合约地址
   *   contractType: string,         // 合约类型（来自 CONTRACT_TYPES）
   *   methodName: string,           // 方法名
   *   params?: any[],               // 方法参数数组
   *   callerAddress?: string        // 调用者地址（用于状态改变的方法）
   * }
   */
  @Post('/estimate-gas')
  async estimateGas(
    @Body('contractAddress') contractAddress: string,
    @Body('contractType') contractType: string,
    @Body('methodName') methodName: string,
    @Body('params') params?: any[],
    @Body('callerAddress') callerAddress?: string
  ) {
    try {
      // 参数验证
      if (!contractAddress || !contractType || !methodName) {
        return ResponseUtil.validationError(
          'contractAddress, contractType, and methodName are required'
        );
      }

      // 验证 contractType 是否在 CONTRACT_TYPES 中
      const validTypes = Object.values(CONTRACT_TYPES);
      if (!validTypes.includes(contractType as any)) {
        return ResponseUtil.validationError(
          `contractType must be one of CONTRACT_TYPES: ${validTypes.join(', ')}`
        );
      }

      // 估算 gas
      const result = await this.contractCallService.estimateGas(
        contractAddress,
        contractType,
        methodName,
        params || [],
        callerAddress
      );

      return ResponseUtil.success(result, 'Gas estimation completed successfully');
    } catch (error) {
      if (error.message.includes('Invalid')) {
        return ResponseUtil.validationError(error.message);
      }
      if (error.message.includes('Failed to estimate')) {
        return ResponseUtil.error(400, error.message);
      }
      throw error;
    }
  }
}

