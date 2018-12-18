package com.redditgifts.mobile.di.modules

import com.google.gson.Gson
import com.redditgifts.mobile.BuildConfig
import com.redditgifts.mobile.services.ApiClient
import com.redditgifts.mobile.services.ApiRepository
import com.redditgifts.mobile.services.ApiService
import dagger.Module
import dagger.Provides
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
open class NetworkModule {

    @Provides
    internal open fun provideApiClient(apiService: ApiService,
                                       gson: Gson): ApiRepository {
        return ApiClient(apiService, gson)
    }

    @Provides
    internal fun provideOkHttpClient(httpLoggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
        val builder = OkHttpClient.Builder()

        // Only log in debug mode to avoid leaking sensitive information.
        if (BuildConfig.DEBUG) {
            builder.addInterceptor(httpLoggingInterceptor)
        }
        builder.connectTimeout(10, TimeUnit.SECONDS)
        builder.cookieJar(object : CookieJar {
            override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {  }
            override fun loadForRequest(url: HttpUrl): List<Cookie> {
                return emptyList()
            }
        })
        return builder.build()
    }

    @Provides
    internal fun provideApiService(gson: Gson,
                                   okHttpClient: OkHttpClient): ApiService {
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl("https://www.redditgifts.com/api/v1/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    @Provides
    @Singleton
    internal fun provideGson(): Gson {
        return Gson()
    }

    @Provides
    internal fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        return interceptor
    }

}