package com.saikalyandaroju.kotlinnews.auth.fragments

import android.app.ProgressDialog
import android.os.Bundle
import android.os.CountDownTimer
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.saikalyandaroju.kotlinnews.R
import kotlinx.android.synthetic.main.fragment_otp.*


class OtpFragment : Fragment() {

    val args: OtpFragmentArgs by navArgs()


    private var mCounterDown: CountDownTimer? = null
    private var timeLeft: Long = -1
    private lateinit var progressDialog:ProgressDialog

    private lateinit var phoneAuthProvider: PhoneAuthProvider
    private lateinit var callbacks:PhoneAuthProvider.OnVerificationStateChangedCallbacks
    private var mresendToken:PhoneAuthProvider.ForceResendingToken?=null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_otp, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        uiUpdate()
    }

    private fun uiUpdate() {
        val number = args.number
        verifyTv.append("Verify " + number)
        waitingTv.append(number + " " + "Wrong Number ?")
        highLightText(number)
        showTimer(60000)

        progressDialog=createDialog("Sending a verification code",false)

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

       callbacks=object :PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
           override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
               super.onCodeSent(p0, p1)
           }
           override fun onVerificationCompleted(p0: PhoneAuthCredential) {
               TODO("Not yet implemented")
           }

           override fun onVerificationFailed(p0: FirebaseException) {
               TODO("Not yet implemented")
           }

       }


    }

    private fun showTimer(milli: Long) {

        resendBtn.isEnabled = false
        mCounterDown = object : CountDownTimer(milli, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                timeLeft = millisUntilFinished
                counterTv.isVisible = true
                counterTv.text = "Seconds remaining: " + millisUntilFinished / 1000


                //here you can have your logic to set text to edittext
            }

            override fun onFinish() {
                resendBtn.isEnabled = true
                counterTv.isVisible = false
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

        span.setSpan(clickSpan, span.length - 13, span.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        waitingTv.movementMethod = LinkMovementMethod.getInstance()
        waitingTv.text = span
    }

    private fun showLoginActivity() {
        findNavController().navigate(R.id.action_otpFragment_to_loginFragment)
    }


}