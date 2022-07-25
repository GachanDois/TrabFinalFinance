package com.example.trabfinalfinance.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.trabfinalfinance.R
import com.example.trabfinalfinance.model.Conta

class  DespesaAdapter(val conta:List<Conta>): RecyclerView.Adapter<DespesaAdapter.ContaViewHolder>()  {

    private var listener: ContaItemListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContaViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_desp, parent, false)
        return ContaViewHolder(itemView, listener)
    }

    override fun onBindViewHolder(holder: ContaViewHolder, position: Int) {
        holder.name.text = conta[position].name
        holder.description.text = conta[position].valor

    }

    override fun getItemCount(): Int {
        return conta.size
    }

    fun setContaItemListener(listener: ContaItemListener) {
        this.listener = listener
    }

    class ContaViewHolder(itemView: View, listener: ContaItemListener?): RecyclerView.ViewHolder(itemView) {

        val name: TextView = itemView.findViewById(R.id.item_despesa_textview_name)
        val description: TextView = itemView.findViewById(R.id.item_despesa_textview_valor)

        init {
            itemView.setOnClickListener {
                listener?.onContaItemClick(it, adapterPosition)
            }

            itemView.setOnLongClickListener {
                listener?.onContaItemLongClick(it, adapterPosition)
                true
            }
        }

    }















}