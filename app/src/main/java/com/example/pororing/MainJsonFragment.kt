package com.example.pororing

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import com.google.firebase.firestore.FirebaseFirestore
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pororing.databinding.FragmentMainJsonBinding
import com.example.pororing.save.ItemAdapter
import com.example.pororing.save.ItemData

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class MainJsonFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    private var _binding: FragmentMainJsonBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainJsonBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val db = FirebaseFirestore.getInstance()
        val currentUserEmail = MyApplication.email

        if (currentUserEmail == null) {
            Log.w("MainJsonFragment", "사용자 이메일이 없습니다.")
            return
        }

        db.collection("saveInfo")
            .whereEqualTo("email", currentUserEmail)
            .get()
            .addOnSuccessListener { result ->
                val dataList = mutableListOf<ItemData>()
                for (document in result) {
                    val name = document.getString("name") ?: "이름 없음"
                    val date = document.getString("date") ?: "날짜 없음"

                    val item = ItemData(name, date, R.drawable.etc)
                    dataList.add(item)
                }

                val adapter = ItemAdapter(dataList)
                binding.jsonRecyclerView.layoutManager = LinearLayoutManager(requireContext())
                binding.jsonRecyclerView.adapter = adapter
            }
            .addOnFailureListener { e ->
                Log.w("MainJsonFragment", "데이터 불러오기 실패", e)
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
