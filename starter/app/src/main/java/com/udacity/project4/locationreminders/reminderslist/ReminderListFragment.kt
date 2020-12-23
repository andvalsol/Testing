package com.udacity.project4.locationreminders.reminderslist

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.app.ActivityCompat.finishAffinity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.Navigator
import com.firebase.ui.auth.AuthUI
import com.udacity.project4.R
import com.udacity.project4.authentication.AuthenticationActivity
import com.udacity.project4.base.BaseFragment
import com.udacity.project4.base.NavigationCommand
import com.udacity.project4.databinding.FragmentRemindersBinding
import com.udacity.project4.utils.setDisplayHomeAsUpEnabled
import com.udacity.project4.utils.setTitle
import com.udacity.project4.utils.setup
import org.koin.androidx.viewmodel.ext.android.viewModel

class ReminderListFragment : BaseFragment() {
    //use Koin to retrieve the ViewModel instance
    override val _viewModel: RemindersListViewModel by viewModel()
    private lateinit var binding: FragmentRemindersBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_reminders, container, false
            )
        binding.viewModel = _viewModel

        addObservers()

        setHasOptionsMenu(true)
        setDisplayHomeAsUpEnabled(false)
        setTitle(getString(R.string.app_name))

        binding.refreshLayout.setOnRefreshListener { _viewModel.loadReminders() }

        return binding.root
    }

    private fun addObservers() {
        with(_viewModel) {
            authenticationState.observe(viewLifecycleOwner, Observer {
                // Log the user out
                logOutUser(requireContext())
            })

            showNoData.observe(viewLifecycleOwner, Observer {
                showToast(requireContext(), getString(R.string.no_reminders_saved))
            })

            showErrorMessage.observe(viewLifecycleOwner, Observer {
                showToast(requireContext(), getString(R.string.error_general))
            })
        }
    }

    private fun showToast(context: Context, messageToShow: String, displayTime: Int = Toast.LENGTH_LONG) {
        Toast.makeText(context, messageToShow, displayTime).show()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = this
        setupRecyclerView()

        binding.addReminderFAB.setOnClickListener {
            navigateToAddReminder()
        }
    }

    override fun onStart() {
        super.onStart()
        //load the reminders list on the ui. NOTE: Adding this here can cause duplication
        _viewModel.loadReminders()
    }

    private fun logOutUser(context: Context) {
        AuthUI.getInstance()
            .signOut(context)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    startActivity(Intent(requireContext(), AuthenticationActivity::class.java))

                    // Flush the back stack
                    finishAffinity(requireActivity())
                } else
                    showToast(requireContext(), getString(R.string.error_logging_out))
        }
    }

    private fun navigateToAddReminder() {
        // use the navigationCommand live data to navigate between the fragments
        _viewModel.navigationCommand.postValue(
            NavigationCommand.To(
                ReminderListFragmentDirections.toSaveReminder()
            )
        )
    }

    private fun setupRecyclerView() {
        val adapter = RemindersListAdapter {
            // Show a Toast message when the reminder is clicked
            Toast.makeText(requireContext(), getString(R.string.remember_to) + it.title, Toast.LENGTH_LONG).show()
        }

//        setup the recycler view using the extension function
        binding.reminderssRecyclerView.setup(adapter)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logout -> logOutUser(requireContext())
        }
        return super.onOptionsItemSelected(item)

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
//        display logout as menu item
        inflater.inflate(R.menu.main_menu, menu)
    }
}
