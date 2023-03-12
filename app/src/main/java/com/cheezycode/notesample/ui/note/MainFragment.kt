package com.cheezycode.notesample.ui.note


import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SearchView
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.cheezycode.notesample.MainActivity
import com.cheezycode.notesample.R
import com.cheezycode.notesample.databinding.FragmentMainBinding
import com.cheezycode.notesample.models.NoteResponse
import com.cheezycode.notesample.utils.NetworkResult
import com.cheezycode.notesample.utils.TokenManager
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class MainFragment : Fragment() {

    @Inject
    lateinit var tokenManager: TokenManager

    lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
    private lateinit var appBar: MaterialToolbar

    private lateinit var navController: NavController
    private var selectedMenuItemId: Int = R.id.mainFrameLayout

    var mInterstitialAd: InterstitialAd? = null
    var sharedPreferences_darkMode: SharedPreferences? = null

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private val noteViewModel by viewModels<NoteViewModel>()

    private lateinit var adapter: NoteAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        adapter = NoteAdapter(::onNoteClicked)

        // Inflate the layout for this fragment
        // access token
        tokenManager = TokenManager(requireContext())

        setHasOptionsMenu(true) // enable fragment to receive onCreateOptionsMenu callback
        //Checking the internet connection before the creation of the main screen
        if (isInternetAvailable()) {
        } else {
            val customDialog = CustomDialog(this.context!!)
            customDialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
            val window = customDialog.window
            window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            customDialog.show()
        }

        setUpViews()
        darkModeApplier()
        return binding.root
    }


    fun darkModeApplier() {
        val sharedPreferencesDarkMode = requireActivity().getSharedPreferences("MODE", Context.MODE_PRIVATE)
        val isNightMode = sharedPreferencesDarkMode.getBoolean("night", false)

        if (isNightMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES) // enables dark mode
        }
    }


    fun isInternetAvailable(): Boolean {
        val connectivityManager = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkCapabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        return networkCapabilities != null &&
                (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                        networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))
    }

    private fun setUpViews() {
        setUpDrawerLayout()
    }



    private fun setUpDrawerLayout() {
        appBar = binding.appBar
        (activity as AppCompatActivity).setSupportActionBar(appBar)
        (activity as AppCompatActivity).supportActionBar?.title = ""
        actionBarDrawerToggle = ActionBarDrawerToggle(activity, binding.drawerLayout, R.string.open_nav, R.string.close_nav)
        actionBarDrawerToggle.syncState()


        //Adding listener to the button of the sidebar
        val navigationView: NavigationView = binding.navigationView
        navigationView.setCheckedItem(selectedMenuItemId)
        navigationView.setNavigationItemSelectedListener { menuItem ->
            //Handles navigation when view item clicked here
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    // Handle home button click
//                    Toast.makeText(context, "Home clicked", Toast.LENGTH_SHORT).show()
                }
                R.id.manage_accounts -> {
                    // Handle manage account button click
                    findNavController().navigate(R.id.action_mainFragment_to_manageAccount)
                }
                R.id.share -> {
                    // Highlight the selected item
                    val shareIntent = Intent(Intent.ACTION_SEND)
                    shareIntent.type = "text/plain"
                    shareIntent.putExtra(
                        Intent.EXTRA_TEXT,
                        "Wanted To Write Down Some Notes Download Book Note " + "https://play.google.com/store/apps/details?id=com.cheezycode.notesample&hl=en&gl=US"
                    )
                    startActivity(Intent.createChooser(shareIntent, "Share via"))
                    // Handle settings button click
                    Toast.makeText(context, "share", Toast.LENGTH_SHORT).show()
                }
                R.id.settings -> {
                    // Handle profile button click
                    findNavController().navigate(R.id.action_mainFragment_to_user_settings2)
                }
                R.id.logout -> {
                    //handling the logout function
                    tokenManager.deleteToken()

                    // replace the current fragment with the LoginFragment
                    val intent = Intent(context, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    context!!.startActivity(intent)
                }
                R.id.trash -> {
                    findNavController().navigate(R.id.action_mainFragment_to_trash2)
                }
                R.id.rateUs -> {
                    // Handle rateUs button click
                    try {
                        startActivity(
                            Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("market://details?id=" + "com.cheezycode.notesample")
                            )
                        )
                    } catch (e: ActivityNotFoundException) {
                        startActivity(
                            Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://play.google.com/store/apps/details?id=com.cheezycode.notesample")
                            )
                        )
                    }
                }

            }


            menuItem.isChecked = true
            // Close the drawer after handling the item click
            binding.drawerLayout.closeDrawer(GravityCompat.START)

            true
        }
        }




    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }




    private fun showIntersialAds() {
        MobileAds.initialize(view!!.context) {}
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            view!!.context,
            "ca-app-pub-9139048484944052/1936562926",
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    mInterstitialAd = null
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    mInterstitialAd = interstitialAd
                }
            })

        mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdClicked() {
                // Called when a click is recorded for an ad.
            }

            override fun onAdDismissedFullScreenContent() {
                // Called when ad is dismissed.
                mInterstitialAd = null
            }

            override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                // Called when ad fails to show.
                mInterstitialAd = null
            }

            override fun onAdImpression() {
                // Called when an impression is recorded for an ad.
            }

            override fun onAdShowedFullScreenContent() {
                // Called when ad is shown.
                mInterstitialAd = null
            }
        }
