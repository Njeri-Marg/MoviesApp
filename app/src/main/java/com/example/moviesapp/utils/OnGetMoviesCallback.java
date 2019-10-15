package com.example.moviesapp.utils;

import com.example.moviesapp.model.Movie;

import java.util.List;

public interface OnGetMoviesCallback {
    void onSuccess(int page, List<Movie> movies);

    void onFailure();
}
