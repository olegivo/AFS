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

package ru.olegivo.afs.favorites.android

import android.view.View
import com.kaspersky.kaspresso.screens.KScreen
import io.github.kakaocup.kakao.recycler.KRecyclerItem
import io.github.kakaocup.kakao.recycler.KRecyclerView
import io.github.kakaocup.kakao.text.KTextView
import io.github.kakaocup.kakao.toolbar.KToolbar
import org.hamcrest.Matcher
import ru.olegivo.afs.R
import ru.olegivo.afs.favorites.presentation.models.FavoritesItem
import ru.olegivo.afs.settings.android.SettingsFragment

object FavoritesFragmentScreen : KScreen<FavoritesFragmentScreen>() {
    private val toolbar = KToolbar {
        withId(R.id.toolbar)
    }

    val recycler: KRecyclerView = KRecyclerView(
        builder = {
            withId(R.id.favoritesRecyclerView)
        },
        itemTypeBuilder = {
            itemType(::Item)
        }
    )

    override val layoutId: Int = R.layout.fragment_favorites
    override val viewClass = SettingsFragment::class.java

    fun assertScreenShown() {
        toolbar {
            hasTitle("Favorites")
        }
    }

    fun assertItemsCount(size: Int) {
        recycler {
            hasSize(size)
        }
    }

    fun assertItemsShown(items: List<FavoritesItem>) {
        recycler {
            items.forEachIndexed { i, item ->
                scrollTo(i)
                childAt<Item>(i) {
                    textViewGroup.hasText(item.filter.group)
                    textViewActivity.hasText(item.filter.activity)
                    textViewDuty.hasText(item.duty)
                }
            }
        }
    }

    fun clickOnItem(favoritesItem: FavoritesItem) {
        recycler {
            val position = getPosition {
                withDescendant {
                    withId(R.id.textViewGroup)
                    withText(favoritesItem.filter.group)
                }
            }
            scrollTo(position)
            childAt<Item>(position) {
                textViewGroup {
                    click()
                }
            }
        }
    }

    class Item(parent: Matcher<View>) : KRecyclerItem<Item>(parent) {
        val textViewGroup = KTextView(parent) { withId(R.id.textViewGroup) }
        val textViewActivity = KTextView(parent) { withId(R.id.textViewActivity) }
        val textViewDuty = KTextView(parent) { withId(R.id.textViewDuty) }
    }
}
