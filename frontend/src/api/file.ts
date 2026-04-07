import type {UVPUploadFile} from "@/types/uview-plus";

export class ApiUploadFile{
	public file:File;
	public name:string;
	public size: number;
	public thumb: string;
	public type: string;
	public url: string;

	constructor(data: UVPUploadFile) {
		this.file = data.file || new File([],'dummy');
		this.name = data.name;
		this.size = data.size;
		this.thumb = data.thumb;
		this.type = data.type;
		this.url = data.url;
	}
}