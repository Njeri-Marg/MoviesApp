package com.example.moviesapp.utils;

import com.example.moviesapp.model.Trailer;

import java.util.List;

public interface OnGetTrailersCallback {
    void onSuccess (List<Trailer>trailers);

    void onFailure();

}
