<script setup lang="ts">
import type { PopupInstance } from '@/types/popup';
import CompPopup from '@/components/popup/CompPopup.vue';
import { defaultPopupExpose } from '@/library/PopupManager';
import ENV from "@/library/env.ts";
import { useI18n } from 'vue-i18n';
//@ts-ignore
import * as pdfjsLib from "pdfjs-dist/build/pdf"
import pdfWorkerUrl from "pdfjs-dist/build/pdf.worker?url"

// 初始化 worker
pdfjsLib.GlobalWorkerOptions.workerSrc = pdfWorkerUrl;

const { t } = useI18n();

const popup = ref<PopupInstance>();
const title = ref<string>('');
const pdfUrl = ref<string>('');
const loading = ref(false);
const error = ref<string>('');
const currentPage = ref(1);
const totalPages = ref(0);
// 使用 shallowRef + markRaw 避免 Vue Proxy 包裹 pdf.js 对象
const pdfDocument = shallowRef<any>(null);
const canvasRef = ref<any>();
const pageImages = ref<string[]>([]);

// 水印配置
const watermark = ref<string>(''); // 水印文本，为空时不显示水印
const watermarkOpacity = ref(0.15); // 水印透明度 (0-1)
const watermarkFontSize = ref(48); // 水印字体大小
const watermarkAngle = ref(-45); // 水印旋转角度（度数）

// 确保 PDF 文档已加载
const isPdfLoaded = computed(() => pdfDocument.value && pdfDocument.value.numPages > 0);

// 在 canvas 上绘制水印
function drawWatermark(canvas: HTMLCanvasElement, context: CanvasRenderingContext2D) {
	if (!watermark.value) return; // 如果没有水印文本，不绘制

	const width = canvas.width;
	const height = canvas.height;

	// 保存当前状态
	context.save();

	// 设置水印样式
	context.globalAlpha = watermarkOpacity.value;
	context.font = `bold ${watermarkFontSize.value}px Arial`;
	context.fillStyle = '#000000';
	context.textAlign = 'center';
	context.textBaseline = 'middle';

	// 计算旋转中心
	const centerX = width / 2;
	const centerY = height / 2;

	// 应用旋转
	context.translate(centerX, centerY);
	context.rotate((watermarkAngle.value * Math.PI) / 180);

	// 绘制水印文本（重复多次以覆盖整个页面）
	const textWidth = context.measureText(watermark.value).width;
	const spacing = textWidth + 100; // 水印之间的间距

	for (let x = -width; x < width; x += spacing) {
		for (let y = -height; y < height; y += spacing) {
			context.fillText(watermark.value, x, y);
		}
	}

	// 恢复状态
	context.restore();
}

// 渲染 PDF 页面为图片
async function renderPage(pageNum: number) {
	if (!pdfDocument.value) {
		error.value = t('components.pdfPreview.loadError');
		return;
	}

	if (pageNum < 1 || pageNum > totalPages.value) {
		error.value = t('components.pdfPreview.loadError');
		return;
	}

	try {
		loading.value = true;
		error.value = '';

		// 获取页面
		let page;
		try {
			page = await pdfDocument.value.getPage(pageNum);
		} catch (pageErr) {
			console.error('Error getting page:', pageErr);
			error.value = t('components.pdfPreview.loadError');
			return;
		}

		if (!page) {
			error.value = t('components.pdfPreview.loadError');
			return;
		}

		// 获取视口
		const viewport = page.getViewport({ scale: 2 });

		// 创建 canvas
		const canvas = document.createElement('canvas');
		canvas.width = viewport.width;
		canvas.height = viewport.height;

		const context = canvas.getContext('2d');
		if (!context) {
			error.value = t('components.pdfPreview.loadError');
			return;
		}

		// 渲染页面
		try {
			const renderTask = page.render({
				canvasContext: context,
				viewport: viewport
			});

			await renderTask.promise;

			// 绘制水印
			drawWatermark(canvas, context);

			// 转换为图片 URL
			pageImages.value[pageNum - 1] = canvas.toDataURL('image/png');
			currentPage.value = pageNum;
		} catch (renderErr) {
			console.error('Error rendering page:', renderErr);
			error.value = t('components.pdfPreview.loadError');
		}
	} catch (err) {
		error.value = t('components.pdfPreview.loadError');
		console.error('Failed to render page:', err);
	} finally {
		loading.value = false;
	}
}

