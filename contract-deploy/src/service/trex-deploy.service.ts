import { Inject, Provide } from '@midwayjs/decorator';
import { ethers } from 'ethers';
import { getPrismaClient } from '../utils/prisma';
import { BlockchainAddressService } from './blockchain-address.service';

// ERC3643 合约
const erc3643 = require('@erc3643org/erc-3643');
// OnchainID 合约
const onchainId = require('@onchain-id/solidity');

// 合约类型常量
export const CONTRACT_TYPES = {
  // OnChainId 合约
  ONCHAIN_ID_IDENTITY: 'OnChainId+Identity',
  ONCHAIN_ID_IMPLEMENTATION_AUTHORITY: 'OnChainId+ImplementationAuthority',
  ONCHAIN_ID_ID_FACTORY: 'OnChainId+IdFactory',
  ONCHAIN_ID_CLAIM_ISSUER: 'OnChainId+ClaimIssuer',
  // ERC3643 合约
  ERC3643_TOKEN_IMPL: 'ERC3643+TokenImpl',
  ERC3643_CLAIM_TOPICS_REGISTRY_IMPL: 'ERC3643+ClaimTopicsRegistryImpl',
  ERC3643_TRUSTED_ISSUERS_REGISTRY_IMPL: 'ERC3643+TrustedIssuersRegistryImpl',
  ERC3643_IDENTITY_REGISTRY_STORAGE_IMPL: 'ERC3643+IdentityRegistryStorageImpl',
  ERC3643_IDENTITY_REGISTRY_IMPL: 'ERC3643+IdentityRegistryImpl',
  ERC3643_MODULAR_COMPLIANCE_IMPL: 'ERC3643+ModularComplianceImpl',
  ERC3643_IMPLEMENTATION_AUTHORITY: 'ERC3643+ImplementationAuthority',
  ERC3643_FACTORY: 'ERC3643+TREXFactory',
  ERC3643_IA_FACTORY: 'ERC3643+IAFactory',
} as const;

// 初始化步骤常量
export const INIT_STEPS = {
  // OnChainId 步骤
  ONCHAIN_ID_IDENTITY: 'init_onchainid_identity',
  ONCHAIN_ID_IA: 'init_onchainid_ia',
  ONCHAIN_ID_FACTORY: 'init_onchainid_factory',
  ONCHAIN_ID_CLAIM_ISSUER: 'init_onchainid_claim_issuer',
  // ERC3643 实现合约步骤
  ERC3643_TOKEN_IMPL: 'init_erc3643_token_impl',
  ERC3643_CTR_IMPL: 'init_erc3643_ctr_impl',
  ERC3643_TIR_IMPL: 'init_erc3643_tir_impl',
  ERC3643_IRS_IMPL: 'init_erc3643_irs_impl',
  ERC3643_IR_IMPL: 'init_erc3643_ir_impl',
  ERC3643_MC_IMPL: 'init_erc3643_mc_impl',
  // ERC3643 基础设施步骤
  ERC3643_IA: 'init_erc3643_ia',
  ERC3643_IA_VERSION: 'init_erc3643_ia_version',
  ERC3643_FACTORY: 'init_erc3643_factory',
  ERC3643_IA_FACTORY: 'init_erc3643_ia_factory',
  ERC3643_IA_CONFIG: 'init_erc3643_ia_config',
  // 首个 IRS 部署
  FIRST_IRS: 'init_first_irs',
} as const;

@Provide()
export class TrexDeployService {
  @Inject()
  blockchainAddressService: BlockchainAddressService;

  private get prisma() {
    return getPrismaClient();
  }

  private getProvider() {
    const rpcUrl = process.env.RPC_URL || 'http://localhost:8545';
    return new ethers.JsonRpcProvider(rpcUrl);
  }

  private async getDeployerWallet() {
    const deployer = await this.prisma.blockchainAddress.findFirst({
      where: { name: 'deployer' },
    });
    if (!deployer) {
      throw new Error('Deployer wallet not found. Please create a wallet with name "deployer"');
    }
    return new ethers.Wallet(deployer.privateKey, this.getProvider());
  }

