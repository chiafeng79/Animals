package com.sample.animals.di

import android.app.Application
import com.sample.animals.util.SharedPreferencesHelper
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class PrefsModule {

    @Provides
    @Singleton
    fun providerSharedPreference(app:Application):SharedPreferencesHelper{
        return SharedPreferencesHelper(app)
    }
}