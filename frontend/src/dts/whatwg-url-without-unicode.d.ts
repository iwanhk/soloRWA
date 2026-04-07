type NativeURL = {
	prototype: URL;
	new(url: string | URL, base?: string | URL): URL;
	createObjectURL(obj: Blob | MediaSource): string;
	revokeObjectURL(url: string): void;
}

type NativeURLSearchParams = {
	prototype: URLSearchParams;
	new(init?: string[][] | Record<string, string> | string | URLSearchParams): URLSearchParams;
	toString(): string;
}

declare module "whatwg-url-without-unicode"{
	const URL: NativeURL;
	const URLSearchParams: NativeURLSearchParams;
}