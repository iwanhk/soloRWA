import {Inject, Provide} from '@midwayjs/decorator';
import {ethers} from 'ethers';
import {getPrismaClient} from '../utils/prisma';
import {AbiEncoder} from '../utils/abi-encoder';
import {BlockchainAddressService} from './blockchain-address.service';
import {CONTRACT_TYPES, TrexDeployService} from './trex-deploy.service';
import {ClaimScheme, KeyPurpose, KeyType} from '../constants/identity.constants';

const onchainId = require('@onchain-id/solidity');
const erc3643 = require('@erc3643org/erc-3643');

@Provide()
export class IdentityService {
  @Inject()
  blockchainAddressService: BlockchainAddressService;

  @Inject()
  trexDeployService: TrexDeployService;

  private get prisma() {
    return getPrismaClient();
  }

  private getProvider() {
    const rpcUrl = process.env.RPC_URL || 'http://localhost:8545';
    return new ethers.JsonRpcProvider(rpcUrl);
  }

  private async getWallet(name='deployer') {
    const wallet = await this.prisma.blockchainAddress.findFirst({
      where: {name},
    });
    if (!wallet) {
      throw new Error(`wallet ${name} not found`);
    }
    return new ethers.Wallet(wallet.privateKey, this.getProvider());
  }

  private async getLatestNonce(wallet: ethers.Wallet): Promise<number> {
    const provider = this.getProvider();
    return provider.getTransactionCount(wallet.address, 'pending');
  }

  private getSignerFromWallet(wallet: ethers.Wallet) {
    return wallet;
  }

  private async getNonce(address: string): Promise<number> {
    const provider = this.getProvider();
    return provider.getTransactionCount(address, 'pending');
  }

  async addClaimIssuer(address: string) {
    // 检查地址是否已存在
    const existingIdentity = await this.prisma.claimIssuerIdentity.findUnique({
      where: { address },
    });

    if (existingIdentity) {
      const result = await this.completePendingClaimIssuerOperations(existingIdentity.id);
      return result;
    }

    const blockchainAddress = await this.prisma.blockchainAddress.findUnique({
      where: { address },
    });
    if (!blockchainAddress) {
      throw new Error(`Address ${address} not found in blockchain_addresses`);
    }

    const deployerWallet = await this.getWallet();
    const managementKey = deployerWallet.address;

    const identity = await this.prisma.claimIssuerIdentity.create({
      data: {
        address: address,
        contractAddress: null,
        managementKey: managementKey,
        blockchainAddressId: blockchainAddress.id,
        salt: null,
        contractDeployed: false,
        claimKeySetup: false,
        status: 'active',
      },
    });

    try {
      const result = await this.completePendingClaimIssuerOperations(identity.id);
      return result;
    } catch (error) {
      throw new Error(`Failed to complete pending operations for ClaimIssuer: ${error.message}`);
    }
  }

  async addUser(address: string, tokenId?: number, countryCode?: number) {
    // 检查地址是否已存在
    const existingIdentity = await this.prisma.userIdentity.findUnique({
      where: { address },
    });

    if (existingIdentity) {
      // 更新 countryCode 和 pendingTokenIds
      const updateData: any = {};
      if (countryCode !== undefined) {
        updateData.countryCode = countryCode;
      }
      if (tokenId !== undefined) {
        const currentPending = existingIdentity.pendingTokenIds as number[] || [];
        if (!currentPending.includes(tokenId)) {
          updateData.pendingTokenIds = [...currentPending, tokenId];
        }
      }
      if (Object.keys(updateData).length > 0) {
        await this.prisma.userIdentity.update({
          where: { id: existingIdentity.id },
          data: updateData,
        });
      }

      const result = await this.completePendingUserOperations(existingIdentity.id);
      return result;
    }

    const blockchainAddress = await this.prisma.blockchainAddress.findUnique({
      where: { address },
    });
    if (!blockchainAddress) {
      throw new Error(`Address ${address} not found in blockchain_addresses`);
    }

    const deployerWallet = await this.getWallet();
    const managementKey = deployerWallet.address;

    const createData: any = {
      address: address,
      contractAddress: null,
      managementKey: managementKey,
      blockchainAddressId: blockchainAddress.id,
      salt: null,
      contractDeployed: false,
      claimKeySetup: false,
      status: 'active',
    };

    if (countryCode !== undefined) {
      createData.countryCode = countryCode;
    }
    if (tokenId !== undefined) {
      createData.pendingTokenIds = [tokenId];
    }

    const identity = await this.prisma.userIdentity.create({
      data: createData,
    });

    try {
      const result = await this.completePendingUserOperations(identity.id);
      return result;
    } catch (error) {
      throw new Error(`Failed to complete pending operations for User: ${error.message}`);
    }
  }