  private async getLatestNonce(wallet: ethers.Wallet): Promise<number> {
    const provider = this.getProvider();
    return provider.getTransactionCount(wallet.address, 'pending');
  }

  // ==================== 初始化状态管理 ====================

  private async getInitStep(step: string) {
    return this.prisma.systemInitialization.findUnique({ where: { step } });
  }

  private async isStepCompleted(step: string): Promise<boolean> {
    const record = await this.getInitStep(step);
    return record?.status === 'completed';
  }

  private async updateInitStep(
    step: string,
    status: 'pending' | 'in_progress' | 'completed' | 'failed',
    data?: { contractAddress?: string; transactionHash?: string; blockNumber?: number; errorMessage?: string; metadata?: any }
  ) {
    return this.prisma.systemInitialization.upsert({
      where: { step },
      create: { step, status, ...data },
      update: { status, ...data },
    });
  }

  async getDeployedContract(contractType: string) {
    return this.prisma.contractDeployment.findFirst({ where: { contractType } });
  }

  async isContractDeployed(contractType: string): Promise<boolean> {
    const existing = await this.getDeployedContract(contractType);
    return !!existing;
  }

  // ==================== 获取初始化状态 ====================

  async getInitializationStatus() {
    const steps = await this.prisma.systemInitialization.findMany({
      orderBy: { createdAt: 'asc' },
    });
    const contracts = await this.prisma.contractDeployment.findMany();
    const irsCount = await this.prisma.identityRegistryStorage.count();

    const allSteps = Object.values(INIT_STEPS);
    const completedSteps = steps.filter(s => s.status === 'completed').length;
    const isFullyInitialized = completedSteps === allSteps.length;

    return {
      isFullyInitialized,
      progress: `${completedSteps}/${allSteps.length}`,
      steps,
      contracts,
      identityRegistryStorageCount: irsCount,
    };
  }

  // ==================== 统一初始化函数 ====================

