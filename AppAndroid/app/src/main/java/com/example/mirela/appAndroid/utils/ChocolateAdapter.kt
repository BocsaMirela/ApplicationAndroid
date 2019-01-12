package com.example.mirela.appAndroid.utils

import android.graphics.BitmapFactory
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import com.example.mirela.appAndroid.chocolate.Chocolate
import com.example.mirela.appAndroid.R
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ChocolateAdapter : RecyclerView.Adapter<ChocolateAdapter.ViewHolder>() {
    private var chocolatesList: List<Chocolate> = ArrayList()
    var clickListener: OnClickInterface? = null

    fun setChocolatesList(list: List<Chocolate>) {
        chocolatesList = list
    }

    fun getChocolatesList(): List<Chocolate> {
        return chocolatesList
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, p: Int): ViewHolder {
        var layoutInflater = LayoutInflater.from(viewGroup.context)
        val view = layoutInflater.inflate(R.layout.row_layout, viewGroup, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return chocolatesList.size
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, p: Int) {
        val chocolate = chocolatesList[p]
        viewHolder.txtDescription.text = chocolate.body
        viewHolder.txtData.text = formatDate(Date(chocolate.date))
        try {
            val bitmap = BitmapFactory.decodeFile(chocolate.imagePath)
            viewHolder.imageView.setImageBitmap(bitmap)
        } catch (e: Exception) {
        }

    }

    fun setOnClickListener(clickInterface: OnClickInterface){
        clickListener=clickInterface
    }


    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view), View.OnCreateContextMenuListener,
        View.OnClickListener {

        var imageView: ImageView = view.findViewById(R.id.imageView)
        var txtDescription: TextView = view.findViewById(R.id.chocolateDescription)
        var txtData: TextView = view.findViewById(R.id.chocolateData)

        init {
            view.setOnCreateContextMenuListener(this)
            view.setOnClickListener(this)
        }

        override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
            menu?.add(0, R.id.update, adapterPosition, "UPDATE")

        }

        override fun onClick(v: View?) {
            clickListener?.onClick(view, adapterPosition); }
    }

    private fun formatDate(data: Date): String {
        val fmt = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault())
        return fmt.format(data)
    }

    fun removeItem(item: Chocolate) {
        (chocolatesList as ArrayList<Chocolate>).remove(item)
        notifyDataSetChanged()
    }

    fun insertItem(item: Chocolate) {
        (chocolatesList as ArrayList<Chocolate>).add(item)
        notifyDataSetChanged()
    }


}