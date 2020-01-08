package com.sample.animals

import com.sample.animals.di.ApiModule
import com.sample.animals.model.AnimalApiService

class ApiModuleTest(val mockService: AnimalApiService) : ApiModule() {
    override fun provideAnimalApiService(): AnimalApiService {
        return mockService
    }
}