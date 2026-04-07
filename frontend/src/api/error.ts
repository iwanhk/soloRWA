import {API_ERROR_CODE} from "@/types/api";
import i18n from "@/i18n";

export class ApiError extends Error{
    public code: API_ERROR_CODE;
    public data: any;

    constructor(code:API_ERROR_CODE, message:string, data?:any) {
        const errorKey = `error.${code}`;
        const translated = i18n.global.t(errorKey, typeof data === "object"?data:undefined);
        if(translated !== errorKey){
            message = translated;
        }
        super(message);
        this.code = code;
        this.data = data;
    }
}

