package com.yurcha.data.di

import android.content.Context
import androidx.room.Room
import com.squareup.moshi.Moshi
import com.yurcha.data.network.MviApi
import com.yurcha.data.repository.BitcoinRatesRepositoryImpl
import com.yurcha.data.repository.TransactionsRepositoryImpl
import com.yurcha.data.room.AppDatabase
import com.yurcha.domain.repository.BitcoinRatesRepository
import com.yurcha.domain.repository.TransactionsRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module(includes = [DataModule.Declarations::class])
@InstallIn(SingletonComponent::class)
class DataModule {

    @Module
    @InstallIn(SingletonComponent::class)
    abstract class Declarations {
        @Binds
        abstract fun provideBitcoinRatesRepository(repository: BitcoinRatesRepositoryImpl): BitcoinRatesRepository

        @Binds
        abstract fun provideTransactionsRepository(repository: TransactionsRepositoryImpl): TransactionsRepository
    }

    /**
     * Provides an API instance with Hilt dependency injection which users can use to request
     * specific endpoints.
     *
     * @param context The [ApplicationContext] provided by Hilt
     * @param moshi The [Moshi] instance provided by the [NetworkModule]
     * @param client The [OkHttpClient] instance provided by the [NetworkClientModule]
     * @return A [MviApi] instance
     */
    @Provides
    fun provideMviApi(
        @ApplicationContext context: Context,
        moshi: Moshi,
        client: OkHttpClient
    ): MviApi {
        val url = "https://rest.coincap.io/v3/assets/"
        Timber.d("URL: $url")

        val authInterceptor = Interceptor { chain ->
            val newRequest = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer 81cdc483cd5a86316d9621954ff07d8f9d5b5b7de61c0009aecfbdb23818c80c")
                .build()
            chain.proceed(newRequest)
        }

        val clientWithAuth = client.newBuilder()
            .addInterceptor(authInterceptor)
            .apply {
                readTimeout(30L, TimeUnit.SECONDS)
                connectTimeout(30L, TimeUnit.SECONDS)
                writeTimeout(30L, TimeUnit.SECONDS)
            }
            .build()

        return Retrofit.Builder()
            .baseUrl(url)
            .client(clientWithAuth)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(MviApi::class.java)
    }

    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext app: Context): AppDatabase = Room
        .databaseBuilder(app, AppDatabase::class.java, "database")
        .fallbackToDestructiveMigration()
        .build()
}