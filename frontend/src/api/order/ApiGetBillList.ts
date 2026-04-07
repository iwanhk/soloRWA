import {Api} from "@/api";
import {type ApiListParams, type ApiListResult, ApiMethod} from "@/types/api";
import type {BillEntity} from "@/types/entity";
import {BillType} from "@/types/enums";

export interface ApiGetBillListParams extends ApiListParams {
	billType?: BillType; // 账单类型
	orderId?: number; // 订单ID
	projectId?: number; // 项目ID
	auditStatus?: number; // 审核状态：1-待审核 2-审核通过 3-审核不通过
}

export class ApiGetBillList extends Api<ApiListResult<BillEntity>> {
	constructor(params: ApiGetBillListParams) {
		super('/project/project-order/getBillList', ApiMethod.GET, params);
	}
}

