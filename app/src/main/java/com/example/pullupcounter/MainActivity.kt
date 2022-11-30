package com.example.pullupcounter

import android.annotation.SuppressLint
import android.app.ProgressDialog.show
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import com.example.pullupcounter.databinding.ActivityMainBinding
import com.example.pullupcounter.dialog.StartDialogFragment
import android.content.Context
import android.content.DialogInterface
import android.content.res.Resources
import android.graphics.Color
import android.graphics.Color.red
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowInsetsController
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.FragmentResultListener
import com.google.android.material.snackbar.Snackbar
import android.widget.ImageView
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.core.view.setPadding
import com.google.android.material.internal.ViewUtils.dpToPx
import kotlin.NumberFormatException


class MainActivity : AppCompatActivity() {

    // initialize binding variable
    private lateinit var binding: ActivityMainBinding
    private var stateCount = 1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        //set click listeners with a feature WITH.
        with(binding) {

            informationButton.setOnClickListener {
                showStartDialogFragment()
            }

            switchForStep.setOnClickListener() {
                makeInputStepEditable()
            }

            buttonStaircaseUp.setOnClickListener {
                scaleUpButton()
                showSimpleSnackBar(R.string.snackbar_text_up, R.string.snackbar_text_button)
                stateCount = 0
            }

            buttonStaircaseUpAndDown.setOnClickListener {
                scaleUpAndDownButton()
                showSimpleSnackBar(
                    R.string.snackbar_text_up_and_down,
                    R.string.snackbar_text_button
                )
                stateCount = 1
            }

            buttonStaircaseDown.setOnClickListener {
                scaleDownButton()
                showSimpleSnackBar(R.string.snackbar_text_down, R.string.snackbar_text_button)
                stateCount = 2
            }

            calculateButton.setOnClickListener { view -> countPullUps(view) }
        }

