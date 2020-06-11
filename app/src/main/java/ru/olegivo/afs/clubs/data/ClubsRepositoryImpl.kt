/*
 * Copyright (C) 2020 Oleg Ivashchenko <olegivo@gmail.com>
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

package ru.olegivo.afs.clubs.data

import ru.olegivo.afs.clubs.domain.ClubsRepository
import ru.olegivo.afs.preferences.data.PreferencesDataSource
import javax.inject.Inject

class ClubsRepositoryImpl @Inject constructor(
    private val clubsNetworkSource: ClubsNetworkSource,
    private val preferencesDataSource: PreferencesDataSource
) : ClubsRepository {
    override fun getClubs() = clubsNetworkSource.getClubs()

    override fun setCurrentClubId(clubId: Int) =
        preferencesDataSource.putInt(CURRENT_CLUB_ID, clubId)

    override fun getCurrentClubId() = preferencesDataSource.getInt(CURRENT_CLUB_ID)

    companion object {
        private const val CURRENT_CLUB_ID = "CLUBS_CURRENT_CLUB_ID"
    }
}
