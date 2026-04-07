
export interface UniFormsRules{
    [key:string]:{
        rules:UniFormsRule[],
        label?:string,
    }
}

export interface UniFormsRule {
    required?: boolean,
    range?: any[],
    format?: UniFormsNativeRuleFormat,
    pattern?: RegExp,
    maximum?: number,
    minimum?: number,
    maxLength?: number,
    minLength?: number,
    errorMessage?: string,
    validateFunction?: (rule:UniFormsRule, value:any, data:Record<string, any>, callback:(message?:string)=>void)=>boolean| void | Promise<any>
}

export type UniFormsNativeRuleFormat = "string" | "number" | "boolean" | "array" | "object" | "url" | "email";

export interface UniFormsInstance{
    validate(keepItem?:string[]):Promise<any>;
    validate(keepItem:string[], callback: (err: any, formData: any) => void):null;
    validateField(field:string):any;
}

export interface UniCommonResponse{
    "code": number,
    "message": string,
    "errCode": number,
    "errMsg": string
}
