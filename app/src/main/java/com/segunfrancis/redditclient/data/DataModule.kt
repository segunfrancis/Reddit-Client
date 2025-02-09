package com.segunfrancis.redditclient.data

import com.segunfrancis.redditclient.data.local.RedditClientDao
import com.segunfrancis.redditclient.data.local.RedditClientDatabase
import com.segunfrancis.redditclient.data.remote.BASE_URL
import com.segunfrancis.redditclient.data.remote.RetrofitService
import com.segunfrancis.redditclient.data.repo.RedditRepository
import com.segunfrancis.redditclient.data.repo.RedditRepositoryImpl
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

val dataModule = module {

    single<HttpLoggingInterceptor> {
        HttpLoggingInterceptor().also {
            it.level = HttpLoggingInterceptor.Level.BODY
        }
    }

    single { OkHttpClient().newBuilder().addInterceptor(get<HttpLoggingInterceptor>()).build() }

    single {
        Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
        }
    }

    single {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(get())
            .addConverterFactory(get<Json>().asConverterFactory("application/json".toMediaType()))
            .build()
            .create(RetrofitService::class.java)
    }

    single<CoroutineDispatcher> { Dispatchers.IO }

    single<RedditClientDao?> { get<RedditClientDatabase>().getDao() }

    single<RedditRepository> { RedditRepositoryImpl(dispatcher = get(), api = get(), dao = get()) }

    single<RedditClientDatabase?> {
        RedditClientDatabase.getDatabase(androidContext())
    }
}
