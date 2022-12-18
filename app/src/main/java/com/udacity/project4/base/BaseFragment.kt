package com.udacity.project4.base

import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar

/**
 * Base Fragment to observe on the common LiveData objects
 */
abstract class BaseFragment : Fragment() {
    /**
     * Every fragment has to have an instance of a view model that extends from the BaseViewModel
     */
    abstract val reminderViewModel: BaseViewModel

    override fun onStart() {
        super.onStart()
        reminderViewModel.showErrorMessage.observe(this) {
            Toast.makeText(activity, it, Toast.LENGTH_LONG).show()
        }//end showErrorMessage observe()
        reminderViewModel.showToast.observe(this) {
            Toast.makeText(activity, it, Toast.LENGTH_LONG).show()
        }//end showToast observe()
        reminderViewModel.showSnackBar.observe(this) {
            Snackbar.make(this.requireView(), it, Snackbar.LENGTH_LONG).show()
        }//end showSnackBar observe()
        reminderViewModel.showSnackBarInt.observe(this) {
            Snackbar.make(this.requireView(), getString(it), Snackbar.LENGTH_LONG).show()
        }//end showSnackBarInt observe()

        reminderViewModel.navigationCommand.observe(this) { command ->
            when (command) {
                is NavigationCommand.To -> requireView().findNavController()
                    .navigate(command.directions)
                is NavigationCommand.Back -> findNavController().popBackStack()
                is NavigationCommand.BackTo -> findNavController().popBackStack(
                    command.destinationId,
                    false
                )
            }//end when()
        }//end navigationCommand observe()
    }//end onStart()
}//end abstract class