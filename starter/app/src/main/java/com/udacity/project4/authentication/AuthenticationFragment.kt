package com.udacity.project4.authentication

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.firebase.ui.auth.AuthUI
import com.udacity.project4.R
import com.udacity.project4.databinding.FragmentAuthenticationBinding


//
// Created by  on 12/3/20.
//

class AuthenticationFragment : Fragment() {

    private val loginResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val directions =
                    AuthenticationFragmentDirections.actionAuthenticationFragmentToReminderActivity()
                findNavController().navigate(directions)
            } else {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.error_login_in),
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = DataBindingUtil.inflate<FragmentAuthenticationBinding>(
            LayoutInflater.from(container!!.context),
            R.layout.fragment_authentication,
            container,
            false
        )

        binding.btnLogin.setOnClickListener {
            launchSignInFlow()
        }

        return binding.root
    }

    private fun launchSignInFlow() {
        val providers = arrayListOf(AuthUI.IdpConfig.GoogleBuilder().build())

        loginResultLauncher.launch(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setLogo(R.drawable.map)
                .build()
        )
    }
}