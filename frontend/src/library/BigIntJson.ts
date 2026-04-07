export function bigIntJSONStringify(value:any){
	return JSON.stringify(value, (_, value) => {
		if (typeof value === 'bigint') {
			return value.toString() + 'n';
		}
		return value;
	});
}
export function bigIntJSONParse(json:string){
	try {
		return JSON.parse(json, (_, value) => {
			if (typeof value === 'string' && /^-?\d+n$/.test(value)) {
				return BigInt(value.slice(0, -1));
			}
			return value;
		});
	}
	catch (e){
		if(json) {
			console.log("invalid json", json)
		}
		return undefined;
	}
}