  private async deployClaimIssuerIdentityContract(identityId: number, claimIssuerAddress: string, salt: string) {
    const identity = await this.prisma.claimIssuerIdentity.findUnique({
      where: { id: identityId },
    });

    if (!identity) {
      throw new Error('ClaimIssuerIdentity not found');
    }

    const idFactory = await this.trexDeployService.getDeployedContract(CONTRACT_TYPES.ONCHAIN_ID_ID_FACTORY);
    if (!idFactory) {
      throw new Error('IdFactory not deployed');
    }

    const wallet = await this.getWallet();
    const nonce = await this.getLatestNonce(wallet);

    try {
      const idFactoryContract = new ethers.Contract(
        idFactory.deploymentAddress,
        onchainId.contracts.Factory.abi,
        wallet
      );

      // 使用 createIdentityWithManagementKeys，将 deployer 设为 managementKey
      const deployerKeyHash = AbiEncoder.hashAddress(identity.managementKey);

      const tx = await idFactoryContract.createIdentityWithManagementKeys(
        claimIssuerAddress,
        salt,
        [deployerKeyHash],
        { nonce }
      );

      const receipt = await tx.wait();

      const iface = new ethers.Interface(onchainId.contracts.Factory.abi);
      let identityAddress = null;

      for (const log of receipt.logs) {
        try {
          const parsed = iface.parseLog({ topics: log.topics as string[], data: log.data });
          if (parsed?.name === 'WalletLinked') {
            identityAddress = parsed.args.identity;
            break;
          }
        } catch {
          // 忽略解析失败的日志
        }
      }

      if (!identityAddress) {
        throw new Error('Failed to extract Identity address from transaction receipt');
      }

      const updatedIdentity = await this.prisma.claimIssuerIdentity.update({
        where: { id: identityId },
        data: {
          contractAddress: identityAddress,
          salt,
          transactionHash: receipt.hash,
          blockNumber: receipt.blockNumber,
          contractDeployed: true,
        },
      });

      // 写入 AddressIdentity 表
      await this.prisma.addressIdentity.upsert({
        where: {
          address_type: {
            address: claimIssuerAddress,
            type: 'ClaimIssuer',
          },
        },
        update: {
          contractAddress: identityAddress,
        },
        create: {
          address: claimIssuerAddress,
          identityId: identityId,
          type: 'ClaimIssuer',
          contractAddress: identityAddress,
        },
      });

      return {
        contractAddress: updatedIdentity.contractAddress,
        contractDeployed: updatedIdentity.contractDeployed,
      };
    } catch (error) {
      throw new Error(`Failed to deploy ClaimIssuerIdentity contract: ${error.message}`);
    }
  }

