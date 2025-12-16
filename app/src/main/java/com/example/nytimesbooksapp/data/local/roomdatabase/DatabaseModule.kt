package com.example.nytimesbooksapp.data.local.roomdatabase

import android.content.Context
import androidx.room.Room
import com.example.nytimesbooksapp.data.controlpanel.AppConfig
import com.example.nytimesbooksapp.data.local.roomdatabase.dao.BookDao
import com.example.nytimesbooksapp.data.local.roomdatabase.booksdb.BooksDb
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule{
    @Provides
    @Singleton
    fun provideBooksDatabase(@ApplicationContext context : Context): BooksDb{
        return Room.databaseBuilder(

            context,
            BooksDb::class.java,
            AppConfig.DATABASE_NAME // here I'm Using my Constant which is defined separately in AppConfig class as Database name
        ).build()
    }


    @Provides
    @Singleton
    fun providesBooksDatabase(database: BooksDb) : BookDao = database.booksDao()


}

























