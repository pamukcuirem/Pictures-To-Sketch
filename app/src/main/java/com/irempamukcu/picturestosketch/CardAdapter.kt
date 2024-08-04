package com.irempamukcu.picturestosketch

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.irempamukcu.picturestosketch.databinding.CardCellBinding

class CardAdapter(private val pics : List<Pic>, private val clickListener: PicClickListener) : RecyclerView.Adapter<CardAdapter.CardViewHolder>() {
    class CardViewHolder(
        private val cardCellBinding: CardCellBinding,
        private val clickListener: PicClickListener

    ) : RecyclerView.ViewHolder(cardCellBinding.root){
        fun bindPic(pic : Pic){
            cardCellBinding.cover.setImageBitmap(pic.picture)
            cardCellBinding.title.text = pic.title
            cardCellBinding.cardView.setOnClickListener {
                clickListener.onClick(pic)
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val from = LayoutInflater.from(parent.context)
        val binding = CardCellBinding.inflate(from, parent, false)
        return CardViewHolder(binding,clickListener)
    }

    override fun getItemCount(): Int {
        return pics.size
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        holder.bindPic(pics[position])
    }


}