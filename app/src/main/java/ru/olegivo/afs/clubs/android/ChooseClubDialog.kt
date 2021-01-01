/*
 * Copyright (C) 2021 Oleg Ivashchenko <olegivo@gmail.com>
 *
 * This file is part of AFS.
 *
 * AFS is free software: you can redistribute it and/or modify
 * it under the terms of the MIT License.
 *
 * AFS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * AFS.
 */

package ru.olegivo.afs.clubs.android

import android.content.Context
import androidx.appcompat.app.AlertDialog
import ru.olegivo.afs.clubs.domain.models.Club

object ChooseClubDialog {
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
