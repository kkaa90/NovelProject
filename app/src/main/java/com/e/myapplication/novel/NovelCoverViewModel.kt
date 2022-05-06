package com.e.myapplication.novel

import androidx.lifecycle.ViewModel
import com.e.myapplication.dataclass.Novels
import com.e.myapplication.retrofit.RetrofitClass
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import retrofit2.Call
import retrofit2.Response

class NovelCoverViewModel : ViewModel(){
    private val _n = MutableStateFlow(emptyList<Novels.Content>())
    val n : StateFlow<List<Novels.Content>> get() = _n

    private val _t = MutableStateFlow(emptyList<List<String>>())
    val t : StateFlow<List<List<String>>> get() = _t




    fun updateNovels(){
        val gNovels = RetrofitClass.api.getNovels("nvcId,ASC")
        gNovels.enqueue(object :retrofit2.Callback<Novels>{
            override fun onResponse(call: Call<Novels>, response: Response<Novels>) {
                _n.value = response.body()!!.content
                _t.value = response.body()!!.tags
            }

            override fun onFailure(call: Call<Novels>, t: Throwable) {
                t.printStackTrace()
            }

        })
    }
}