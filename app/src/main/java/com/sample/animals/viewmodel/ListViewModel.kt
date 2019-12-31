package com.sample.animals.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.sample.animals.model.Animal
import com.sample.animals.model.AnimalApiService
import com.sample.animals.model.ApiKey
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers

class ListViewModel (application: Application): AndroidViewModel(application) {
    val animal by lazy {
        MutableLiveData<List<Animal>>()
    }
    val loadError by lazy{
        MutableLiveData<Boolean>()
    }
    val loading by lazy {
        MutableLiveData<Boolean>()
    }

    private val disposable = CompositeDisposable()
    private val apiService = AnimalApiService()

    fun refresh(){
        loading.value = true
        getKey()
    }

    private fun getKey(){
        disposable.add(
            apiService.getApiKey()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<ApiKey>(){
                    override fun onSuccess(t: ApiKey) {
                        if (t.key.isNullOrEmpty()){
                            loadError.value = true
                            loading.value = false
                        }else{
                            getAnimal(t.key)
                        }
                    }
                    override fun onError(e: Throwable) {
                        e.printStackTrace()
                        loadError.value = false
                        loading.value = true
                    }
                })
        )
    }

    private fun getAnimal(key:String){
        disposable.add(
            apiService.getAnimal(key)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<List<Animal>>(){
                    override fun onSuccess(t: List<Animal>) {
                        loadError.value = false
                        animal.value = t
                        loading.value = false
                    }

                    override fun onError(e: Throwable) {
                        e.printStackTrace()
                        loading.value = false
                        animal.value = null
                        loadError.value = true
                    }

                })
        )
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}