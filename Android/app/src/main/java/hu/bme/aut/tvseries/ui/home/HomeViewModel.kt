package hu.bme.aut.tvseries.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import aut.bme.hu.tvseries.data.Series

class HomeViewModel : ViewModel() {

    private val _series = MutableLiveData<MutableList<Series>>().apply {
        var seriesList : MutableList<Series> = mutableListOf()
        val one = Series(name = "Star wars", rating = "8.7")
        seriesList.add(one)
        seriesList.add(one)
        seriesList.add(one)
        seriesList.add(one)
        seriesList.add(one)
        seriesList.add(one)
        seriesList.add(one)
        seriesList.add(one)
        seriesList.add(one)
        seriesList.add(one)
        seriesList.add(one)
        seriesList.add(one)
        seriesList.add(one)
        seriesList.add(one)
        value = seriesList
    }
    val series: LiveData<MutableList<Series>> = _series


}

