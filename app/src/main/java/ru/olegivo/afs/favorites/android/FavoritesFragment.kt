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
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import ru.olegivo.afs.R
import ru.olegivo.afs.databinding.FragmentFavoritesBinding
import ru.olegivo.afs.favorites.presentation.FavoritesContract
import ru.olegivo.afs.favorites.presentation.models.FavoritesItem
import javax.inject.Inject

class FavoritesFragment @Inject constructor(private val presenter: FavoritesContract.Presenter) :
    Fragment(R.layout.fragment_favorites),
    FavoritesContract.View {

    private val viewBinding: FragmentFavoritesBinding by viewBinding(FragmentFavoritesBinding::bind)

    private val favoritesAdapter: FavoritesAdapter by lazy {
        FavoritesAdapter(requireContext(), {})
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding.favoritesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        viewBinding.favoritesRecyclerView.adapter = favoritesAdapter
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
        favoritesAdapter.items = favorites.toMutableList()
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
}
