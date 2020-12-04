package com.udacity.project4.authentication

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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

    private val SIGN_IN_REQUEST_CODE = 7

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SIGN_IN_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                val directions =
                    AuthenticationFragmentDirections.actionAuthenticationFragmentToReminderActivity()
                findNavController().navigate(directions)
            } else
                Toast.makeText(
                    requireContext(),
                    getString(R.string.error_login_in),
                    Toast.LENGTH_LONG
                ).show()

        }
    }

    private fun launchSignInFlow() {
        val providers = arrayListOf(AuthUI.IdpConfig.GoogleBuilder().build())

        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build(),
            SIGN_IN_REQUEST_CODE
        )
    }
}