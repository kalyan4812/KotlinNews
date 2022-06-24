package com.saikalyandaroju.kotlinnews.auth.fragments

import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.saikalyandaroju.kotlinnews.R
import kotlinx.android.synthetic.main.fragment_login.*


class LoginFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listeners()
    }

    private fun listeners() {

        ccp.registerCarrierNumberEditText(phoneNumberEt)

        phoneNumberEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(s: CharSequence, p1: Int, p2: Int, p3: Int) {
                val num = s.toString()

                if (num.length == 10) {
                    nextBtn.isEnabled = true
                } else {
                    nextBtn.isEnabled = false
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })


        nextBtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {

                if (ccp.isValidFullNumber) {
                    val dialog = MaterialAlertDialogBuilder(view.context).apply {
                        setMessage("Proceed for verification of number ?")
                        setPositiveButton("OK", object : DialogInterface.OnClickListener {
                            override fun onClick(dialog: DialogInterface, p1: Int) {
                                val bundle = Bundle()
                                bundle.apply {

                                    putString("number", ccp.fullNumberWithPlus)
                                }
                                view?.post {
                                    findNavController().navigate(
                                        R.id.action_loginFragment_to_otpFragment,
                                        bundle
                                    )
                                }
                            }

                        })
                        setNegativeButton("CANCEL", object : DialogInterface.OnClickListener {
                            override fun onClick(dialog: DialogInterface, p1: Int) {
                                dialog.dismiss()
                            }

                        })
                        setCancelable(false)
                    }
                    dialog.show()
                } else {

                     Toast.makeText(context, "Please Enter a valid number", Toast.LENGTH_SHORT)
                        .show()
                }

            }

        })
    }


}