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

package ru.olegivo.afs.favorites.domain

import io.reactivex.Single
import ru.olegivo.afs.extensions.mapList
import ru.olegivo.afs.favorites.domain.models.FavoriteFilter
import ru.olegivo.afs.favorites.presentation.models.FavoritesItem
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class GetFavoritesUseCaseImpl @Inject constructor(
    private val favoritesRepository: FavoritesRepository,
    private val locale: Locale
) : GetFavoritesUseCase {

    private val hoursMinutesFormat: SimpleDateFormat by lazy {
        SimpleDateFormat(FORMAT, locale)
    }

    override operator fun invoke(): Single<List<FavoritesItem>> =
        favoritesRepository.getFavoriteFilters()
            .mapList {
                it.toFavoriteItem()
            }

    private fun FavoriteFilter.toFavoriteItem() =
        FavoritesItem(
            group = group,
            activity = activity,
            duty = hoursMinutesFormat.format(Date(timeOfDay))
        )

    companion object {
        private const val FORMAT = "HH:mm"
    }
}
