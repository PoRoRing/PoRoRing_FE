package com.example.pororing.search

data class FacilityResponse(
    val fcltOpenInfo_OMI: FacilityData
)

data class FacilityData(
    val list_total_count: Int,
    val RESULT: ResultInfo,
    val row: List<FacilityItem>
)

data class ResultInfo(
    val CODE: String,
    val MESSAGE: String
)

data class FacilityItem(
    val FCLT_NM: String,
    val FCLT_ADDR: String,
)