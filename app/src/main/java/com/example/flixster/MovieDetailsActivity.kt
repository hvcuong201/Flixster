package com.example.flixster

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.RatingBar
import android.widget.TextView
import com.codepath.asynchttpclient.AsyncHttpClient
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import com.google.android.youtube.player.YouTubeBaseActivity
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayerView
import okhttp3.Headers
import org.json.JSONException

private const val YOUTUBE_API_KEY = "AIzaSyBMVLxsGnRacB2jdOiC94WSW-XtVRZ3M3g"
private const val TRAILER_URL = "https://api.themoviedb.org/3/movie/%d/videos?api_key=a07e22bc18f5cb106bfe4cc1f83ad8ed"
class MovieDetailsActivity : YouTubeBaseActivity() {
    private lateinit var tvTitle: TextView
    private lateinit var tvOverview: TextView
    private lateinit var rbVoteAverage: RatingBar
    private lateinit var tvReleaseDate: TextView
    private lateinit var ytPlayerView: YouTubePlayerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_details)

        tvTitle = findViewById(R.id.tvTitle)
        tvOverview = findViewById(R.id.tvOverview)
        rbVoteAverage = findViewById(R.id.rbVoteAverage)
        tvReleaseDate = findViewById(R.id.tvReleaseDate)
        ytPlayerView = findViewById(R.id.player)

        val movie = intent.getParcelableExtra<Movie>(MOVIE_EXTRA) as Movie
        Log.i("CUONG", movie.toString())

        tvTitle.text = movie.title
        tvOverview.text = movie.overview
        rbVoteAverage.rating = movie.voteAvg.toFloat()
        tvReleaseDate.text = "Release date: " + movie.releaseDate


        val client  = AsyncHttpClient()
        client.get(TRAILER_URL.format(movie.movieId), object:JsonHttpResponseHandler() {
            override fun onFailure(
                statusCode: Int,
                headers: Headers?,
                response: String?,
                throwable: Throwable?
            ) {
                Log.e(TAG, "onFailure $statusCode")
            }

            override fun onSuccess(statusCode: Int, headers: Headers?, json: JSON) {
                Log.i(TAG, "onSuccess: JSON data $json")
                try {
                    val movieVideoJsonArray = json.jsonObject.getJSONArray("results")
                    if (movieVideoJsonArray.length() == 0) {
                        Log.w(TAG, "No trailer available")
                        return
                    }
                    lateinit var youtubeKey: String
                    // get the latest official trailer from YouTube only.
                    for (i in 0 until movieVideoJsonArray.length()) {
                        val movieTrailerJson = movieVideoJsonArray.getJSONObject(i)
                        if (movieTrailerJson.getString("type").equals("Trailer") && movieTrailerJson.getString("site").equals("YouTube")) {
                            youtubeKey = movieTrailerJson.getString("key")
                            break
                        }
                    }
                    // found no trailer type video on site YouTube
                    if (youtubeKey.isNullOrBlank())
                        youtubeKey = movieVideoJsonArray.getJSONObject(0).getString("key")
                    initializeYouTube(youtubeKey)
                } catch (e: JSONException) {
                    Log.e(TAG, "Encountered JSON Exception $e")
                }
            }

        })
    }

    private fun initializeYouTube(youtubeKey: String) {
        ytPlayerView.initialize(YOUTUBE_API_KEY, object: YouTubePlayer.OnInitializedListener {
            override fun onInitializationSuccess(
                provider: YouTubePlayer.Provider?,
                player: YouTubePlayer?,
                p2: Boolean
            ) {
                Log.i(TAG, "onInitializationSuccess")
                player?.cueVideo(youtubeKey);
            }

            override fun onInitializationFailure(
                p0: YouTubePlayer.Provider?,
                p1: YouTubeInitializationResult?
            ) {
                Log.e(TAG, "onInitializationFailure")
            }

        })
    }
}