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

package ru.olegivo.afs.schedules.android

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import ru.olegivo.afs.R
import ru.olegivo.afs.common.android.BaseAdapter
import ru.olegivo.afs.databinding.RowScheduleItemBinding
import ru.olegivo.afs.schedules.domain.models.SportsActivity
import java.text.SimpleDateFormat
import java.util.Locale

class SportsActivitiesAdapter(context: Context, private val onItemClick: (SportsActivity) -> Unit) :
    BaseAdapter<SportsActivity, SportsActivitiesAdapter.SportsActivityViewHolder>(context) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SportsActivityViewHolder =
        SportsActivityViewHolder(
            RowScheduleItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: SportsActivity) {
        (holder as SportsActivityViewHolder).setData(item, onItemClick)
    }

    class SportsActivityViewHolder(private val binding: RowScheduleItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun setData(item: SportsActivity, onItemClick: (SportsActivity) -> Unit) {
            val res =
                if (item.isReserved) R.drawable.ic_check_box_black_24dp else R.drawable.ic_check_box_outline_blank_black_24dp
            binding.imageViewIsReserved.setImageResource(res)

            val schedule = item.schedule

            binding.textViewGroup.text = schedule.group
            binding.textViewActivity.text = schedule.activity
            binding.textViewDuty.text = hoursMinutesFormat.format(schedule.datetime)
            if (schedule.totalSlots != null && item.availableSlots != null) {
                binding.textViewSlots.isVisible = true
                binding.textViewSlots.text =
                    binding.root.context.getString(
                        R.string.slots_count,
                        item.availableSlots,
                        schedule.totalSlots
                    )
            } else {
                binding.textViewSlots.isVisible = false
            }

            listOf(
                binding.textViewSlots,
                binding.textViewGroup,
                binding.textViewActivity,
                binding.textViewDuty
            ).forEach {
                it.typeface = Typeface.defaultFromStyle(if (item.isFavorite) Typeface.BOLD else Typeface.NORMAL)
            }

            binding.root.setOnClickListener { onItemClick(item) }
        }
    }

    @SuppressLint("ConstantLocale")
    companion object {
        private const val FORMAT = "HH:mm"

        private val hoursMinutesFormat: SimpleDateFormat by lazy {
            SimpleDateFormat(FORMAT, Locale.getDefault())
        }
    }
}
