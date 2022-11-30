package com.example.pullupcounter.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Build.VERSION_CODES.P
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import com.example.pullupcounter.R
import kotlinx.coroutines.NonCancellable.cancel
import kotlinx.coroutines.NonCancellable.start

class StartDialogFragment: DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val listener = DialogInterface.OnClickListener{ _, which ->
            parentFragmentManager.setFragmentResult(REQUEST_KEY, bundleOf(KEY_RESPONSE to which))
        }

        return AlertDialog.Builder(requireContext())
            .setCancelable(true)
            //.setIcon(R.drawable.ic_dialog)
            .setTitle(R.string.dialog_title)
            .setMessage(R.string.dialog_information)
            .setPositiveButton(R.string.positive_button, listener)
            .setNegativeButton(R.string.negative_button, listener)
            .setNeutralButton(R.string.neutral_button, listener)
            .create()
    }

    companion object {
        const val TAG = "StartDialogFragment"
        const val REQUEST_KEY = "$TAG:defaultRequestKey"
        const val KEY_RESPONSE = "RESPONSE"
    }
}