package es.kix2902.cd2spotify.ui.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import es.kix2902.cd2spotify.data.models.Release
import es.kix2902.cd2spotify.databinding.RowHistoryBinding
import es.kix2902.cd2spotify.helpers.listen

class HistoryAdapter : RecyclerView.Adapter<HistoryAdapter.Holder>() {

    private var items = listOf<Release>()

    private var listener: OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = RowHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding).listen { position, _ ->
            listener?.onClick(items[position])
        }
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val release = items[position]

        holder.binding.album.text = release.title
        holder.binding.artist.text = release.author
        holder.binding.barcode.text = release.barcode
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun setItems(newItems: List<Release>) {
        items = newItems
        notifyDataSetChanged()
    }

    fun setOnClickListener(newListener: OnClickListener) {
        listener = newListener
    }

    class Holder(val binding: RowHistoryBinding) : RecyclerView.ViewHolder(binding.root)

    interface OnClickListener {
        fun onClick(item: Release)
    }
}