package com.example.pororing

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pororing.databinding.FragmentPlusBinding
import com.example.pororing.search.ItemAdapter
import com.example.pororing.search.ItemData
import com.example.pororing.search.ApiService
import com.example.pororing.search.FacilityResponse
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory

class PlusFragment : Fragment() {

    private val retrofit = Retrofit.Builder()
        .baseUrl("http://openapi.seoul.go.kr:8088/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val apiService = retrofit.create(ApiService::class.java)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentPlusBinding.inflate(inflater, container, false)

        val startIndex = (arguments?.getString("searchIndex") ?: "0").toIntOrNull() ?: 0
        val endIndex = startIndex + 9

        val call = apiService.getFacilityInfo(
            key = "",
            startIndex = startIndex,
            endIndex = endIndex,
            facilityKind = "요양시설"
        )

        call.enqueue(object : Callback<FacilityResponse> {
            override fun onResponse(
                call: Call<FacilityResponse>,
                response: Response<FacilityResponse>
            ) {
                if (response.isSuccessful) {
                    val body = response.body()
                    val items = body?.fcltOpenInfo_OMI?.row

                    val parsedList = items?.map {
                        ItemData(
                            name = it.FCLT_NM,
                            address = it.FCLT_ADDR
                        )
                    } ?: emptyList()
                    val adapter = ItemAdapter(parsedList)
                    binding.jsonRecyclerView.layoutManager = LinearLayoutManager(requireContext())
                    binding.jsonRecyclerView.adapter = adapter
                } else {
                    Log.e("API", "응답 실패 - 코드: ${response.code()}")
                }
            }
            override fun onFailure(call: Call<FacilityResponse>, t: Throwable) {
                Log.e("API", "호출 실패: ${t.message}", t)
            }
        })
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(searchIndex: String) =
            PlusFragment().apply {
                arguments = Bundle().apply {
                    putString("searchIndex", searchIndex)
                }
            }
    }
}