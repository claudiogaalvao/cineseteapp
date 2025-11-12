package com.claudiogalvaodev.moviemanager.ui.peopledetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import coil3.compose.AsyncImage
import com.claudiogalvaodev.moviemanager.R
import com.claudiogalvaodev.moviemanager.ui.components.PosterList
import com.claudiogalvaodev.moviemanager.ui.model.MovieModel
import com.claudiogalvaodev.moviemanager.ui.model.PersonModel
import com.claudiogalvaodev.moviemanager.ui.moviedetails.MovieDetailsActivity
import com.claudiogalvaodev.moviemanager.utils.format.FormatUtils
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf

class PeopleDetailsFragment : Fragment() {
    private lateinit var viewModel: PeopleDetailsViewModel

    private val args: PeopleDetailsFragmentArgs by navArgs()

    private val personId by lazy {
        args.personId
    }
    private val leastOneMovieId by lazy {
        args.leastOneMovieId
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = getViewModel { parametersOf(personId, leastOneMovieId) }

        return ComposeView(requireContext()).apply {
            setContent {
                val personDetails by viewModel.personDetails.collectAsState()
                val movies by viewModel.movies.collectAsState()
                personDetails?.let { person ->
                    PersonDetailsScreen(
                        person = person,
                        movies = movies
                    )
                }
            }
        }
    }

    private fun goToMovieDetails(movieId: Int) {
        context?.let {
            startActivity(MovieDetailsActivity.newInstance(it, movieId, ""))
        }
    }
}

@Composable
fun PersonDetailsScreen(
    person: PersonModel,
    movies: List<MovieModel>
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement
            .spacedBy(8.dp)
    ) {
        PersonDetailsHeader(
            name = person.name,
            role = person.knownForDepartment,
            birthday = person.birthday,
            birthplace = person.placeOfBirth,
            photoUrl = person.getProfileImageUrl()
        )
        Biography(person.biography)

        Text(
            text = stringResource(R.string.movies_with_label),
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        PosterList(
            imagesUrl = movies.map { it.getPosterUrl() }
        )
    }
}

@Composable
fun PersonDetailsHeader(
    name: String,
    role: String,
    birthday: String?,
    birthplace: String?,
    photoUrl: String
) {
    val formattedBirthday = FormatUtils
        .dateFromAmericanFormatToDateWithMonthName(birthday ?: "") ?: "N/A"
    val age = FormatUtils.dateFromAmericanFormatToAge(birthday ?: "") ?: "N/A"
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        AsyncImage(
            modifier = Modifier
                .height(180.dp)
                .width(120.dp)
                .clip(RoundedCornerShape(8.dp)),
            model = photoUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = name,
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = role,
                color = Color.LightGray
            )
            Text(
                text = stringResource(R.string.birthday_with_age, formattedBirthday, age),
                color = Color.LightGray
            )
            Text(
                text = birthplace ?: "N/A",
                color = Color.LightGray
            )
        }
    }
}

@Composable
fun ColumnScope.Biography(
    biography: String
) {
    Text(
        text = stringResource(R.string.biography_label),
        fontSize = 18.sp,
        color = Color.White,
        fontWeight = FontWeight.Bold
    )
    Text(
        text = biography,
        color = Color.White,
        maxLines = 4,
        overflow = TextOverflow.Ellipsis
    )
}