package com.indie.stockapp.core.resources

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class ResourceModule {

    @Binds
    abstract fun bindResourceProvider(
        impl: ResourceProviderImpl
    ): ResourceProvider
}