  /**
   * 初始化系统 - 部署 Token 之前的所有准备工作
   * 支持断点续传，如果中断可以继续执行
   */
  async initializeSystem(claimIssuerManagementKey?: string) {
    const wallet = await this.getDeployerWallet();
    const managementKey = claimIssuerManagementKey || wallet.address;
    const results: Record<string, any> = {};

    // ========== 第一阶段：OnChainId 部署 ==========

    // Step 1: 部署 Identity 实现
    if (!(await this.isStepCompleted(INIT_STEPS.ONCHAIN_ID_IDENTITY))) {
      await this.updateInitStep(INIT_STEPS.ONCHAIN_ID_IDENTITY, 'in_progress');
      try {
        const address = await this.deployContract(
          onchainId.contracts.Identity,
          CONTRACT_TYPES.ONCHAIN_ID_IDENTITY,
          wallet,
          [wallet.address, true]
        );
        await this.updateInitStep(INIT_STEPS.ONCHAIN_ID_IDENTITY, 'completed', { contractAddress: address });
        results.identityImpl = address;
      } catch (error) {
        await this.updateInitStep(INIT_STEPS.ONCHAIN_ID_IDENTITY, 'failed', { errorMessage: error.message });
        throw error;
      }
    } else {
      const contract = await this.getDeployedContract(CONTRACT_TYPES.ONCHAIN_ID_IDENTITY);
      results.identityImpl = contract?.deploymentAddress;
    }

    // Step 2: 部署 Identity ImplementationAuthority
    if (!(await this.isStepCompleted(INIT_STEPS.ONCHAIN_ID_IA))) {
      await this.updateInitStep(INIT_STEPS.ONCHAIN_ID_IA, 'in_progress');
      try {
        const address = await this.deployContract(
          onchainId.contracts.ImplementationAuthority,
          CONTRACT_TYPES.ONCHAIN_ID_IMPLEMENTATION_AUTHORITY,
          wallet,
          [results.identityImpl]
        );
        await this.updateInitStep(INIT_STEPS.ONCHAIN_ID_IA, 'completed', { contractAddress: address });
        results.identityIA = address;
      } catch (error) {
        await this.updateInitStep(INIT_STEPS.ONCHAIN_ID_IA, 'failed', { errorMessage: error.message });
        throw error;
      }
    } else {
      const contract = await this.getDeployedContract(CONTRACT_TYPES.ONCHAIN_ID_IMPLEMENTATION_AUTHORITY);
      results.identityIA = contract?.deploymentAddress;
    }

    // Step 3: 部署 Identity Factory
    if (!(await this.isStepCompleted(INIT_STEPS.ONCHAIN_ID_FACTORY))) {
      await this.updateInitStep(INIT_STEPS.ONCHAIN_ID_FACTORY, 'in_progress');
      try {
        const address = await this.deployContract(
          onchainId.contracts.Factory,
          CONTRACT_TYPES.ONCHAIN_ID_ID_FACTORY,
          wallet,
          [results.identityIA]
        );
        await this.updateInitStep(INIT_STEPS.ONCHAIN_ID_FACTORY, 'completed', { contractAddress: address });
        results.identityFactory = address;
      } catch (error) {
        await this.updateInitStep(INIT_STEPS.ONCHAIN_ID_FACTORY, 'failed', { errorMessage: error.message });
        throw error;
      }
    } else {
      const contract = await this.getDeployedContract(CONTRACT_TYPES.ONCHAIN_ID_ID_FACTORY);
      results.identityFactory = contract?.deploymentAddress;
    }

    // Step 4: 部署 ClaimIssuer
    if (!(await this.isStepCompleted(INIT_STEPS.ONCHAIN_ID_CLAIM_ISSUER))) {
      await this.updateInitStep(INIT_STEPS.ONCHAIN_ID_CLAIM_ISSUER, 'in_progress');
      try {
        const address = await this.deployContract(
          onchainId.contracts.ClaimIssuer,
          CONTRACT_TYPES.ONCHAIN_ID_CLAIM_ISSUER,
          wallet,
          [managementKey]
        );
        await this.updateInitStep(INIT_STEPS.ONCHAIN_ID_CLAIM_ISSUER, 'completed', {
          contractAddress: address,
          metadata: { managementKey },
        });
        results.claimIssuer = address;
      } catch (error) {
        await this.updateInitStep(INIT_STEPS.ONCHAIN_ID_CLAIM_ISSUER, 'failed', { errorMessage: error.message });
        throw error;
      }
    } else {
      const contract = await this.getDeployedContract(CONTRACT_TYPES.ONCHAIN_ID_CLAIM_ISSUER);
      results.claimIssuer = contract?.deploymentAddress;
    }

    // ========== 第二阶段：ERC3643 实现合约部署 ==========

    const implSteps = [
      { step: INIT_STEPS.ERC3643_TOKEN_IMPL, contract: erc3643.contracts.Token, type: CONTRACT_TYPES.ERC3643_TOKEN_IMPL, key: 'tokenImpl' },
      { step: INIT_STEPS.ERC3643_CTR_IMPL, contract: erc3643.contracts.ClaimTopicsRegistry, type: CONTRACT_TYPES.ERC3643_CLAIM_TOPICS_REGISTRY_IMPL, key: 'ctrImpl' },
      { step: INIT_STEPS.ERC3643_TIR_IMPL, contract: erc3643.contracts.TrustedIssuersRegistry, type: CONTRACT_TYPES.ERC3643_TRUSTED_ISSUERS_REGISTRY_IMPL, key: 'tirImpl' },
      { step: INIT_STEPS.ERC3643_IRS_IMPL, contract: erc3643.contracts.IdentityRegistryStorage, type: CONTRACT_TYPES.ERC3643_IDENTITY_REGISTRY_STORAGE_IMPL, key: 'irsImpl' },
      { step: INIT_STEPS.ERC3643_IR_IMPL, contract: erc3643.contracts.IdentityRegistry, type: CONTRACT_TYPES.ERC3643_IDENTITY_REGISTRY_IMPL, key: 'irImpl' },
      { step: INIT_STEPS.ERC3643_MC_IMPL, contract: erc3643.contracts.ModularCompliance, type: CONTRACT_TYPES.ERC3643_MODULAR_COMPLIANCE_IMPL, key: 'mcImpl' },
    ];

    for (const impl of implSteps) {
      if (!(await this.isStepCompleted(impl.step))) {
        await this.updateInitStep(impl.step, 'in_progress');
        try {
          const address = await this.deployContract(impl.contract, impl.type, wallet, []);
          await this.updateInitStep(impl.step, 'completed', { contractAddress: address });
          results[impl.key] = address;
        } catch (error) {
          await this.updateInitStep(impl.step, 'failed', { errorMessage: error.message });
          throw error;
        }
      } else {
        const contract = await this.getDeployedContract(impl.type);
        results[impl.key] = contract?.deploymentAddress;
      }
    }

    // ========== 第三阶段：TREX IA 和工厂部署 ==========

    // Step: 部署 TREXImplementationAuthority
    if (!(await this.isStepCompleted(INIT_STEPS.ERC3643_IA))) {
      await this.updateInitStep(INIT_STEPS.ERC3643_IA, 'in_progress');
      try {
        const address = await this.deployContract(
          erc3643.contracts.TREXImplementationAuthority,
          CONTRACT_TYPES.ERC3643_IMPLEMENTATION_AUTHORITY,
          wallet,
          [true, ethers.ZeroAddress, ethers.ZeroAddress]
        );
        await this.updateInitStep(INIT_STEPS.ERC3643_IA, 'completed', { contractAddress: address });
        results.trexIA = address;
      } catch (error) {
        await this.updateInitStep(INIT_STEPS.ERC3643_IA, 'failed', { errorMessage: error.message });
        throw error;
      }
    } else {
      const contract = await this.getDeployedContract(CONTRACT_TYPES.ERC3643_IMPLEMENTATION_AUTHORITY);
      results.trexIA = contract?.deploymentAddress;
    }

    // Step: 添加版本到 TREX IA
    if (!(await this.isStepCompleted(INIT_STEPS.ERC3643_IA_VERSION))) {
      await this.updateInitStep(INIT_STEPS.ERC3643_IA_VERSION, 'in_progress');
      try {
        const nonce = await this.getLatestNonce(wallet);
        const trexIAContract = new ethers.Contract(
          results.trexIA,
          erc3643.contracts.TREXImplementationAuthority.abi,
          wallet
        );
        const versionStruct = { major: 4, minor: 0, patch: 0 };
        const contractsStruct = {
          tokenImplementation: results.tokenImpl,
          ctrImplementation: results.ctrImpl,
          irImplementation: results.irImpl,
          irsImplementation: results.irsImpl,
          tirImplementation: results.tirImpl,
          mcImplementation: results.mcImpl,
        };
        const tx = await trexIAContract.addAndUseTREXVersion(versionStruct, contractsStruct, { nonce });
        const receipt = await tx.wait();
        await this.updateInitStep(INIT_STEPS.ERC3643_IA_VERSION, 'completed', {
          transactionHash: receipt.hash,
          blockNumber: receipt.blockNumber,
          metadata: { version: '4.0.0' },
        });
      } catch (error) {
        await this.updateInitStep(INIT_STEPS.ERC3643_IA_VERSION, 'failed', { errorMessage: error.message });
        throw error;
      }
    }

    // Step: 部署 TREXFactory
    if (!(await this.isStepCompleted(INIT_STEPS.ERC3643_FACTORY))) {
      await this.updateInitStep(INIT_STEPS.ERC3643_FACTORY, 'in_progress');
      try {
        const address = await this.deployContract(
          erc3643.contracts.TREXFactory,
          CONTRACT_TYPES.ERC3643_FACTORY,
          wallet,
          [results.trexIA, results.identityFactory]
        );
        // 注册 TREXFactory 到 Identity Factory
        const nonce = await this.getLatestNonce(wallet);
        const idFactoryContract = new ethers.Contract(
          results.identityFactory,
          onchainId.contracts.Factory.abi,
          wallet
        );
        const tx = await idFactoryContract.addTokenFactory(address, { nonce });
        await tx.wait();
        await this.updateInitStep(INIT_STEPS.ERC3643_FACTORY, 'completed', { contractAddress: address });
        results.trexFactory = address;
      } catch (error) {
        await this.updateInitStep(INIT_STEPS.ERC3643_FACTORY, 'failed', { errorMessage: error.message });
        throw error;
      }
    } else {
      const contract = await this.getDeployedContract(CONTRACT_TYPES.ERC3643_FACTORY);
      results.trexFactory = contract?.deploymentAddress;
    }

    // Step: 部署 IAFactory
    if (!(await this.isStepCompleted(INIT_STEPS.ERC3643_IA_FACTORY))) {
      await this.updateInitStep(INIT_STEPS.ERC3643_IA_FACTORY, 'in_progress');
      try {
        const address = await this.deployContract(
          erc3643.interfaces.IAFactory,
          CONTRACT_TYPES.ERC3643_IA_FACTORY,
          wallet,
          [results.trexFactory]
        );
        await this.updateInitStep(INIT_STEPS.ERC3643_IA_FACTORY, 'completed', { contractAddress: address });
        results.iaFactory = address;
      } catch (error) {
        await this.updateInitStep(INIT_STEPS.ERC3643_IA_FACTORY, 'failed', { errorMessage: error.message });
        throw error;
      }
    } else {
      const contract = await this.getDeployedContract(CONTRACT_TYPES.ERC3643_IA_FACTORY);
      results.iaFactory = contract?.deploymentAddress;
    }

    // Step: 配置 TREX IA
    if (!(await this.isStepCompleted(INIT_STEPS.ERC3643_IA_CONFIG))) {
      await this.updateInitStep(INIT_STEPS.ERC3643_IA_CONFIG, 'in_progress');
      try {
        const trexIAContract = new ethers.Contract(
          results.trexIA,
          erc3643.contracts.TREXImplementationAuthority.abi,
          wallet
        );
        let nonce = await this.getLatestNonce(wallet);
        const tx1 = await trexIAContract.setTREXFactory(results.trexFactory, { nonce: nonce++ });
        await tx1.wait();
        nonce = await this.getLatestNonce(wallet);
        const tx2 = await trexIAContract.setIAFactory(results.iaFactory, { nonce });
        await tx2.wait();
        await this.updateInitStep(INIT_STEPS.ERC3643_IA_CONFIG, 'completed');
      } catch (error) {
        await this.updateInitStep(INIT_STEPS.ERC3643_IA_CONFIG, 'failed', { errorMessage: error.message });
        throw error;
      }
    }

    // ========== 第四阶段：部署首个 IdentityRegistryStorage ==========

    if (!(await this.isStepCompleted(INIT_STEPS.FIRST_IRS))) {
      await this.updateInitStep(INIT_STEPS.FIRST_IRS, 'in_progress');
      try {
        const irsAddress = await this.deployIdentityRegistryStorageInternal(wallet, results.trexIA);
        await this.updateInitStep(INIT_STEPS.FIRST_IRS, 'completed', { contractAddress: irsAddress });
        results.firstIRS = irsAddress;
      } catch (error) {
        await this.updateInitStep(INIT_STEPS.FIRST_IRS, 'failed', { errorMessage: error.message });
        throw error;
      }
    } else {
      const irs = await this.prisma.identityRegistryStorage.findFirst({ orderBy: { id: 'asc' } });
      results.firstIRS = irs?.address;
    }

    return {
      success: true,
      message: 'System initialized successfully',
      contracts: results,
    };
  }

