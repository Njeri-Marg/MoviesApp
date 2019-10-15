package com.example.moviesapp.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.example.moviesapp.utils.OnGetGenresCallback;
import com.example.moviesapp.utils.OnGetMoviesCallback;
import com.example.moviesapp.utils.OnMoviesClickCallback;
import com.example.moviesapp.R;
import com.example.moviesapp.adapter.RecyclerViewMovieAdapter;
import com.example.moviesapp.databinding.ActivityMainBinding;
import com.example.moviesapp.model.Genre;
import com.example.moviesapp.model.Movie;
import com.example.moviesapp.repository.MoviesRepository;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding mBinding;
    MoviesRepository moviesRepository;
    RecyclerViewMovieAdapter movieAdapter;
    private List<Genre> movieGenres;
    private int currentPage = 1;
    private boolean isFetchingMovies;
    private String sortBy = MoviesRepository.MOST_POPULAR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        moviesRepository = MoviesRepository.getInstance();




        //init RecyclerView
        RecyclerView moviesList = mBinding.moviesList;
        GridLayoutManager manager = new GridLayoutManager(this,2);
        moviesList.setLayoutManager(manager);

        getGenres();

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.movies_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sort:
                showSortMenu();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showSortMenu() {
        PopupMenu sortMenu = new PopupMenu(this, findViewById(R.id.sort));
        sortMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                currentPage = 1;

                switch (item.getItemId()) {
                    case R.id.most_popular:
                        sortBy = MoviesRepository.MOST_POPULAR;
                        getMovies(currentPage);
                        return true;
                    case R.id.highest_rated:
                        sortBy = MoviesRepository.HIGHEST_RATED;
                       getMovies(currentPage);
                        return true;
                    default:
                        return false;
                }
            }
        });
        sortMenu.inflate(R.menu.sort_movies_menu);
        sortMenu.show();
    }
    private void getGenres() {
        moviesRepository.getGenres(new OnGetGenresCallback() {
            @Override
            public void onSuccess(List<Genre> genres) {
                movieGenres = genres;
                getMovies(currentPage);
            }

            @Override
            public void onFailure() {
                Toast.makeText(MainActivity.this, "An error occurred", Toast.LENGTH_SHORT).show();
            }
        });
    }
  /*  private void getMovies() {
        isFetchingMovies = true;
        moviesRepository.getMovies( sortBy, new OnGetMoviesCallback() {
            @Override
            public void onSuccess(int page, List<Movie> movies) {
                if (movieAdapter == null) {
                    movieAdapter = new RecyclerViewMovieAdapter(movies, movieGenres, callback);
                    mBinding.moviesList.setAdapter(movieAdapter);
                } else {
                    if (page == 1) {

                    }

                }
                currentPage = page;
                isFetchingMovies = false;
                menuTitle();
            }

            @Override
            public void onFailure() { }
        });
    }*/
  private void getMovies(int page) {
      isFetchingMovies = true;
      moviesRepository.getMovies(page, sortBy, new OnGetMoviesCallback() {
          @Override
          public void onSuccess(int page, List<Movie> movies) {
              if (movieAdapter == null) {
                  movieAdapter = new RecyclerViewMovieAdapter(movies, movieGenres, callback);
                  mBinding.moviesList.setAdapter(movieAdapter);
              } else {
                  if (page == 1) {
                      movieAdapter.clearMovies();
                  }
                  movieAdapter.appendMovies(movies);
              }
              currentPage = page;
              isFetchingMovies = false;

              menuTitle();
          }

          @Override
          public void onFailure() { }
      });
  }
  OnMoviesClickCallback callback = new OnMoviesClickCallback() {
        @Override
        public void onClick(Movie movie) {
            Intent intent = new Intent(MainActivity.this, MovieActivity.class);
            intent.putExtra(MovieActivity.MOVIE_ID, movie.getId());
            startActivity(intent);
        }
    };
    private void menuTitle() {
        switch (sortBy) {
            case MoviesRepository.MOST_POPULAR:
                setTitle(getString(R.string.popular));
                break;
            case MoviesRepository.HIGHEST_RATED:
                setTitle(getString(R.string.highest_rated));
                break;

        }
    }

}
