package com.example.flixster

import android.os.Parcelable
import android.util.Log
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import org.json.JSONArray
import org.json.JSONException
import com.codepath.asynchttpclient.AsyncHttpClient as AsyncHttpClient1

@Parcelize
data class Movie(
    val movieId: Int,
    private val posterPath: String,
    val title: String,
    val overview: String,
    val voteAvg: Double,
    val releaseDate: String
): Parcelable {
    @IgnoredOnParcel
    val posterImageUrl = "https://image.tmdb.org/t/p/w342$posterPath"
    // allow to call methods from the Movie class w/t having to create a instance
    companion object {
        fun fromJsonArray(movieJsonArray: JSONArray): List<Movie> {
            val movies = mutableListOf<Movie>()
            for (i in 0 until movieJsonArray.length()) {
                val movieJson = movieJsonArray.getJSONObject(i)
                movies.add(
                    Movie(
                        movieJson.getInt("id"),
                        movieJson.getString("poster_path"),
                        movieJson.getString("title"),
                        movieJson.getString("overview"),
                        movieJson.getDouble("vote_average"),
                        movieJson.getString("release_date")
                    )
                )
            }
            return movies
        }
    }
}