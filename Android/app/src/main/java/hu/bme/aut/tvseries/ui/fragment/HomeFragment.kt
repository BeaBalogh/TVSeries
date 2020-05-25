package hu.bme.aut.tvseries.ui.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import hu.bme.aut.tvseries.R
import hu.bme.aut.tvseries.api.BackendInteractor
import hu.bme.aut.tvseries.entities.Series
import hu.bme.aut.tvseries.ui.activity.BaseActivity
import hu.bme.aut.tvseries.ui.adapter.SeriesCardAdapter
import hu.bme.aut.tvseries.ui.viewmodel.SharedViewModel
import hu.bme.aut.tvseries.utils.PrefUtils

class HomeFragment : Fragment(), SeriesCardAdapter.SeriesItemClickListener {

    private lateinit var topAdapter: SeriesCardAdapter
    private lateinit var latestAdapter: SeriesCardAdapter
    private lateinit var popularAdapter: SeriesCardAdapter
    private lateinit var viewModel: SharedViewModel
    private lateinit var backendInteractor: BackendInteractor
    private var loaded = false


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        backendInteractor = BackendInteractor(requireContext())
        val vm: SharedViewModel by activityViewModels()
        viewModel = vm
        loadSeries()
        observeViewModel()
        val latestSeries: RecyclerView = root.findViewById(R.id.latest_series)
        val popularSeries: RecyclerView = root.findViewById(R.id.popular_series)
        val topSeries: RecyclerView = root.findViewById(R.id.top_series)
        val btnCreateShow: Button = root.findViewById(R.id.btnNewShow)


        val linearLayoutManager = LinearLayoutManager(
            this.context, RecyclerView.HORIZONTAL, false
        )
        val linearLayoutManager2 = LinearLayoutManager(
            this.context, RecyclerView.HORIZONTAL, false
        )
        val linearLayoutManager3 = LinearLayoutManager(
            this.context, RecyclerView.HORIZONTAL, false
        )


        latestSeries.layoutManager = linearLayoutManager
        popularSeries.layoutManager = linearLayoutManager2
        topSeries.layoutManager = linearLayoutManager3

        topAdapter.itemClickListener = this
        latestAdapter.itemClickListener = this
        popularAdapter.itemClickListener = this

        latestSeries.adapter = latestAdapter
        popularSeries.adapter = popularAdapter
        topSeries.adapter = topAdapter

        btnCreateShow.setOnClickListener {
            view?.findNavController()!!.navigate(
                R.id.action_navigation_home_to_newSeriesFragment
            )
        }
        val role = PrefUtils.getRole(requireContext())
        if (role != "ROLE_ADMIN") {
            btnCreateShow.visibility = View.GONE
        }
        return root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        topAdapter = SeriesCardAdapter()
        latestAdapter = SeriesCardAdapter()
        popularAdapter = SeriesCardAdapter()

    }

    override fun onItemClick(series: Series) {
        (activity as BaseActivity).showProgressDialog()
        backendInteractor.getSeriesById(
            series.id,
            { newSeries ->
                newSeries.follow = series.follow
                viewModel.setSelected(newSeries)
                (activity as BaseActivity).hideProgressDialog()
                view?.findNavController()!!.navigate(
                    R.id.action_navigation_home_to_detailsFragment
                )
            },
            {
                (activity as BaseActivity).hideProgressDialog()
                Toast.makeText(requireContext(), "Error", Toast.LENGTH_LONG).show()
            })
    }

    override fun onFollowedClick(series: Series) {
        val followed = PrefUtils.getUserFollowed(requireContext())?.toMutableSet() ?: mutableSetOf()
        if (followed.contains(series.id.toString())) {
            backendInteractor.deleteID(
                getby = "followed",
                id = series.id,
                onSuccess = {
                    series.follow = !series.follow
                    notifyAdapter()
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
                    notifyAdapter()
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

    private fun observeViewModel() {
        viewModel.latestSeriesList.observe(viewLifecycleOwner, Observer {
            latestAdapter.addAll(it)
            notifyAdapter()
        })
        viewModel.topSeriesList.observe(viewLifecycleOwner, Observer {
            topAdapter.addAll(it)
            notifyAdapter()
        })
        viewModel.popularSeriesList.observe(viewLifecycleOwner, Observer {
            popularAdapter.addAll(it)
            notifyAdapter()
        })

    }

    private fun loadSeries() {
        if (!viewModel.getLatestSeries().isNullOrEmpty())
            loaded = true
        if (!loaded) {
            val followed =
                PrefUtils.getUserFollowed(requireContext())?.toMutableSet() ?: mutableSetOf()
            backendInteractor.getSeries(
                "top_rated",
                onSuccess = { list ->
                    list.forEach { s ->
                        s.follow = followed.contains(s.id.toString())
                    }
                    viewModel.addTopSeries(list)
                    topAdapter.addAll(list)
                    loaded = true
                },
                onError = {}
            )
            backendInteractor.getSeries(
                "airing_today",
                onSuccess = { list ->
                    list.forEach { s ->
                        s.follow = followed.contains(s.id.toString())
                    }
                    viewModel.addLatestSeries(list)
                    latestAdapter.addAll(list)
                },
                onError = {}
            )
            backendInteractor.getSeries(
                "popular",
                onSuccess = { list ->
                    list.forEach { s ->
                        s.follow = followed.contains(s.id.toString())
                    }
                    viewModel.addPopularSeries(list)
                    popularAdapter.addAll(list)
                },
                onError = {}
            )
        } else {
            if (topAdapter.itemCount == 0) {
                viewModel.getTopSeries()?.let { topAdapter.addAll(it) }
                viewModel.getPopularSeries()?.let { topAdapter.addAll(it) }
                viewModel.getLatestSeries()?.let { topAdapter.addAll(it) }
            }
        }
    }

    fun notifyAdapter() {
        topAdapter.notifyDataSetChanged()
        latestAdapter.notifyDataSetChanged()
        popularAdapter.notifyDataSetChanged()
    }
}
