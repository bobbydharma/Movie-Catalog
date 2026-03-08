package com.example.network.model

import com.google.gson.annotations.SerializedName

data class VideoResponse(
    @SerializedName("results")
    val results: List<VideoDetailResponse>?
)
data class VideoDetailResponse(
    @SerializedName("id")
    val id: String?,
    @SerializedName("key")
    val key: String?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("type")
    val type: String?,
    @SerializedName("site")
    val site: String?
)