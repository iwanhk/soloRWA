import { Controller, Post, Get, Put, Del, Inject, Body, Param } from '@midwayjs/decorator';
import { ClaimTopicService } from '../service/claim-topic.service';
import { ResponseUtil } from '../utils/response';

@Controller('/api/claim-topics')
export class ClaimTopicController {
  @Inject()
  claimTopicService: ClaimTopicService;

  /**
   * 创建 ClaimTopic
   */
  @Post('/')
  async createClaimTopic(@Body('name') name: string, @Body('value') value: string) {
    try {
      if (!name || !value) {
        return ResponseUtil.validationError('name and value are required');
      }

      const result = await this.claimTopicService.createClaimTopic({ name, value });
      return ResponseUtil.success(result, 'ClaimTopic created successfully');
    } catch (error) {
      return ResponseUtil.error(400, error.message);
    }
  }

  /**
   * 获取所有 ClaimTopic
   */
  @Get('/')
  async getAllClaimTopics() {
    try {
      const result = await this.claimTopicService.getAllClaimTopics();
      return ResponseUtil.success(result, 'ClaimTopics retrieved successfully');
    } catch (error) {
      return ResponseUtil.error(500, error.message);
    }
  }

  /**
   * 根据 ID 获取 ClaimTopic
   */
  @Get('/:id')
  async getClaimTopicById(@Param('id') id: string) {
    try {
      const numId = parseInt(id, 10);
      if (isNaN(numId)) {
        return ResponseUtil.validationError('Invalid ClaimTopic ID');
      }

      const result = await this.claimTopicService.getClaimTopicById(numId);
      return ResponseUtil.success(result, 'ClaimTopic retrieved successfully');
    } catch (error) {
      return ResponseUtil.error(404, error.message);
    }
  }

  /**
   * 根据 value 获取 ClaimTopic
   */
  @Get('/by-value/:value')
  async getClaimTopicByValue(@Param('value') value: string) {
    try {
      const result = await this.claimTopicService.getClaimTopicByValue(value);
      return ResponseUtil.success(result, 'ClaimTopic retrieved successfully');
    } catch (error) {
      return ResponseUtil.error(404, error.message);
    }
  }

  /**
   * 根据 topic 获取 ClaimTopic
   */
  @Get('/by-topic/:topic')
  async getClaimTopicByTopic(@Param('topic') topic: string) {
    try {
      const result = await this.claimTopicService.getClaimTopicByTopic(topic);
      return ResponseUtil.success(result, 'ClaimTopic retrieved successfully');
    } catch (error) {
      return ResponseUtil.error(404, error.message);
    }
  }

  /**
   * 更新 ClaimTopic
   */
  @Put('/:id')
  async updateClaimTopic(
    @Param('id') id: string,
    @Body('name') name?: string,
    @Body('value') value?: string
  ) {
    try {
      const numId = parseInt(id, 10);
      if (isNaN(numId)) {
        return ResponseUtil.validationError('Invalid ClaimTopic ID');
      }

      if (!name && !value) {
        return ResponseUtil.validationError('At least one of name or value must be provided');
      }

      const result = await this.claimTopicService.updateClaimTopic(numId, { name, value });
      return ResponseUtil.success(result, 'ClaimTopic updated successfully');
    } catch (error) {
      return ResponseUtil.error(400, error.message);
    }
  }

  /**
   * 删除 ClaimTopic
   */
  @Del('/:id')
  async deleteClaimTopic(@Param('id') id: string) {
    try {
      const numId = parseInt(id, 10);
      if (isNaN(numId)) {
        return ResponseUtil.validationError('Invalid ClaimTopic ID');
      }

      const result = await this.claimTopicService.deleteClaimTopic(numId);
      return ResponseUtil.success(result, 'ClaimTopic deleted successfully');
    } catch (error) {
      return ResponseUtil.error(400, error.message);
    }
  }
}

