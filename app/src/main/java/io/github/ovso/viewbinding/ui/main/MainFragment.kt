package io.github.ovso.viewbinding.ui.main

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import io.github.ovso.viewbinding.R
import io.github.ovso.viewbinding.databinding.MainFragmentBinding

class MainFragment : Fragment(R.layout.main_fragment) {

    companion object {
        fun newInstance() = MainFragment()
    }

    private val binding by viewBinding(MainFragmentBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.message.setOnClickListener {
            Toast.makeText(requireContext(), "Fragment!!", Toast.LENGTH_SHORT).show()
        }
    }


}