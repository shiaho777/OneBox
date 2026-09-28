package com.shifenmiao.model.ocr

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class DocConvertRequestResponse(
    @SerialName("success")
    val success: Boolean? = null,
    @SerialName("log_id")
    val logId: String? = null,
    @SerialName("result")
    val result: DocConvertTaskResult? = null,
    @SerialName("code")
    val code: Int? = null,
    @SerialName("message")
    val message: String? = null,
    @SerialName("error_code")
    val errorCode: Int? = null,
    @SerialName("error_msg")
    val errorMsg: String? = null,
) : Parcelable

@Parcelize
@Serializable
data class DocConvertTaskResult(
    @SerialName("task_id")
    val taskId: String? = null
) : Parcelable

@Parcelize
@Serializable
data class DocConvertQueryResponse(
    @SerialName("success")
    val success: Boolean? = null,
    @SerialName("log_id")
    val logId: String? = null,
    @SerialName("result")
    val result: DocConvertQueryResult? = null,
    @SerialName("code")
    val code: Int? = null,
    @SerialName("message")
    val message: String? = null,
    @SerialName("error_code")
    val errorCode: Int? = null,
    @SerialName("error_msg")
    val errorMsg: String? = null,
) : Parcelable

@Parcelize
@Serializable
data class DocConvertQueryResult(
    @SerialName("task_id")
    val taskId: String? = null,
    @SerialName("ret_code")
    val retCode: Int? = null,
    @SerialName("ret_msg")
    val retMsg: String? = null,
    @SerialName("percent")
    val percent: Int? = null,
    @SerialName("result_data")
    val resultData: DocConvertResultData? = null,
    @SerialName("create_time")
    val createTime: String? = null,
    @SerialName("start_time")
    val startTime: String? = null,
    @SerialName("end_time")
    val endTime: String? = null
) : Parcelable

@Parcelize
@Serializable
data class DocConvertResultData(
    @SerialName("word")
    val word: String? = null,
    @SerialName("excel")
    val excel: String? = null
) : Parcelable

