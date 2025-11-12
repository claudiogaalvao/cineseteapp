package com.claudiogalvaodev.moviemanager.ui.peopledetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.claudiogalvaodev.moviemanager.ui.model.FilterModel
import com.claudiogalvaodev.moviemanager.ui.model.MovieModel
import com.claudiogalvaodev.moviemanager.ui.model.PersonModel
import com.claudiogalvaodev.moviemanager.usecases.movies.GetMovieDetailsUseCase
import com.claudiogalvaodev.moviemanager.usecases.movies.GetMoviesByCriterionUseCase
import com.claudiogalvaodev.moviemanager.usecases.movies.GetPersonDetailsUseCase
import com.claudiogalvaodev.moviemanager.utils.OrderByConstants
import com.claudiogalvaodev.moviemanager.utils.enums.FilterType
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.lang.Exception

class PeopleDetailsViewModel(
    val personId: Int,
    val leastOneMovieId: Int,
    private val getMovieDetailsUseCase: GetMovieDetailsUseCase,
    private val getMoviesByCriterionUseCase: GetMoviesByCriterionUseCase,
    private val getPersonDetailsUseCase: GetPersonDetailsUseCase,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
): ViewModel() {

    private val _personDetails = MutableStateFlow<PersonModel?>(null)
    val personDetails = _personDetails.asStateFlow()

    private val _movies = MutableStateFlow<List<MovieModel>>(mutableListOf())
    val movies = _movies.asStateFlow()

    private var isFinishMovies: Boolean = false
    var isMoviesLoading: Boolean = false
    var isFirstLoading: Boolean = false
    var getSecondPage: Boolean = false

    init {
        getPersonDetails()
        getMovies()
    }

    private fun getFilter(): List<FilterModel> {
        val filters: MutableList<FilterModel> = mutableListOf()
        filters.add(FilterModel(type = FilterType.SORT_BY, nameRes = null, currentValue = OrderByConstants.POPULARITY_DESC))
        filters.add(FilterModel(type = FilterType.PEOPLE, nameRes = null, currentValue = personId.toString()))
        return filters
    }

    fun getPersonDetails() = viewModelScope.launch(dispatcher) {
        val personDetailsResult = getPersonDetailsUseCase.invoke(personId)

        if(personDetailsResult.isSuccess) {
            personDetailsResult.getOrNull()?.let { person ->
                _personDetails.emit(person)
            }
        }
    }

    fun getMovies() = viewModelScope.launch(dispatcher) {
        isMoviesLoading = true
        val moviesList = mutableListOf<MovieModel>()
        val moviesResult = if (!isFinishMovies) {
            getMoviesByCriterionUseCase.invoke(getFilter(), isFirstLoading)
        } else Result.failure(Exception("There is no more movies to get"))

        if(isFirstLoading) {
            val movieDetailsResult = getMovieDetailsUseCase.invoke(leastOneMovieId)
            if(movieDetailsResult.isSuccess) {
                movieDetailsResult.getOrNull()?.let {
                    moviesList.addAll(listOf(it))
                }
            }
        }

        if(getSecondPage && _movies.value.isNotEmpty()) getSecondPage = false

        if(moviesResult.isSuccess) {
            moviesResult.getOrNull()?.let { movies ->
                if(movies.size < MAX_ITEMS_ON_REQUEST) isFinishMovies = true
                moviesList.addAll(_movies.value)
                moviesList.addAll(movies.filter { movie -> movie.id != leastOneMovieId })
                _movies.emit(moviesList)
            }
        }
        isMoviesLoading = false
        isFirstLoading = false
    }

    companion object {
        const val MAX_ITEMS_ON_REQUEST = 20
    }

}