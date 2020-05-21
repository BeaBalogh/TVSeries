package hu.bme.aut.tvseries.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputLayout
import hu.bme.aut.tvseries.R
import hu.bme.aut.tvseries.api.BackendInteractor
import hu.bme.aut.tvseries.entities.Actor


class NewCastFragment : Fragment() {
    private lateinit var backendInteractor: BackendInteractor
    private var suggestions = mutableListOf<Actor>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_new_cast, container, false)
        val actorTextView: TextInputLayout = root.findViewById(R.id.etActor)
        backendInteractor = BackendInteractor(requireContext())
        //TODO: Actor dropdown search list
//        val adapter = ActorAdapter(requireContext(),R.layout.item_actor, actorTextView.editText?.id!!, suggestions )
//        (actorTextView.editText as AutoCompleteTextView).setAdapter(adapter)
//
//        actorTextView.editText?.addTextChangedListener { text ->
//            if (text.toString() != "") {
//                viewModel.onQueryChanged(text.toString())
//                backendInteractor.searchActor(
//                    text.toString(),
//                     {
//                        viewModel.addSearchList(it)
//                        suggestions = it as MutableList<Actor>
//                        adapter.notifyDataSetChanged()
//
//                    },
//                    {
//                        viewModel.addSearchList(listOf())
//                    })
//            }
//        }

        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

}
