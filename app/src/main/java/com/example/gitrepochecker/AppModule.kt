package com.example.gitrepochecker

import android.content.Context
import androidx.room.Room
import androidx.work.WorkManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideWorkManager(@ApplicationContext context: Context): WorkManager {
        return WorkManager.getInstance(context)
    }
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "git_tracker_db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideRepoDao(db: AppDatabase): RepoDao = db.repoDao()

    @Provides
    @Singleton
    fun provideGitHelper(@ApplicationContext context: Context): GitHelper = GitHelper(context)
}