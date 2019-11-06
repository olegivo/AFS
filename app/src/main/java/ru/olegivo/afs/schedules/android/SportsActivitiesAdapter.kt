package ru.olegivo.afs.schedules.android

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.row_schedule_item.view.imageViewIsReserved
import kotlinx.android.synthetic.main.row_schedule_item.view.textViewActivity
import kotlinx.android.synthetic.main.row_schedule_item.view.textViewDuty
import kotlinx.android.synthetic.main.row_schedule_item.view.textViewGroup
import kotlinx.android.synthetic.main.row_schedule_item.view.textViewSlots
import ru.olegivo.afs.R
import ru.olegivo.afs.common.android.BaseAdapter
import ru.olegivo.afs.schedules.domain.models.SportsActivity
import java.text.SimpleDateFormat
import java.util.*

class SportsActivitiesAdapter(context: Context, private val onItemClick: (SportsActivity) -> Unit) :
    BaseAdapter<SportsActivity, SportsActivitiesAdapter.SportsActivityViewHolder>(context) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SportsActivityViewHolder =
        SportsActivityViewHolder(inflater, parent)

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: SportsActivity) {
        val res =
            if (item.isReserved) R.drawable.ic_check_box_black_24dp else R.drawable.ic_check_box_outline_blank_black_24dp
        holder.itemView.imageViewIsReserved.setImageResource(res)

        holder.itemView.textViewGroup.text = item.schedule.group
        holder.itemView.textViewActivity.text = item.schedule.activity
        holder.itemView.textViewDuty.text = hoursMinutesFormat.format(item.schedule.datetime)
        item.schedule.totalSlots?.let {
            holder.itemView.textViewSlots.text =
                context.getString(R.string.slots_count, item.availableSlots, item.schedule.totalSlots)
        } ?: run {
            holder.itemView.textViewSlots.visibility = View.GONE
        }
        holder.itemView.setOnClickListener { onItemClick(item) }
    }

    class SportsActivityViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
        RecyclerView.ViewHolder(inflater.inflate(R.layout.row_schedule_item, parent, false))

    @SuppressLint("ConstantLocale")
    companion object {
        private const val FORMAT = "HH:mm"

        private val hoursMinutesFormat: SimpleDateFormat by lazy {
            SimpleDateFormat(FORMAT, Locale.getDefault())
        }

    }
}
