package com.konstantinos.myworld

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.konstantinos.myworld.data.HexEntity

class HexAdapter : RecyclerView.Adapter<HexAdapter.HexViewHolder>() {
    private var hexList = listOf<HexEntity>()

    class HexViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val hexText = view.findViewById<TextView>(R.id.hexText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HexViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.hex_item, parent, false)
        return HexViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    @OptIn(ExperimentalStdlibApi::class)
    override fun onBindViewHolder(holder: HexViewHolder, position: Int) {
        val hex = hexList[position]
        holder.hexText.text = "0x" + hex.hex.toHexString()
    }

    override fun getItemCount() = hexList.size

    fun submitList(newList: List<HexEntity>) {
        hexList = newList
        notifyDataSetChanged()
    }
}