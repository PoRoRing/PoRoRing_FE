package com.example.pororing.save

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("B553748/CertImgListServiceV3/getCertImgListServiceV3")
    fun getJsonList(
        @Query("serviceKey") serviceKey: String,
        @Query("prdlstReportNo") prdlstReportNo: String? = null,
        @Query("prdlstNm") prdlstNm: String? = null,
        @Query("returnType") returnType: String = "json",
        @Query("pageNo") pageNo: Int = 1,
        @Query("numOfRows") numOfRows: Int = 1,
        @Query("prdkind") prdkind: String? = null,
        @Query("manufacture") manufacture: String? = null,
        @Query("allergy") allergy: String? = null
    ): Call<String>
}