  private async deployUserIdentityContract(identityId: number, userAddress: string, salt: string) {
    const identity = await this.prisma.userIdentity.findUnique({
      where: { id: identityId },
    });

    if (!identity) {
      throw new Error('UserIdentity not found');
    }

    const idFactory = await this.trexDeployService.getDeployedContract(CONTRACT_TYPES.ONCHAIN_ID_ID_FACTORY);
    if (!idFactory) {
      throw new Error('IdFactory not deployed');
    }

    const wallet = await this.getWallet();
    const nonce = await this.getLatestNonce(wallet);

    try {
      const idFactoryContract = new ethers.Contract(
        idFactory.deploymentAddress,
        onchainId.contracts.Factory.abi,
        wallet
      );

      const deployerKeyHash = AbiEncoder.hashAddress(identity.managementKey);

      const tx = await idFactoryContract.createIdentityWithManagementKeys(
        userAddress,
        salt,
        [deployerKeyHash],
        { nonce }
      );

      const receipt = await tx.wait();

      const iface = new ethers.Interface(onchainId.contracts.Factory.abi);
      let identityAddress = null;

      for (const log of receipt.logs) {
        try {
          const parsed = iface.parseLog({ topics: log.topics as string[], data: log.data });
          if (parsed?.name === 'WalletLinked') {
            identityAddress = parsed.args.identity;
            break;
          }
        } catch {
          // 忽略解析失败的日志
        }
      }

      if (!identityAddress) {
        throw new Error('Failed to extract Identity address from transaction receipt');
      }

      const updatedIdentity = await this.prisma.userIdentity.update({
        where: { id: identityId },
        data: {
          contractAddress: identityAddress,
          salt,
          transactionHash: receipt.hash,
          blockNumber: receipt.blockNumber,
          contractDeployed: true,
        },
      });

      // 写入 AddressIdentity 表
      await this.prisma.addressIdentity.upsert({
        where: {
          address_type: {
            address: userAddress,
            type: 'User',
          },
        },
        update: {
          contractAddress: identityAddress,
        },
        create: {
          address: userAddress,
          identityId: identityId,
          type: 'User',
          contractAddress: identityAddress,
        },
      });

      return {
        contractAddress: updatedIdentity.contractAddress,
        contractDeployed: updatedIdentity.contractDeployed,
      };
    } catch (error) {
      throw new Error(`Failed to deploy UserIdentity contract: ${error.message}`);
    }
  }

  private async completePendingClaimIssuerOperations(identityId: number) {
    let identity = await this.prisma.claimIssuerIdentity.findUnique({
      where: { id: identityId },
    });

    if (!identity) {
      throw new Error('ClaimIssuerIdentity not found');
    }

    const result = {
      id: identity.id,
      contractAddress: identity.contractAddress,
      managementKey: identity.managementKey,
      contractDeployed: identity.contractDeployed,
      claimKeySetup: identity.claimKeySetup,
      operations: [] as string[],
    };

    if (!identity.contractDeployed) {
      try {
        const salt = `ClaimIssuer${identity.id}`;
        const deployed = await this.deployClaimIssuerIdentityContract(identity.id, identity.address, salt);
        result.contractAddress = deployed.contractAddress;
        result.contractDeployed = deployed.contractDeployed;
        result.operations.push('Contract deployed');
        identity = await this.prisma.claimIssuerIdentity.findUnique({
          where: { id: identityId },
        });
      } catch (error) {
        throw new Error(`Failed to deploy contract: ${error.message}`);
      }
    } else {
      result.operations.push('Contract already deployed');
    }

    if (!result.claimKeySetup && result.contractDeployed) {
      try {
        const managementKeyWallet = await this.getWallet();
        const keyHash = AbiEncoder.hashAddress(identity.managementKey);

        const identityContract = new ethers.Contract(
          result.contractAddress,
          onchainId.contracts.Identity.abi,
          this.getSignerFromWallet(managementKeyWallet)
        );

        const nonce = await this.getNonce(managementKeyWallet.address);
        const tx = await identityContract.addKey(
          keyHash,
          KeyPurpose.CLAIM,
          KeyType.ECDSA,
          { nonce }
        );
        await tx.wait();

        await this.prisma.claimIssuerIdentity.update({
          where: { id: identityId },
          data: { claimKeySetup: true },
        });

        result.claimKeySetup = true;
        result.operations.push('ClaimKey setup completed');
      } catch (error) {
        throw new Error(`Failed to set up ClaimKey: ${error.message}`);
      }
    } else if (result.claimKeySetup) {
      result.operations.push('ClaimKey already setup');
    }

    if (result.operations.length === 0) {
      result.operations.push('No pending operations');
    }

    return result;
  }

