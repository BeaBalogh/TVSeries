package hu.bme.aut.tvseries.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import hu.bme.aut.tvseries.R
import hu.bme.aut.tvseries.api.BackendAPI
import hu.bme.aut.tvseries.entities.Cast
import kotlinx.android.synthetic.main.item_cast.view.*

class CastAdapter : RecyclerView.Adapter<CastAdapter.ViewHolder>() {

    private var castList: MutableList<Cast> = mutableListOf()

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cast, parent, false)
        context = parent.context
        return ViewHolder(view)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val cast = castList[position]

        holder.name.text = cast.actor.name
        holder.role.text = cast.role
        if (cast.actor.image != "")
            Glide.with(context)
                .load(BackendAPI.IMAGE_URL + cast.actor.image)
                .into(holder.image)

    }

    override fun getItemCount() = castList.size

    fun addAll(cast: List<Cast>) {
        castList.addAll(cast)
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val name: TextView = view.name
        val role: TextView = view.role
        val image: ImageView = view.photo

    }
}