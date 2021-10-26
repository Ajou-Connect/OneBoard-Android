package kr.khs.oneboard.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kr.khs.oneboard.api.ApiService
import kr.khs.oneboard.repository.UserRepository
import kr.khs.oneboard.repository.UserRepositoryImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {
    @Singleton
    @Provides
    fun provideUserRepository(
        apiService: ApiService
    ): UserRepository = UserRepositoryImpl(apiService)
}