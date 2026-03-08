package com.example.network.model

import com.google.gson.annotations.SerializedName

data class ReviewResponse(
    @SerializedName("results")
    val results: List<ReviewDetailResponse>?
)
data class ReviewDetailResponse(
    @SerializedName("id")
    val id: String?,
    @SerializedName("author")
    val author: String?,
    @SerializedName("content")
    val content: String?,
    @SerializedName("created_at")
    val createdAt: String?,
    @SerializedName("author_details")
    val authorDetails: AuthorDetailsResponse?
)
data class AuthorDetailsResponse(
    @SerializedName("rating")
    val rating: Double?,
    @SerializedName("avatar_path")
    val avatarPath: String?
)