        setupStartDialogListener()

    }

    //count a sum of pullups with a given values. View parameter is for lambda fun to hide keyboard
    private fun countPullUps(view: View) {

        //setting values and catching NumberFormatException.
        var resultOfCounting = 0
        var inputStart = try {
            binding.editInputStart.text.toString().toInt()
        } catch (e: NumberFormatException) {
            Snackbar.make(binding.constraintLayoutMain, R.string.number_format_exception, 20000)
                .setAction(R.string.snackbar_text_button) {}
                .show()
            hideKeyboard(view)
            return
        }

        val inputEnd = try {
            binding.editInputEnd.text.toString().toInt()
        } catch (e: NumberFormatException) {
            Snackbar.make(binding.constraintLayoutMain, R.string.number_format_exception, 20000)
                .setAction(R.string.snackbar_text_button) {}
                .show()
            hideKeyboard(view)
            return
        }

        //catching a mistake when start number>end number. Ask user to put proper numbers
        if (inputStart > inputEnd) {
            Snackbar.make(binding.constraintLayoutMain, R.string.wrong_values_exception, 20000)
                .setAction(R.string.snackbar_text_button) {}
                .show()
            hideKeyboard(view)
            return
        }

        //Two cycles to finally count the sum of repetitions. First with STEP value, second - without
        //Try and catch for editInputStep exception
        when (binding.editInputStep.isEnabled) {
            true -> {
                val inputStep = try {
                    binding.editInputStep.text.toString().toInt()
                } catch (e: NumberFormatException) {
                    Snackbar.make(
                        binding.constraintLayoutMain,
                        R.string.number_format_exception,
                        20000
                    )
                        .setAction(R.string.snackbar_text_button) {}
                        .show()
                    hideKeyboard(view)
                    return
                }

                //check for mistake if STEP value is bigger than END value
                if (inputStep >= inputEnd || inputStep > (inputEnd - inputStart)) {
                    Snackbar.make(binding.constraintLayoutMain, R.string.wrong_step_value, 20000)
                        .setAction(R.string.snackbar_text_button) {}
                        .show()
                    hideKeyboard(view)
                    return
                }

                //First cycle counts with STEP value
                while (inputStart <= inputEnd) {
                    resultOfCounting += inputStart
                    inputStart += inputStep
                }

            }
            //Second cycle counts without STEP value
            else -> {
                while (inputStart <= inputEnd) {
                    resultOfCounting += inputStart
                    inputStart++
                }
            }
        }

        //show text int result text view
        binding.result.text = when (stateCount) {
            0 -> resultOfCounting.toString()
            1 -> (resultOfCounting * 2).toString()
            else -> resultOfCounting.toString()
        }

    }

    //show Snackbar with two string res properties
    private fun showSimpleSnackBar(textToShow: Int, textButton: Int) {
        Snackbar.make(binding.constraintLayoutMain, textToShow, 4000)
            .setAction(textButton) {/*можно вставить логику*/ }
            .setBackgroundTint(Color.parseColor("#000a12"))
            .setTextColor(Color.parseColor("#fafafa"))
            .setActionTextColor(Color.parseColor("#fafafa"))
            .show()
    }

    //make input_step Enabled/Disabled via switch button and show a Toast
    private fun makeInputStepEditable() {

        if (binding.switchForStep.isChecked) {
            binding.inputStep.isEnabled = true
            showToast(R.string.notification_step_on)
        } else {
            binding.inputStep.isEnabled = false
            showToast(R.string.notification_step_off)
        }
    }

    //make input_step Enabled/Disabled via dialog button and show a Toast
    private fun turnStepOn() {
        when (binding.switchForStep.isChecked) {
            true -> showToast(R.string.notification_step_on)
            else -> {
                binding.inputStep.isEnabled = true
                binding.switchForStep.isChecked = true
                showToast(R.string.notification_step_off)
            }
        }
    }

    //make input_step Enabled/Disabled via dialog button and show a Toast
    private fun turnStepOff() {
        when (binding.switchForStep.isChecked) {
            false -> showToast(R.string.notification_step_off)
            else -> {
                binding.inputStep.isEnabled = false
                binding.switchForStep.isChecked = false
                showToast(R.string.notification_step_off)
            }
        }
    }

    //function to show DialogFragment
    private fun showStartDialogFragment() {
        val dialogFragment = StartDialogFragment()
        dialogFragment.show(supportFragmentManager, StartDialogFragment.TAG)

    }

    //function to listen result of the dialog
    private fun setupStartDialogListener() {
        supportFragmentManager.setFragmentResultListener(
            StartDialogFragment.REQUEST_KEY,
            this,
            FragmentResultListener { _, result ->
                val which: Int = result.getInt(StartDialogFragment.KEY_RESPONSE)
                when (which) {
                    DialogInterface.BUTTON_POSITIVE -> turnStepOn()
                    DialogInterface.BUTTON_NEGATIVE -> turnStepOff()
                    DialogInterface.BUTTON_NEUTRAL -> {
                        showToast(R.string.neutral_button)
                    }
                }
            })
    }

    //converting dp to px to set it in code. Layouts use dp but code needs px.
    private fun convertDpToPx(dp: Int) = dp * Resources.getSystem().displayMetrics.density

    //scale up button_staircase_up and scale down other 2 buttons
    private fun scaleUpButton() {

        //use convertDpToPx() to make a proper value for setPadding
        val scaleUp = convertDpToPx(10).toInt()
        val scaleDown = convertDpToPx(25).toInt()

        //setting padding. One button up, two down
        with(binding) {
            buttonStaircaseUp.setPadding(scaleUp)
            buttonStaircaseUpAndDown.setPadding(scaleDown)
            buttonStaircaseDown.setPadding(scaleDown)
        }

    }

    //scale up button_staircase_up_and_down and scale down other 2 buttons
    private fun scaleUpAndDownButton() {

        //use convertDpToPx() to make a proper value for setPadding
        val scaleUp = convertDpToPx(10).toInt()
        val scaleDown = convertDpToPx(25).toInt()

        //setting padding. One button up, two down
        with(binding) {
            buttonStaircaseUpAndDown.setPadding(scaleUp)
            buttonStaircaseUp.setPadding(scaleDown)
            buttonStaircaseDown.setPadding(scaleDown)
        }

    }

    //scale up button_staircase_down and scale down other 2 buttons
    private fun scaleDownButton() {

        //use convertDpToPx() to make a proper value for setPadding
        val scaleUp = convertDpToPx(10).toInt()
        val scaleDown = convertDpToPx(25).toInt()

        //setting padding. One button up, two down
        with(binding) {
            buttonStaircaseDown.setPadding(scaleUp)
            buttonStaircaseUpAndDown.setPadding(scaleDown)
            buttonStaircaseUp.setPadding(scaleDown)
        }

    }

    //hide keyboard when Snackbar is shown. PS: view-parameter is a current Edit text shown
    private fun hideKeyboard(view: View) {
        val inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

}