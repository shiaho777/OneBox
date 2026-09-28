package com.wanbaohe.xiangqi.data.online

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * REST API for generic game online room management.
 * 服务端按 gameType 隔离房间存储,中国象棋固定传 "xiangqi"。
 */
interface SignalingApi {

    @POST("game/rooms")
    suspend fun createRoom(@Body request: RoomCreateRequest): Response<RoomResponse>

    @GET("game/rooms")
    suspend fun listRooms(
        @Query("gameType") gameType: String,
    ): Response<RoomsListResponse>

    @POST("game/rooms/{roomId}/join")
    suspend fun joinRoom(
        @Path("roomId") roomId: String,
        @Body request: RoomJoinRequest,
    ): Response<RoomResponse>

    @DELETE("game/rooms/{roomId}/leave")
    suspend fun leaveRoom(@Path("roomId") roomId: String): Response<SuccessResponse>
}

@Serializable
data class RoomCreateRequest(
    val gameType: String,
    val hostName: String,
    val hostAvatarUrl: String = "",
    val gameConfig: JsonObject? = null,
)

@Serializable
data class RoomJoinRequest(
    val guestName: String,
    val guestAvatarUrl: String = "",
)

@Serializable
data class RoomResponse(@SerialName("data") val room: RoomDto? = null)

@Serializable
data class RoomsListResponse(@SerialName("data") val rooms: List<RoomDto>? = null)

@Serializable
data class SuccessResponse(val message: String = "")

@Serializable
data class RoomDto(
    // 默认值对齐 Gson 容错:服务端(go-proxy game 模块)字段稳定,缺字段时不至于整包解析失败
    val id: String = "",
    val gameType: String = "",
    val hostId: Int = 0,
    val hostName: String = "",
    val hostAvatarUrl: String? = null,
    val hostAvatar: String? = null,
    val guestId: Int = 0,
    val guestName: String = "",
    val guestAvatarUrl: String? = null,
    val guestAvatar: String? = null,
    val status: String = "",
    val createdAt: Long = 0L,
    val gameConfig: JsonObject? = null,
)
