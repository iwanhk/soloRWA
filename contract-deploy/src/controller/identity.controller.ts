import {Body, Controller, Inject, Param, Post} from '@midwayjs/decorator';
import {IdentityService} from '../service/identity.service';
import {ResponseUtil} from '../utils/response';
import {ClaimScheme} from '../constants/identity.constants';

@Controller('/api/identity')
export class IdentityController {
  @Inject()
  identityService: IdentityService;

  /**
   * 添加 ClaimIssuer
   * POST /api/identity/claim-issuer
   * Body: { address: string }
   */
  @Post('/claim-issuer')
  async addClaimIssuer(
    @Body('address') address: string
  ) {
    if (!address) {
      return ResponseUtil.validationError('address is required');
    }

    try {
      const result = await this.identityService.addClaimIssuer(address);
      return ResponseUtil.success(result, 'ClaimIssuer added successfully');
    } catch (error) {
      if (error.message.includes('not found')) {
        return ResponseUtil.error(400, error.message);
      }
      if (error.message.includes('not deployed')) {
        return ResponseUtil.error(400, error.message);
      }
      throw error;
    }
  }

  /**
   * 添加 User
   * POST /api/identity/user
   * Body: { address: string, token_id?: number, countryCode?: number }
   */
  @Post('/user')
  async addUser(
    @Body('address') address: string,
    @Body('token_id') tokenId?: number,
    @Body('countryCode') countryCode?: number
  ) {
    if (!address) {
      return ResponseUtil.validationError('address is required');
    }

    try {
      const result = await this.identityService.addUser(address, tokenId, countryCode);
      return ResponseUtil.success(result, 'User added successfully');
    } catch (error) {
      if (error.message.includes('not found')) {
        return ResponseUtil.error(400, error.message);
      }
      if (error.message.includes('not deployed')) {
        return ResponseUtil.error(400, error.message);
      }
      throw error;
    }
  }
  /**
   * 链接新钱包到现有 Identity
   * POST /api/identity/link-wallet
   * Body: { new_wallet_address: string, old_wallet_address: string }
   */
  @Post('/link-wallet')
  async linkIdentity(
    @Body('new_wallet_address') newWalletAddress: string,
    @Body('old_wallet_address') oldWalletAddress: string
  ) {
    if (!newWalletAddress || !oldWalletAddress) {
      return ResponseUtil.validationError('new_wallet_address and old_wallet_address are required');
    }

    try {
      const result = await this.identityService.linkIdentity(newWalletAddress, oldWalletAddress);
      return ResponseUtil.success(result, 'Wallet linked successfully');
    } catch (error) {
      if (error.message.includes('not found')) {
        return ResponseUtil.error(400, error.message);
      }
      if (error.message.includes('not deployed')) {
        return ResponseUtil.error(400, error.message);
      }
      throw error;
    }
  }

  /**
   * ClaimIssuer 对用户签发 Claim
   * POST /api/identity/:id/issue-claim
   * Body: { userId: number, topic: number, data?: string, uri?: string, scheme?: number }
   */
  @Post('/:id/issue-claim')
  async issueClaim(
    @Param('id') id: string,
    @Body('userId') userId: number,
    @Body('topic') topic: string,
    @Body('data') data?: string,
    @Body('uri') uri?: string,
    @Body('scheme') scheme?: number
  ) {
    try {
      const claimIssuerId = parseInt(id, 10);
      if (isNaN(claimIssuerId)) {
        return ResponseUtil.validationError('Invalid ClaimIssuer ID');
      }
      if (!userId || topic === undefined) {
        return ResponseUtil.validationError('userId and topic are required');
      }

      const result = await this.identityService.issueClaim(
        claimIssuerId,
        userId,
        topic,
        data || '0x',
        uri || '',
        scheme || ClaimScheme.ECDSA_SIGNATURE
      );
      return ResponseUtil.success(result, 'Claim issued successfully');
    } catch (error) {
      if (error.message.includes('ClaimIssuer not found')) {
        return ResponseUtil.notFound(error.message);
      }
      if (error.message.includes('not found')) {
        return ResponseUtil.notFound(error.message);
      }
      if (error.message.includes('not a')) {
        return ResponseUtil.error(400, error.message);
      }
      if (error.message.includes('wallet not found')) {
        return ResponseUtil.error(400, error.message);
      }
      throw error;
    }
  }

}

