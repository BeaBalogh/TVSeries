package hu.bme.aut.tvseries.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.afollestad.vvalidator.form
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import hu.bme.aut.tvseries.R
import hu.bme.aut.tvseries.api.BackendInteractor
import hu.bme.aut.tvseries.entities.Episode
import hu.bme.aut.tvseries.entities.Season
import hu.bme.aut.tvseries.ui.activity.BaseActivity
import hu.bme.aut.tvseries.ui.viewmodel.SharedViewModel

class NewEpisodeFragment : Fragment() {

    private lateinit var sharedViewModel: SharedViewModel

    //TODO: Save images
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_new_episode, container, false)
        val title: TextInputLayout = root.findViewById(R.id.etTitle)
        val airDate: TextInputLayout = root.findViewById(R.id.etAirDate)
        val season: TextInputLayout = root.findViewById(R.id.etSeason)
        val number: TextInputLayout = root.findViewById(R.id.etNumber)
        val overview: TextInputLayout = root.findViewById(R.id.etOverview)
        val saveBtn: MaterialButton = root.findViewById(R.id.btnSave)
        form {
            useRealTimeValidation(disableSubmit = true)
            inputLayout(title, name = "title") {
                isNotEmpty()
            }
            inputLayout(airDate, name = "date") {
                isNotEmpty()
            }
            inputLayout(season, name = "season") {
                isNotEmpty()
                isNumber()
            }
            inputLayout(number, name = "number") {
                isNotEmpty()
                isNumber()
            }
            inputLayout(overview, name = "overview") {
                isNotEmpty()
            }
        }.submitWith(saveBtn) { formResult ->
            (activity as BaseActivity).showProgressDialog()
            val episode = Episode(
                title = formResult["title"]?.asString()!!,
                date = formResult["date"]?.asString()!!,
                number = formResult["number"]?.asInt()!!,
                overview = formResult["overview"]?.asString()!!,
                watched = false
            )
            BackendInteractor(requireContext()).postEpisode(
                sharedViewModel.selected.value?.id!!,
                formResult["season"]?.asInt() ?: 0,
                formResult["number"]?.asInt() ?: 0,
                episode,
                {
                    val season = sharedViewModel.selected.value?.seasons?.find { s ->
                        s.number == (formResult["season"]?.asInt() ?: 0)
                    }
                    if (season == null) {
                        val s = Season(
                            number = formResult["season"]?.asInt()!!,
                            episodes = mutableListOf(episode)
                        )
                        sharedViewModel.selected.value?.seasons?.add(s)
                    } else {
                        sharedViewModel.selected.value?.seasons?.find { s ->
                            s.number == (formResult["season"]?.asInt() ?: 0)
                        }?.episodes?.add(episode)
                    }
                    (activity as BaseActivity).hideProgressDialog()
                    findNavController().navigateUp()
                },
                {
                    (activity as BaseActivity).hideProgressDialog()
                    Toast.makeText(requireContext(), "Error", Toast.LENGTH_LONG).show()
                })
        }
        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val vm = activityViewModels<SharedViewModel>()
        sharedViewModel = vm.value

    }

}
