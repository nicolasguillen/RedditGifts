package com.redditgifts.mobile.di.modules

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.redditgifts.mobile.BuildConfig
import com.redditgifts.mobile.services.ApiClient
import com.redditgifts.mobile.services.ApiRepository
import com.redditgifts.mobile.services.ApiService
import com.redditgifts.mobile.storage.CookieRepository
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

@Module
open class NetworkModule {

    @Provides
    internal open fun provideApiClient(apiService: ApiService,
                                       cookieRepository: CookieRepository,
                                       gson: Gson): ApiRepository {
        return ApiClient(apiService, cookieRepository, gson)
    }

    @Provides
    internal fun provideOkHttpClient(httpLoggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
        val builder = OkHttpClient.Builder()

        // Only log in debug mode to avoid leaking sensitive information.
        if (BuildConfig.DEBUG) {
            builder.addInterceptor(httpLoggingInterceptor)
        }
        builder.connectTimeout(10, TimeUnit.SECONDS)
        builder.followRedirects(false)
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
    internal fun provideGson(): Gson {
        return GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            .create()
    }

    @Provides
    internal fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        return interceptor
    }

}