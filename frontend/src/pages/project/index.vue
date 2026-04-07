<script lang="ts" setup>

import {useI18n} from 'vue-i18n';
import NavBar from "@/components/NavBar.vue";
import CompImage from "@/components/CompImage.vue";
import MessageCount from "@/components/MessageCount.vue";
import UserVerify from "@/components/UserVerify.vue";
import ProjectSwitcher from "@/pages/project/components/ProjectSwitcher.vue";
import {useUserStore} from "@/store/user.ts";
import ENV from "@/library/env.ts";
import UniRouter from "@/library/UniRouter.ts";
import {isTabbarPage, loginPage} from "@/library/GlobalVars.ts";
import {onHide, onPullDownRefresh, onShow} from "@dcloudio/uni-app";
import CompLogo from "@/components/CompLogo.vue";
import {useConfigStore} from "@/store/config.ts";

const {t} = useI18n();

const userStore = useUserStore();
const configStore = useConfigStore();
const fontScale = computed(()=>configStore.fontScale);
function goLogin(){
	UniRouter.to(loginPage)
}

onShow(()=>{
	isTabbarPage.value = true
})
onHide(()=>{
	isTabbarPage.value = false
})

onPullDownRefresh(async ()=>{
	await userStore.loadUserInfo();
	uni.stopPullDownRefresh()
})
</script>

<template>
	<NavBar title="" transparent side-width="auto">
		<template #left>
			<view class="logo-wrap">
				<CompLogo class="logo" />
			</view>
		</template>
		<template #right>
			<view class="message-wrap">
				<MessageCount />
			</view>
		</template>
	</NavBar>
	<view class="page page-market">
		<template v-if="userStore.user">
			<UserVerify />

			<ProjectSwitcher />
		</template>
		<view v-else class="login wide-section">
			<view class="main-vision">
				<view class="main-title">{{ ENV.foreign ? t('pages.project.index.welcomeTitleRWA') : t('pages.project.index.welcomeTitle') }}</view>
				<view class="sub-title">{{ t('pages.project.index.subTitle') }}</view>
			</view>
			<view class="action">
				<up-button type="primary" shape="circle" size="small" @click="goLogin">
					{{ t('pages.project.index.registerLogin') }}
				</up-button>
			</view>

			<view class="feature">
				<view class="feature-item">
					<view class="image">
						<CompImage src="/static/images/launch/feature-1.png" />
					</view>
					<view class="text">
						<view class="title">
							{{ ENV.foreign ? t('pages.project.index.whyChooseRWA') : t('pages.project.index.whyChoose') }}<view>{{ t('pages.project.index.hongKongCompliance') }}</view>
						</view>
						<view class="content">
							<view class="item">{{ t('pages.project.index.feature1Content1') }}</view>
							<view class="item">{{ t('pages.project.index.feature1Content2') }}</view>
							<view class="item">{{ t('pages.project.index.feature1Content3') }}</view>
						</view>
					</view>
				</view>
				<view class="feature-item">
					<view class="image">
						<CompImage src="/static/images/launch/feature-2.png" />
					</view>
					<view class="text">
						<view class="title">{{ t('pages.project.index.realAssets') }}</view>
						<view class="content">
							<view class="item">{{ t('pages.project.index.feature2Content1') }}</view>
							<view class="item">{{ t('pages.project.index.feature2Content2') }}</view>
							<view class="item">{{ t('pages.project.index.feature2Content3') }}</view>
						</view>
					</view>
				</view>
				<view class="feature-item">
					<view class="image">
						<CompImage src="/static/images/launch/feature-3.png" />
					</view>
					<view class="text">
						<view class="title">{{ t('pages.project.index.modularInfrastructure') }}</view>
						<view class="content">
							<view class="item">{{ t('pages.project.index.feature3Content1') }}</view>
							<view class="item">{{ t('pages.project.index.feature3Content2') }}</view>
							<view class="item">{{ t('pages.project.index.feature3Content3') }}</view>
							<view class="item">{{ t('pages.project.index.feature3Content4') }}</view>
						</view>
					</view>
				</view>
			</view>
			
			<view class="slogan">
				<view class="main-text">{{ t('pages.project.index.slogan') }}</view>
				<view class="sub-text">{{ t('pages.project.index.slogan', {}, 'en') }}</view>
			</view>
		</view>
	</view>
</template>

<style scoped lang="scss">
.page{
	--font-scale:v-bind(fontScale);
}
.nav-bar{
	:deep(.uni-navbar__header-btns){
		overflow: visible;
	}
}
.login{
	padding-left: 20px;
	padding-right: 20px;
	.main-vision{
		height: 160px;
		background: url("@/static/images/launch/main-vision.png") no-repeat center 40%/contain;
		color: $color-text-white;
		text-align: center;
		@include flex-column(center, flex-end);
		margin: 0 auto 18px;

		.main-title{
			@include fs(18);
			position: relative;
			top: 18px;
		}
		.sub-title{
			@include fs(12);
			position: relative;
			top: 18px;
		}
	}
	.action{
		padding: 20px;
		.u-button{
			height: 2em;
			width: 178px;
			@include fs(16);
		}
	}
	.feature{
		margin-top: 50px;
		.feature-item{
			@include flex-row(center);
			gap: 20px;
			.image{
				width: 138px;
				flex: none;
				.comp-image{
					width: 100%;
				}
			}
			.text{
				flex: 1;
				.title{
					@include fs(14);
					color: $color-text-white;
				}
				.content{
					margin-top: 5px;
					@include fs(10);
					color: $color-gray;
					
					.item{
						padding-left: 1em;
						text-indent: -1em;
						&:before{
							padding-left: 0;
							text-indent: 0;
							content: '·';
							display: inline-block;
							width: 1em;
							text-align: center;
							vertical-align: top;
						}
					}
				}
			}

			+.feature-item{
				margin-top: 25px;
			}

			&:nth-child(2n+1){
				flex-direction: row-reverse;
			}
		}
	}
	.slogan{
		text-align: center;
		margin-top: 30px;
		.main-text{
			@include fs(18);
			color: $color-text-white;
		}
		.sub-text{
			@include fs(12);
			color: $color-gray;
		}
	}
}
</style>

