-- DropIndex
DROP INDEX `tokens_identityRegistryStorageId_fkey` ON `tokens`;

-- CreateTable
CREATE TABLE `address_identities` (
    `id` INTEGER NOT NULL AUTO_INCREMENT,
    `address` VARCHAR(100) NOT NULL,
    `identityId` INTEGER NOT NULL,
    `type` VARCHAR(50) NOT NULL,
    `contractAddress` VARCHAR(100) NULL,
    `createdAt` DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    `updatedAt` DATETIME(3) NOT NULL,

    UNIQUE INDEX `address_identities_address_type_key`(`address`, `type`),
    PRIMARY KEY (`id`)
) DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- AddForeignKey
ALTER TABLE `tokens` ADD CONSTRAINT `tokens_identityRegistryStorageId_fkey` FOREIGN KEY (`identityRegistryStorageId`) REFERENCES `identity_registry_storages`(`id`) ON DELETE RESTRICT ON UPDATE CASCADE;
