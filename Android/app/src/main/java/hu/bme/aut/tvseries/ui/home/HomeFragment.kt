package hu.bme.aut.tvseries.ui.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import hu.bme.aut.tvseries.R

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var homeAdapter: HomeAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        homeViewModel =
            ViewModelProviders.of(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        val linearLayoutManager = LinearLayoutManager(
            this.context, RecyclerView.HORIZONTAL,false)
        val listSeries: RecyclerView = root.findViewById(R.id.list_series)
        listSeries.layoutManager = linearLayoutManager
        homeViewModel.series.observe(this, Observer {
            homeAdapter = HomeAdapter(context!!)
            homeAdapter.addAll(it)
            listSeries.adapter = homeAdapter
        })

        return root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        homeAdapter = HomeAdapter(context)
    }
}