  // ==================== 部署 Token ====================

  async deployToken(data: {
    salt: string;
    ownerAddress: string;
    name: string;
    symbol: string;
    decimals?: number;
    tokenAgents?: string[];
    claimTopics?: string[];
    issuers?: string[];
    issuerClaims?: string[][];
  }) {
    // 检查系统是否已初始化
    const status = await this.getInitializationStatus();
    if (!status.isFullyInitialized) {
      throw new Error('System not fully initialized. Please call initializeSystem() first.');
    }

    // 获取可用的 IdentityRegistryStorage
    const irs = await this.prisma.identityRegistryStorage.findFirst({
      where: { boundTokenCount: { lt: 300 } },
      orderBy: { boundTokenCount: 'asc' },
    });
    if (!irs) {
      throw new Error('No available IdentityRegistryStorage. All storages have reached the limit of 300 tokens. Please deploy a new one.');
    }

    const wallet = await this.getDeployerWallet();
    const trexFactory = await this.getDeployedContract(CONTRACT_TYPES.ERC3643_FACTORY);
    if (!trexFactory) {
      throw new Error('TREXFactory not deployed. Please call initializeSystem() first.');
    }

    // 创建 Token 记录
    const token = await this.prisma.token.create({
      data: {
        name: data.name,
        symbol: data.symbol,
        decimals: data.decimals || 18,
        ownerAddress: data.ownerAddress,
        deployerAddress: wallet.address,
        identityRegistryStorageId: irs.id,
        salt: data.salt,
        tokenAgents: data.tokenAgents || [wallet.address],
        claimTopics: data.claimTopics || [],
        issuers: data.issuers || [],
        issuerClaims: data.issuerClaims || [],
        status: 'pending',
      },
    });

    try {
      const factoryContract = new ethers.Contract(
        trexFactory.deploymentAddress,
        erc3643.contracts.TREXFactory.abi,
        wallet
      );

      const nonce = await this.getLatestNonce(wallet);
      const tx = await factoryContract.deployTREXSuite(
        data.salt,
        {
          owner: data.ownerAddress,
          name: data.name,
          symbol: data.symbol,
          decimals: data.decimals || 18,
          irs: irs.address,
          ONCHAINID: ethers.ZeroAddress,
          irAgents: [wallet.address],
          tokenAgents: data.tokenAgents || [wallet.address],
          complianceModules: [],
          complianceSettings: [],
        },
        {
          claimTopics: data.claimTopics || [],
          issuers: data.issuers || [],
          issuerClaims: data.issuerClaims || [],
        },
        { nonce }
      );
      const receipt = await tx.wait();

      // 解析事件获取部署的合约地址
      const iface = new ethers.Interface(erc3643.contracts.TREXFactory.abi);
      let tokenAddress = null;
      let irAddress = null;
      let ctrAddress = null;
      let tirAddress = null;
      let mcAddress = null;
      let tokenOidAddress = null;

      for (const log of receipt.logs) {
        try {
          const parsed = iface.parseLog({ topics: log.topics as string[], data: log.data });
          if (parsed?.name === 'TREXSuiteDeployed') {
            tokenAddress = parsed.args._token;
            irAddress = parsed.args._ir;
            ctrAddress = parsed.args._ctr;
            tirAddress = parsed.args._tir;
            mcAddress = parsed.args._mc;
            tokenOidAddress = parsed.args._tokenOID;
            break;
          }
        } catch {
          // 忽略解析失败的日志
        }
      }

      // 更新 Token 记录
      const updatedToken = await this.prisma.token.update({
        where: { id: token.id },
        data: {
          address: tokenAddress,
          identityRegistryAddress: irAddress,
          claimTopicsRegistryAddress: ctrAddress,
          trustedIssuersRegistryAddress: tirAddress,
          modularComplianceAddress: mcAddress,
          tokenOnchainIdAddress: tokenOidAddress,
          transactionHash: receipt.hash,
          blockNumber: receipt.blockNumber,
          status: 'deployed',
        },
      });

      // 更新 IRS 的 boundTokenCount
      await this.prisma.identityRegistryStorage.update({
        where: { id: irs.id },
        data: { boundTokenCount: { increment: 1 }, status: 'active' },
      });

      return updatedToken;
    } catch (error) {
      await this.prisma.token.update({
        where: { id: token.id },
        data: { status: 'failed', errorMessage: error.message },
      });
      throw new Error(`Failed to deploy Token: ${error.message}`);
    }
  }

