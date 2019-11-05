package ru.olegivo.afs.favorites.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import ru.olegivo.afs.common.db.AfsDatabase
import ru.olegivo.afs.favorites.data.FavoritesDbSource
import ru.olegivo.afs.favorites.data.FavoritesRepositoryImpl
import ru.olegivo.afs.favorites.db.FavoritesDbSourceImpl
import ru.olegivo.afs.favorites.domain.AddToFavoritesUseCase
import ru.olegivo.afs.favorites.domain.AddToFavoritesUseCaseImpl
import ru.olegivo.afs.favorites.domain.FavoritesRepository

@Module(includes = [FavoritesModule.ProvidesModule::class])
abstract class FavoritesModule {
    //    @Binds
//    abstract fun bindReservePresenter(impl: ScheduleDetailsPresenter): ScheduleDetailsContract.Presenter
//
    @Binds
    abstract fun bindAddToFavoritesUseCase(impl: AddToFavoritesUseCaseImpl): AddToFavoritesUseCase

    //
//    @Binds
//    abstract fun bindSavedReserveContactsUseCase(impl: SavedReserveContactsUseCaseImpl): SavedReserveContactsUseCase
//
    @Binds
    abstract fun bindFavoritesRepository(impl: FavoritesRepositoryImpl): FavoritesRepository

    @Binds
    abstract fun bindFavoritesDbSource(impl: FavoritesDbSourceImpl): FavoritesDbSource

    @Module
    object ProvidesModule {
        @Provides
        fun provideFavoriteDao(afsDatabase: AfsDatabase) = afsDatabase.favorites
    }
}