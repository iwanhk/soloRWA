import { Provide } from '@midwayjs/decorator';
import { ethers } from 'ethers';
import { getPrismaClient } from '../utils/prisma';

@Provide()
export class ClaimTopicService {
  private get prisma() {
    return getPrismaClient();
  }

  /**
   * 创建 ClaimTopic
   * 自动计算 topic 为 value 的 keccak256 哈希
   */
  async createClaimTopic(data: { name: string; value: string }): Promise<any> {
    // 计算 keccak256 哈希
    const topic = ethers.id(data.value);

    // 检查 value 是否已存在
    const existing = await this.prisma.claimTopic.findUnique({
      where: { value: data.value },
    });
    if (existing) {
      throw new Error(`ClaimTopic with value "${data.value}" already exists`);
    }

    // 检查 topic 是否已存在
    const existingTopic = await this.prisma.claimTopic.findUnique({
      where: { topic },
    });
    if (existingTopic) {
      throw new Error(`ClaimTopic with topic "${topic}" already exists`);
    }

    return this.prisma.claimTopic.create({
      data: {
        name: data.name,
        value: data.value,
        topic,
      },
    });
  }

  /**
   * 获取所有 ClaimTopic
   */
  async getAllClaimTopics(): Promise<any[]> {
    return this.prisma.claimTopic.findMany({
      orderBy: { createdAt: 'desc' },
    });
  }

  /**
   * 根据 ID 获取 ClaimTopic
   */
  async getClaimTopicById(id: number): Promise<any> {
    const claimTopic = await this.prisma.claimTopic.findUnique({
      where: { id },
    });
    if (!claimTopic) {
      throw new Error(`ClaimTopic with id ${id} not found`);
    }
    return claimTopic;
  }

  /**
   * 根据 value 获取 ClaimTopic
   */
  async getClaimTopicByValue(value: string): Promise<any> {
    const claimTopic = await this.prisma.claimTopic.findUnique({
      where: { value },
    });
    if (!claimTopic) {
      throw new Error(`ClaimTopic with value "${value}" not found`);
    }
    return claimTopic;
  }

  /**
   * 根据 topic 获取 ClaimTopic
   */
  async getClaimTopicByTopic(topic: string): Promise<any> {
    const claimTopic = await this.prisma.claimTopic.findUnique({
      where: { topic },
    });
    if (!claimTopic) {
      throw new Error(`ClaimTopic with topic "${topic}" not found`);
    }
    return claimTopic;
  }

  /**
   * 更新 ClaimTopic
   */
  async updateClaimTopic(id: number, data: { name?: string; value?: string }): Promise<any> {
    // 检查是否存在
    const existing = await this.prisma.claimTopic.findUnique({
      where: { id },
    });
    if (!existing) {
      throw new Error(`ClaimTopic with id ${id} not found`);
    }

    // 如果更新 value，需要重新计算 topic
    let updateData: any = { ...data };
    if (data.value && data.value !== existing.value) {
      // 检查新 value 是否已存在
      const existingValue = await this.prisma.claimTopic.findUnique({
        where: { value: data.value },
      });
      if (existingValue) {
        throw new Error(`ClaimTopic with value "${data.value}" already exists`);
      }
      updateData.topic = ethers.id(data.value);
    }

    return this.prisma.claimTopic.update({
      where: { id },
      data: updateData,
    });
  }

  /**
   * 删除 ClaimTopic
   */
  async deleteClaimTopic(id: number): Promise<any> {
    const existing = await this.prisma.claimTopic.findUnique({
      where: { id },
    });
    if (!existing) {
      throw new Error(`ClaimTopic with id ${id} not found`);
    }

    return this.prisma.claimTopic.delete({
      where: { id },
    });
  }
}

