package hu.bme.aut.tvseries.ui.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import hu.bme.aut.tvseries.R
import hu.bme.aut.tvseries.entities.Rating
import hu.bme.aut.tvseries.utils.PrefUtils
import kotlinx.android.synthetic.main.item_rating.view.*

class RatingAdapter :
    RecyclerView.Adapter<RatingAdapter.ViewHolder>() {

    private val ratingsList = mutableListOf<Rating>()
    internal var itemClickListener: RatingItemClickListener? = null
    private lateinit var context: Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_rating, parent, false)
        context = parent.context
        return ViewHolder(view)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val rating = ratingsList[position]

        holder.name.text = rating.userName
        holder.rate.rating = rating.rating.toFloat()
        holder.comment.text = rating.comment
        holder.date.text = rating.date

        if (PrefUtils.getRole(context) != "ROLE_ADMIN" || rating.userID != PrefUtils.getUserID(
                context
            )
        ) {
            holder.btnDelete.visibility = View.GONE
        }

    }


    override fun getItemCount() = ratingsList.size

    fun addAll(ratings: List<Rating>) {
        val size = ratings.size
        ratingsList += ratings
        notifyItemRangeInserted(size, ratings.size)
    }

    fun remove(rating: Rating) {
        ratingsList.remove(rating)
        notifyDataSetChanged()
    }

    fun add(rating: Rating) {
        ratingsList.add(rating)
        notifyDataSetChanged()
    }

    fun getRating(position: Int): Rating {
        return ratingsList[position]
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view),
        View.OnClickListener {
        val name: TextView = view.tvName
        val comment: TextView = view.tvComment
        val date: TextView = view.tvDate
        val rate: RatingBar = view.ratingBar
        val btnDelete: ImageButton = view.btnDelete
        var rating: Rating? = null

        init {
            btnDelete.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            when (v.id) {
                R.id.btnDelete -> {
                    val position = layoutPosition
                    rating = getRating(position)
                    rating?.let { rating ->
                        itemClickListener?.onRatingDeleteClick(rating)
                        Log.i("onRatingClick", rating.userName)
                    }
                }
            }
        }
    }


    interface RatingItemClickListener {
        fun onRatingDeleteClick(rating: Rating)
//        fun onItemLongClick(position: Int, view: View): Boolean
    }
}
