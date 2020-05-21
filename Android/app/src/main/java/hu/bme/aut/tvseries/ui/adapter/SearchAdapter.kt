package hu.bme.aut.tvseries.ui.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import hu.bme.aut.tvseries.R
import hu.bme.aut.tvseries.api.BackendAPI
import hu.bme.aut.tvseries.entities.Series
import kotlinx.android.synthetic.main.item_series.view.*

class SearchAdapter :
    RecyclerView.Adapter<SearchAdapter.ViewHolder>() {

    private var seriesList: MutableList<Series> = mutableListOf()
    internal var itemClickListener: SeriesItemClickListener? = null
    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_series, parent, false)
        context = parent.context
        return ViewHolder(view)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val series = seriesList[position]

        holder.title.text = series.title
        holder.year.text = series.year.toString()
        holder.rating.text = "★" + series.rating
        Glide.with(context)
            .load(BackendAPI.IMAGE_URL + series.image)
            .into(holder.image)
    }


    override fun getItemCount() = seriesList.size

    fun addAll(series: List<Series>) {
        seriesList = series as MutableList<Series>
    }

    fun getSeries(position: Int): Series {
        return seriesList[position]
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        val title: TextView = view.title
        val year: TextView = view.date
        val rating: TextView = view.rating
        val image: ImageView = view.imageView
        var series: Series? = null

        init {
            view.setOnClickListener(this)
//            view.setOnLongClickListener {view ->
//                itemClickListener?.onItemLongClick(adapterPosition, view)
//                true
//            }

        }

        override fun onClick(v: View?) {
            val position = layoutPosition
            series = getSeries(position)
            series?.let { series ->
                itemClickListener?.onItemClick(series)
                Log.i("onSeriesClick", series.toString())
            }
        }
    }

    interface SeriesItemClickListener {
        fun onItemClick(series: Series)
//        fun onItemLongClick(position: Int, view: View): Boolean
    }

}