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

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.olegivo.afs.common.android.BaseAdapter
import ru.olegivo.afs.databinding.RowFavoritesItemBinding
import ru.olegivo.afs.favorites.presentation.models.FavoritesItem

class FavoritesAdapter(context: Context, private val onItemClick: (FavoritesItem) -> Unit) :
    BaseAdapter<FavoritesItem, FavoritesAdapter.FavoriteViewHolder>(context) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder =
        FavoriteViewHolder(
            RowFavoritesItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: FavoritesItem) =
        (holder as FavoriteViewHolder).setData(item)

    class FavoriteViewHolder(private val binding: RowFavoritesItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun setData(item: FavoritesItem) {
            binding.textViewGroup.text = item.filter.group
            binding.textViewActivity.text = item.filter.activity
            binding.textViewDuty.text = item.duty
        }
    }
}
