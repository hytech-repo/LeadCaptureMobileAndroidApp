package com.eva.lead.capture.ui.fragments.recordinglist

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.eva.lead.capture.databinding.ItemRecordingBinding
import com.eva.lead.capture.domain.model.entity.LeadAudioRecording
import com.eva.lead.capture.utils.formatDuration
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RecordingListAdapter(val mContext: Context) :
    RecyclerView.Adapter<RecordingListAdapter.EvaRecordingListVH>() {
    private var recordingList: List<LeadAudioRecording>? = null
    var onItemClick: (LeadAudioRecording, Int) -> Unit = { data: LeadAudioRecording, position: Int -> }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): EvaRecordingListVH {
        val binding = ItemRecordingBinding.inflate(LayoutInflater.from(mContext), parent, false)
        return EvaRecordingListVH(binding)
    }

    override fun onBindViewHolder(
        holder: EvaRecordingListVH,
        position: Int,
    ) {
        holder.bind(recordingList!![position], position)
    }

    override fun getItemCount(): Int {
        return recordingList?.size?: 0
    }

    fun setRecordingList(list: List<LeadAudioRecording>?) {
        this.recordingList = list
        notifyDataSetChanged()
    }

    inner class EvaRecordingListVH(val binding: ItemRecordingBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(model: LeadAudioRecording, position: Int) {
            binding.nameText.text = model.recordingName
            binding.dateText.text = SimpleDateFormat("dd MM yyyy", Locale.getDefault()).format(Date(model.recordingDate!!))
            binding.tvDuration.text  = ((model.duration ?: 0) * 1000).formatDuration()

            binding.root.setOnClickListener {
                onItemClick(model, position)
            }
        }

    }

}