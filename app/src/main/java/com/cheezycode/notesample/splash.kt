package com.cheezycode.notesample

import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.cheezycode.notesample.databinding.FragmentRegisterBinding
import com.cheezycode.notesample.databinding.FragmentSplashBinding


class splash : Fragment() {

    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!

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
            findNavController().navigate(R.id.action_splash_to_registerFragment)
        }
        handler.postDelayed(runnable, 5000) // 5000 milliseconds = 5 seconds
        _binding =  FragmentSplashBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}