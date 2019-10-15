package com.example.moviesapp.repository;

import com.example.moviesapp.utils.AppConstants;
import com.example.moviesapp.response.MoviesResponse;
import com.example.moviesapp.utils.OnGetGenresCallback;
import com.example.moviesapp.utils.OnGetMovieCallback;
import com.example.moviesapp.utils.OnGetMoviesCallback;
import com.example.moviesapp.utils.OnGetTrailersCallback;
import com.example.moviesapp.utils.TMDBApi;
import com.example.moviesapp.response.TrailerResponse;
import com.example.moviesapp.model.Movie;
import com.example.moviesapp.response.GenresResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MoviesRepository {
    public static final String MOST_POPULAR = "most_popular";
    public static final String HIGHEST_RATED = "highest_rated";

    private static final String URL = "https://api.themoviedb.org/3/";
    private static final String LANGUAGE = "en-US";

    private static MoviesRepository repository;

    private TMDBApi api;

    private MoviesRepository(TMDBApi api){
        this.api = api;
    }
    public static MoviesRepository getInstance() {
        if (repository == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            repository = new MoviesRepository(retrofit.create(TMDBApi.class));
        }

        return repository;
    }
    public void getMovies(int page, String sortBy, final OnGetMoviesCallback callback) {
        Callback<MoviesResponse> call = new Callback<MoviesResponse>() {
            @Override
            public void onResponse(Call<MoviesResponse> call, Response<MoviesResponse> response) {
                if (response.isSuccessful()) {
                    MoviesResponse moviesResponse = response.body();
                    if (moviesResponse != null && moviesResponse.getMovies() != null) {
                        callback.onSuccess(moviesResponse.getPage(), moviesResponse.getMovies());
                    } else {
                        callback.onFailure();
                    }
                } else {
                    callback.onFailure();
                }
            }

            @Override
            public void onFailure(Call<MoviesResponse> call, Throwable t) {
                callback.onFailure();
            }
        };

        switch (sortBy) {

            case MOST_POPULAR:
                api.getPopularMovies(AppConstants.TMDB_API_KEY, LANGUAGE, page)
                        .enqueue(call);
                break;
            case HIGHEST_RATED:
            default:
                api.getTopRatedMovies(AppConstants.TMDB_API_KEY, LANGUAGE, page)
                        .enqueue(call);
                break;
        }
    }

    public void getGenres(final OnGetGenresCallback callback) {
        api.getGenres(AppConstants.TMDB_API_KEY, LANGUAGE)
                .enqueue(new Callback<GenresResponse>() {
                    @Override
                    public void onResponse(Call<GenresResponse> call, Response<GenresResponse> response) {
                        if (response.isSuccessful()) {
                            GenresResponse genresResponse = response.body();
                            if (genresResponse != null && genresResponse.getGenres() != null) {
                                callback.onSuccess(genresResponse.getGenres());
                            } else {
                                callback.onFailure();
                            }
                        } else {
                            callback.onFailure();
                        }
                    }

                    @Override
                    public void onFailure(Call<GenresResponse> call, Throwable t) {
                        callback.onFailure();
                    }
                });
    }

    public void getMovie(int movieId, final OnGetMovieCallback callback) {
        api.getMovie(movieId, AppConstants.TMDB_API_KEY, LANGUAGE)
                .enqueue(new Callback<Movie>() {
                    @Override
                    public void onResponse(Call<Movie> call, Response<Movie> response) {
                        if (response.isSuccessful()) {
                            Movie movie = response.body();
                            if (movie != null) {
                                callback.onSuccess(movie);
                            } else {
                                callback.onFailure();
                            }
                        } else {
                            callback.onFailure();
                        }
                    }

                    @Override
                    public void onFailure(Call<Movie> call, Throwable t) {
                        callback.onFailure();
                    }
                });
    }

    public void getTrailers(int movieId, final OnGetTrailersCallback callback) {
        api.getTrailers(movieId, AppConstants.TMDB_API_KEY, LANGUAGE)
                .enqueue(new Callback<TrailerResponse>() {
                    @Override
                    public void onResponse(Call<TrailerResponse> call, Response<TrailerResponse> response) {
                        if (response.isSuccessful()) {
                            TrailerResponse trailerResponse = response.body();
                            if (trailerResponse != null && trailerResponse.getTrailers() != null) {
                                callback.onSuccess(trailerResponse.getTrailers());
                            } else {
                                callback.onFailure();
                            }
                        } else {
                            callback.onFailure();
                        }
                    }

                    @Override
                    public void onFailure(Call<TrailerResponse> call, Throwable t) {
                        callback.onFailure();
                    }
                });
    }


}
