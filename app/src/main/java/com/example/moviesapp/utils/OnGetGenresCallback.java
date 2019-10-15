package com.example.moviesapp.utils;

import com.example.moviesapp.model.Genre;

import java.util.List;

public interface OnGetGenresCallback {
    void onSuccess(List<Genre>genres);

    void onFailure();


}
