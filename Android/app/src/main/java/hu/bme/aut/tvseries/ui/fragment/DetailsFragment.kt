package hu.bme.aut.tvseries.ui.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.vvalidator.form
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.textview.MaterialTextView
import hu.bme.aut.tvseries.R
import hu.bme.aut.tvseries.api.BackendAPI
import hu.bme.aut.tvseries.api.BackendInteractor
import hu.bme.aut.tvseries.entities.Episode
import hu.bme.aut.tvseries.entities.Rating
import hu.bme.aut.tvseries.entities.Series
import hu.bme.aut.tvseries.ui.activity.BaseActivity
import hu.bme.aut.tvseries.ui.adapter.CastAdapter
import hu.bme.aut.tvseries.ui.adapter.EpisodeAdapter
import hu.bme.aut.tvseries.ui.adapter.RatingAdapter
import hu.bme.aut.tvseries.ui.viewmodel.SharedViewModel
import hu.bme.aut.tvseries.utils.PrefUtils
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class DetailsFragment : Fragment(), RatingAdapter.RatingItemClickListener, View.OnClickListener,
    EpisodeAdapter.EpisodeItemClickListener {
    private lateinit var episodeAdapter: EpisodeAdapter
    private lateinit var castAdapter: CastAdapter
    private lateinit var ratingAdapter: RatingAdapter

    private lateinit var viewModel: SharedViewModel
    private lateinit var btnFollow: MaterialButton
    private lateinit var btnDelete: ImageButton
    private lateinit var seasonPicker: TextInputLayout
    private lateinit var ratingsList: RecyclerView
    private lateinit var noRating: MaterialTextView
    private lateinit var backendInteractor: BackendInteractor

    private lateinit var series: Series

    companion object {
        private var selected_season = 1
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_details, container, false)
        backendInteractor = BackendInteractor(requireContext())
        val vm: SharedViewModel by activityViewModels()
        viewModel = vm
        series = viewModel.selected.value!!
        loadData()
        loadInfosCard(root)
        loadEpisodesCard(root)
        loadCastCard(root)
        loadRatingsCard(root)
        return root
    }

    private fun loadData() {
        viewModel.selected.observe(viewLifecycleOwner, Observer { series ->
            this.series = series
        })
    }

    private fun loadInfosCard(root: View) {
        val cover: ImageView = root.findViewById(R.id.cover)
        val title: TextView = root.findViewById(R.id.title)
        val overview: TextView = root.findViewById(R.id.overview)
        val rate: TextView = root.findViewById(R.id.rating)
        val year: TextView = root.findViewById(R.id.date)
        val pegi: TextView = root.findViewById(R.id.PEGI)
        val seasons: TextView = root.findViewById(R.id.seasons)
        btnDelete = root.findViewById(R.id.btnDelete)
        btnFollow = root.findViewById(R.id.btnFollow)

        btnFollow.setOnClickListener(this)
        btnDelete.setOnClickListener(this)
        if (!PrefUtils.getUserLoggedIn(requireContext())) {
            btnFollow.visibility = View.GONE
        }
        if ((PrefUtils.getRole(requireContext()) ?: "") != "ROLE_ADMIN") {
            btnDelete.visibility = View.GONE
        }
        viewModel.selected.observe(viewLifecycleOwner, Observer { series ->
            title.text = series.title
            rate.text = "★" + series.rating
            year.text = series.year.toString()
            val pegitext = if (series.pegi.contains("+")) series.pegi else series.pegi + "+"
            pegi.text = pegitext
            overview.text = series.overview
            seasons.text = "${series.seasonCount} Seasons"
            changeFollowButton(series.follow)
            val image = if (series.imageLandscape == "") series.image else series.imageLandscape

            Glide.with(this)
                .load(BackendAPI.BACKGROUND_IMAGE_URL + image)
                .into(cover)
        })
    }

    private fun loadEpisodesCard(root: View) {
        val btnNewEpisode: Button = root.findViewById(R.id.btnNewEpisode)
        val episodeList: RecyclerView = root.findViewById(R.id.episodeList)
        val episodeLayoutManager = LinearLayoutManager(this.context, RecyclerView.VERTICAL, false)
        seasonPicker = root.findViewById(R.id.seasonPicker)
        episodeAdapter.itemClickListener = this
        episodeList.layoutManager = episodeLayoutManager
        episodeList.adapter = episodeAdapter
        getEpisodesBySeason(selected_season)
        if (PrefUtils.getRole(requireContext()) != "ROLE_ADMIN") {
            btnNewEpisode.visibility = View.GONE
        }
        btnNewEpisode.setOnClickListener {
            view?.findNavController()!!.navigate(
                R.id.action_detailsFragment_to_newEpisodeFragment
            )
        }
        (seasonPicker.editText as? AutoCompleteTextView)?.setText("Season $selected_season")
        if (series.seasons.size != 0)
            (seasonPicker.editText as? AutoCompleteTextView)?.onItemClickListener =
                AdapterView.OnItemClickListener { _, view, _, _ ->
                    val number = (view as TextView).text.toString().split(" ")[1].toInt()
                    getEpisodesBySeason(number)
                }
        else
            seasonPicker.visibility = View.GONE
    }

    private fun loadCastCard(root: View) {
        val castList: RecyclerView = root.findViewById(R.id.castList)
        val castLayoutManager = LinearLayoutManager(this.context, RecyclerView.HORIZONTAL, false)
        val btnNewCast: Button = root.findViewById(R.id.btnNewCast)
        castList.layoutManager = castLayoutManager
        castList.adapter = castAdapter
        viewModel.selected.observe(viewLifecycleOwner, Observer {
            castAdapter.addAll(it.cast)
        })
        btnNewCast.setOnClickListener {
            view?.findNavController()!!.navigate(
                R.id.action_detailsFragment_to_newCastFragment
            )
        }

    }

    private fun loadRatingsCard(root: View) {
        ratingsList = root.findViewById(R.id.ratingsList)
        val newRating: ConstraintLayout = root.findViewById(R.id.newComment)
        noRating = root.findViewById(R.id.noRating)
        val ratingsLayoutManager = LinearLayoutManager(this.context, RecyclerView.VERTICAL, false)
        ratingAdapter.itemClickListener = this
        ratingsList.layoutManager = ratingsLayoutManager
        ratingsList.adapter = ratingAdapter
        loadNewRating(newRating)
        backendInteractor.getRatings(series.id, {
            viewModel.addRatings(it)
            ratingAdapter.addAll(it)
        }, {})
        viewModel.selected.observe(viewLifecycleOwner, Observer { series ->
            ratingAdapter.addAll(series.ratings)
            showRatings()
        })
        if (!PrefUtils.getUserLoggedIn(requireContext())) {
            newRating.visibility = View.GONE
        }
    }

    private fun loadNewRating(root: ConstraintLayout) {
        val comment: EditText = root.findViewById(R.id.etTextNewComment)
        val ratingBar: RatingBar = root.findViewById(R.id.rbNewComment)
        val btnSend: MaterialButton = root.findViewById(R.id.btnSendComment)
        form {
            input(comment) {
                isNotEmpty()
            }
        }.submitWith(btnSend) {
            (activity as BaseActivity).showProgressDialog()
            val rating = Rating(
                userName = (activity as BaseActivity).userName!!,
                userID = (activity as BaseActivity).userEmail!!,
                comment = comment.text.toString(),
                rating = ratingBar.rating.toDouble(),
                date = dateNowToString()
            )
            backendInteractor.postRating(series.id, rating, {
                comment.setText("")
                ratingBar.rating = 5.0F
                series.ratings.add(rating)
                ratingAdapter.add(it)
                viewModel.addRating(it)
                showRatings()
                (activity as BaseActivity).hideProgressDialog()
            }, {
                Toast.makeText(requireContext(), "Error", Toast.LENGTH_LONG).show()
                (activity as BaseActivity).hideProgressDialog()

            })
        }


    }

    private fun getEpisodesBySeason(number: Int) {
        episodeAdapter.removeAll()
        selected_season = number
        viewModel.selected.observe(viewLifecycleOwner, Observer { series ->
            val items = series.seasons.map { s -> "Season ${s.number}" }
            val adapter = ArrayAdapter(requireContext(), R.layout.item_dropdown, items)
            (seasonPicker.editText as? AutoCompleteTextView)?.setAdapter(adapter)
            (seasonPicker.editText as? AutoCompleteTextView)?.setDropDownBackgroundResource(R.color.colorPrimaryDark)
            val watched = PrefUtils.getUserWatched(requireContext())?.toMutableSet()
                ?: mutableSetOf()
            series.seasons.find { s -> s.number == number }?.episodes?.let { episodes ->
                episodes.forEach { e ->
                    e.watched = (watched.contains(e.id.toString()))
                    episodeAdapter.add(e)
                }

            }
        })
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btnFollow -> {
                viewModel.selected.value?.let { series ->
                    val followed = PrefUtils.getUserFollowed(requireContext())?.toMutableSet()
                        ?: mutableSetOf()
                    if (followed.contains(series.id.toString())) {
                        backendInteractor.deleteID(
                            getby = "followed",
                            id = series.id,
                            onSuccess = {
                                series.follow = !series.follow
                                changeFollowButton(series.follow)
                                viewModel.followClicked(series)
                                followed.remove(series.id.toString())
                                PrefUtils.setUserFollowed(followed as Set<String>, requireContext())
                            },
                            onError = {
                                (activity as BaseActivity).hideProgressDialog()
                                Toast.makeText(requireContext(), "Error", Toast.LENGTH_LONG).show()
                            })
                    } else {
                        backendInteractor.postID(
                            getby = "followed",
                            id = series.id,
                            onSuccess = {
                                series.follow = !series.follow
                                changeFollowButton(series.follow)
                                viewModel.followClicked(series)
                                followed.add(series.id.toString())
                                PrefUtils.setUserFollowed(followed as Set<String>, requireContext())

                            },
                            onError = {
                                (activity as BaseActivity).hideProgressDialog()
                                Toast.makeText(requireContext(), "Error", Toast.LENGTH_LONG).show()
                            })
                    }
                }
            }
            R.id.btnDelete -> {
                AlertDialog.Builder(requireContext())
                    .setTitle("Delete Series")
                    .setMessage("Are you sure you want to delete this series?")
                    .setPositiveButton(
                        android.R.string.yes
                    ) { _, _ ->
                        backendInteractor.deleteSeries(series.id, {
                            findNavController().navigateUp()
                            Toast.makeText(
                                requireContext(),
                                "Series removed successfully",
                                Toast.LENGTH_LONG
                            ).show()
                        }, {
                            Toast.makeText(requireContext(), "Error", Toast.LENGTH_LONG).show()
                        })
                    }
                    .setNegativeButton(android.R.string.no, null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show()
            }

        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        episodeAdapter = EpisodeAdapter()
        castAdapter = CastAdapter()
        ratingAdapter = RatingAdapter()
    }

    override fun onRatingDeleteClick(rating: Rating) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete rating")
            .setMessage("Are you sure you want to delete this rating?")
            .setPositiveButton(
                android.R.string.yes
            ) { _, _ ->
                backendInteractor.deleteRating(series.id, rating.id, {
                    ratingAdapter.remove(rating)
                    viewModel.selected.observe(viewLifecycleOwner, Observer {
                        it.ratings.remove(rating)
                    })
                }, {
                    Toast.makeText(requireContext(), "Error", Toast.LENGTH_LONG).show()
                })
            }
            .setNegativeButton(android.R.string.no, null)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .show()
    }

    private fun changeFollowButton(follow: Boolean) {
        if (follow) {
            btnFollow.setIconResource(R.drawable.ic_done_black_16dp)
            btnFollow.text = getString(R.string.followed)
        } else {
            btnFollow.setIconResource(R.drawable.ic_add_black_16dp)
            btnFollow.text = getString(R.string.follow)
        }
    }

    override fun onWatchedClick(episode: Episode) {
        episode.watched = !episode.watched
        episodeAdapter.notifyDataSetChanged()
        val watched = PrefUtils.getUserWatched(requireContext())?.toMutableSet() ?: mutableSetOf()
        if (watched.contains(episode.id.toString())) {
            backendInteractor.deleteID(
                getby = "watched",
                id = episode.id,
                onSuccess = {
                    viewModel.watchedClicked(episode)
                    watched.remove(episode.id.toString())
                    PrefUtils.setUserWatched(watched as Set<String>, requireContext())
                },
                onError = {
                    Toast.makeText(requireContext(), "Error", Toast.LENGTH_LONG).show()
                })
        } else {
            backendInteractor.postID(
                getby = "watched",
                id = episode.id,
                onSuccess = {
                    watched.add(episode.id.toString())
                    viewModel.watchedClicked(episode)
                    PrefUtils.setUserWatched(watched as Set<String>, requireContext())

                },
                onError = {
                    Toast.makeText(requireContext(), "Error", Toast.LENGTH_LONG).show()
                })
        }
    }

    override fun onDeleteClick(episode: Episode) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Episode")
            .setMessage("Are you sure you want to delete this episode?")
            .setPositiveButton(
                android.R.string.yes
            ) { _, _ ->
                backendInteractor.deleteEpisode(viewModel.selected.value?.id!!,
                    selected_season,
                    episode.number,
                    {
                        viewModel.selected.value!!.seasons.find { s -> s.number == selected_season }?.episodes?.remove(
                            episode
                        )
                        episodeAdapter.remove(episode)
                    },
                    {
                        Toast.makeText(requireContext(), "Error", Toast.LENGTH_LONG).show()

                    })
            }
            .setNegativeButton(android.R.string.no, null)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .show()
    }

    private fun showRatings() {
        if (series.ratings.size == 0) {
            noRating.visibility = View.VISIBLE
            ratingsList.visibility = View.GONE
        } else {
            noRating.visibility = View.GONE
            ratingsList.visibility = View.VISIBLE
        }
    }

    private fun dateNowToString(): String {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
    }
}
