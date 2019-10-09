package ru.olegivo.afs.schedule.android

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.row_schedule_item.view.textViewActivity
import kotlinx.android.synthetic.main.row_schedule_item.view.textViewDuty
import kotlinx.android.synthetic.main.row_schedule_item.view.textViewGroup
import ru.olegivo.afs.R
import ru.olegivo.afs.common.android.BaseAdapter
import ru.olegivo.afs.schedule.domain.models.Schedule
import java.text.SimpleDateFormat
import java.util.*

class SchedulesAdapter(context: Context, private val onItemClick: (Schedule) -> Unit) :
    BaseAdapter<Schedule, SchedulesAdapter.ScheduleViewHolder>(context) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleViewHolder =
        ScheduleViewHolder(inflater, parent)

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Schedule) {
        holder.itemView.textViewGroup.text = item.group
        holder.itemView.textViewActivity.text = item.activity
        holder.itemView.textViewDuty.text = hoursMinutesFormat.format(item.datetime)
        holder.itemView.setOnClickListener { onItemClick(item) }
    }

    class ScheduleViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
        RecyclerView.ViewHolder(inflater.inflate(R.layout.row_schedule_item, parent, false))

    @SuppressLint("ConstantLocale")
    companion object {
        private const val FORMAT = "HH:mm"

        private val hoursMinutesFormat: SimpleDateFormat by lazy {
            SimpleDateFormat(FORMAT, Locale.getDefault())
        }

    }
}
