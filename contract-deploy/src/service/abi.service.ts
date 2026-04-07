import { Provide } from '@midwayjs/decorator';
import { CONTRACT_TYPES } from './trex-deploy.service';

const erc3643 = require('@erc3643org/erc-3643');
const onchainId = require('@onchain-id/solidity');

@Provide()
export class AbiService {
  /**
   * 获取合约 ABI
   */
  getABI(contractType: string): any[] {
    switch (contractType) {
      // ERC3643 合约
      case CONTRACT_TYPES.ERC3643_TOKEN_IMPL:
        return erc3643.contracts.Token.abi;
      case CONTRACT_TYPES.ERC3643_CLAIM_TOPICS_REGISTRY_IMPL:
        return erc3643.contracts.ClaimTopicsRegistry.abi;
      case CONTRACT_TYPES.ERC3643_TRUSTED_ISSUERS_REGISTRY_IMPL:
        return erc3643.contracts.TrustedIssuersRegistry.abi;
      case CONTRACT_TYPES.ERC3643_IDENTITY_REGISTRY_STORAGE_IMPL:
        return erc3643.contracts.IdentityRegistryStorage.abi;
      case CONTRACT_TYPES.ERC3643_IDENTITY_REGISTRY_IMPL:
        return erc3643.contracts.IdentityRegistry.abi;
      case CONTRACT_TYPES.ERC3643_MODULAR_COMPLIANCE_IMPL:
        return erc3643.contracts.ModularCompliance.abi;
      case CONTRACT_TYPES.ERC3643_IMPLEMENTATION_AUTHORITY:
        return erc3643.contracts.ImplementationAuthority.abi;
      case CONTRACT_TYPES.ERC3643_FACTORY:
        return erc3643.contracts.TREXFactory.abi;
      case CONTRACT_TYPES.ERC3643_IA_FACTORY:
        return erc3643.contracts.IAFactory.abi;

      // OnChainId 合约
      case CONTRACT_TYPES.ONCHAIN_ID_IDENTITY:
        return onchainId.contracts.Identity.abi;
      case CONTRACT_TYPES.ONCHAIN_ID_CLAIM_ISSUER:
        return onchainId.contracts.ClaimIssuer.abi;
      case CONTRACT_TYPES.ONCHAIN_ID_ID_FACTORY:
        return onchainId.contracts.Factory.abi;
      case CONTRACT_TYPES.ONCHAIN_ID_IMPLEMENTATION_AUTHORITY:
        return onchainId.contracts.ImplementationAuthority.abi;

      default:
        throw new Error(`Unknown contract type: ${contractType}`);
    }
  }

  /**
   * 从 ABI 中提取方法列表
   * 只返回 function 类型的 ABI 项
   */
  extractMethods(abi: any[]): any[] {
    return abi
      .filter(item => item.type === 'function')
      .map(item => ({
        name: item.name,
        inputs: item.inputs || [],
        outputs: item.outputs || [],
        stateMutability: item.stateMutability || 'nonpayable',
        constant: item.constant || false,
        payable: item.payable || false,
      }));
  }

  /**
   * 获取合约类型对应的所有方法
   */
  getMethodsByContractType(contractType: string): any[] {
    const abi = this.getABI(contractType);
    return this.extractMethods(abi);
  }

  /**
   * 获取所有支持的合约类型
   */
  getSupportedContractTypes(): string[] {
    return Object.values(CONTRACT_TYPES) as string[];
  }
}

