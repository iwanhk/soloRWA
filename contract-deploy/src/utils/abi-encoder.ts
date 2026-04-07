import { ethers } from 'ethers';

/**
 * ABI 编码工具类
 * 提供统一的 abi.encode 和相关操作的接口
 */
export class AbiEncoder {
  private static abiCoder = ethers.AbiCoder.defaultAbiCoder();

  /**
   * 编码单个地址
   * 等价于 Solidity 中的 abi.encode(address)
   * @param address 以太坊地址
   * @returns 编码后的字节数据
   */
  static encodeAddress(address: string): string {
    return this.abiCoder.encode(['address'], [address]);
  }

  /**
   * 编码多个地址
   * 等价于 Solidity 中的 abi.encode(address, address, ...)
   * @param addresses 地址数组
   * @returns 编码后的字节数据
   */
  static encodeAddresses(addresses: string[]): string {
    const types = addresses.map(() => 'address');
    return this.abiCoder.encode(types, addresses);
  }

  /**
   * 编码地址和 uint256
   * 等价于 Solidity 中的 abi.encode(address, uint256)
   * @param address 以太坊地址
   * @param value uint256 值
   * @returns 编码后的字节数据
   */
  static encodeAddressAndUint256(address: string, value: string | number | bigint): string {
    return this.abiCoder.encode(['address', 'uint256'], [address, value]);
  }

  /**
   * 编码地址、uint256 和字节数据
   * 等价于 Solidity 中的 abi.encode(address, uint256, bytes)
   * @param address 以太坊地址
   * @param topic uint256 topic
   * @param data 字节数据
   * @returns 编码后的字节数据
   */
  static encodeAddressUint256Bytes(address: string, topic: string | number | bigint, data: string): string {
    return this.abiCoder.encode(['address', 'uint256', 'bytes'], [address, topic, data]);
  }

  /**
   * 生成地址的 keccak256 哈希
   * 等价于 Solidity 中的 keccak256(abi.encode(address))
   * @param address 以太坊地址
   * @returns keccak256 哈希值
   */
  static hashAddress(address: string): string {
    const encoded = this.encodeAddress(address);
    return ethers.keccak256(encoded);
  }

  /**
   * 生成地址和 uint256 的 keccak256 哈希
   * 等价于 Solidity 中的 keccak256(abi.encode(address, uint256))
   * @param address 以太坊地址
   * @param value uint256 值
   * @returns keccak256 哈希值
   */
  static hashAddressAndUint256(address: string, value: string | number | bigint): string {
    const encoded = this.encodeAddressAndUint256(address, value);
    return ethers.keccak256(encoded);
  }

  /**
   * 生成地址、uint256 和字节数据的 keccak256 哈希
   * 等价于 Solidity 中的 keccak256(abi.encode(address, uint256, bytes))
   * @param address 以太坊地址
   * @param topic uint256 topic
   * @param data 字节数据
   * @returns keccak256 哈希值
   */
  static hashAddressUint256Bytes(address: string, topic: string | number | bigint, data: string): string {
    const encoded = this.encodeAddressUint256Bytes(address, topic, data);
    return ethers.keccak256(encoded);
  }

  /**
   * 通用编码函数
   * @param types 类型数组，如 ['address', 'uint256', 'bytes']
   * @param values 值数组
   * @returns 编码后的字节数据
   */
  static encode(types: string[], values: any[]): string {
    return this.abiCoder.encode(types, values);
  }

  /**
   * 通用哈希函数
   * @param types 类型数组
   * @param values 值数组
   * @returns keccak256 哈希值
   */
  static hash(types: string[], values: any[]): string {
    const encoded = this.encode(types, values);
    return ethers.keccak256(encoded);
  }

  /**
   * 解码数据
   * @param types 类型数组
   * @param data 编码后的数据
   * @returns 解码后的值数组
   */
  static decode(types: string[], data: string): any[] {
    return this.abiCoder.decode(types, data);
  }
}

