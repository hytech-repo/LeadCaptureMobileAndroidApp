package com.eva.lead.capture.ui.fragments.appointment

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.eva.lead.capture.R
import com.eva.lead.capture.databinding.ItemAppointmentBinding
import com.eva.lead.capture.domain.model.entity.Appointment
import com.eva.lead.capture.utils.changeDrawableBgAndStroke
import com.eva.lead.capture.utils.convertIntoDate

class EvaAppointmentAdapter(val mContext: Context) :
    RecyclerView.Adapter<EvaAppointmentAdapter.EvaAppointmentVH>() {

    private var appointmentList: List<Appointment>? = null

    var onItemClickListener: (model: Appointment, id: Int, position: Int) -> Unit = { model, id, position -> }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): EvaAppointmentVH {
        val binding = ItemAppointmentBinding.inflate(LayoutInflater.from(mContext), parent, false)
        return EvaAppointmentVH(binding)
    }

    override fun onBindViewHolder(
        holder: EvaAppointmentVH,
        position: Int
    ) {
        val model = appointmentList!![position]
        holder.bind(model, position)
    }

    override fun getItemCount(): Int = appointmentList?.size ?: 0

    fun setAppointmentList(list: List<Appointment>) {
        this.appointmentList = list
        notifyDataSetChanged()
    }

    inner class EvaAppointmentVH(val binding: ItemAppointmentBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(model: Appointment, position: Int) {
            binding.userName.text = model.userName
            binding.countdownTimer.background = mContext.changeDrawableBgAndStroke(
                R.drawable.bg_rounded_status,
                R.color.color_lime_green,
                2,
                15
            )
            val usernames = model.userName?.split(" ")?: emptyList()
            if (usernames.isNotEmpty()) {
                val firstletter = if (usernames.size >= 2) {
                    "${usernames[0].first()}${usernames[1].first()}"
                } else {
                    "${usernames[0].first()}"
                }
                binding.avatarText.text = firstletter
            }
            binding.locationOrMeeting.visibility =
                if (model.location.isNullOrEmpty()) View.GONE else View.VISIBLE
            binding.locationOrMeeting.text = model.location
            val date = model.timestamp?.convertIntoDate("dd MM yyyy")
            val time = model.timestamp?.convertIntoDate("hh:mm a")

            model.timestamp?.let { timestamp ->
                val currentTime = System.currentTimeMillis()
                var difference = timestamp - currentTime
                if (difference > 0) {
                    val secondsInMilli: Long = 1000
                    val minutesInMilli = secondsInMilli * 60
                    val hoursInMilli = minutesInMilli * 60
                    val daysInMilli = hoursInMilli * 24
                    val elapsedDays = difference / daysInMilli
                    difference %= daysInMilli

                    val elapsedHours = difference / hoursInMilli
                    difference %= hoursInMilli

                    val elapsedMinutes = difference / minutesInMilli
                    difference %= minutesInMilli

                    val elapsedSeconds = difference / secondsInMilli

                    val countdownText = String.format(
                        "%02dd: %02dh: %02dm: %02ds",
                        elapsedDays,
                        elapsedHours,
                        elapsedMinutes,
                        elapsedSeconds
                    )
                    binding.countdownTimer.text = countdownText
                }

            }

            binding.appointmentDate.text = date
            binding.appointmentTime.text = time
            binding.organizationName.text = model.companyName
        }

    }
}