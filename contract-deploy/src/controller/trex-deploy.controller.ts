import { Controller, Post, Get, Inject, Body, Param } from '@midwayjs/decorator';
import { TrexDeployService } from '../service/trex-deploy.service';
import { ResponseUtil } from '../utils/response';

@Controller('/api/trex')
export class TrexDeployController {
  @Inject()
  trexDeployService: TrexDeployService;

  /**
   * 获取系统初始化状态
   */
  @Get('/status')
  async getInitializationStatus() {
    try {
      const status = await this.trexDeployService.getInitializationStatus();
      return ResponseUtil.success(status, 'Initialization status retrieved');
    } catch (error) {
      return ResponseUtil.error(500, error.message);
    }
  }

  /**
   * 初始化系统 - 部署 Token 之前的所有准备工作
   * 支持断点续传，如果中断可以继续执行
   */
  @Post('/initialize')
  async initializeSystem(@Body('claimIssuerManagementKey') claimIssuerManagementKey?: string) {
    try {
      const result = await this.trexDeployService.initializeSystem(claimIssuerManagementKey);
      return ResponseUtil.success(result, 'System initialized successfully');
    } catch (error) {
      return ResponseUtil.error(400, error.message);
    }
  }

  /**
   * 部署新的 Token
   */
  @Post('/deploy-token')
  async deployToken(
    @Body('salt') salt: string,
    @Body('ownerAddress') ownerAddress: string,
    @Body('name') name: string,
    @Body('symbol') symbol: string,
    @Body('decimals') decimals?: number,
    @Body('tokenAgents') tokenAgents?: string[],
    @Body('claimTopics') claimTopics?: string[],
    @Body('issuers') issuers?: string[],
    @Body('issuerClaims') issuerClaims?: string[][]
  ) {
    try {
      if (!salt || !ownerAddress || !name || !symbol) {
        return ResponseUtil.validationError('salt, ownerAddress, name, and symbol are required');
      }

      const result = await this.trexDeployService.deployToken({
        salt,
        ownerAddress,
        name,
        symbol,
        decimals,
        tokenAgents,
        claimTopics,
        issuers,
        issuerClaims,
      });
      return ResponseUtil.success(result, 'Token deployed successfully');
    } catch (error) {
      return ResponseUtil.error(400, error.message);
    }
  }

  /**
   * 部署新的 IdentityRegistryStorage
   */
  @Post('/deploy-identity-registry-storage')
  async deployIdentityRegistryStorage() {
    try {
      const result = await this.trexDeployService.deployIdentityRegistryStorage();
      return ResponseUtil.success(result, 'IdentityRegistryStorage deployed successfully');
    } catch (error) {
      return ResponseUtil.error(400, error.message);
    }
  }

  /**
   * 获取所有 Token 列表
   */
  @Get('/tokens')
  async getTokens() {
    try {
      const tokens = await this.trexDeployService.getTokens();
      return ResponseUtil.success(tokens, 'Tokens retrieved successfully');
    } catch (error) {
      return ResponseUtil.error(500, error.message);
    }
  }

  /**
   * 获取单个 Token 详情
   */
  @Get('/tokens/:id')
  async getTokenById(@Param('id') id: string) {
    try {
      const numId = parseInt(id, 10);
      if (isNaN(numId)) {
        return ResponseUtil.validationError('Invalid token ID');
      }
      const token = await this.trexDeployService.getTokenById(numId);
      if (!token) {
        return ResponseUtil.notFound('Token not found');
      }
      return ResponseUtil.success(token, 'Token retrieved successfully');
    } catch (error) {
      return ResponseUtil.error(500, error.message);
    }
  }

  /**
   * 获取所有 IdentityRegistryStorage 列表
   */
  @Get('/identity-registry-storages')
  async getIdentityRegistryStorages() {
    try {
      const storages = await this.trexDeployService.getIdentityRegistryStorages();
      return ResponseUtil.success(storages, 'IdentityRegistryStorages retrieved successfully');
    } catch (error) {
      return ResponseUtil.error(500, error.message);
    }
  }
}

