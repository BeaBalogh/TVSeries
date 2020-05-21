package hu.bme.aut.tvseries.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import hu.bme.aut.tvseries.R
import hu.bme.aut.tvseries.api.BackendAPI
import hu.bme.aut.tvseries.entities.Actor


class ActorAdapter(
    context: Context,
    resource: Int,
    textViewResourceId: Int,
    objects: MutableList<Actor>
) : ArrayAdapter<Actor>(context, resource, textViewResourceId, objects) {

    var actorList = mutableListOf<Actor>()
    override fun getCount(): Int {
        return actorList.size
    }

    override fun getItem(position: Int): Actor? {
        return actorList[position]
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView: View? = convertView
        val actor: Actor? = getItem(position)
        if (convertView == null) {
            convertView =
                LayoutInflater.from(context).inflate(R.layout.item_actor, parent, false)
        }
        val actorName = convertView?.findViewById(R.id.etActorName) as TextView
        val imageView: ImageView =
            convertView.findViewById(R.id.imgActor) as ImageView
        actorName.text = actor?.name
        Glide.with(context)
            .load(BackendAPI.IMAGE_URL + actor?.image)
            .into(imageView)

        return convertView
    }
}