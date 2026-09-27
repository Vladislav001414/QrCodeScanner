package com.example.qrcodescanner.RVAdapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.qrcodescanner.DataBase.Tables.QrCodeItemTable
import com.example.qrcodescanner.SealedInterface.QrItemCreation
import com.example.qrcodescanner.UIExtensions.getIcon
import com.example.qrcodescanner.UIExtensions.getText
import com.example.qrcodescanner.databinding.ItemQrTypeBinding

class QrCreationAdapter(
    private val onItemClick: (QrItemCreation) -> Unit
) : ListAdapter<QrItemCreation, QrCreationAdapter.ViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemQrTypeBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(
        private val binding: ItemQrTypeBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: QrItemCreation) {

            binding.ivIcon.setImageResource(item.getIcon())
            binding.tvTitle.setText(item.getText())

            binding.root.setOnClickListener {
                onItemClick(item)
            }
        }
    }

    private object DiffCallback : DiffUtil.ItemCallback<QrItemCreation>() {
        override fun areItemsTheSame(oldItem: QrItemCreation, newItem: QrItemCreation): Boolean =
            oldItem == newItem

        override fun areContentsTheSame(oldItem: QrItemCreation, newItem: QrItemCreation): Boolean =
            oldItem == newItem
        }

}