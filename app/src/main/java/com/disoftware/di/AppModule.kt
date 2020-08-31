package com.disoftware.di

import android.app.Application
import androidx.room.Room
import com.disoftware.api.GithubApi
import com.disoftware.dao.RepoDao
import com.disoftware.dao.UserDao
import com.disoftware.db.GitHubDb
import com.disoftware.utils.LiveDataCallAdapterFactory
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module(includes = [ViewModelModule::class])
class AppModule {
    @Singleton
    @Provides
    fun provideGithubApi(): GithubApi {
        return Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(LiveDataCallAdapterFactory())
            .build()
            .create(GithubApi::class.java)
    }

    @Singleton
    @Provides
    fun provideDb(app: Application): GitHubDb {
        return Room.databaseBuilder(app, GitHubDb::class.java, "github.db")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Singleton
    @Provides
    fun provideUserDao(db: GitHubDb): UserDao {
        return db.userDao()
    }

    @Singleton
    @Provides
    fun provideRepoDao(db: GitHubDb): RepoDao {
        return db.repoDao()
    }
}