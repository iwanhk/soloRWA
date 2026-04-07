// SPDX-License-Identifier: MIT
pragma solidity ^0.8.17;

import "@openzeppelin/contracts/access/Ownable.sol";

/**
 * @title DividendRecord
 * @dev 分红记录合约，用于记录和查询分红信息
 */
contract DividendRecord is Ownable {
    constructor() Ownable() {}
    // 分红记录结构体
    struct Dividend {
        string date;            // 日期 (字符串格式)
        uint256 projectId;      // 项目ID
        uint256 amount;         // 分红数量
        string currency;        // 分红币种
        uint256 totalShares;    // 总份数
    }

    // 按日期存储分红记录: date => projectId => Dividend[]
    mapping(string => mapping(uint256 => Dividend[])) private dividendsByDateAndProject;

    // 按日期存储所有分红: date => Dividend[]
    mapping(string => Dividend[]) private dividendsByDate;

    // 按项目ID存储分红数量
    mapping(uint256 => uint256) private dividendCountByProject;

    // 按项目ID存储所有分红记录: projectId => Dividend[]
    mapping(uint256 => Dividend[]) private dividendsByProject;

    // 事件：分红记录事件
    // indexed: projectId (date 是字符串，不能 indexed)
    event DividendRecorded(
        string date,
        uint256 indexed projectId,
        uint256 amount,
        string currency,
        uint256 totalShares,
        address[] addresses,
        uint256[] addressShares
    );

    /**
     * @dev 记录分红信息
     * @param date 日期 (字符串格式，如 "2024-01-15")
     * @param projectId 项目ID
     * @param amount 分红数量
     * @param currency 分红币种
     * @param addresses 分红地址列表
     * @param shares 每个地址所占份数
     */
    function recordDividend(
        string memory date,
        uint256 projectId,
        uint256 amount,
        string memory currency,
        address[] memory addresses,
        uint256[] memory shares
    ) external onlyOwner {
        require(bytes(date).length > 0, "Date cannot be empty");
        require(projectId > 0, "Project ID must be greater than 0");
        //require(amount > 0, "Amount must be greater than 0");//分红可以为负
        require(bytes(currency).length > 0, "Currency cannot be empty");
        require(addresses.length > 0, "Addresses list cannot be empty");
        require(addresses.length == shares.length, "Addresses and shares length mismatch");

        // 计算总份数
        uint256 totalShares = 0;
        for (uint256 i = 0; i < shares.length; i++) {
            totalShares += shares[i];
        }

        Dividend memory dividend = Dividend({
            date: date,
            projectId: projectId,
            amount: amount,
            currency: currency,
            totalShares: totalShares
        });

        // 存储到按日期和项目ID的映射
        dividendsByDateAndProject[date][projectId].push(dividend);

        // 存储到按日期的映射
        dividendsByDate[date].push(dividend);

        // 存储到按项目ID的映射
        dividendsByProject[projectId].push(dividend);

        // 更新项目ID的分红数量计数
        dividendCountByProject[projectId]++;

        // 发出事件，包含总份数、地址列表和每个地址的份数
        emit DividendRecorded(date, projectId, amount, currency, totalShares, addresses, shares);
    }

    /**
     * @dev 根据日期查询当日所有分红信息
     * @param date 日期 (字符串格式)
     * @return 分红记录数组
     */
    function getDividendsByDate(string memory date)
        external
        view
        returns (Dividend[] memory)
    {
        return dividendsByDate[date];
    }

    /**
     * @dev 根据日期和项目ID查询当日该项目的所有分红信息
     * @param date 日期 (字符串格式)
     * @param projectId 项目ID
     * @return 分红记录数组
     */
    function getDividendsByDateAndProject(string memory date, uint256 projectId)
        external
        view
        returns (Dividend[] memory)
    {
        return dividendsByDateAndProject[date][projectId];
    }

    /**
     * @dev 根据项目ID查询分红数量
     * @param projectId 项目ID
     * @return 该项目的分红记录总数
     */
    function getDividendCountByProject(uint256 projectId) 
        external 
        view 
        returns (uint256) 
    {
        return dividendCountByProject[projectId];
    }

    /**
     * @dev 根据项目ID和序号查询单条分红信息
     * @param projectId 项目ID
     * @param index 序号 (0-based)
     * @return 分红记录
     */
    function getDividendByProjectAndIndex(uint256 projectId, uint256 index)
        external
        view
        returns (Dividend memory)
    {
        require(index < dividendCountByProject[projectId], "Index out of bounds");
        return dividendsByProject[projectId][index];
    }
}

