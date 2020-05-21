package hu.bme.aut.tvseries.ui.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputLayout
import hu.bme.aut.tvseries.R
import hu.bme.aut.tvseries.api.BackendInteractor
import hu.bme.aut.tvseries.entities.Series
import hu.bme.aut.tvseries.ui.activity.BaseActivity
import hu.bme.aut.tvseries.ui.adapter.SearchAdapter
import hu.bme.aut.tvseries.ui.viewmodel.SharedViewModel

class SearchFragment : Fragment(),
    SearchAdapter.SeriesItemClickListener {

    private lateinit var viewModel: SharedViewModel
    private lateinit var seriesAdapter: SearchAdapter
    private lateinit var noResult: TextView
    private lateinit var searchResult: RecyclerView
    private lateinit var backendInteractor: BackendInteractor

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_search, container, false)
        backendInteractor = BackendInteractor(requireContext())

        val vm: SharedViewModel by activityViewModels()
        viewModel = vm

        val searchView: TextInputLayout = root.findViewById(R.id.search)
        noResult = root.findViewById(R.id.noresult)
        searchResult = root.findViewById(R.id.results)

        val linearLayoutManager = LinearLayoutManager(
            this.context, RecyclerView.VERTICAL, false
        )
        searchResult.layoutManager = linearLayoutManager
        seriesAdapter.itemClickListener = this
        searchResult.adapter = seriesAdapter

        viewModel.getSearchList()?.let { seriesAdapter.addAll(it) }

        searchView.editText?.setText(viewModel.query.value)
        if (viewModel.query.value != null && viewModel.query.value != "") {
            showResults()
        }

        searchView.editText?.addTextChangedListener { text ->
            if (text.toString() == "") {
                viewModel.addSearchSeries(listOf())
                showResults()
            } else {
                viewModel.onQueryChanged(text.toString())
                backendInteractor.searchSeries(
                    text.toString(),
                    {
                        viewModel.addSearchSeries(it)
                        seriesAdapter.addAll(it)
                        seriesAdapter.notifyDataSetChanged()
                        showResults()

                    },
                    {
                        viewModel.addSearchSeries(listOf())
                        showResults()
                    })
            }
        }


        return root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        seriesAdapter = SearchAdapter()
    }

    override fun onItemClick(series: Series) {
        (activity as BaseActivity).showProgressDialog()
        backendInteractor.getSeriesById(
            series.id,
            { newSeries ->
                (activity as BaseActivity).hideProgressDialog()
                viewModel.setSelected(newSeries)
                view?.findNavController()!!.navigate(
                    R.id.action_navigation_search_to_detailsFragment
                )
            },
            {
                (activity as BaseActivity).hideProgressDialog()
                Toast.makeText(requireContext(), "Error", Toast.LENGTH_LONG).show()

            })
    }

    private fun showResults() {
        if (seriesAdapter.itemCount == 0) {
            noResult.text = getString(R.string.no_result)
            noResult.visibility = View.VISIBLE
            searchResult.visibility = View.GONE
        } else {
            noResult.visibility = View.GONE
            searchResult.visibility = View.VISIBLE
        }
    }
}