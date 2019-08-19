package ru.olegivo.afs.clubs.android

import android.content.Context
import androidx.appcompat.app.AlertDialog
import ru.olegivo.afs.clubs.domain.models.Club

class ChooseClubDialog {
    companion object {
        fun chooseClub(
            clubs: List<Club>,
            selectedClub: Club?,
            context: Context,
            onClubChosen: (Club) -> Unit
        ) {
            val items = clubs.map { it.title }.toTypedArray()
            var selectedClubIndex = clubs.indexOf(selectedClub)
            // setup the alert builder
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Choose the club")
            // add a radio button list
            builder.setSingleChoiceItems(items, selectedClubIndex) { _, which ->
                // user checked an item
                selectedClubIndex = which
            }
            // add OK and Cancel buttons
            builder.setPositiveButton("OK") { _, _ ->
                // user clicked OK
                if (selectedClubIndex >= 0) onClubChosen(clubs[selectedClubIndex])
            }
            builder.setNegativeButton("Cancel", null)
            // create and show the alert dialog
            val dialog = builder.create()
            dialog.show()
        }
    }
}