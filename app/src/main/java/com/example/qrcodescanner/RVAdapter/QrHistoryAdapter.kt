package com.example.qrcodescanner.RVAdapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.qrcodescanner.DataBase.Tables.QrCodeItemTable
import com.example.qrcodescanner.Handler.TextFormatHandler
import com.example.qrcodescanner.R
import com.example.qrcodescanner.SealedInterface.QrItemCreation
import com.example.qrcodescanner.UIExtensions.fromString
import com.example.qrcodescanner.UIExtensions.getIcon
import com.example.qrcodescanner.UIExtensions.getText
import com.example.qrcodescanner.UIExtensions.toFormattedDateString
import com.example.qrcodescanner.databinding.QrHistoryItemBinding

class QrHistoryAdapter(
    private val onItemClick: (QrCodeItemTable) -> Unit,
    private val onFavoriteClick: (QrCodeItemTable) -> Unit
) : ListAdapter<QrCodeItemTable, QrHistoryAdapter.ViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = QrHistoryItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(
        private val binding: QrHistoryItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: QrCodeItemTable) {
            // Применяем ваши Extension-функции

            val qrObj = QrItemCreation.fromString(item.qrType)
            binding.ivTypeIcon.setImageResource(qrObj.getIcon())
            binding.tvType.setText(qrObj.getText())
            binding.tvDate.text = item.dateAdded.toFormattedDateString()
            binding.tvContent.text = TextFormatHandler.formatRawPayload(binding.root.context, item.rawValue)
            binding.btnFavorite.setImageResource(
                if(item.favorite) R.drawable.baseline_star_24
                else R.drawable.outline_star_border_24
            )

            binding.root.setOnClickListener {
                onItemClick(item)
            }
            binding.btnFavorite.setOnClickListener {
                onFavoriteClick(item)
            }
        }
    }

    private companion object {
        val DiffCallback = object : DiffUtil.ItemCallback<QrCodeItemTable>() {

            override fun areItemsTheSame(
                oldItem: QrCodeItemTable,
                newItem: QrCodeItemTable
            ): Boolean = oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: QrCodeItemTable,
                newItem: QrCodeItemTable
            ): Boolean = oldItem == newItem


            override fun getChangePayload(
                oldItem: QrCodeItemTable,
                newItem: QrCodeItemTable
            ): Any? {

                return if (oldItem.favorite != newItem.favorite) {
                    newItem.favorite
                } else {
                    super.getChangePayload(oldItem, newItem)
                }
            }
        }
    }
}
