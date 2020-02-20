package ru.olegivo.afs.favorites.android

import android.content.Intent
import ru.olegivo.afs.favorites.domain.models.FavoriteRecordReminderParameters

fun Intent.putFavoriteRecordReminderParameters(favoriteRecordReminderParameters: FavoriteRecordReminderParameters) =
    apply {
        putExtra("SCHEDULE_ID", favoriteRecordReminderParameters.scheduleId)
        putExtra("CLUB_ID", favoriteRecordReminderParameters.clubId)
    }

fun Intent.getExtraFavoriteRecordReminderParameters() =
    FavoriteRecordReminderParameters(
        getLongExtra("SCHEDULE_ID", 0),
        getIntExtra("CLUB_ID", 0)
    )
