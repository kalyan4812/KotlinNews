package com.saikalyandaroju.kotlinnews.ui.fragments

import android.R.attr.bitmap
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import com.bumptech.glide.RequestManager
import com.github.dhaval2404.imagepicker.ImagePicker.Companion.with
import com.google.firebase.auth.FirebaseAuth
import com.saikalyandaroju.kotlinnews.utils.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_profile.*
import javax.inject.Inject


@AndroidEntryPoint
class ProfileFragment : Fragment() {

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    @Inject
    lateinit var requestManager:RequestManager

    lateinit var editor: SharedPreferences.Editor

    private lateinit var bitmap: Bitmap

    private lateinit var imageuri:Uri
    private lateinit var imgPath:String
    private  var namechanged:Boolean=false
    private lateinit var progressDialog:ProgressDialog


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(com.saikalyandaroju.kotlinnews.R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        editor=sharedPreferences.edit()
        bindData()

        setUpListeners()

    }

    private fun setUpListeners() {
        display_name.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                display_name.setCursorVisible(true)
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                namechanged = true
                display_name.setCursorVisible(true)
                save.setVisibility(View.VISIBLE)
            }

            override fun afterTextChanged(s: Editable) {}
        })

        save.setOnClickListener { view ->
            logout.visibility = View.GONE
            saves(view)
        }
        openCamera.setOnClickListener(View.OnClickListener { v ->
            save.visibility = View.VISIBLE
            openPicker(v)
        })
        logout.setOnClickListener {
            editor.clear().apply()
            findNavController().navigate(com.saikalyandaroju.kotlinnews.R.id.auth_nav_graph)
            activity?.finish()
        }

    }

    private fun saves(view: View?) {
        setUpProgressDialog();
        display_name.setCursorVisible(false);
        save.setText("Please wait...");
        editor.putString(Constants.U_NAME, display_name.text.toString()).apply();
        editor.putString(Constants.U_PROFILEPIC, imgPath).apply();


        progressDialog.dismiss();
        save.setVisibility(View.GONE);
        logout.setVisibility(View.VISIBLE);
    }



    private fun setUpProgressDialog() {
        progressDialog = ProgressDialog(context);
        progressDialog.setMessage("Please Wait..");
        progressDialog.setIndeterminate(true);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
    }

    private fun openPicker(v: View?) {
        with(this)
            .cropSquare() //Crop image(Optional), Check Customization for more option
            .compress(1024) //Final image size will be less than 1 MB(Optional)
            .maxResultSize(
                1080,
                1080
            ) //Final image resolution will be less than 1080 x 1080(Optional)
            //.saveDir(new File(Environment.getExternalStorageDirectory(), "CricFrik"))
            .galleryMimeTypes(
                arrayOf(
                    "image/png",
                    "image/jpg",
                    "image/jpeg"
                )
            )
            .start()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, @Nullable data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (data != null) {
                imgPath = data.data!!.path!!
                val options = BitmapFactory.Options()
                options.inSampleSize = 8
                 bitmap = BitmapFactory.decodeFile(imgPath, options)
                userImgView.setImageBitmap(bitmap)
                userImgView.setScaleType(ImageView.ScaleType.CENTER_CROP)
                imageuri = data.data!!
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            Toast.makeText(getApplicationContext(), "Unable to pick the image.", Toast.LENGTH_SHORT)
                .show()
        } else {
            Toast.makeText(getApplicationContext(), "Unable to pick the image.", Toast.LENGTH_SHORT)
                .show()
        }
    }


    private fun bindData() {
        ed_phone.setText(FirebaseAuth.getInstance().currentUser!!.phoneNumber)
     //   nameEt.setText(FirebaseAuth.getInstance().currentUser!!.displayName)
        display_name.setText(sharedPreferences.getString(Constants.U_NAME,""))

        if (sharedPreferences.getString(Constants.U_PROFILEPIC,"") != "") {
            val image = sharedPreferences.getString(Constants.U_PROFILEPIC, "")
            requestManager.load(image).centerCrop().fitCenter()
                .placeholder(com.saikalyandaroju.kotlinnews.R.drawable.placeholder)
                .error(com.saikalyandaroju.kotlinnews.R.drawable.placeholder)
                .into(userImgView);

        } else {

        }
    }



}