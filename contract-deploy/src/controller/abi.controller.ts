import { Controller, Get, Inject, Param } from '@midwayjs/decorator';
import { AbiService } from '../service/abi.service';
import { ResponseUtil } from '../utils/response';

@Controller('/api/abi')
export class AbiController {
  @Inject()
  abiService: AbiService;

  /**
   * 获取所有支持的合约类型
   * GET /api/abi/contract-types
   */
  @Get('/contract-types')
  async getContractTypes() {
    try {
      const types = this.abiService.getSupportedContractTypes();
      return ResponseUtil.success(types, 'Contract types retrieved successfully');
    } catch (error) {
      return ResponseUtil.error(500, error.message);
    }
  }

  /**
   * 获取合约类型对应的所有方法
   * GET /api/abi/methods/:contractType
   * 
   * 示例:
   * GET /api/abi/methods/ERC3643+TokenImpl
   */
  @Get('/methods/:contractType')
  async getMethodsByContractType(@Param('contractType') contractType: string) {
    try {
      // URL 解码 contractType（因为 + 号会被编码为 %2B）
      const decodedType = decodeURIComponent(contractType);
      
      const methods = this.abiService.getMethodsByContractType(decodedType);
      return ResponseUtil.success(methods, 'Methods retrieved successfully');
    } catch (error) {
      if (error.message.includes('Unknown contract type')) {
        return ResponseUtil.validationError(error.message);
      }
      return ResponseUtil.error(500, error.message);
    }
  }

  /**
   * 获取完整的 ABI
   * GET /api/abi/:contractType
   * 
   * 示例:
   * GET /api/abi/ERC3643+TokenImpl
   */
  @Get('/:contractType')
  async getABI(@Param('contractType') contractType: string) {
    try {
      // URL 解码 contractType
      const decodedType = decodeURIComponent(contractType);
      
      const abi = this.abiService.getABI(decodedType);
      return ResponseUtil.success(abi, 'ABI retrieved successfully');
    } catch (error) {
      if (error.message.includes('Unknown contract type')) {
        return ResponseUtil.validationError(error.message);
      }
      return ResponseUtil.error(500, error.message);
    }
  }
}

