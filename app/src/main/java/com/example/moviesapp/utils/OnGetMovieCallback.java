package com.example.moviesapp.utils;


import com.example.moviesapp.model.Movie;

public interface OnGetMovieCallback {
    void onSuccess(Movie movie);

    void onFailure();

}
