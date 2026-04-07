import {Inject, Provide} from '@midwayjs/decorator';
import {ethers} from 'ethers';
import {getPrismaClient} from '../utils/prisma';
import {TrexDeployService} from './trex-deploy.service';
import {AbiService} from './abi.service';

@Provide()
export class ContractCallService {
  @Inject()
  trexDeployService: TrexDeployService;

  @Inject()
  abiService: AbiService;

  private get prisma() {
    return getPrismaClient();
  }

  private getProvider() {
    const rpcUrl = process.env.RPC_URL || 'http://localhost:8545';
    return new ethers.JsonRpcProvider(rpcUrl);
  }

  /**
   * 调用合约方法
   * @param contractAddress 合约地址
   * @param contractType 合约类型（来自 CONTRACT_TYPES）
   * @param methodName 方法名
   * @param params 方法参数数组
   * @param callerAddress 调用者地址（可选，用于状态改变的方法）
   * @param gas gas price (wei)，可选
   * @param gasLimit gas limit，可选
   * @returns 调用结果
   */
  async callContract(
    contractAddress: string,
    contractType: string,
    methodName: string,
    params: any[] = [],
    callerAddress?: string,
    gas?: string,
    gasLimit?: string
  ) {
    // 验证合约地址格式
    if (!ethers.isAddress(contractAddress)) {
      throw new Error(`Invalid contract address: ${contractAddress}`);
    }

    // 获取对应的 ABI
    const abi = this.abiService.getABI(contractType);

    // 创建合约实例
    let contract: ethers.Contract;
    if (callerAddress) {
      // 需要签名者的方法
      const wallet = await this.getWalletByAddress(callerAddress);
      if (!wallet) {
        throw new Error(`Wallet not found for address: ${callerAddress}`);
      }
      contract = new ethers.Contract(contractAddress, abi, wallet);
    } else {
      // 只读方法
      const provider = this.getProvider();
      contract = new ethers.Contract(contractAddress, abi, provider);
    }

    try {
      // 构建交易选项
      const txOptions: any = {};
      if (gas) {
        txOptions.gasPrice = gas;
      }
      if (gasLimit) {
        txOptions.gasLimit = gasLimit;
      }

      // 调用方法
      const result = await contract[methodName](...params, txOptions);

      // 如果是交易（有 wait 方法），等待确认
      if (result && typeof result.wait === 'function') {
        const receipt = await result.wait();
        return {
          success: true,
          transactionHash: receipt.hash,
          blockNumber: receipt.blockNumber,
          gasUsed: receipt.gasUsed.toString(),
          status: receipt.status,
        };
      }

      // 如果是查询结果，根据 ABI 解析结果名称
      const methodAbi = this.getMethodAbi(abi, methodName);
      const parsedResult = this.parseResultWithNames(result, methodAbi);

      return {
        success: true,
        result: parsedResult,
      };
    } catch (error) {
      throw new Error(`Failed to call ${methodName}: ${error.message}`);
    }
  }

  /**
   * 估算 gas 费用
   * @param contractAddress 合约地址
   * @param contractType 合约类型
   * @param methodName 方法名
   * @param params 方法参数数组
   * @param callerAddress 调用者地址（可选）
   * @returns gas 估算结果
   */
  async estimateGas(
    contractAddress: string,
    contractType: string,
    methodName: string,
    params: any[] = [],
    callerAddress?: string
  ) {
    // 验证合约地址格式
    if (!ethers.isAddress(contractAddress)) {
      throw new Error(`Invalid contract address: ${contractAddress}`);
    }

    // 获取对应的 ABI
    const abi = this.abiService.getABI(contractType);

    // 创建合约实例
    let contract: ethers.Contract;
    const provider = this.getProvider();

    if (callerAddress) {
      // 需要签名者的方法
      const wallet = await this.getWalletByAddress(callerAddress);
      if (!wallet) {
        throw new Error(`Wallet not found for address: ${callerAddress}`);
      }
      contract = new ethers.Contract(contractAddress, abi, wallet);
    } else {
      // 只读方法
      contract = new ethers.Contract(contractAddress, abi, provider);
    }

    try {
      // 估算 gas
      const gasEstimate = await contract[methodName].estimateGas(...params);


      // 计算预估费用（加上 20% 的缓冲）
      const gasLimit = (gasEstimate * BigInt(120)) / BigInt(100);

      return {
        success: true,
        gasEstimate: gasEstimate.toString(),
        gasLimit: gasLimit.toString(),
      };
    } catch (error) {
      throw new Error(`Failed to estimate gas for ${methodName}: ${error.message}`);
    }
  }





  /**
   * 从 ABI 中获取指定方法的定义
   */
  private getMethodAbi(abi: any[], methodName: string): any {
    return abi.find(item => item.type === 'function' && item.name === methodName);
  }

  /**
   * 根据 ABI 的 outputs 定义来解析结果名称
   * 如果有多个返回值，会创建一个对象，键为返回值的名称
   */
  private parseResultWithNames(result: any, methodAbi: any): any {
    if (!methodAbi || !methodAbi.outputs || methodAbi.outputs.length === 0) {
      // 没有返回值
      return null;
    }

    // 如果只有一个返回值
    if (methodAbi.outputs.length === 1) {
      const output = methodAbi.outputs[0];
      const formattedValue = this.formatResult(result);

      // 如果有名称，返回对象；否则直接返回值
      if (output.name) {
        return {
          [output.name]: formattedValue,
        };
      }
      return formattedValue;
    }

    // 如果有多个返回值
    const parsedResult: any = {};
    for (let i = 0; i < methodAbi.outputs.length; i++) {
      const output = methodAbi.outputs[i];
      const value = result[i];
      const formattedValue = this.formatResult(value);

      // 使用返回值的名称作为键，如果没有名称则使用索引
      const key = output.name || `output_${i}`;
      parsedResult[key] = formattedValue;
    }

    return parsedResult;
  }

  /**
   * 获取钱包
   */
  private async getWalletByAddress(address: string) {
    const blockchainAddress = await this.prisma.blockchainAddress.findFirst({
      where: { address },
    });
    if (!blockchainAddress) {
      return null;
    }
    return new ethers.Wallet(blockchainAddress.privateKey, this.getProvider());
  }

  /**
   * 格式化结果（处理 bigint 等特殊类型）
   */
  private formatResult(result: any): any {
    if (typeof result === 'bigint') {
      return result.toString();
    }
    if (Array.isArray(result)) {
      return result.map(item => this.formatResult(item));
    }
    if (typeof result === 'object' && result !== null) {
      const formatted: any = {};
      for (const key in result) {
        if (!isNaN(Number(key))) continue; // 跳过数组索引
        formatted[key] = this.formatResult(result[key]);
      }
      return formatted;
    }
    return result;
  }
}

