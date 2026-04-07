<script lang="ts" setup>
import {onLoad, onPullDownRefresh} from "@dcloudio/uni-app";
import {useI18n} from 'vue-i18n';
import ProjectDetailBuy from "@/pages/project/components/ProjectDetailBuy.vue";
import {useProjectDetail} from "@/composable/useProjectDetail.ts";
import {formatImageUrl} from "@/library/Utility.ts";
import {Currency, ProjectStatus} from "@/types/enums.ts";
import AssetProfitChart from "@/components/AssetProfitChart.vue";
import ProjectInfo from "@/pages/project/components/ProjectInfo.vue";
import {accessToken} from "@/library/GlobalVars.ts";
import {formatPrice, formatPriceInfo, transformCurrency} from "@/library/format.ts";
import {useConfigStore} from "@/store/config.ts";
import {dictKey} from "@/library/dict.ts";
import {currencyOptions} from "@/types/options.ts";

const {t} = useI18n();
const configStore = useConfigStore();
const fontScale = computed(()=>configStore.fontScale);

const {loading, project, load, state: projectState} = useProjectDetail()
const images = computed(()=>{
	let result: { url:string, title?:string, type: 'image' | 'video', poster?:string }[] = [];
	const images = project.value?.projectImageUrls?.split(',')??[]

	result = images.map(image=>{
		return{
			url: formatImageUrl(image),
			type: 'image'
		}
	});
	
	if(project.value?.projectVideoUrl){
		result.unshift({
			url: project.value.projectVideoUrl,
			type: "video",
			poster: project.value.cover
		})
	}
	
	return result;
})

const projectNumber = computed(()=>{
	return project.value?.projectId.toString().padStart(5, "0");
})

const priceInfo = computed(()=>{
	if(!project.value) return ['--', undefined];
	return formatPriceInfo(transformCurrency(project.value.issueUnitPrice, Currency.USDT, project.value.investmentCurrency));
})

onLoad((query)=>{
	const id = parseInt(query?.id??'');
	if(id){
		load(id)
	}
})

onPullDownRefresh(async ()=>{
	await load();
	uni.stopPullDownRefresh();
})

</script>

<template>
	<view class="page page-project-detail">
		<view class="loading" v-if="loading">
			<up-loading-icon></up-loading-icon>
		</view>
		<template v-else-if="project">
			<view class="swiper wide-section">
				<u-swiper :list="images" :height="207" :autoplay="!project.projectVideoUrl" circular indicator indicator-mode="dot" indicator-inactive-color="#fff"></u-swiper>
			</view>
			<view class="basic-info">
				<view class="title">
					{{ project.projectName }}
					<text class="serial">{{ projectNumber }}</text>
				</view>
				<view class="info">
					<view class="info-item">
						<view class="label">{{ t('pages.project.detail.remainingQuantity') }}</view>
						<view class="value">{{ project.remainingQuantity }}</view>
					</view>
					<view class="info-item">
						<view class="label">{{ t('pages.project.detail.expectedAnnualReturn') }}</view>
						<view class="value">{{ project.expectedAnnualReturn }}%</view>
					</view>
					<view class="info-item">
						<view class="label">{{ t('pages.project.detail.issueUnitPrice') }}</view>
						<view class="value">
							<text style="margin-right: 5px">{{dictKey(currencyOptions, priceInfo[1], 'unit')}}</text>
							<text class="i-block">{{priceInfo[0]}}</text>
						</view>
					</view>
				</view>
			</view>

			<AssetProfitChart v-if="accessToken && project.projectStatus === ProjectStatus.PROFITABLE" :project="project" />

			<ProjectInfo :project="project" />
			
			<view class="project-buy-wrap" v-if="project && projectState">
				<ProjectDetailBuy :project="project" :project-state="projectState" />
			</view>
		</template>
	</view>
</template>

<style scoped lang="scss">
.page{
	--font-scale:v-bind(fontScale);
}
.page-project-detail {
	.loading{
		height: calc(100vh - 50px - 44px - 100px);
		@include flex-column(center, center);
	}
}
.swiper{
	margin-bottom: 10px;
}
.basic-info{
	.title{
		@include fs(14);
		color: $color-text-white;
		.serial{
			display: inline-block;
			background: $theme-color;
			color: $color-text-white;
			@include fs(8);
			border-radius: 4px;
			padding: 1px 4px;
		}
	}

	.info{
		margin-top: 10px;
		margin-bottom: 16px;
		
		@include flex-row();
		.info-item{
			flex: 1;
			@include flex-column(center,center);
			flex-direction: column-reverse;
			.label{
				color: $color-gray;
				@include fs(11);
				@include en-break;
				text-align: center;
			}
			.value{
				color: $theme-color;
				@include fs(17);
				text-align: center;
			}
			+.info-item{
				border-left: 1px solid #fff;
			}
		}
	}
	
	.actions {
		@include fs(10);
		
		.switcher.switcher-display-button{
			height: 26px;
			:deep(.switcher-item){
				background: #4A4A4A;
				border-radius: 5px;
				@include fw(medium);
				&.active{
					background: #ffffff;
					.text {
						color: $bg-color;
					}
				}
			}
		}
	}
}
.project-detail-info {
	margin-top: 20px;
}
.project-buy-wrap{
	height: 60px;
	
}
</style>

