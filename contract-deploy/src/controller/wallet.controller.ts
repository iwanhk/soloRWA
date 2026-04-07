import { Controller, Post, Get, Put, Del, Inject, Param, Body } from '@midwayjs/decorator';
import { BlockchainAddressService } from '../service/blockchain-address.service';
import { ResponseUtil } from '../utils/response';

@Controller('/api/wallet')
export class WalletController {
  @Inject()
  blockchainAddressService: BlockchainAddressService;

  @Post('/')
  async create(
    @Body('name') name: string,
    @Body('privateKey') privateKey: string,
    @Body('description') description?: string
  ) {
    if (!name || !privateKey) {
      return ResponseUtil.validationError('name and privateKey are required');
    }

    const result = await this.blockchainAddressService.create({
      name,
      privateKey,
      description,
    });
    return ResponseUtil.success(result, 'Wallet created successfully');
  }

  @Get('/')
  async findAll() {
    const result = await this.blockchainAddressService.findAll();
    return ResponseUtil.success(result, 'Wallets retrieved successfully');
  }

  @Get('/:id')
  async findById(@Param('id') id: number) {
    const result = await this.blockchainAddressService.findById(id);

    if (!result) {
      return ResponseUtil.notFound('Wallet not found');
    }

    return ResponseUtil.success(result, 'Wallet retrieved successfully');
  }

  @Put('/:id')
  async update(
    @Param('id') id: number,
    @Body('name') name?: string,
    @Body('description') description?: string
  ) {
    const result = await this.blockchainAddressService.update(id, {
      name,
      description,
    });
    return ResponseUtil.success(result, 'Wallet updated successfully');
  }

  @Del('/:id')
  async delete(@Param('id') id: number) {
    const result = await this.blockchainAddressService.delete(id);
    return ResponseUtil.success(result, 'Wallet deleted successfully');
  }
}