// Show an interstitial ad after a delay of 5 seconds
        try {
            val handler = Handler()
            val runnable = Runnable {
                mInterstitialAd?.let {
                    activity?.let {
                        if (it.isFinishing) {
                            return@let
                        }
                        if (it.isDestroyed) {
                            return@let
                        }
                        if (it.isChangingConfigurations) {
                            return@let
                        }
                        if (it.window == null) {
                            return@let
                        }
                        if (it.window!!.isFloating()) {
                            return@let
                        }
                        it.runOnUiThread {
                            if (it.isDestroyed || it.isFinishing) {
                                return@runOnUiThread
                            }
                            if (mInterstitialAd !== null) {
                                mInterstitialAd?.show(activity!!)
                            } else {
                                println("The interstitial ad wasn't ready yet.")
                            }
                        }
                    }
                }
            }
            handler.postDelayed(runnable, 5000) // 5000 milliseconds = 5 seconds
        } catch (e: Exception) {
            mInterstitialAd = null
            e.printStackTrace() // print the exception stack trace for debugging purposes
        }

    }



        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            if (isInternetAvailable()) {
                noteViewModel.getAllNotes()
            } else {
                val customDialog = CustomDialog(this.context!!)
                customDialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
                val window = customDialog.window
                window?.setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                customDialog.show()
            }
            try {
                showIntersialAds()
            } catch (e: java.lang.NullPointerException) {
                println("Some exception occurred $e")
            }
            binding.noteList.layoutManager =
                StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            binding.noteList.adapter = adapter
            binding.addNote.setOnClickListener {
                findNavController().navigate(R.id.action_mainFragment_to_noteFragment)
            }
            bindHandlers()
            bindObservers()

        }


        private fun bindObservers() {
            noteViewModel.notesLiveData.observe(viewLifecycleOwner) {
                binding.progressBar.isVisible = false
                when (it) {
                    is NetworkResult.Success -> {
                        adapter.submitList(it.data)
                    }
                    is NetworkResult.Error -> {
                        Toast.makeText(requireContext(), it.message.toString(), Toast.LENGTH_SHORT)
                            .show()
                    }
                    is NetworkResult.Loading -> {
                        binding.progressBar.isVisible = true
                    }
                }
            }
        }

        private fun onNoteClicked(noteResponse: NoteResponse) {
            val bundle = Bundle()
            bundle.putString("note", Gson().toJson(noteResponse))
            findNavController().navigate(R.id.action_mainFragment_to_noteFragment, bundle)
        }

        override fun onDestroyView() {
            super.onDestroyView()
            _binding = null
        }

        private fun bindHandlers() {


            val searchBox: SearchView = binding.searchView
            searchBox.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    if (isInternetAvailable()) {
                        if (query != null) {
                            noteViewModel.searchNote(query)
                        }
                    } else {
                        val customDialog = CustomDialog(context!!)
                        customDialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
                        val window = customDialog.window
                        window?.setLayout(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                        )
                        customDialog.show()
                    }
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    return false
                }

            })
        }


    override fun onDestroy() {
        super.onDestroy()
        mInterstitialAd = null
    }


}