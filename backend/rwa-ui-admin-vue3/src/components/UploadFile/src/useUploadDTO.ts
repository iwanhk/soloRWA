import * as FileApi from '@/api/infra/file'
import { UploadRawFile, UploadRequestOptions, UploadProgressEvent } from 'element-plus/es/components/upload/src/upload'
import axios, { AxiosProgressEvent } from 'axios'

/**
 * 获得上传 URL
 */
export const getUploadUrl = (): string => {
    return import.meta.env.VITE_BASE_URL + import.meta.env.VITE_API_URL + '/infra/file/upload-dto'
}

export const useUploadDTO = (directory?: string) => {
    // 后端上传地址
    const uploadUrl = getUploadUrl()
    // 模式二：后端上传
    // 重写 el-upload httpRequest 文件上传成功会走成功的钩子，失败走失败的钩子
    const httpRequest = async (options: UploadRequestOptions) => {
        // 文件上传进度监听
        const uploadProgressHandler = (evt: AxiosProgressEvent) => {
            const upEvt: UploadProgressEvent = Object.assign(evt.event)
            upEvt.percent = evt.progress ? (evt.progress * 100) : 0
            options.onProgress(upEvt) // 触发 el-upload 的 on-progress
        }

        return new Promise((resolve, reject) => {
            FileApi.uploadFileDto({ file: options.file, path: directory }, uploadProgressHandler)
                .then((res) => {
                    if (res.code === 0) {
                        resolve(res)
                    } else {
                        reject(res)
                    }
                })
                .catch((res) => {
                    reject(res)
                })
        })
    }

    return {
        uploadUrl,
        httpRequest
    }
}
