package com.mobile.searchmvvm.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Jokes(
    val categories: List<String>,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String,
    @SerializedName("icon_url")
    val iconUrl: String,
    val id: String,
    val url: String,
    val value: String
): Serializable
