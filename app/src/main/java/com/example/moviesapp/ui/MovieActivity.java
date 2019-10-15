package com.example.moviesapp.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.moviesapp.utils.OnGetGenresCallback;
import com.example.moviesapp.utils.OnGetMovieCallback;
import com.example.moviesapp.utils.OnGetTrailersCallback;
import com.example.moviesapp.R;
import com.example.moviesapp.databinding.ActivityMovieBinding;
import com.example.moviesapp.model.Genre;
import com.example.moviesapp.model.Movie;
import com.example.moviesapp.model.Trailer;
import com.example.moviesapp.repository.MoviesRepository;

import java.util.ArrayList;
import java.util.List;

public class MovieActivity extends AppCompatActivity {
    private static String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/w780";
    private static String YOUTUBE_VIDEO_URL = "http://www.youtube.com/watch?v=%s";
    private static String YOUTUBE_THUMBNAIL_URL = "http://img.youtube.com/vi/%s/0.jpg";

    ActivityMovieBinding mBinding;
    public static String MOVIE_ID = "movie_id";
    private MoviesRepository moviesRepository;
    private int movieId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_movie);

        movieId = getIntent().getIntExtra(MOVIE_ID, movieId);

        moviesRepository=MoviesRepository.getInstance();
        getMovie();




    }
    private void getMovie() {
        moviesRepository.getMovie(movieId, new OnGetMovieCallback() {
            @Override
            public void onSuccess(Movie movie) {
                mBinding.movieDetailsTitle.setText(movie.getTitle());
                mBinding.summaryLabel.setVisibility(View.VISIBLE);
                mBinding.movieDetailsOverview.setText(movie.getOverview());
                mBinding.movieDetailsRating.setVisibility(View.VISIBLE);
                mBinding.movieDetailsRating.setRating(movie.getRating() / 2);
                getGenres(movie);
                mBinding.movieDetailsReleaseDate.setText(movie.getReleaseDate());
                Glide.with(MovieActivity.this)
                        .load(IMAGE_BASE_URL + movie.getBackdrop())
                        .apply(RequestOptions.placeholderOf(R.color.colorPrimary))
                        .into(mBinding.movieDetailsBackdrop);
                getTrailers(movie);
            }

            @Override
            public void onFailure() {
                finish();
            }
        });
    }

    private void getGenres(final Movie movie) {
        moviesRepository.getGenres(new OnGetGenresCallback() {
            @Override
            public void onSuccess(List<Genre> genres) {
                if (movie.getGenres() != null) {
                    List<String> currentGenres = new ArrayList<>();
                    for (Genre genre : movie.getGenres()) {
                        currentGenres.add(genre.getName());
                    }
                    mBinding.movieDetailsGenres.setText(TextUtils.join(", ", currentGenres));
                }
            }

            @Override
            public void onFailure() {

            }
        });
    }

    private void getTrailers(Movie movie) {
        moviesRepository.getTrailers(movie.getId(), new OnGetTrailersCallback() {
            @Override
            public void onSuccess(List<Trailer> trailers) {
                mBinding.trailersLabel.setVisibility(View.VISIBLE);
                mBinding.movieTrailers.removeAllViews();
                for (final Trailer trailer : trailers) {
                    View parent = getLayoutInflater().inflate(R.layout.item_trailer,mBinding.movieTrailers, false);
                    ImageView thumbnail = parent.findViewById(R.id.thumbnail);
                    thumbnail.requestLayout();
                    thumbnail.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showTrailer(String.format(YOUTUBE_VIDEO_URL, trailer.getKey()));
                        }
                    });
                    if (!isFinishing()) {
                        Glide.with(MovieActivity.this)
                                .load(String.format(YOUTUBE_THUMBNAIL_URL, trailer.getKey()))
                                .apply(RequestOptions.placeholderOf(R.color.colorPrimary).centerCrop())
                                .into(thumbnail);
                    }
                    mBinding.movieTrailers.addView(parent);
                }
            }

            @Override
            public void onFailure() {
            }
        });
    }

    private void showTrailer(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }


}
