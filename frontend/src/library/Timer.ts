import {ApiGetSystemTime} from "@/api/generic/ApiGetSystemTime.ts";
import {getTimeoutPromise} from "@/library/Utility.ts";

let now = Date.now();
let diff = ref(0);
const second = ref(now)
const minute = ref(now);
//const hour = ref(now);
//const day = ref(now);

function tick(){
	now = Date.now() + diff.value;
	second.value = now;

	if(now - minute.value > 60*1000){
		minute.value = now;
	}
	// if(now - hour.value > 60*60*1000){
	// 	hour.value = now;
	// }
	// if (now - day.value > 24 * 60 * 60 * 1000) {
	// 	day.value = this.now;
	// }

	setTimeout(tick,1000);
}

async function initDiff(){
	try {
		const api = new ApiGetSystemTime();
		const {data} = await api.call()
		diff.value = data - timer.second.value;
	}
	catch (e){
		await getTimeoutPromise(10000);
		initDiff();
	}
}

initDiff();
tick()

const timer = {
	second,
	minute,
	diff,
}

export default timer;