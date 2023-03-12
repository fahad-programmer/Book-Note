package com.cheezycode.notesample.ui.profile;

import static androidx.navigation.fragment.FragmentKt.findNavController;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cheezycode.notesample.MainActivity;
import com.cheezycode.notesample.R;
import com.cheezycode.notesample.models.NoteResponse;
import com.cheezycode.notesample.ui.note.NoteAdapter;
import com.cheezycode.notesample.utils.TokenManager;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

import javax.inject.Inject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class trash extends Fragment {

    @Inject
    TokenManager tokenManager;
    String token;

    private RecyclerView recyclerView;
    private TrashNoteAdapter noteAdapter;

    private SpinKitView progressBar;
    ActionBarDrawerToggle actionBarDrawerToggle;
    MaterialToolbar app_bar;





    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // access token
        tokenManager = new TokenManager(requireContext());
        View view = inflater.inflate(R.layout.fragment_trash, container, false);

        progressBar = view.findViewById(R.id.progress_bar);


        recyclerView = view.findViewById(R.id.trash_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        noteAdapter = new TrashNoteAdapter(getContext(),note -> {
            new Handler().postDelayed(this::getTrashNotes, 0); // Delayed for 3 seconds
        });
        recyclerView.setAdapter(noteAdapter);
        getTrashNotes();
        return view;

    }

    //After the view is created
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Call methods to set up the views and handle user settings
        setUpViews();
    }

    private void setUpViews() {
        setUpDrawerLayout();
    }

    private void setUpDrawerLayout() {
        app_bar = requireView().findViewById(R.id.appBar);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(app_bar);
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setTitle("");
        DrawerLayout drawerLayout = requireView().findViewById(R.id.drawer_layout);

        actionBarDrawerToggle = new ActionBarDrawerToggle(requireActivity(), drawerLayout, app_bar, R.string.open_nav, R.string.close_nav){
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                requireActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                requireActivity().invalidateOptionsMenu();
            }
        };
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        NavigationView navigationView = requireView().findViewById(R.id.navigationView);
        navigationView.setCheckedItem(R.id.setting_layout);
        navigationView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.nav_home:
                    // Highlight the selected item
                    findNavController(this).navigate(R.id.action_trash2_to_mainFragment);
                    item.setChecked(true);
                    break;

                case R.id.logout:
                    // Handling the logout function
                    tokenManager.deleteToken();

                    // Replace the current fragment with the LoginFragment
                    Intent intent = new Intent(getContext(), MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    if (getContext() != null) {
                        getContext().startActivity(intent);
                    }
                    break;

                case R.id.share:
                    // Highlight the selected item
                    item.setChecked(true);
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_TEXT, "Wanted To Write Down Some Notes Download Book Note " + "https://play.google.com/store/apps/details?id=com.cheezycode.notesample&hl=en&gl=US");
                    startActivity(Intent.createChooser(shareIntent, "Share via"));
                    break;
                case R.id.manage_accounts:
                    findNavController(this).navigate(R.id.action_trash2_to_manageAccount);
                case R.id.settings:
                    // Highlight the selected item
                    findNavController(this).navigate(R.id.action_trash2_to_user_settings);
                    item.setChecked(true);
                    break;
                case R.id.rateUs:
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + "com.cheezycode.notesample")));
                    } catch (ActivityNotFoundException e) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.cheezycode.notesample")));
                    }
                case R.id.trash:
                    break;
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }




    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }




    @SuppressLint("StaticFieldLeak")
    public void getTrashNotes() {
        new AsyncTask<Void, Void, NoteResponse[]>() {


            @Override
            protected void onPreExecute() {
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            protected NoteResponse[] doInBackground(Void... voids) {
                OkHttpClient client = new OkHttpClient();

                // Replace the URL with the base URL of your Django project
                String url = "https://unexpectedprogrammer.pythonanywhere.com/api/trash";

                // Create a new HTTP request
                Request request = new Request.Builder()
                        .url(url)
                        .addHeader("Authorization", "Token " + tokenManager.getToken())
                        .build();

                try {
                    // Execute the request and get the response
                    Response response = client.newCall(request).execute();

                    // Parse the response JSON into a list of notes
                    Gson gson = new Gson();
                    return gson.fromJson(Objects.requireNonNull(response.body()).string(), NoteResponse[].class);

                } catch (IOException e) {
                    // Handle the exception
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(NoteResponse[] notes) {
                if (notes != null) {
                    // Do something with the notes (e.g., display them in a RecyclerView)
                    // Update the adapter with the retrieved notes data
                    noteAdapter.submitList(Arrays.asList(notes));
                    // Notify the adapter about the data change
                    noteAdapter.notifyDataSetChanged();
                }
                progressBar.setVisibility(View.GONE);
            }
        }.execute();
    }



}