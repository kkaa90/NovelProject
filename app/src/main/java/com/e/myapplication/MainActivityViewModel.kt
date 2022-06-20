package com.e.myapplication

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.e.myapplication.dataclass.Novels
import com.e.myapplication.retrofit.RetrofitClass
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Response

class MainActivityViewModel : ViewModel() {
    private val _n = MutableStateFlow(emptyList<Novels.Content>())
    val n : StateFlow<List<Novels.Content>> get() = _n

    private val _t = MutableStateFlow(emptyList<List<String>>())
    val t : StateFlow<List<List<String>>> get() = _t

    fun updateNovels(){
        val gNovels = RetrofitClass.api.getNovels("nvcHit,DESC",1)
        gNovels.enqueue(object :retrofit2.Callback<Novels>{
            override fun onResponse(call: Call<Novels>, response: Response<Novels>) {
                println(response.body()!!.content.size)
                _n.value = response.body()!!.content
                _t.value = response.body()!!.tags

            }

            override fun onFailure(call: Call<Novels>, t: Throwable) {
                t.printStackTrace()
            }

        })
    }
}