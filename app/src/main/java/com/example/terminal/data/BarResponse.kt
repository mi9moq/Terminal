package com.example.terminal.data

import com.google.gson.annotations.SerializedName

data class BarResponse(
    @SerializedName("results")
    val bars: List<Bar>
)