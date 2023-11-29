package com.example.terminal.data

import retrofit2.http.GET

interface BarApi {

    @GET("/aggs/ticker/AAPL/range/1/hour/2023-01-09/2023-01-09?adjusted=true&sort=asc&limit=50000&apiKey=LQ8xY1Y2WDUiYDQa06HpdbOusdqyR84X")
    suspend fun getBars(): BarResponse
}