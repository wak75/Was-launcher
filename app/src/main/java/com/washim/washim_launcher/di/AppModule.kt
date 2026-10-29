package com.washim.washim_launcher.di

import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providePackageManager(application: Application): PackageManager =
        application.packageManager

    @Provides
    @Singleton
    fun provideApplicationContext(application: Application): Context =
        application.applicationContext
}
