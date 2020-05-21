package hu.bme.aut.tvseries.ui.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import hu.bme.aut.tvseries.R
import hu.bme.aut.tvseries.entities.Episode
import hu.bme.aut.tvseries.utils.PrefUtils
import kotlinx.android.synthetic.main.item_episode.view.*

class EpisodeAdapter : RecyclerView.Adapter<EpisodeAdapter.ViewHolder>() {

    private var episodeList: MutableList<Episode> = mutableListOf()
    internal var itemClickListener: EpisodeItemClickListener? = null
    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_episode, parent, false)
        context = parent.context
        return ViewHolder(view, context)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val episode = episodeList[position]

        holder.title.text = "${episode.number}. ${episode.title}"
        holder.overview.text = episode.overview
        holder.date.text = episode.date
        if (episode.watched) {
            holder.btnWatched.setIconResource(R.drawable.ic_done_black_16dp)
        } else {
            holder.btnWatched.setIconResource(R.drawable.ic_add_black_16dp)
        }
    }

    override fun getItemCount() = episodeList.size

    fun addAll(episode: MutableList<Episode>) {
        episodeList = episode
        notifyDataSetChanged()
    }

    fun removeAll() {
        episodeList = mutableListOf()
        notifyDataSetChanged()
    }

    fun remove(episode: Episode) {
        episodeList.remove(episode)
        notifyDataSetChanged()
    }

    fun add(episode: Episode) {
        episodeList.add(episode)
        notifyItemInserted(episodeList.size - 1)
        Log.d("ADAPTERSIZE", episodeList.size.toString())
    }

    fun getEpisode(position: Int): Episode {
        return episodeList[position]
    }

    inner class ViewHolder(view: View, context: Context) : RecyclerView.ViewHolder(view),
        View.OnClickListener {
        val title: TextView = view.title
        val date: TextView = view.date
        val overview: TextView = view.rating
        val btnWatched: MaterialButton = view.btnWatched
        val btnDelete: ImageButton = view.btnDelete

        var episode: Episode? = null

        init {
            btnWatched.setOnClickListener(this)
            btnDelete.setOnClickListener(this)
            if (!PrefUtils.getUserLoggedIn(context)) {
                btnWatched.visibility = View.GONE
            }
        }

        override fun onClick(v: View) {
            episode = getEpisode(layoutPosition)
            when (v.id) {
                R.id.btnWatched -> episode?.let { episode ->
                    itemClickListener?.onWatchedClick(episode)
                    notifyDataSetChanged()
                }
                R.id.btnDelete ->
                    episode?.let { episode ->
                        itemClickListener?.onDeleteClick(episode)
                        notifyDataSetChanged()
                    }

            }
        }
    }

    interface EpisodeItemClickListener {
        fun onWatchedClick(episode: Episode)
        fun onDeleteClick(episode: Episode)
//        fun onItemLongClick(position: Int, view: View): Boolean
    }
}