package hu.bme.aut.tvseries.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import hu.bme.aut.tvseries.entities.Episode
import hu.bme.aut.tvseries.entities.Rating
import hu.bme.aut.tvseries.entities.Series

class SharedViewModel : ViewModel() {

    private val _topSeriesList = MutableLiveData<MutableList<Series>>()
    val topSeriesList: LiveData<MutableList<Series>> = _topSeriesList
    fun addTopSeries(series: List<Series>) {
        _topSeriesList.value?.addAll(series)
    }

    fun getTopSeries(): List<Series>? {
        return _topSeriesList.value
    }

    private val _popularSeriesList = MutableLiveData<MutableList<Series>>()
    val popularSeriesList: LiveData<MutableList<Series>> = _popularSeriesList
    fun addPopularSeries(series: List<Series>) {
        _popularSeriesList.value?.addAll(series)
    }

    fun getPopularSeries(): List<Series>? {
        return _popularSeriesList.value
    }

    private val _latestSeriesList = MutableLiveData<MutableList<Series>>()
    val latestSeriesList: LiveData<MutableList<Series>> = _latestSeriesList
    fun addLatestSeries(series: List<Series>) {
        _latestSeriesList.value?.addAll(series)
    }

    fun getLatestSeries(): List<Series>? {
        return _latestSeriesList.value
    }

    private val _searchList = MutableLiveData<MutableList<Series>>()
    val searchList: LiveData<MutableList<Series>> = _searchList
    fun addSearchSeries(series: List<Series>) {
        _searchList.value?.addAll(series)
    }

    fun getSearchList(): List<Series>? {
        return _searchList.value
    }

    private val _selected = MutableLiveData<Series>()
    val selected: LiveData<Series> = _selected
    fun setSelected(series: Series?) {
        _selected.apply {
            value = series
        }
    }

    fun addRatings(ratings: List<Rating>) {
        _selected.apply {
            value?.ratings?.addAll(ratings)
        }
    }

    fun addRating(rating: Rating) {
        _selected.apply {
            value?.ratings?.add(rating)
        }
    }


    private val _query = MutableLiveData<String>()
    val query: LiveData<String> = _query
    fun onQueryChanged(query: String) {
        _query.apply {
            value = query
        }
    }


    private val _followedList = MutableLiveData<MutableList<Series>>()
    val followedList: LiveData<MutableList<Series>> = _followedList
    fun addFollowed(series: Series) {
        _followedList.apply {
            value?.add(series)
        }
    }

    fun removeFollowed(series: Series) {
        _followedList.apply {
            value?.remove(series)
        }
    }

    fun followClicked(series: Series) {
        _topSeriesList.value?.forEach {
            if (it == series) {
                it.follow = !it.follow
            }
        }
        _popularSeriesList.value?.forEach {
            if (it == series) {
                it.follow = !it.follow
            }
        }
        _latestSeriesList.value?.forEach {
            if (it == series) {
                it.follow = !it.follow
            }
        }
    }


    fun watchedClicked(episode: Episode) {
        _selected.value?.seasons?.forEach { s ->
            s.episodes.forEach {
                if (it == episode) {
                    it.watched = !it.watched
                    return
                }
            }
        }
    }


}