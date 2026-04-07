# AbiEncoder 使用指南

## 概述

`AbiEncoder` 是一个工具类，提供统一的 `abi.encode` 和 `keccak256` 操作接口。它确保 JavaScript/TypeScript 代码中的编码方式与 Solidity 合约中的编码方式完全一致。

## 为什么需要 AbiEncoder？

在 Solidity 中：
```solidity
bytes32 keyHash = keccak256(abi.encode(address));
```

在 ethers.js 中有两种编码方式：
- `solidityPacked()` - 紧凑编码（不推荐）
- `AbiCoder.encode()` - 标准 ABI 编码（推荐）

**AbiEncoder 确保使用正确的编码方式。**

## 常用方法

### 1. 编码地址

```typescript
import { AbiEncoder } from '../utils/abi-encoder';

// 编码单个地址
const encoded = AbiEncoder.encodeAddress('0x5dcD2165DF157Bb1717c60F976A5e933E73a084D');
// 结果: 0x0000000000000000000000005dcd2165df157bb1717c60f976a5e933e73a084d
```

### 2. 生成地址的 keccak256 哈希

```typescript
// 等价于 Solidity 中的 keccak256(abi.encode(address))
const keyHash = AbiEncoder.hashAddress('0x5dcD2165DF157Bb1717c60F976A5e933E73a084D');
// 结果: 0xb3df3428e47601839aee22a04b461dbc31e57f5d92bc2ce6993a87f8f68e5697
```

### 3. 编码地址和 uint256

```typescript
const encoded = AbiEncoder.encodeAddressAndUint256(
  '0x5dcD2165DF157Bb1717c60F976A5e933E73a084D',
  123
);
```

### 4. 编码地址、uint256 和字节数据

```typescript
// 等价于 Solidity 中的 keccak256(abi.encode(address, uint256, bytes))
const hash = AbiEncoder.hashAddressUint256Bytes(
  userIdentity.contractAddress,
  topic,
  data
);
```

### 5. 通用编码和哈希

```typescript
// 自定义类型编码
const encoded = AbiEncoder.encode(
  ['address', 'uint256', 'string'],
  [address, 123, 'hello']
);

// 自定义类型哈希
const hash = AbiEncoder.hash(
  ['address', 'uint256', 'string'],
  [address, 123, 'hello']
);
```

### 6. 解码数据

```typescript
const decoded = AbiEncoder.decode(
  ['address', 'uint256'],
  encodedData
);
// 结果: [address, 123]
```

## 在 Identity Service 中的使用

### 生成 Management Key Hash

```typescript
// 之前（错误）
const keyHash = ethers.keccak256(
  ethers.solidityPacked(['address'], [identity.managementKey])
);

// 现在（正确）
const keyHash = AbiEncoder.hashAddress(identity.managementKey);
```

### 签发 Claim 时的数据哈希

```typescript
// 之前（错误）
const dataHash = ethers.keccak256(
  ethers.AbiCoder.defaultAbiCoder().encode(
    ['address', 'uint256', 'bytes'],
    [userIdentity.contractAddress, topic, data]
  )
);

// 现在（正确）
const dataHash = AbiEncoder.hashAddressUint256Bytes(
  userIdentity.contractAddress,
  topic,
  data
);
```

## 与 Solidity 的对应关系

| Solidity | AbiEncoder 方法 |
|----------|-----------------|
| `abi.encode(address)` | `encodeAddress(address)` |
| `keccak256(abi.encode(address))` | `hashAddress(address)` |
| `abi.encode(address, uint256)` | `encodeAddressAndUint256(address, value)` |
| `keccak256(abi.encode(address, uint256, bytes))` | `hashAddressUint256Bytes(address, topic, data)` |
| `abi.encode(types[], values[])` | `encode(types, values)` |
| `keccak256(abi.encode(types[], values[]))` | `hash(types, values)` |

## 注意事项

1. **始终使用 AbiEncoder** - 不要直接使用 `ethers.solidityPacked()` 或 `ethers.AbiCoder.encode()`
2. **类型必须匹配** - 确保传入的值类型与声明的类型一致
3. **地址格式** - 地址必须是有效的以太坊地址格式（0x 开头）
4. **uint256 值** - 可以是字符串、数字或 BigInt

## 示例：完整的 Key 生成流程

```typescript
import { AbiEncoder } from '../utils/abi-encoder';

// 1. 获取 management key 地址
const managementKeyAddress = '0x5dcD2165DF157Bb1717c60F976A5e933E73a084D';

// 2. 生成 key hash（与 Solidity 中的 keccak256(abi.encode(address)) 一致）
const keyHash = AbiEncoder.hashAddress(managementKeyAddress);

// 3. 使用 key hash 调用合约
const tx = await identityContract.addKey(
  keyHash,
  KeyPurpose.CLAIM,
  KeyType.ECDSA
);
```

## 常见错误

❌ **错误：使用 solidityPacked**
```typescript
const keyHash = ethers.keccak256(
  ethers.solidityPacked(['address'], [address])
);
```

✅ **正确：使用 AbiEncoder**
```typescript
const keyHash = AbiEncoder.hashAddress(address);
```

