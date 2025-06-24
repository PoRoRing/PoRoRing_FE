package com.example.pororing.search

import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.Call
import retrofit2.http.Path

interface ApiService {

    @GET("{key}/{type}/{service}/{start}/{end}")
    fun getFacilityInfo(
        @Path("key") key: String,
        @Path("type") type: String = "json",
        @Path("service") service: String = "fcltOpenInfo_OMI",
        @Path("start") startIndex: Int,
        @Path("end") endIndex: Int,
        @Query("FCLT_KIND_DTL_NM") facilityKind: String? = null
    ): Call<FacilityResponse>
}