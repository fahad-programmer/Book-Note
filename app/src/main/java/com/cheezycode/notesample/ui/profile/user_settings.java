package com.cheezycode.notesample.ui.profile;

import static androidx.navigation.fragment.FragmentKt.findNavController;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cheezycode.notesample.MainActivity;
import com.cheezycode.notesample.R;
import com.cheezycode.notesample.models.NoteResponse;
import com.cheezycode.notesample.models.UserProfile;
import com.cheezycode.notesample.ui.note.MainFragment;
import com.cheezycode.notesample.utils.TokenManager;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.Objects;

import javax.inject.Inject;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class user_settings extends Fragment {

    @Inject
    TokenManager tokenManager;

    SwitchCompat Dark_switch_button;
    SwitchCompat Notification_switch_button;
    SwitchCompat Private_account_switch_button;
    boolean notification_allow;
    boolean private_account;
    SharedPreferences sharedPreferences_darkMode;
    SharedPreferences sharedPreferences_notification;
    SharedPreferences sharedPreferences_account;
    SharedPreferences.Editor editor;
    ActionBarDrawerToggle actionBarDrawerToggle;
    MaterialToolbar app_bar;
    ImageView aboutPage;
    ImageView faqPage;
    ImageView help_support;
    TextView user_email;
    TextView user_username;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        tokenManager = new TokenManager(requireContext());
        return inflater.inflate(R.layout.fragment_user_settings, container, false);
    }

    //After the view is created
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Call methods to set up the views and handle user settings
        user_email = requireActivity().findViewById(R.id.user_name);
        user_username = requireActivity().findViewById(R.id.user_email);
        getUserProfile();
        linksSetUp();
        setUpViews();
        darkModeApplier();
        NotificationSwitchButtonHandler();
        PrivateAccountButtonHandler();
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
                    findNavController(this).navigate(R.id.action_user_settings_to_mainFragment);
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
                    findNavController(this).navigate(R.id.action_user_settings_to_manageAccount);
                case R.id.settings:
                    // Highlight the selected item
                    break;
                case R.id.rateUs:
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + "com.cheezycode.notesample")));
                    } catch (ActivityNotFoundException e) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.cheezycode.notesample")));
                    }
                case R.id.trash:
                    findNavController(this).navigate(R.id.action_user_settings_to_trash2);
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }

    public void linksSetUp() {
        aboutPage = requireActivity().findViewById(R.id.aboutPage);
        aboutPage.setOnClickListener(view -> {
            findNavController(this).navigate(R.id.action_user_settings_to_aboutFragment);
        });
        faqPage = requireActivity().findViewById(R.id.faq_image);
        faqPage.setOnClickListener(view -> {
            findNavController(this).navigate(R.id.action_user_settings_to_faq2);
        });
        help_support = requireActivity().findViewById(R.id.help_support_btn);
        help_support.setOnClickListener(view -> {
            findNavController(this).navigate(R.id.action_user_settings_to_help_support2);
        });
    }

    public void getUserProfile() {
        String url = "https://unexpectedprogrammer.pythonanywhere.com/api/user-profile";
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Token " + tokenManager.getToken())
                .build();
        client.newCall(request).enqueue(new okhttp3.Callback() {


            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = Objects.requireNonNull(response.body()).string();
                    Gson gson = new Gson();
                    UserProfile userProfile = gson.fromJson(responseBody, UserProfile.class);

                    // Update the user email view with the email returned from the API
                    requireActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            user_email.setText(userProfile.getEmail());
                            user_username.setText(userProfile.getUsername());
                        }
                    });
                } else {
                    // Handle error response
                }
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                // Handle network error
            }
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


    public void darkModeApplier() {
        Dark_switch_button = requireActivity().findViewById(R.id.nightModeSwitch);
        sharedPreferences_darkMode = requireActivity().getSharedPreferences("MODE", Context.MODE_PRIVATE);
        boolean night_mode = sharedPreferences_darkMode.getBoolean("night", false);
        if (night_mode) {
            Dark_switch_button.setChecked(true);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES); // enables dark mode
        }
        Dark_switch_button.setOnCheckedChangeListener((buttonView, isChecked) -> {
            editor = sharedPreferences_darkMode.edit();
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                editor.putBoolean("night", true);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO); // disables dark mode
                editor.putBoolean("night", false);
            }
            editor.apply();
        });
    }

    public void NotificationSwitchButtonHandler() {

        //checking the mode value from the sharedPreferences
        sharedPreferences_notification = requireActivity().getSharedPreferences("Notification_Setting", Context.MODE_PRIVATE);
        notification_allow = sharedPreferences_notification.getBoolean("Notification", false); //notification mode is default

        //Assign the button of switch
        Notification_switch_button = requireActivity().findViewById(R.id.notificationbtn);

        if (notification_allow) {
            Notification_switch_button.setChecked(true);
        }

        //Got the switch Button
        Notification_switch_button.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                editor = sharedPreferences_notification.edit();
                Toast.makeText(getActivity(), "Notifications Turned On", Toast.LENGTH_SHORT).show();
                editor.putBoolean("Notification", true);
            } else {
                editor = sharedPreferences_notification.edit();
                Toast.makeText(getActivity(), "Notifications Turned Off", Toast.LENGTH_SHORT).show();
                editor.putBoolean("Notification", false);
            }
            editor.apply();
        });

    }

    public void PrivateAccountButtonHandler() {

        //checking the mode value from the sharedPreferences
        sharedPreferences_account = requireActivity().getSharedPreferences("Account_Type", Context.MODE_PRIVATE);
        //Private account mode is default
        private_account = sharedPreferences_account.getBoolean("PrivateAccount", false);

        //Assign the button of switch
        Private_account_switch_button = requireActivity().findViewById(R.id.private_account);

        if (private_account) {
            Private_account_switch_button.setChecked(true);
        }

        //Got the switch Button
        Private_account_switch_button.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                editor = sharedPreferences_account.edit();
                Toast.makeText(getActivity(), "Account Made Private", Toast.LENGTH_SHORT).show();
                editor.putBoolean("PrivateAccount", true);

            } else {
                editor = sharedPreferences_account.edit();
                Toast.makeText(getActivity(), "Account Made Public", Toast.LENGTH_SHORT).show();
                editor.putBoolean("PrivateAccount", false);

            }
            editor.apply();
        });

    }




}