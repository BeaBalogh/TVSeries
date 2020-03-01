package hu.bme.aut.tvseries.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import aut.bme.hu.tvseries.data.Series
import hu.bme.aut.tvseries.R
import kotlinx.android.synthetic.main.card_series.view.*

class HomeAdapter(private val context: Context) :
    RecyclerView.Adapter<HomeAdapter.ViewHolder>() {

    private var seriesList: MutableList<Series> = mutableListOf()
    private val layoutInflater = LayoutInflater.from(context)
//    internal var itemClickListener: RecipeItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = layoutInflater
            .inflate(R.layout.card_series, parent, false)
        return ViewHolder(view)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val series = seriesList[position]

        holder.name.text = series.name
        holder.rating.text = series.rating
    }


    override fun getItemCount() = seriesList.size

    fun addAll(series: List<Series>){
        seriesList.addAll(series)
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.series_name
        val rating: TextView = view.series_rating

        var series: Series? = null

//        init {
//            view.setOnClickListener {
//                series?.let { series -> itemClickListener?.onItemClick(recipe) }
//            }
//            view.setOnLongClickListener {view ->
//                itemClickListener?.onItemLongClick(adapterPosition, view)
//                true
//            }
//
//        }
    }

//    interface RecipeItemClickListener {
//        fun onItemClick(recipe: Recipe)
//        fun onItemLongClick(position: Int, view: View): Boolean
//    }

}