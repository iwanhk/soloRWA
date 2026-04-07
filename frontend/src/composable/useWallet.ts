import {
	createWeb3Modal,
	defaultConfig,
	useWeb3Modal,
	useWeb3ModalAccount,
	useWeb3ModalProvider
} from "@web3modal/ethers/vue";
import {BrowserProvider} from "ethers";
import ENV from "@/library/env.ts";

// Web3Modal 配置
const projectId = ENV.reownProjectId;

const mainnet = {
	chainId: 1,
	name: 'Ethereum',
	currency: 'ETH',
	explorerUrl: 'https://etherscan.io',
	rpcUrl: 'https://cloudflare-eth.com'
};

const metadata = {
	name: 'Solo RWA',
	description: 'Solo RWA Platform',
	url: 'https://solo-rwa.com',
	icons: ['https://solo-rwa.com/icon.png']
};

const ethersConfig = defaultConfig({
	metadata,
	enableEIP6963: true,
	enableInjected: true,
	enableCoinbase: true,
});

// 初始化 Web3Modal（只执行一次）
let initialized = false;

function initWeb3Modal() {
	if (initialized) return;
	
	createWeb3Modal({
		ethersConfig,
		chains: [mainnet],
		projectId,
		enableAnalytics: false
	});
	
	initialized = true;
}

export function useWallet() {
	// 确保 Web3Modal 已初始化
	initWeb3Modal();
	
	const {open} = useWeb3Modal();
	const {address, isConnected} = useWeb3ModalAccount();
	const {walletProvider} = useWeb3ModalProvider();

	// 连接钱包
	async function connect() {
		await open();
	}

	// 切换钱包地址
	async function switchAddress() {
		await open({view: 'Account'});
	}

	// 签名消息
	async function signMessage(message: string): Promise<string> {
		if (!walletProvider.value) {
			throw new Error("钱包未连接");
		}

		const provider = new BrowserProvider(walletProvider.value);
		const signer = await provider.getSigner();
		return await signer.signMessage(message);
	}

	return {
		address,
		isConnected,
		walletProvider,
		connect,
		switchAddress,
		signMessage,
	};
}