  // ==================== 部署新的 IdentityRegistryStorage ====================

  async deployIdentityRegistryStorage() {
    // 检查系统是否已初始化
    const trexIA = await this.getDeployedContract(CONTRACT_TYPES.ERC3643_IMPLEMENTATION_AUTHORITY);
    if (!trexIA) {
      throw new Error('System not initialized. Please call initializeSystem() first.');
    }

    const wallet = await this.getDeployerWallet();
    const address = await this.deployIdentityRegistryStorageInternal(wallet, trexIA.deploymentAddress);

    return {
      success: true,
      address,
      message: 'IdentityRegistryStorage deployed successfully',
    };
  }

  private async deployIdentityRegistryStorageInternal(wallet: ethers.Wallet, trexIAAddress: string): Promise<string> {
    // 获取最新的 nonce
    let nonce = await this.getLatestNonce(wallet);

    // 部署 IdentityRegistryStorageProxy
    const factory = new ethers.ContractFactory(
      erc3643.contracts.IdentityRegistryStorageProxy.abi,
      erc3643.contracts.IdentityRegistryStorageProxy.bytecode,
      wallet
    );
    const deployed = await factory.deploy(trexIAAddress, { nonce });
    await deployed.waitForDeployment();
    const address = await deployed.getAddress();
    const txReceipt = await deployed.deploymentTransaction()?.wait();

    // 转移 IRS 的 owner 到 TREXFactory
    const trexFactory = await this.getDeployedContract(CONTRACT_TYPES.ERC3643_FACTORY);
    if (trexFactory) {
      nonce = await this.getLatestNonce(wallet);
      const irsContract = new ethers.Contract(
        address,
        erc3643.contracts.IdentityRegistryStorage.abi,
        wallet
      );
      const tx = await irsContract.transferOwnership(trexFactory.deploymentAddress, { nonce, gasLimit: "60000" });
      await tx.wait();
    }

    // 记录到数据库
    await this.prisma.identityRegistryStorage.create({
      data: {
        address,
        deployerAddress: wallet.address,
        transactionHash: txReceipt?.hash,
        blockNumber: txReceipt?.blockNumber,
        boundTokenCount: 0,
        status: 'deployed',
      },
    });

    return address;
  }

