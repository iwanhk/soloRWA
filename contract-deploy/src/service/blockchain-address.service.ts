import { Provide } from '@midwayjs/decorator';
import { Web3 } from 'web3';
import { getPrismaClient } from '../utils/prisma';

@Provide()
export class BlockchainAddressService {
  private web3: Web3;

  constructor() {
    this.web3 = new Web3();
  }

  private get prisma() {
    return getPrismaClient();
  }

  async create(data: {
    name: string;
    privateKey: string;
    description?: string;
  }) {
    // 从私钥解析出地址
    const account = this.web3.eth.accounts.privateKeyToAccount(data.privateKey);
    const address = account.address;

    const record = await this.prisma.blockchainAddress.create({
      data: {
        name: data.name,
        address,
        privateKey: data.privateKey,
        description: data.description,
      },
    });

    // 返回时不包含私钥
    return {
      id: record.id,
      name: record.name,
      address: record.address,
      description: record.description,
      createdAt: record.createdAt,
      updatedAt: record.updatedAt,
    };
  }

  async findAll() {
    const records = await this.prisma.blockchainAddress.findMany();
    // 返回时不包含私钥
    return records.map(record => ({
      id: record.id,
      name: record.name,
      address: record.address,
      description: record.description,
      createdAt: record.createdAt,
      updatedAt: record.updatedAt,
    }));
  }

  async findById(id: number) {
    const record = await this.prisma.blockchainAddress.findUnique({
      where: { id },
    });
    if (!record) return null;
    // 返回时不包含私钥
    return {
      id: record.id,
      name: record.name,
      address: record.address,
      description: record.description,
      createdAt: record.createdAt,
      updatedAt: record.updatedAt,
    };
  }

  async findByAddress(address: string) {
    const record = await this.prisma.blockchainAddress.findUnique({
      where: { address },
    });
    if (!record) return null;
    // 返回时不包含私钥
    return {
      id: record.id,
      name: record.name,
      address: record.address,
      description: record.description,
      createdAt: record.createdAt,
      updatedAt: record.updatedAt,
    };
  }

  async update(
    id: number,
    data: {
      name?: string;
      description?: string;
    }
  ) {
    const record = await this.prisma.blockchainAddress.update({
      where: { id },
      data,
    });
    // 返回时不包含私钥
    return {
      id: record.id,
      name: record.name,
      address: record.address,
      description: record.description,
      createdAt: record.createdAt,
      updatedAt: record.updatedAt,
    };
  }

  async delete(id: number) {
    return this.prisma.blockchainAddress.delete({
      where: { id },
    });
  }
}

