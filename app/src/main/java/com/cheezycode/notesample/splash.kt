package com.cheezycode.notesample

import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController


class splash : Fragment() {




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val handler = Handler()
        val runnable = Runnable {
            // Code to be executed after 5 seconds
            findNavController().navigate(R.id.registerFragment)
        }
        handler.postDelayed(runnable, 5000) // 5000 milliseconds = 5 seconds
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }


}