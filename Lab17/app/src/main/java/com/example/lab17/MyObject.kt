package com.example.lab17

import com.google.gson.annotations.SerializedName

data class MyObject(
    @SerializedName("result") val result: Result
)

data class Result(
    @SerializedName("records") val records: List<Record>
)

data class Record(
    @SerializedName("SiteName") val siteName: String = "未知地區",
    @SerializedName("Status") val status: String = "未知狀態"
)
