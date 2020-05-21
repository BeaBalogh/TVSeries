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
import com.google.android.material.button.MaterialButton
import hu.bme.aut.tvseries.R
import hu.bme.aut.tvseries.api.BackendAPI
import hu.bme.aut.tvseries.entities.Series
import hu.bme.aut.tvseries.utils.PrefUtils
import kotlinx.android.synthetic.main.item_card_series.view.*

class SeriesCardAdapter :
    RecyclerView.Adapter<SeriesCardAdapter.ViewHolder>() {

    private var seriesList = mutableListOf<Series>()
    internal var itemClickListener: SeriesItemClickListener? = null
    private lateinit var context: Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_card_series, parent, false)
        context = parent.context
        return ViewHolder(view, parent.context)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val series = seriesList[position]

        holder.name.text = series.title
        holder.rating.text = series.rating
        Glide.with(context)
            .load(BackendAPI.IMAGE_URL + series.image)
            .into(holder.cover)

        if (series.follow) {
            holder.btnFollow.setIconResource(R.drawable.ic_done_black_16dp)
            holder.btnFollow.text = "Followed"
        } else {
            holder.btnFollow.setIconResource(R.drawable.ic_add_black_16dp)
            holder.btnFollow.text = "Follow"
        }

    }


    override fun getItemCount() = seriesList.size

    fun addAll(series: List<Series>) {
        seriesList = series as MutableList<Series>
        notifyItemRangeInserted(0, series.size)
    }

    fun add(series: Series) {
        seriesList.add(series)
        notifyDataSetChanged()

    }

    fun getSeries(position: Int): Series {
        return seriesList[position]
    }

    inner class ViewHolder(view: View, context: Context) : RecyclerView.ViewHolder(view),
        View.OnClickListener {
        val name: TextView = view.series_name
        val rating: TextView = view.series_rating
        val cover: ImageView = view.series_cover
        val btnFollow: MaterialButton = view.btnFollow

        var series: Series? = null

        init {
            view.setOnClickListener(this)
            btnFollow.setOnClickListener(this)
            if (!PrefUtils.getUserLoggedIn(context)) {
                btnFollow.visibility = View.GONE
            }
        }

        override fun onClick(v: View) {
            val position = layoutPosition
            series = getSeries(position)
            series?.let { series ->
                if (v.id == R.id.btnFollow) {
                    itemClickListener?.onFollowedClick(series)
                    Log.i("onFollowedClick", series.toString())
                    notifyDataSetChanged()
                    return
                }

                itemClickListener?.onItemClick(series)
                Log.i("onSeriesClick", series.toString())

            }
        }
    }


    interface SeriesItemClickListener {
        fun onItemClick(series: Series)
        fun onFollowedClick(series: Series)
//        fun onItemLongClick(position: Int, view: View): Boolean
    }

}