// 加载 PDF
async function loadPdf(url: string) {
	try {
		loading.value = true;
		error.value = '';
		pageImages.value = [];
		pdfDocument.value = null;

		// 加载 PDF 文档 - 使用 fetch 获取 ArrayBuffer
		const response = await fetch(url);
		if (!response.ok) {
			error.value = t('components.pdfPreview.loadError');
			return;
		}

		const arrayBuffer = await response.arrayBuffer();

		// 使用 ArrayBuffer 加载 PDF
		const loadingTask = pdfjsLib.getDocument({
			data: arrayBuffer
		});

		const pdf = await loadingTask.promise;

		if (!pdf) {
			error.value = t('components.pdfPreview.loadError');
			return;
		}

		const numPages = pdf.numPages;
		if (!numPages || numPages <= 0) {
			error.value = t('components.pdfPreview.loadError');
			return;
		}

		// 使用 markRaw 避免 Vue Proxy 包裹 pdf.js 对象
		pdfDocument.value = markRaw(pdf);
		totalPages.value = numPages;
		currentPage.value = 1;

		// 延迟一下再渲染，确保文档完全初始化
		await new Promise(resolve => setTimeout(resolve, 100));

		// 渲染第一页
		await renderPage(1);
	} catch (err) {
		error.value = t('components.pdfPreview.loadError');
		console.error('Failed to load PDF:', err);
		pdfDocument.value = null;
	} finally {
		loading.value = false;
	}
}

// 上一页
async function prevPage() {
	if (currentPage.value > 1) {
		await renderPage(currentPage.value - 1);
	}
}

// 下一页
async function nextPage() {
	if (currentPage.value < totalPages.value) {
		await renderPage(currentPage.value + 1);
	}
}

// 预览图片（支持缩放）
async function previewImage() {
	// 确保所有页面都已渲染
	for (let i = 1; i <= totalPages.value; i++) {
		if (!pageImages.value[i - 1]) {
			await renderPage(i);
		}
	}
	const urls = pageImages.value.filter(Boolean);
	if (urls.length === 0) return;
	uni.previewImage({
		current: currentPage.value - 1,
		urls,
	});
}

defineExpose(defaultPopupExpose(popup, {
	open(popupTitle: string, url: string ) {
		title.value = popupTitle;
		
		const watermarkText = ENV.foreign?"Solo RWA":'阳光基金';
		const watermarkConfig = { opacity: 0.15, fontSize: 24, angle: 45 }

		// 设置水印
		watermark.value = watermarkText || '';
		if (watermarkConfig) {
			if (watermarkConfig.opacity !== undefined) {
				watermarkOpacity.value = watermarkConfig.opacity;
			}
			if (watermarkConfig.fontSize !== undefined) {
				watermarkFontSize.value = watermarkConfig.fontSize;
			}
			if (watermarkConfig.angle !== undefined) {
				watermarkAngle.value = watermarkConfig.angle;
			}
		}

		pdfUrl.value = url;
		popup.value?.open();
		loadPdf(url);
	}
}));
</script>

<template>
	<CompPopup
		ref="popup"
		:title="title"
		closable
		class="pdf-preview-popup"
	>
		<!-- 加载中 -->
		<view v-if="loading" class="loading">
			<text>{{ t('components.pdfPreview.loading') }}</text>
		</view>

		<!-- 错误提示 -->
		<view v-else-if="error" class="error">
			<text>{{ error }}</text>
		</view>

		<!-- PDF 内容 -->
		<view v-else-if="pageImages[currentPage - 1]" class="pdf-content">
			<image :src="pageImages[currentPage - 1]" class="pdf-image" mode="aspectFit" @click="previewImage"></image>

			<!-- 分页控制 -->
			<view class="pagination">
				<button
					:disabled="currentPage <= 1"
					@click="prevPage"
					class="btn-nav"
				>
					{{ t('components.pdfPreview.prevPage') }}
				</button>
				<text class="page-info">{{ currentPage }} / {{ totalPages }}</text>
				<button
					:disabled="currentPage >= totalPages"
					@click="nextPage"
					class="btn-nav"
				>
					{{ t('components.pdfPreview.nextPage') }}
				</button>
			</view>
		</view>

		<!-- 空状态 -->
		<view v-else class="empty">
			<text>{{ t('components.pdfPreview.empty') }}</text>
		</view>
	</CompPopup>
</template>

<style scoped lang="scss">
.pdf-preview-popup {
	:deep(.popup-container){
		height: 90vh;
		max-height: 90vh;
	}
	padding: 10px 0;
	@include fs(12);
	color: $color-text-black;

	.loading,
	.error,
	.empty {
		display: flex;
		justify-content: center;
		align-items: center;
		min-height: 300px;
		text-align: center;
	}

	.error {
		color: #ff6b6b;
	}

	.pdf-content {
		height: 100%;
		display: flex;
		flex-direction: column;
		align-items: center;
		gap: 15px;

		.pdf-image {
			width: 100%;
			height: 100%;
			flex: 1;
		}

		.pagination {
			display: flex;
			justify-content: center;
			align-items: center;
			gap: 10px;
			width: 100%;

			.btn-nav {
				background-color: #f0f0f0;
				border: 1px solid #d0d0d0;
				border-radius: 4px;
				cursor: pointer;
				@include fs(12);

				&:disabled {
					opacity: 0.5;
					cursor: not-allowed;
				}

				&:not(:disabled):active {
					background-color: #e0e0e0;
				}
			}

			.page-info {
				min-width: 80px;
				text-align: center;
				font-weight: 500;
			}
		}
	}
}
</style>

