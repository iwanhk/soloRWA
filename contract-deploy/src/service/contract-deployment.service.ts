import { Provide } from '@midwayjs/decorator';
import { getPrismaClient } from '../utils/prisma';

@Provide()
export class ContractDeploymentService {
  constructor() {}

  private get prisma() {
    return getPrismaClient();
  }

  async create(data: {
    contractType: string;
    deploymentAddress: string;
    deployerAddress: string;
    deploymentInfo?: any;
    transactionHash?: string;
    blockNumber?: number;
    status?: string;
  }) {
    return this.prisma.contractDeployment.create({
      data,
    });
  }

  async findAll() {
    return this.prisma.contractDeployment.findMany();
  }

  async findById(id: number) {
    return this.prisma.contractDeployment.findUnique({
      where: { id },
    });
  }

  async findByDeploymentAddress(deploymentAddress: string) {
    return this.prisma.contractDeployment.findUnique({
      where: { deploymentAddress },
    });
  }

  async findByContractType(contractType: string) {
    return this.prisma.contractDeployment.findMany({
      where: { contractType },
    });
  }

  async update(
    id: number,
    data: {
      contractType?: string;
      deploymentAddress?: string;
      deployerAddress?: string;
      deploymentInfo?: any;
      transactionHash?: string;
      blockNumber?: number;
      status?: string;
    }
  ) {
    return this.prisma.contractDeployment.update({
      where: { id },
      data,
    });
  }

  async delete(id: number) {
    return this.prisma.contractDeployment.delete({
      where: { id },
    });
  }
}

