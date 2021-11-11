package kr.khs.oneboard.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kr.khs.oneboard.api.ApiService
import kr.khs.oneboard.repository.*
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {

    @Singleton
    @Provides
    fun provideBasicRepository(
        @Named("withoutJWT") apiService: ApiService
    ): BasicRepository = BasicRepositoryImpl(apiService)

    @Singleton
    @Provides
    fun provideUserRepository(
        @Named("withJWT") jwtApiService: ApiService
    ): UserRepository = UserRepositoryImpl(jwtApiService)

    @Singleton
    @Provides
    fun provideLectureRepository(
        @Named("withJWT") jwtApiService: ApiService
    ): LectureRepository = LectureRepositoryImpl(jwtApiService)
}