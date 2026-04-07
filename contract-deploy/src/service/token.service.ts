import { Provide, Inject } from '@midwayjs/decorator';
import { ethers } from 'ethers';
import { getPrismaClient } from '../utils/prisma';
import { TrexDeployService, CONTRACT_TYPES } from './trex-deploy.service';

const erc3643 = require('@erc3643org/erc-3643');

const MAX_TOKENS_PER_IRS = 300;

@Provide()
export class TokenService {
  @Inject()
  trexDeployService: TrexDeployService;

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
      throw new Error('Deployer wallet not found');
    }
    return new ethers.Wallet(deployer.privateKey, this.getProvider());
  }

  private async getLatestNonce(wallet: ethers.Wallet): Promise<number> {
    const provider = this.getProvider();
    return provider.getTransactionCount(wallet.address, 'pending');
  }

  // 获取可用的 IdentityRegistryStorage
  async getAvailableIRS() {
    return this.prisma.identityRegistryStorage.findFirst({
      where: { boundTokenCount: { lt: MAX_TOKENS_PER_IRS } },
      orderBy: { boundTokenCount: 'asc' },
    });
  }

  // 创建新的 IdentityRegistryStorage
  async createIdentityRegistryStorage() {
    const erc3643IA = await this.trexDeployService.getDeployedContract(CONTRACT_TYPES.ERC3643_IMPLEMENTATION_AUTHORITY);
    if (!erc3643IA) {
      throw new Error('ERC3643 ImplementationAuthority must be deployed first. Call /api/trex/initialize-erc3643 first.');
    }

    const wallet = await this.getDeployerWallet();
    let nonce = await this.getLatestNonce(wallet);

    // 部署 IdentityRegistryStorageProxy
    const irsProxyFactory = new ethers.ContractFactory(
      erc3643.contracts.IdentityRegistryStorageProxy.abi,
      erc3643.contracts.IdentityRegistryStorageProxy.bytecode,
      wallet
    );
    const irsProxy = await irsProxyFactory.deploy(erc3643IA.deploymentAddress, { nonce: nonce++ });
    const receipt = await irsProxy.waitForDeployment();
    const address = await irsProxy.getAddress();
    const txReceipt = await receipt.deploymentTransaction()?.wait();

    // 转移 IRS 的 owner 到 TREXFactory
    const trexFactory = await this.trexDeployService.getDeployedContract(CONTRACT_TYPES.ERC3643_FACTORY);
    if (trexFactory) {
    nonce = await this.getLatestNonce(wallet);
    const irsContract = new ethers.Contract(
      address,
      erc3643.contracts.IdentityRegistryStorage.abi,
      wallet
    );
      const transferTx = await irsContract.transferOwnership(trexFactory.deploymentAddress, { nonce });
      await transferTx.wait();
    }

    return this.prisma.identityRegistryStorage.create({
      data: {
        address,
        deployerAddress: wallet.address,
        transactionHash: txReceipt?.hash,
        blockNumber: txReceipt?.blockNumber,
        boundTokenCount: 0,
        status: 'deployed',
      },
    });
  }

  // 获取所有 Token
  async findAll() {
    return this.prisma.token.findMany({
      include: { identityRegistryStorage: true },
    });
  }

  // 根据 ID 获取 Token
  async findById(id: number) {
    return this.prisma.token.findUnique({
      where: { id },
      include: { identityRegistryStorage: true },
    });
  }

  // 获取所有 IdentityRegistryStorage
  async findAllIRS() {
    return this.prisma.identityRegistryStorage.findMany({
      include: { tokens: true },
    });
  }

  // 根据 ID 获取 IdentityRegistryStorage
  async findIRSById(id: number) {
    return this.prisma.identityRegistryStorage.findUnique({
      where: { id },
      include: { tokens: true },
    });
  }

  // Mint 代币
  async mint(tokenId: number, toAddress: string, amount: string, agentAddress?: string) {
    const token = await this.prisma.token.findUnique({
      where: { id: tokenId },
    });

    if (!token) {
      throw new Error('Token not found');
    }

    if (!token.address) {
      throw new Error('Token not deployed yet');
    }

    // 确定使用的 agent 地址
    let finalAgentAddress = agentAddress;
    if (!finalAgentAddress) {
      // 如果没有提供 agent 地址，使用 token 配置中的第一个
      const tokenAgents = token.tokenAgents as string[];
      if (!tokenAgents || tokenAgents.length === 0) {
        throw new Error('No token agents configured');
      }
      finalAgentAddress = tokenAgents[0];
    }

    const agentWallet = await this.getWalletByAddress(finalAgentAddress);
    if (!agentWallet) {
      throw new Error(`Agent wallet not found for address: ${finalAgentAddress}`);
    }

    const nonce = await this.getLatestNonce(agentWallet);

    const tokenContract = new ethers.Contract(
      token.address,
      erc3643.contracts.Token.abi,
      agentWallet
    );

    const tx = await tokenContract.mint(toAddress, ethers.parseUnits(amount, token.decimals), { nonce });
    const receipt = await tx.wait();

    return {
      tokenId,
      toAddress,
      amount,
      agentAddress: finalAgentAddress,
      transactionHash: receipt.hash,
      blockNumber: receipt.blockNumber,
    };
  }

  // 根据地址获取钱包
  private async getWalletByAddress(address: string) {
    const blockchainAddress = await this.prisma.blockchainAddress.findFirst({
      where: { address },
    });
    if (!blockchainAddress) {
      return null;
    }
    return new ethers.Wallet(blockchainAddress.privateKey, this.getProvider());
  }
}

