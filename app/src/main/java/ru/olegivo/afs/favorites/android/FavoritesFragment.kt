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

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.squareup.cycler.Recycler
import com.squareup.cycler.toDataSource
import ru.olegivo.afs.R
import ru.olegivo.afs.analytics.domain.ScreenNameProvider
import ru.olegivo.afs.common.android.doOnApplyWindowInsets
import ru.olegivo.afs.databinding.FragmentFavoritesBinding
import ru.olegivo.afs.databinding.RowFavoritesItemBinding
import ru.olegivo.afs.favorites.analytics.FavoritesAnalytics
import ru.olegivo.afs.favorites.presentation.FavoritesContract
import ru.olegivo.afs.favorites.presentation.models.FavoritesItem
import javax.inject.Inject

class FavoritesFragment @Inject constructor(private val presenter: FavoritesContract.Presenter) :
    Fragment(R.layout.fragment_favorites),
    ScreenNameProvider by FavoritesAnalytics.Screens.Favorites,
    FavoritesContract.View {

    private val viewBinding: FragmentFavoritesBinding by viewBinding(FragmentFavoritesBinding::bind)
    private lateinit var cycler: Recycler<FavoritesItem>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewBinding.toolbarLayout.toolbar.title = "Favorites"

        initRecyclerView()

        viewBinding.toolbarLayout.appbarLayout.doOnApplyWindowInsets { view, insets, padding ->
            view.updatePadding(
                top = padding.top + insets.systemWindowInsetTop
            )
            insets
        }
        viewBinding.favoritesRecyclerView.doOnApplyWindowInsets { view, insets, padding ->
            view.updatePadding(
                bottom = padding.bottom + insets.systemWindowInsetBottom
            )
            insets
        }
    }

    override fun onStart() {
        super.onStart()
        presenter.bindView(this)
    }

    override fun onStop() {
        presenter.unbindView()
        super.onStop()
    }

    override fun showFavorites(favorites: List<FavoritesItem>) {
        cycler.data = favorites.toDataSource()
    }

    override fun showErrorMessage(message: String) {
        Toast.makeText(requireContext(), "Error \n$message", Toast.LENGTH_LONG).show()
    }

    override fun showProgress() {
        viewBinding.progressBar.isVisible = true
    }

    override fun hideProgress() {
        viewBinding.progressBar.isVisible = false
    }

    private fun initRecyclerView() {
        viewBinding.favoritesRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        cycler = Recycler.adopt(viewBinding.favoritesRecyclerView) {
            row<FavoritesItem, View> {
                create(R.layout.row_favorites_item) {
                    val itemBinding = RowFavoritesItemBinding.bind(view)
                    bind { item ->
                        itemBinding.root.setOnClickListener { presenter.onItemClick(item) }
                        itemBinding.textViewGroup.text = item.filter.group
                        itemBinding.textViewActivity.text = item.filter.activity
                        itemBinding.textViewDuty.text = item.duty
                    }
                }
            }
        }
    }
}
