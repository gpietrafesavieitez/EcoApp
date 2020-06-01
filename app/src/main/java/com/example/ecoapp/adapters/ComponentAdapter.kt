package com.example.ecoapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.ecoapp.R
import com.example.ecoapp.classes.Component
import kotlinx.android.synthetic.main.row_component_details.view.*
import java.util.*

class ComponentAdapter(private var arrayList: ArrayList<Component>, val context: Context) :
    RecyclerView.Adapter<ComponentAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(mComponent: Component) {
            itemView.tvCompName.text = mComponent.name
            var icon: Int = R.drawable.ic_light_null
            when (mComponent.recycleType) {
                1 -> {
                    icon = R.drawable.ic_light_yellow
                }
                2 -> {
                    icon = R.drawable.ic_light_green
                }
                3 -> {
                    icon = R.drawable.ic_light_blue
                }
                4 -> {
                    icon = R.drawable.ic_light_brown
                }
                5 -> {
                    icon = R.drawable.ic_light_red
                }
                6 -> {
                    icon = R.drawable.ic_light_grey
                }
                7 -> {
                    icon = R.drawable.ic_light_white
                }
                8 -> {
                    icon = R.drawable.ic_light_white
                }
                9 -> {
                    icon = R.drawable.ic_light_white
                }
                10 -> {
                    icon = R.drawable.ic_light_white
                }
            }
            itemView.tvCompName.setCompoundDrawablesWithIntrinsicBounds(0, 0, icon, 0)
        }

    }

    fun update(modelList: ArrayList<Component>) {
        arrayList = modelList
        this.notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_component_details, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(arrayList[position])
        holder.itemView.setOnClickListener {
            var msg = R.string.explanation_unknown
            when (arrayList[position].recycleType) {
                1 -> {
                    msg = R.string.explanation_1
                }
                2 -> {
                    msg = R.string.explanation_2
                }
                3 -> {
                    msg = R.string.explanation_3
                }
                4 -> {
                    msg = R.string.explanation_4
                }
                5 -> {
                    msg = R.string.explanation_5
                }
                6 -> {
                    msg = R.string.explanation_6
                }
                7 -> {
                    msg = R.string.explanation_7
                }
                8 -> {
                    msg = R.string.explanation_8
                }
                9 -> {
                    msg = R.string.explanation_9
                }
                10 -> {
                    msg = R.string.explanation_10
                }
            }
            Toast.makeText(context, context.resources.getString(msg), Toast.LENGTH_SHORT).show()
        }
    }

}