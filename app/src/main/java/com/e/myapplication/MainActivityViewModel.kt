package com.e.myapplication

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.e.myapplication.dataclass.Novels
import com.e.myapplication.retrofit.RetrofitClass
import retrofit2.Call
import retrofit2.Response

class MainActivityViewModel : ViewModel() {
    private val _novels : MutableLiveData<MutableList<Novels.Content>> = MutableLiveData()
    val novels : LiveData<MutableList<Novels.Content>> get() = _novels

    fun updateNovels(){
        val gNovels = RetrofitClass.api.getNovels("nvcid,ASC")
        gNovels.enqueue(object :retrofit2.Callback<Novels>{
            override fun onResponse(call: Call<Novels>, response: Response<Novels>) {
                _novels.value?.addAll(response.body()!!.content)
            }

            override fun onFailure(call: Call<Novels>, t: Throwable) {
                t.printStackTrace()
            }

        })
    }


}