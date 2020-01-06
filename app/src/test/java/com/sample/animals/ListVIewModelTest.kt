package com.sample.animals

import android.app.Application
import android.content.SharedPreferences
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.sample.animals.di.ApiModule
import com.sample.animals.di.AppModule
import com.sample.animals.di.DaggerViewModelComponent
import com.sample.animals.model.AnimalApiService
import com.sample.animals.util.SharedPreferencesHelper
import com.sample.animals.viewmodel.ListViewModel
import io.reactivex.Scheduler
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.internal.schedulers.ExecutorScheduler
import io.reactivex.plugins.RxJavaPlugins
import org.junit.Before
import org.junit.Rule
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import java.util.concurrent.ExecutionException
import java.util.concurrent.Executor

class ListVIewModelTest {
    @get:Rule
    var rule = InstantTaskExecutorRule()

    @Mock
    lateinit var animalService:AnimalApiService

    @Mock
    lateinit var prefe:SharedPreferencesHelper
    val application = Mockito.mock(Application::class.java)
    val lsitViewModel = ListViewModel(application)

    @Before
    fun setup(){
        MockitoAnnotations.initMocks(this)

        val testComponet = DaggerViewModelComponent.builder()
            .appModule(AppModule(application))
            .apiModule(ApiModuelTest(animalService))
            .build()
            .inject(lsitViewModel)
    }

    @Before
    fun setupRxSchedule(){
        val immediate = object: Scheduler(){
            override fun createWorker(): Worker {
                return ExecutorScheduler.ExecutorWorker(Executor { it.run()}, true)
            }
        }

        RxJavaPlugins.setInitNewThreadSchedulerHandler { schedular -> immediate }
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { schedular -> immediate }

    }
}