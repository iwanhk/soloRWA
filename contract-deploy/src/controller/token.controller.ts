import {Controller, Get, Inject, Param, Post, Body} from '@midwayjs/decorator';
import {TokenService} from '../service/token.service';
import {ResponseUtil} from '../utils/response';

@Controller('/api/token')
export class TokenController {
  @Inject()
  tokenService: TokenService;

  @Get('/')
  async findAll() {
    const tokens = await this.tokenService.findAll();
    return ResponseUtil.success(tokens, 'Tokens retrieved successfully');
  }

  @Get('/:id')
  async findById(@Param('id') id: string) {
    const numId = parseInt(id, 10);
    if (isNaN(numId)) {
      return ResponseUtil.notFound('Token not found');
    }
    const token = await this.tokenService.findById(numId);
    if (!token) {
      return ResponseUtil.notFound('Token not found');
    }
    return ResponseUtil.success(token, 'Token retrieved successfully');
  }

  // IdentityRegistryStorage 相关接口
  @Get('/irs/list')
  async findAllIRS() {
    const irsList = await this.tokenService.findAllIRS();
    return ResponseUtil.success(irsList, 'IdentityRegistryStorages retrieved successfully');
  }

  @Get('/irs/:id')
  async findIRSById(@Param('id') id: string) {
    const numId = parseInt(id, 10);
    if (isNaN(numId)) {
      return ResponseUtil.notFound('IdentityRegistryStorage not found');
    }
    const irs = await this.tokenService.findIRSById(numId);
    if (!irs) {
      return ResponseUtil.notFound('IdentityRegistryStorage not found');
    }
    return ResponseUtil.success(irs, 'IdentityRegistryStorage retrieved successfully');
  }

  @Post('/irs')
  async createIRS() {
    try {
      const irs = await this.tokenService.createIdentityRegistryStorage();
      return ResponseUtil.success(irs, 'IdentityRegistryStorage created successfully');
    } catch (error) {
      if (error.message.includes('must be deployed first')) {
        return ResponseUtil.error(400, error.message);
      }
      throw error;
    }
  }

  @Post('/:id/mint')
  async mint(
    @Param('id') id: string,
    @Body('toAddress') toAddress: string,
    @Body('amount') amount: string,
    @Body('agentAddress') agentAddress?: string
  ) {
    try {
      const tokenId = parseInt(id, 10);
      if (isNaN(tokenId)) {
        return ResponseUtil.validationError('Invalid token ID');
      }
      if (!toAddress || !amount) {
        return ResponseUtil.validationError('toAddress and amount are required');
      }
      const result = await this.tokenService.mint(tokenId, toAddress, amount, agentAddress);
      return ResponseUtil.success(result, 'Tokens minted successfully');
    } catch (error) {
      if (error.message.includes('Token not found')) {
        return ResponseUtil.notFound(error.message);
      }
      if (error.message.includes('not deployed')) {
        return ResponseUtil.error(400, error.message);
      }
      if (error.message.includes('Agent wallet not found')) {
        return ResponseUtil.error(400, error.message);
      }
      throw error;
    }
  }
}

