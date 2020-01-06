package com.sample.animals

import com.sample.animals.di.ApiModule
import com.sample.animals.model.AnimalApiService

class ApiModuelTest(val mockService:AnimalApiService):ApiModule() {
    override fun providerAnimalApiService(): AnimalApiService {
        return mockService
    }
}