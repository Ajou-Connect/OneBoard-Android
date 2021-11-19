package kr.khs.oneboard.di

import android.content.Context
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kr.khs.oneboard.BuildConfig
import kr.khs.oneboard.api.ApiService
import kr.khs.oneboard.utils.API_URL
import kr.khs.oneboard.utils.UserInfoUtil
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class ApiModule {
    @Provides
    fun provideApiUrl() = API_URL

    @Provides
    fun provideMoshi(): Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    @Named("withoutJWT")
    @Provides
    fun provideOkHttpClient(@ApplicationContext context: Context): OkHttpClient {
        val builder = OkHttpClient.Builder()

        if (BuildConfig.DEBUG) {
            builder.addInterceptor(HttpLoggingInterceptor().apply {
                setLevel(HttpLoggingInterceptor.Level.BODY)
            })
        }

        return builder.build()
    }

    @Named("withoutJWT")
    @Singleton
    @Provides
    fun provideRetrofit(
        @Named("withoutJWT")
        okHttpClient: OkHttpClient,
        url: String,
        moshi: Moshi
    ): Retrofit = Retrofit.Builder()
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .baseUrl(url)
        .client(okHttpClient)
        .build()

    @Named("withoutJWT")
    @Singleton
    @Provides
    fun provideApiService(@Named("withoutJWT") retrofit: Retrofit): ApiService =
        retrofit.create(ApiService::class.java)


    @Named("withJWT")
    @Provides
    fun provideJWTOkHttpClient(@ApplicationContext context: Context): OkHttpClient {
        val builder = OkHttpClient.Builder().addInterceptor {
            it.proceed(
                it.request().newBuilder()
                    .addHeader("X-AUTH-TOKEN", "${UserInfoUtil.getToken(context)}")
                    .build()
            )
        }

        if (BuildConfig.DEBUG) {
            builder.addInterceptor(HttpLoggingInterceptor().apply {
                setLevel(HttpLoggingInterceptor.Level.BODY)
            })
        }

        return builder.build()
    }

    @Named("withJWT")
    @Singleton
    @Provides
    fun provideJWTRetrofit(
        @Named("withJWT")
        okHttpClient: OkHttpClient,
        url: String,
        moshi: Moshi
    ): Retrofit = Retrofit.Builder()
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .baseUrl(url)
        .client(okHttpClient)
        .build()

    @Named("withJWT")
    @Singleton
    @Provides
    fun provideJWTApiService(@Named("withJWT") retrofit: Retrofit): ApiService =
        retrofit.create(ApiService::class.java)
}