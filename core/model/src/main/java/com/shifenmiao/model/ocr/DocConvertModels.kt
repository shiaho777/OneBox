package com.shifenmiao.model.ocr

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class DocConvertRequestResponse(
    @SerializedName("success")
    @SerialName("success")
    val success: Boolean? = null,
    @SerializedName("log_id")
    @SerialName("log_id")
    val logId: String? = null,
    @SerializedName("result")
    @SerialName("result")
    val result: DocConvertTaskResult? = null,
    @SerializedName("code")
    @SerialName("code")
    val code: Int? = null,
    @SerializedName("message")
    @SerialName("message")
    val message: String? = null,
    @SerializedName("error_code")
    @SerialName("error_code")
    val errorCode: Int? = null,
    @SerializedName("error_msg")
    @SerialName("error_msg")
    val errorMsg: String? = null,
) : Parcelable

@Parcelize
@Serializable
data class DocConvertTaskResult(
    @SerializedName("task_id")
    @SerialName("task_id")
    val taskId: String? = null
) : Parcelable

@Parcelize
@Serializable
data class DocConvertQueryResponse(
    @SerializedName("success")
    @SerialName("success")
    val success: Boolean? = null,
    @SerializedName("log_id")
    @SerialName("log_id")
    val logId: String? = null,
    @SerializedName("result")
    @SerialName("result")
    val result: DocConvertQueryResult? = null,
    @SerializedName("code")
    @SerialName("code")
    val code: Int? = null,
    @SerializedName("message")
    @SerialName("message")
    val message: String? = null,
    @SerializedName("error_code")
    @SerialName("error_code")
    val errorCode: Int? = null,
    @SerializedName("error_msg")
    @SerialName("error_msg")
    val errorMsg: String? = null,
) : Parcelable

@Parcelize
@Serializable
data class DocConvertQueryResult(
    @SerializedName("task_id")
    @SerialName("task_id")
    val taskId: String? = null,
    @SerializedName("ret_code")
    @SerialName("ret_code")
    val retCode: Int? = null,
    @SerializedName("ret_msg")
    @SerialName("ret_msg")
    val retMsg: String? = null,
    @SerializedName("percent")
    @SerialName("percent")
    val percent: Int? = null,
    @SerializedName("result_data")
    @SerialName("result_data")
    val resultData: DocConvertResultData? = null,
    @SerializedName("create_time")
    @SerialName("create_time")
    val createTime: String? = null,
    @SerializedName("start_time")
    @SerialName("start_time")
    val startTime: String? = null,
    @SerializedName("end_time")
    @SerialName("end_time")
    val endTime: String? = null
) : Parcelable

@Parcelize
@Serializable
data class DocConvertResultData(
    @SerializedName("word")
    @SerialName("word")
    val word: String? = null,
    @SerializedName("excel")
    @SerialName("excel")
    val excel: String? = null
) : Parcelable