  private async completePendingUserOperations(identityId: number) {
    let identity = await this.prisma.userIdentity.findUnique({
      where: { id: identityId },
    });

    if (!identity) {
      throw new Error('UserIdentity not found');
    }

    const result = {
      id: identity.id,
      contractAddress: identity.contractAddress,
      managementKey: identity.managementKey,
      contractDeployed: identity.contractDeployed,
      claimKeySetup: identity.claimKeySetup,
      operations: [] as string[],
    };

    if (!identity.contractDeployed) {
      try {
        const salt = `User${identity.id}`;
        const deployed = await this.deployUserIdentityContract(identity.id, identity.address, salt);
        result.contractAddress = deployed.contractAddress;
        result.contractDeployed = deployed.contractDeployed;
        result.operations.push('Contract deployed');
        identity = await this.prisma.userIdentity.findUnique({
          where: { id: identityId },
        });
      } catch (error) {
        throw new Error(`Failed to deploy contract: ${error.message}`);
      }
    } else {
      result.operations.push('Contract already deployed');
    }

    if (!result.claimKeySetup && result.contractDeployed) {
      try {
        const managementKeyWallet = await this.getWallet();
        const keyHash = AbiEncoder.hashAddress(identity.managementKey);

        const identityContract = new ethers.Contract(
          result.contractAddress,
          onchainId.contracts.Identity.abi,
          this.getSignerFromWallet(managementKeyWallet)
        );

        const nonce = await this.getNonce(managementKeyWallet.address);
        const tx = await identityContract.addKey(
          keyHash,
          KeyPurpose.CLAIM,
          KeyType.ECDSA,
          { nonce }
        );
        await tx.wait();

        await this.prisma.userIdentity.update({
          where: { id: identityId },
          data: { claimKeySetup: true },
        });

        result.claimKeySetup = true;
        result.operations.push('ClaimKey setup completed');
      } catch (error) {
        throw new Error(`Failed to set up ClaimKey: ${error.message}`);
      }
    } else if (result.claimKeySetup) {
      result.operations.push('ClaimKey already setup');
    }

    if (identity.pendingTokenIds && Array.isArray(identity.pendingTokenIds) && identity.pendingTokenIds.length > 0) {
      try {
        const pendingTokenIds = identity.pendingTokenIds as number[];
        const associatedTokenIds = (identity.associatedTokenIds as number[]) || [];
        const countryCode = identity.countryCode || 0;

        for (const tokenId of pendingTokenIds) {
          try {
            const token = await this.prisma.token.findUnique({
              where: { id: tokenId },
            });

            if (!token) {
              throw new Error(`Token with ID ${tokenId} not found`);
            }

            if (!token.identityRegistryAddress) {
              throw new Error(`Token ${tokenId} does not have an IdentityRegistry address`);
            }

            const deployerWallet = await this.getWallet();
            const nonce = await this.getLatestNonce(deployerWallet);

            const irContract = new ethers.Contract(
              token.identityRegistryAddress,
              erc3643.contracts.IdentityRegistry.abi,
              deployerWallet
            );

            const tx = await irContract.registerIdentity(
              identity.address,
              result.contractAddress,
              countryCode,
              { nonce }
            );
            await tx.wait();

            associatedTokenIds.push(tokenId);
            const updatedPendingTokenIds = pendingTokenIds.filter(id => id !== tokenId);

            await this.prisma.userIdentity.update({
              where: { id: identityId },
              data: {
                associatedTokenIds: associatedTokenIds,
                pendingTokenIds: updatedPendingTokenIds.length > 0 ? updatedPendingTokenIds : null,
              },
            });

            result.operations.push(`Registered to Token ${tokenId}`);
          } catch (error) {
            result.operations.push(`Failed to register to Token ${tokenId}: ${error.message}`);
          }
        }
      } catch (error) {
        throw new Error(`Failed to register to Token: ${error.message}`);
      }
    }

    if (result.operations.length === 0) {
      result.operations.push('No pending operations');
    }

    return result;
  }

