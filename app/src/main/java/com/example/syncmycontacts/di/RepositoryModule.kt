package com.example.syncmycontacts.di

import android.content.Context
import com.example.syncmycontacts.data.repository.ContactsRepositoryImpl
import com.example.syncmycontacts.domain.ContactsRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule{

    @Provides
    @Singleton
    fun provideContactsRepository(
        @ApplicationContext context: Context
    ): ContactsRepository{
        return ContactsRepositoryImpl(context)
    }

}