package com.sample.animals.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.sample.animals.di.AppModule
import com.sample.animals.di.CONTEXT_APP
import com.sample.animals.di.DaggerViewModelComponent
import com.sample.animals.di.TypeOfContext
import com.sample.animals.model.Animal
import com.sample.animals.model.AnimalApiService
import com.sample.animals.model.ApiKey
import com.sample.animals.util.SharedPreferencesHelper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class ListViewModel (application: Application): AndroidViewModel(application) {
    constructor(application: Application, test:Boolean = true):this(application){
        inject = true
    }

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

    @Inject
    lateinit var apiService:AnimalApiService

    @Inject
    @field:TypeOfContext(CONTEXT_APP)
    lateinit var prefs:SharedPreferencesHelper

    private var invalidApiKey = false
    private var inject = false

    fun inject(){
        if (!inject){
            DaggerViewModelComponent.builder()
                .appModule(AppModule(getApplication()))
                .build()
                .inject(this)
        }
    }

    init {
        DaggerViewModelComponent.builder()
            .appModule(AppModule(getApplication()))
            .build()
            .inject(this)
    }



    fun refresh(){
        inject()
        loading.value = true
        invalidApiKey = false
        val key = prefs.getApiKey()
        if (key.isNullOrEmpty()){
            getKey()
        }else{
            getAnimal(key)
        }
    }

    fun hardRefresh(){
        inject()
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
                            prefs.saveApiKey(t.key)
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
                        if (!invalidApiKey){
                            invalidApiKey = true
                            getKey()
                        }else{
                            e.printStackTrace()
                            loading.value = false
                            animal.value = null
                            loadError.value = true
                        }
                    }
                })
        )
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}