  async linkIdentity(newWalletAddress: string, oldWalletAddress: string) {
    // 从 AddressIdentity 表找出 oldWalletAddress 对应的 Identity
    const addressIdentity = await this.prisma.addressIdentity.findUnique({
      where: {
        address_type: {
          address: oldWalletAddress,
          type: 'User',
        },
      },
    });

    if (!addressIdentity) {
      throw new Error(`AddressIdentity not found for address ${oldWalletAddress}`);
    }

    // 获取对应的 UserIdentity
    const userIdentity = await this.prisma.userIdentity.findUnique({
      where: { id: addressIdentity.identityId },
    });

    if (!userIdentity) {
      throw new Error(`UserIdentity not found for ID ${addressIdentity.identityId}`);
    }

    if (!userIdentity.contractAddress) {
      throw new Error('UserIdentity contract not deployed yet');
    }

    // 获取 deployer 钱包
    const deployerWallet = await this.getWallet();

    try {
      // 获取所有关联的 Token，为新钱包注册 Identity
      const tokens = await this.prisma.token.findMany({
        where: {
          status: 'deployed',
        },
      });

      const results = [];

      for (const token of tokens) {
        if (!token.identityRegistryAddress) {
          continue;
        }

        try {
          const irContract = new ethers.Contract(
            token.identityRegistryAddress,
            erc3643.contracts.IdentityRegistry.abi,
            deployerWallet
          );

          const currentNonce = await this.getLatestNonce(deployerWallet);
          const tx = await irContract.registerIdentity(
            newWalletAddress,
            userIdentity.contractAddress,
            userIdentity.countryCode || 0,
            { nonce: currentNonce }
          );

          const receipt = await tx.wait();
          results.push({
            tokenId: token.id,
            tokenAddress: token.address,
            transactionHash: receipt.hash,
            blockNumber: receipt.blockNumber,
          });
        } catch (error) {
          // 记录错误但继续处理其他 Token
          results.push({
            tokenId: token.id,
            tokenAddress: token.address,
            error: error.message,
          });
        }
      }

      // 写入 AddressIdentity 表
      await this.prisma.addressIdentity.create({
        data: {
          address: newWalletAddress,
          identityId: userIdentity.id,
          type: 'User',
          contractAddress: userIdentity.contractAddress,
        },
      });

      return {
        oldWalletAddress,
        newWalletAddress,
        identityAddress: userIdentity.contractAddress,
        registrationResults: results,
      };
    } catch (error) {
      throw new Error(`Failed to link wallet: ${error.message}`);
    }
  }

  async issueClaim(
    claimIssuerId: number,
    userId: number,
    topic: string,
    data: string = '0x',
    uri: string = '',
    scheme: number = ClaimScheme.ECDSA_SIGNATURE
  ) {
    const claimIssuer = await this.prisma.claimIssuerIdentity.findUnique({
      where: { id: claimIssuerId },
    });

    if (!claimIssuer) {
      throw new Error('ClaimIssuer not found');
    }

    const userIdentity = await this.prisma.userIdentity.findUnique({
      where: { id: userId },
    });

    if (!userIdentity) {
      throw new Error('User identity not found');
    }

    // 使用 deployer 来签发 claim
    const issuerWallet = await this.getWallet();

    const dataHash = AbiEncoder.hashAddressUint256Bytes(userIdentity.contractAddress, topic, data);

    const signature = await issuerWallet.signMessage(ethers.getBytes(dataHash));

    const userIdentityContract = new ethers.Contract(
      userIdentity.contractAddress,
      onchainId.contracts.Identity.abi,
      issuerWallet
    );

    const nonce = await this.getLatestNonce(issuerWallet);

    const tx = await userIdentityContract.addClaim(
      topic,
      scheme,
      claimIssuer.contractAddress,
      signature,
      data,
      uri,
      { nonce }
    );

    const receipt = await tx.wait();

    return {
      claimIssuerId,
      userId,
      userIdentityAddress: userIdentity.contractAddress,
      topic,
      data,
      uri,
      scheme,
      issuerAddress: claimIssuer.contractAddress,
      issuerWalletAddress: claimIssuer.managementKey,
      transactionHash: receipt.hash,
      blockNumber: receipt.blockNumber,
    };
  }
}
