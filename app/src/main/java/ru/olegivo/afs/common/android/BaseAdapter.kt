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

package ru.olegivo.afs.common.android

import android.content.Context
import android.view.LayoutInflater
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

abstract class BaseAdapter<Item, TViewHolder : RecyclerView.ViewHolder> constructor(
    protected val context: Context,
    items: List<Item> = listOf(),
    private val diffCallback: BaseDiffCallback<Item> = BaseDiffCallback()
) : RecyclerView.Adapter<TViewHolder>() {

    protected val inflater: LayoutInflater = LayoutInflater.from(context)
    private var innerItems = items.toMutableList()

    override fun getItemCount(): Int {
        return innerItems.size
    }

    override fun onBindViewHolder(holder: TViewHolder, position: Int) {
        onBindViewHolder(holder, items[position])
    }

    abstract fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Item)

    var items: MutableList<Item>
        get() = innerItems
        set(items) {
            diffCallback.setItems(this.innerItems, items)
            val diffResult = DiffUtil.calculateDiff(diffCallback)

            this.innerItems.clear()
            this.innerItems.addAll(items)
            diffResult.dispatchUpdatesTo(this)
        }

    fun deleteItem(item: Item) {
        val index = items.indexOf(item)
        items.remove(item)
        notifyItemRemoved(index)
    }
}

class BaseDiffCallback<T> : DiffUtil.Callback() {

    private lateinit var oldItems: List<T>
    private lateinit var newItems: List<T>

    override fun getOldListSize(): Int {
        return oldItems.size
    }

    override fun getNewListSize(): Int {
        return newItems.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return equals(oldItemPosition, newItemPosition)
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return equals(oldItemPosition, newItemPosition)
    }

    fun setItems(oldItems: List<T>, newItems: List<T>) {
        this.oldItems = oldItems
        this.newItems = newItems
    }

    private fun equals(oldItemPosition: Int, newItemPosition: Int): Boolean {
        if (oldItemPosition > oldItems.size) return false
        if (newItemPosition > newItems.size) return false
        val oldItem = oldItems[oldItemPosition]
        val newItem = newItems[newItemPosition]
        return oldItem == newItem
    }
}
