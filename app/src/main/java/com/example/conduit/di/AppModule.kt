package com.example.conduit.di
import android.content.Context
import androidx.room.Room
import com.example.conduit.BuildConfig
import com.example.conduit.data.local.AppDataBase
import com.example.conduit.data.local.AuthDao
import com.example.conduit.data.remote.ConduitApiService
import com.example.conduit.repository.MainRepository
import com.example.conduit.util.Constants.BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    fun providesBaseUrl() = BASE_URL

    @Singleton
    @Provides
    fun provideOkHttpClient() = if (BuildConfig.DEBUG){
        val loggingInterceptor =HttpLoggingInterceptor()
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }else{
        OkHttpClient
            .Builder()
            .build()
    }

    @Singleton
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient,BASE_URL:String) : Retrofit = Retrofit
        .Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .build()

    @Singleton
    @Provides
    fun provideApiService(retrofit: Retrofit): ConduitApiService = retrofit.create(ConduitApiService::class.java)

    @Singleton
    @Provides
    fun providesRepository(apiService: ConduitApiService,authDao: AuthDao) = MainRepository(apiService,authDao)

    @Singleton
    @Provides
    fun provideAuthDao(appDataBase: AppDataBase) : AuthDao {
        return appDataBase.authDao()
    }

    @Singleton
    @Provides
    fun providesAppDatabase(@ApplicationContext appContext: Context) : AppDataBase {
        return Room.databaseBuilder(
            appContext,
            AppDataBase::class.java,
            "my_database"
        ).build()
    }

}