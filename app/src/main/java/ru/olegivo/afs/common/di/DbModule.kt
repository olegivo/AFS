package ru.olegivo.afs.common.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import dagger.Module
import dagger.Provides
import ru.olegivo.afs.BuildConfig
import ru.olegivo.afs.common.db.AfsDatabase
import javax.inject.Named
import javax.inject.Singleton

@Module
class DbModule {
    @Singleton
    @Provides
    fun providesAfsDatabase(@Named("application") context: Context): AfsDatabase {
        return Room
            .databaseBuilder(context, AfsDatabase::class.java, BuildConfig.DB_NAME)
            .fallbackToDestructiveMigration()
            .setJournalMode(RoomDatabase.JournalMode.WRITE_AHEAD_LOGGING)
            .build()
    }
}

