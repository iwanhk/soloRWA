-- CreateTable
CREATE TABLE `blockchain_addresses` (
    `id` INTEGER NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(255) NOT NULL,
    `address` VARCHAR(100) NOT NULL,
    `privateKey` LONGTEXT NOT NULL,
    `description` TEXT NULL,
    `createdAt` DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    `updatedAt` DATETIME(3) NOT NULL,

    UNIQUE INDEX `blockchain_addresses_address_key`(`address`),
    PRIMARY KEY (`id`)
) DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- CreateTable
CREATE TABLE `system_initialization` (
    `id` INTEGER NOT NULL AUTO_INCREMENT,
    `step` VARCHAR(100) NOT NULL,
    `status` VARCHAR(50) NOT NULL DEFAULT 'pending',
    `contractAddress` VARCHAR(100) NULL,
    `transactionHash` VARCHAR(100) NULL,
    `blockNumber` INTEGER NULL,
    `errorMessage` TEXT NULL,
    `metadata` JSON NULL,
    `createdAt` DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    `updatedAt` DATETIME(3) NOT NULL,

    UNIQUE INDEX `system_initialization_step_key`(`step`),
    PRIMARY KEY (`id`)
) DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- CreateTable
CREATE TABLE `contract_deployments` (
    `id` INTEGER NOT NULL AUTO_INCREMENT,
    `contractType` VARCHAR(100) NOT NULL,
    `deploymentAddress` VARCHAR(100) NOT NULL,
    `deployerAddress` VARCHAR(100) NOT NULL,
    `deploymentInfo` JSON NULL,
    `transactionHash` VARCHAR(100) NULL,
    `blockNumber` INTEGER NULL,
    `status` VARCHAR(50) NOT NULL DEFAULT 'deployed',
    `createdAt` DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    `updatedAt` DATETIME(3) NOT NULL,

    UNIQUE INDEX `contract_deployments_contractType_key`(`contractType`),
    UNIQUE INDEX `contract_deployments_deploymentAddress_key`(`deploymentAddress`),
    PRIMARY KEY (`id`)
) DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- CreateTable
CREATE TABLE `identity_registry_storages` (
    `id` INTEGER NOT NULL AUTO_INCREMENT,
    `address` VARCHAR(100) NOT NULL,
    `deployerAddress` VARCHAR(100) NOT NULL,
    `transactionHash` VARCHAR(100) NULL,
    `blockNumber` INTEGER NULL,
    `boundTokenCount` INTEGER NOT NULL DEFAULT 0,
    `status` VARCHAR(50) NOT NULL DEFAULT 'deployed',
    `createdAt` DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    `updatedAt` DATETIME(3) NOT NULL,

    UNIQUE INDEX `identity_registry_storages_address_key`(`address`),
    PRIMARY KEY (`id`)
) DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- CreateTable
CREATE TABLE `tokens` (
    `id` INTEGER NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(255) NOT NULL,
    `symbol` VARCHAR(50) NOT NULL,
    `decimals` INTEGER NOT NULL DEFAULT 18,
    `address` VARCHAR(100) NULL,
    `ownerAddress` VARCHAR(100) NOT NULL,
    `deployerAddress` VARCHAR(100) NOT NULL,
    `identityRegistryStorageId` INTEGER NOT NULL,
    `identityRegistryAddress` VARCHAR(100) NULL,
    `claimTopicsRegistryAddress` VARCHAR(100) NULL,
    `trustedIssuersRegistryAddress` VARCHAR(100) NULL,
    `modularComplianceAddress` VARCHAR(100) NULL,
    `tokenOnchainIdAddress` VARCHAR(100) NULL,
    `transactionHash` VARCHAR(100) NULL,
    `blockNumber` INTEGER NULL,
    `status` VARCHAR(50) NOT NULL DEFAULT 'pending',
    `salt` VARCHAR(255) NOT NULL,
    `tokenAgents` JSON NULL,
    `claimTopics` JSON NULL,
    `issuers` JSON NULL,
    `issuerClaims` JSON NULL,
    `deploymentInfo` JSON NULL,
    `errorMessage` TEXT NULL,
    `createdAt` DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    `updatedAt` DATETIME(3) NOT NULL,

    UNIQUE INDEX `tokens_address_key`(`address`),
    PRIMARY KEY (`id`)
) DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- CreateTable
CREATE TABLE `claim_topics` (
    `id` INTEGER NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(255) NOT NULL,
    `value` VARCHAR(100) NOT NULL,
    `topic` VARCHAR(100) NOT NULL,
    `createdAt` DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    `updatedAt` DATETIME(3) NOT NULL,

    UNIQUE INDEX `claim_topics_value_key`(`value`),
    UNIQUE INDEX `claim_topics_topic_key`(`topic`),
    PRIMARY KEY (`id`)
) DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- CreateTable
CREATE TABLE `claim_issuer_identities` (
    `id` INTEGER NOT NULL AUTO_INCREMENT,
    `address` VARCHAR(100) NOT NULL,
    `contractAddress` VARCHAR(100) NULL,
    `managementKey` VARCHAR(100) NOT NULL,
    `blockchainAddressId` INTEGER NULL,
    `salt` VARCHAR(255) NULL,
    `transactionHash` VARCHAR(100) NULL,
    `blockNumber` INTEGER NULL,
    `contractDeployed` BOOLEAN NOT NULL DEFAULT false,
    `claimKeySetup` BOOLEAN NOT NULL DEFAULT false,
    `status` VARCHAR(50) NOT NULL DEFAULT 'active',
    `createdAt` DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    `updatedAt` DATETIME(3) NOT NULL,

    UNIQUE INDEX `claim_issuer_identities_address_key`(`address`),
    UNIQUE INDEX `claim_issuer_identities_contractAddress_key`(`contractAddress`),
    PRIMARY KEY (`id`)
) DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- CreateTable
CREATE TABLE `user_identities` (
    `id` INTEGER NOT NULL AUTO_INCREMENT,
    `address` VARCHAR(100) NOT NULL,
    `contractAddress` VARCHAR(100) NULL,
    `managementKey` VARCHAR(100) NOT NULL,
    `blockchainAddressId` INTEGER NULL,
    `salt` VARCHAR(255) NULL,
    `transactionHash` VARCHAR(100) NULL,
    `blockNumber` INTEGER NULL,
    `contractDeployed` BOOLEAN NOT NULL DEFAULT false,
    `claimKeySetup` BOOLEAN NOT NULL DEFAULT false,
    `countryCode` INTEGER NULL,
    `associatedTokenIds` JSON NULL,
    `pendingTokenIds` JSON NULL,
    `status` VARCHAR(50) NOT NULL DEFAULT 'active',
    `createdAt` DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    `updatedAt` DATETIME(3) NOT NULL,

    UNIQUE INDEX `user_identities_address_key`(`address`),
    UNIQUE INDEX `user_identities_contractAddress_key`(`contractAddress`),
    PRIMARY KEY (`id`)
) DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- AddForeignKey
ALTER TABLE `tokens` ADD CONSTRAINT `tokens_identityRegistryStorageId_fkey` FOREIGN KEY (`identityRegistryStorageId`) REFERENCES `identity_registry_storages`(`id`) ON DELETE RESTRICT ON UPDATE CASCADE;