  // ==================== 辅助方法 ====================

  private async deployContract(
    contract: any,
    contractType: string,
    wallet: ethers.Wallet,
    args: any[]
  ): Promise<string> {
    // 获取最新的 nonce
    const nonce = await this.getLatestNonce(wallet);

    const factory = new ethers.ContractFactory(contract.abi, contract.bytecode, wallet);
    const deployed = await factory.deploy(...args, { nonce });
    await deployed.waitForDeployment();
    const address = await deployed.getAddress();
    const txReceipt = await deployed.deploymentTransaction()?.wait();

    await this.prisma.contractDeployment.create({
      data: {
        contractType,
        deploymentAddress: address,
        deployerAddress: wallet.address,
        transactionHash: txReceipt?.hash,
        blockNumber: txReceipt?.blockNumber,
        status: 'deployed',
      },
    });

    return address;
  }

  // ==================== 查询方法 ====================

  async getTokens() {
    return this.prisma.token.findMany({
      include: { identityRegistryStorage: true },
      orderBy: { createdAt: 'desc' },
    });
  }

  async getTokenById(id: number) {
    return this.prisma.token.findUnique({
      where: { id },
      include: { identityRegistryStorage: true },
    });
  }

  async getIdentityRegistryStorages() {
    return this.prisma.identityRegistryStorage.findMany({
      include: { tokens: true },
      orderBy: { id: 'asc' },
    });
  }
}

