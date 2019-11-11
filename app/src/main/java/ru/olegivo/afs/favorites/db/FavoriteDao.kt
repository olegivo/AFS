package ru.olegivo.afs.favorites.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import io.reactivex.Completable
import io.reactivex.Single
import ru.olegivo.afs.favorites.db.modes.FavoriteFilterEntity

@Dao
interface FavoriteDao {
    @Insert
    fun addFilter(favoriteFilterEntity: FavoriteFilterEntity): Completable

    @Query("select id, [group], activity, dayOfWeek, timeOfDay from favoriteFilters")
    fun getFavoriteFilters(): Single<List<FavoriteFilterEntity>>

    @Query("delete from favoriteFilters where [group] = :group and activity = :activity and dayOfWeek = :dayOfWeek and timeOfDay = :timeOfDay")
    fun removeFilter(group: String, activity: String, dayOfWeek: Int, timeOfDay: Long): Completable
}
