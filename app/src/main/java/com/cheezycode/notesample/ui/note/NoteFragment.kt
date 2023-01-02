package com.cheezycode.notesample.ui.note

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.cheezycode.notesample.R
import com.cheezycode.notesample.databinding.FragmentNoteBinding
import com.cheezycode.notesample.models.NoteRequest
import com.cheezycode.notesample.models.NoteResponse
import com.cheezycode.notesample.utils.NetworkResult
import com.google.android.gms.ads.*
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

@AndroidEntryPoint
class NoteFragment : Fragment() {

    private var _binding: FragmentNoteBinding? = null
    private val binding get() = _binding!!
    private val noteViewModel by viewModels<NoteViewModel>()
    private var note: NoteResponse? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNoteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showBannerAds()
        setInitialData()
        bindHandlers()
        bindObservers()
    }

     private fun showIntersialAds() {
        MobileAds.initialize(view!!.context) {}
        var mInterstitialAd: InterstitialAd? = null
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(view!!.context,"ca-app-pub-9139048484944052/1936562926", adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                mInterstitialAd = null
            }
            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                if (!activity?.isFinishing!!) {
                    interstitialAd.show(activity!!)
                }
            }
        })

        mInterstitialAd?.fullScreenContentCallback = object: FullScreenContentCallback() {
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

        val handler = Handler()
        val runnable = Runnable {

            if (mInterstitialAd != null) {
                mInterstitialAd?.show(activity!!)
            } else {
                println("The interstitial ad wasn't ready yet.")
            }
        }
        handler.postDelayed(runnable, 5000) // 5000 milliseconds = 5 seconds

    }

    private fun showBannerAds() {
        MobileAds.initialize(view!!.context) {}
        val adView = view!!.findViewById(R.id.bannerAd) as AdView
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
    }

    private fun bindObservers() {
        noteViewModel.statusLiveData.observe(viewLifecycleOwner, Observer {
            when (it) {
                is NetworkResult.Success -> {
                    findNavController().popBackStack()
                }
                is NetworkResult.Error -> {

                }
                is NetworkResult.Loading -> {

                }
            }
        })
    }

    private fun bindHandlers() {
        binding.btnDelete.setOnClickListener {
            note?.let { noteViewModel.deleteNote(it.id) }
            showIntersialAds()
        }
        binding.apply {
            btnSubmit.setOnClickListener {
                val title = txtTitle.text.toString()
                val body = txtDescription.text.toString()
                val noteRequest = NoteRequest(title, body)
                if (note == null) {
                    noteViewModel.createNote(noteRequest)
                } else {
                    noteViewModel.updateNote(note!!.id, noteRequest)
                }
            }
        }
    }

    private fun setInitialData() {
        try {
            showIntersialAds()
        } catch (e: java.lang.NullPointerException) {
            println("Some exception occurred $e")
        }

        val jsonNote = arguments?.getString("note")
        if (jsonNote != null) {
            note = Gson().fromJson<NoteResponse>(jsonNote, NoteResponse::class.java)
            note?.let {
                binding.txtTitle.setText(it.title)
                binding.txtDescription.setText(it.body)
            }
        }
        else{
            binding.addEditText.text = resources.getString(R.string.add_note)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}