package com.sample.animals.di

import com.sample.animals.model.AnimalApiService
import dagger.Component

@Component(modules = [ApiModule::class])
interface ApiComponent {
    fun inject(service: AnimalApiService)
}