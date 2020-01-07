package com.sample.animals

import android.app.Application
import android.content.SharedPreferences
import com.sample.animals.di.PrefsModule
import com.sample.animals.util.SharedPreferencesHelper

class PrefsModuleTest(val mockPrefs:SharedPreferencesHelper):PrefsModule() {
    override fun provideSharedPreference(app: Application): SharedPreferencesHelper {
        return mockPrefs
    }
}