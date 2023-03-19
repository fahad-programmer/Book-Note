package com.cheezycode.notesample.ui.note



import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.text.Editable
import android.text.Html
import android.text.Spannable
import android.text.style.StyleSpan
import android.view.*
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.text.toHtml
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.cheezycode.notesample.R
import com.cheezycode.notesample.databinding.FragmentNoteBinding
import com.cheezycode.notesample.models.NoteRequest
import com.cheezycode.notesample.models.NoteResponse
import com.cheezycode.notesample.ui.note.textApi.*
import com.cheezycode.notesample.utils.Helper.Companion.hideKeyboard
import com.cheezycode.notesample.utils.NetworkResult
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class NoteFragment : Fragment() {

    private var _binding: FragmentNoteBinding? = null
    private val binding get() = _binding!!
    private val noteViewModel by viewModels<NoteViewModel>()
    private var note: NoteResponse? = null
    private lateinit var secondLayout: View
    private lateinit var placeholderView: FrameLayout
    private lateinit var boldButton: ImageView
    private lateinit var italicButton: ImageView
    private lateinit var underlineButton: ImageView
    private lateinit var strikeThroughBtn: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNoteBinding.inflate(inflater, container, false)
        return binding.root
    }


    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setInitialData()
        bindHandlers()
        bindObservers()


        val inflater = LayoutInflater.from(this.context)
        secondLayout =
            inflater.inflate(R.layout.fragment_notes_bottom_sheet, null)
        

        boldButton = secondLayout.findViewById(R.id.bold)
        underlineButton = secondLayout.findViewById(R.id.underline)
        italicButton = secondLayout.findViewById(R.id.italic)
        strikeThroughBtn = secondLayout.findViewById(R.id.strikethrough)

        // Add click listeners to the views


        // Add click listeners to the views
        boldButton.setOnClickListener {
            // Do something when the bold button is clicked
            val editText: EditText = binding.txtDescription
            val editable: Editable = editText.text
            applyBoldSpan(editable)
        }

        underlineButton.setOnClickListener {
            // Do something when the underline button is clicked
            val editText: EditText = binding.txtDescription
            val editable: Editable = editText.text
            applyUnderlineSpan(editable)

        }

        italicButton.setOnClickListener {
            // Do something when the italic button is clicked
            val editText: EditText = binding.txtDescription
            val editable: Editable = editText.text
            applyItalicSpan(editable)
        }

        strikeThroughBtn.setOnClickListener {
            // Do something when the strikeThrough button is clicked
            val editText: EditText = binding.txtDescription
            val editable: Editable = editText.text
            applyStrikethroughSpan(editable)
        }


        // Step 4: Initially set the visibility of the second layout to GONE
        secondLayout.visibility = View.GONE

        binding.optionLayout.setOnClickListener {
            // Step 6: Toggle the visibility of the second layout
            if (secondLayout.visibility == View.VISIBLE) {
                secondLayout.visibility = View.GONE
            } else {
                secondLayout.visibility = View.VISIBLE
                this.view?.let { it1 -> hideKeyboard(it1) }
            }
        }

        // Add the secondLayout to the placeholder view
        // Add the secondLayout to the placeholder view
        placeholderView = binding.placeholderView
        placeholderView.addView(secondLayout)
    }





    private fun bindObservers() {
        noteViewModel.statusLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is NetworkResult.Success -> {
                    findNavController().popBackStack()
                }
                is NetworkResult.Error -> {

                }
                is NetworkResult.Loading -> {

                }
            }
        }
    }

    private fun isInternetAvailable(): Boolean {
        val connectivityManager =
            activity?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
        return activeNetwork?.isConnected == true
    }


    // create a method to open the image picker


    private fun bindHandlers() {

      //        binding.btnDelete.setOnClickListener {
//            if (isInternetAvailable()) {
//                note?.let { noteViewModel.deleteNote(it.id) }
//                showIntersialAds()
//            } else  {
//                val customDialog = CustomDialog(this.context!!)
//                customDialog.window?.attributes?.windowAnimations = com.cheezycode.notesample.R.style.DialogAnimation
//                val window = customDialog.window
//                window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
//                customDialog.show()
//            }
//
//        }
        binding.apply {
            btnSubmit.setOnClickListener {
                if (isInternetAvailable()) {
                    val title = txtTitle.text.toString()
                    val body = txtDescription.text.toHtml()


                    val noteRequest = NoteRequest(title, body)
                    if (note == null) {
                        noteViewModel.createNote(noteRequest)
                    } else {
                        noteViewModel.updateNote(note!!.id, noteRequest)
                    }
                } else {
                    val customDialog = CustomDialog(context!!)
                    customDialog.window?.attributes?.windowAnimations = com.cheezycode.notesample.R.style.DialogAnimation
                    val window = customDialog.window
                    window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                    customDialog.show()
                }

            }
        }
    }

    private fun setInitialData() {
        try {
        } catch (e: java.lang.NullPointerException) {
            println("Some exception occurred $e")
        }

        val jsonNote = arguments?.getString("note")
        if (jsonNote != null) {
            note = Gson().fromJson<NoteResponse>(jsonNote, NoteResponse::class.java)
            note?.let {
                binding.txtTitle.setText(it.title)
                val body = Html.fromHtml(it.body)
                binding.txtDescription.setText(body)
            }
        }
        else{
            binding.addEditText.text = resources.getString(com.cheezycode.notesample.R.string.add_note)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}