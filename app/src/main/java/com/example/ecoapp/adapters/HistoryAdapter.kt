package com.example.ecoapp.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.ecoapp.R
import com.example.ecoapp.activities.ProductActivity
import com.example.ecoapp.classes.Product
import kotlinx.android.synthetic.main.row_product_history.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

class HistoryAdapter(private var arrayList: ArrayList<Product>, val context: Context) : RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        @SuppressLint("SimpleDateFormat")
        fun daysDiffToString(date: String): Int {
            val d: Date? = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'").parse(date)
            return (abs(d!!.time - Date().time) / (24 * 60 * 60 * 1000)).toInt()
        }

        @SuppressLint("SetTextI18n")
        fun bindItems(mProduct: Product) {
            Glide.with(itemView)
                .load(mProduct.image)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(itemView.imgComponent)
            when (val days = daysDiffToString(mProduct.date)) {
                0 -> {
                    itemView.dateRow.text = itemView.context.resources.getString(R.string.today)
                }
                1 -> {
                    itemView.dateRow.text = itemView.context.resources.getString(R.string.yesterday)
                }
                else -> {
                    itemView.dateRow.text = "$days ${itemView.context.resources.getString(R.string.days)}"
                }
            }
            itemView.displayName.text = mProduct.name
        }

    }

    fun update(modelList: ArrayList<Product>) {
        arrayList = modelList
        this.notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.row_product_history, parent, false)
        return ViewHolder(v)
    }


    override fun getItemCount(): Int {
        return arrayList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(arrayList[position])
        holder.itemView.setOnClickListener {
            val intent = Intent(context, ProductActivity::class.java)
            val mProduct = arrayList[position]
            intent.putExtra("Product", mProduct)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }

}