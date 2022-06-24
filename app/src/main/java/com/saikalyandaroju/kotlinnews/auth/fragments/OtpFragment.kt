package com.saikalyandaroju.kotlinnews.auth.fragments

import android.app.ProgressDialog
import android.content.SharedPreferences
import android.os.Bundle
import android.os.CountDownTimer
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.saikalyandaroju.kotlinnews.R
import com.saikalyandaroju.kotlinnews.auth.activities.AuthActivity
import com.saikalyandaroju.kotlinnews.utils.Constants
import dagger.hilt.android.AndroidEntryPoint
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.fragment_otp.*
import kotlinx.android.synthetic.main.fragment_otp.view.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject


@AndroidEntryPoint
class OtpFragment : Fragment() {

    val args: OtpFragmentArgs by navArgs()


    @Inject
    lateinit var sharedPreferences: SharedPreferences

    lateinit var editor: SharedPreferences.Editor

    private var mCounterDown: CountDownTimer? = null
    private var timeLeft: Long = -1
    private lateinit var progressDialog: ProgressDialog


    private lateinit var phoneAuthProvider: PhoneAuthProvider
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    private var mresendToken: PhoneAuthProvider.ForceResendingToken? = null
    private lateinit var verificationId: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_otp, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        editor = sharedPreferences.edit()
        val number = args.number
        uiUpdate(number)
        setUpListeners(number)
    }

    private fun setUpListeners(number: String) {
        verificationBtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                progressDialog.show()
                val otp: String = sentcodeEt.text.toString()
                if (otp.length == 6) {


                    val credential =
                        PhoneAuthProvider.getCredential(verificationId, otp)
                    FirebaseAuth.getInstance().signInWithCredential(credential)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                progressDialog.dismiss()
                                editor.putString(Constants.U_NUMBER, number).commit()
                                editor.putBoolean(Constants.OTP_STEP, true).commit()
                                findNavController().navigate(R.id.action_otpFragment_to_signupFragment)
                                Toasty.success(
                                    requireContext(),
                                    "Success",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                progressDialog.dismiss()
                                Toasty.info(
                                    requireContext(),
                                    "Failed to Verify",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                } else {
                    progressDialog.dismiss()
                    Toasty.warning(requireContext(), "otp should be a 6 digit number", Toast.LENGTH_SHORT)
                        .show()
                }
            }

        })
        sentcodeEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val code = s.toString()
                if (code.length == 6) {
                    verificationBtn.isEnabled = true
                    resendBtn.isEnabled = false
                }

            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })

        resendBtn.setOnClickListener(View.OnClickListener {
            showTimer(60000)
            resendBtn.isEnabled = false
            sentcodeEt.text.clear()
            // sendOtp(number)
            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                number, // Phone number to verify
                60, // Timeout duration
                TimeUnit.SECONDS, // Unit of timeout
                this.activity as AuthActivity, // Activity (for callback binding)
                callbacks, // OnVerificationStateChangedCallbacks
                mresendToken
            )
        })
    }

    private fun uiUpdate(number: String) {

        verifyTv.append("Verify " + number)

        val textnew: StringBuilder = StringBuilder()
        textnew.append("Waiting to automatically detect an SMS sent to")
            .append(" " + number + " ").append("Wrong Number ?")

        waitingTv.text = textnew
        highLightText(waitingTv.text.toString())
        showTimer(60000)

        progressDialog = createDialog("Sending a verification code", false)

        sendOtp(number)
        resendBtn.isVisible = false

    }

    private fun createDialog(s: String, b: Boolean): ProgressDialog {
        return ProgressDialog(context).apply {
            setCancelable(b)
            setCanceledOnTouchOutside(false)
            setMessage(s)
        }

    }

    private fun sendOtp(number: String) {
        progressDialog.show()
        phoneAuthProvider = PhoneAuthProvider.getInstance()
        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onCodeSent(
                s: String,
                resendingToken: PhoneAuthProvider.ForceResendingToken
            ) {
                super.onCodeSent(s, resendingToken)
                verificationId = s
                mresendToken = resendingToken
                progressDialog.dismiss();
                Log.i("info", "codesent");

            }

            override fun onVerificationCompleted(s: PhoneAuthCredential) {
                val code = s.smsCode

                if (code != null) {
                    val c = java.lang.StringBuilder()
                    c.append(code)
                    sentcodeEt.text.clear()
                    sentcodeEt.text.append(c)
                    progressDialog.dismiss()
                }


            }

            override fun onVerificationFailed(exception: FirebaseException) {
                progressDialog.dismiss()
                Toast.makeText(context, exception.localizedMessage, Toast.LENGTH_SHORT).show()
            }


        }
        phoneAuthProvider.verifyPhoneNumber(
            number,
            60,
            TimeUnit.SECONDS,
            this.activity as AuthActivity,
            callbacks
        )


    }

    private fun showTimer(milli: Long) {

        resendBtn.isEnabled = false
        mCounterDown = object : CountDownTimer(milli, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                if (mCounterDown != null) {
                    timeLeft = millisUntilFinished
                    counterTv?.isVisible = true
                    counterTv?.text = "Seconds remaining: " + millisUntilFinished / 1000
                }

                //here you can have your logic to set text to edittext
            }

            override fun onFinish() {
                if (resendBtn != null) {
                    resendBtn.isVisible = true
                    resendBtn.isEnabled = true
                }
                counterTv?.isVisible = false
            }
        }.start()

    }

    private fun highLightText(number: String) {
        val span = SpannableString(number)
        val clickSpan: ClickableSpan = object : ClickableSpan() {
            override fun updateDrawState(ds: TextPaint) {
                ds.color = ds.linkColor // you can use custom color
                ds.isUnderlineText = false // this remove the underline
            }

            override fun onClick(textView: View) { // handle click event
                showLoginActivity()
            }
        }

        span.setSpan(clickSpan, span.length - 14, span.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        waitingTv.movementMethod = LinkMovementMethod.getInstance()
        waitingTv.text = span
    }

    private fun showLoginActivity() {
        mCounterDown?.let {
            it.cancel()

        }
        mCounterDown = null
        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.destination_to_pop, true)
            .build()
        findNavController().navigate(R.id.action_otpFragment_to_loginFragment, null, navOptions)

    }

    override fun onDestroy() {
        super.onDestroy();
        mCounterDown?.let {
            it.cancel()

        }
        // mCounterDown=null

    }

}