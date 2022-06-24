package com.saikalyandaroju.kotlinnews.ui.fragments

import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.ContentResolver
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.net.Uri
import android.opengl.Visibility
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.PopupMenu
import android.widget.ProgressBar
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import com.bumptech.glide.RequestManager
import com.github.dhaval2404.imagepicker.ImagePicker.Companion.with
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.dialog.MaterialDialogs
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.saikalyandaroju.kotlinnews.R
import com.saikalyandaroju.kotlinnews.utils.Constants
import dagger.hilt.android.AndroidEntryPoint
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.fragment_news.*
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.userImgView
import javax.inject.Inject


@AndroidEntryPoint
class ProfileFragment : Fragment() {

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    @Inject
    lateinit var requestManager: RequestManager

    lateinit var editor: SharedPreferences.Editor

    private lateinit var imageuri: Uri
    private lateinit var imgPath: String
    private var namechanged: Boolean = false


    private lateinit var storageReference: StorageReference
    private lateinit var storageTask: StorageTask<*>
    private lateinit var downloadurl: String


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(
            com.saikalyandaroju.kotlinnews.R.layout.fragment_profile,
            container,
            false
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        editor = sharedPreferences.edit()
        storageReference =
            com.google.firebase.storage.FirebaseStorage.getInstance().getReference("uploads");


        imgPath = Uri.parse(
            ContentResolver.SCHEME_ANDROID_RESOURCE +
                    "://" + getResources().getResourcePackageName(R.drawable.ic_baseline_face_24)
                    + '/' + getResources().getResourceTypeName(R.drawable.ic_baseline_face_24)
                    + '/' + getResources().getResourceEntryName(R.drawable.ic_baseline_face_24)
        ).toString();
        imageuri = Uri.parse(
            ContentResolver.SCHEME_ANDROID_RESOURCE +
                    "://" + getResources().getResourcePackageName(R.drawable.ic_baseline_face_24)
                    + '/' + getResources().getResourceTypeName(R.drawable.ic_baseline_face_24)
                    + '/' + getResources().getResourceEntryName(R.drawable.ic_baseline_face_24)
        )

        bindData()

        setUpListeners()

    }

    private fun showProgressBar() {
        progressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        progressBar.visibility = View.GONE
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

            showProgressBar()
            saves(view)

        }
        openCamera.setOnClickListener(View.OnClickListener { v ->
            save.visibility = View.VISIBLE

            openPicker(v)
        })
        logout.setOnClickListener {
            editor.clear().commit()
            findNavController().navigate(R.id.action_profileFragment_to_authActivity)
        }

    }

    private fun saves(view: View?) {

        display_name.setCursorVisible(false);
        save.setText("Please wait...");
        editor.putString(Constants.U_NAME, display_name.text.toString()).apply();
        editor.putString(Constants.U_PROFILEPIC, imgPath).apply();

        uploadImage(imageuri, imgPath)
        if (namechanged) {

            updateNameInFirebase(display_name.getText().toString());
        }

        save.setVisibility(View.GONE);
        logout.setVisibility(View.VISIBLE);
    }

    private fun updateNameInFirebase(name: String) {
        if (namechanged)
            FirebaseFirestore.getInstance().collection("Users")
                .document(FirebaseAuth.getInstance().getUid().toString()).update("name", name);
    }

    private fun updateImageInFirestore(imgurl: String) {


        FirebaseFirestore.getInstance().collection("Users")
            .document(FirebaseAuth.getInstance().uid.toString()).update("imgUrl", imgurl)
            .addOnSuccessListener {
                hideProgressBar()
                Toasty.success(
                    requireContext(),
                    "Profile Dp Updated succesfully...",
                    Toast.LENGTH_SHORT
                ).show()
            }.addOnFailureListener {
                hideProgressBar()
                Toasty.error(
                    requireContext(),
                    "Failed to update..",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun uploadImage(imageURi: Uri?, imgPath: String) {
        if (imageURi != null) {
            val mstorageReference = storageReference.child(
                System.currentTimeMillis().toString() + "." + getFileExtension(imageURi)
            )
            storageTask = mstorageReference.putFile(imageURi)
            // Task<Uri> urlTask =
            (storageTask as UploadTask).addOnSuccessListener {


                mstorageReference.downloadUrl

            }.addOnCompleteListener {
                if (it.isSuccessful()) {


                    downloadurl = it.getResult().toString();


                    editor.putString(Constants.U_PROFILEPIC, imgPath).apply();
                    updateImageInFirestore(downloadurl);
                    //   Toast.makeText(context, "Sucessfully uploaded the image", Toast.LENGTH_SHORT)
                    //     .show()
                }
            }.addOnFailureListener {
                hideProgressBar()
                Toasty.info(requireContext(), it?.localizedMessage, Toast.LENGTH_SHORT).show()

            }

        } else {
            hideProgressBar()
            Toasty.error(requireContext(), "Error in  uploading the image ", Toast.LENGTH_SHORT)
                .show()

        }
    }

    // to get type of file jpg/mp4/mp3 etc..
    private fun getFileExtension(uri: Uri): String? {
        val cR: ContentResolver = context?.contentResolver!!
        val mime = MimeTypeMap.getSingleton()
        return mime.getExtensionFromMimeType(cR.getType(uri))
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
                setImage(imgPath)

                imageuri = data.data!!
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            Toasty.error(getApplicationContext(), "Unable to pick the image.", Toast.LENGTH_SHORT)
                .show()
        } else {
            Toasty.error(getApplicationContext(), "Unable to pick the image.", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun setImage(imgPath: String) {
        requestManager.load(imgPath).centerCrop().fitCenter()
            .placeholder(R.drawable.ic_baseline_face_24)
            .error(R.drawable.ic_baseline_face_24)
            .into(userImgView);
    }


    private fun bindData() {
        ed_phone.setText(FirebaseAuth.getInstance().currentUser!!.phoneNumber)

        display_name.setText(sharedPreferences.getString(Constants.U_NAME, ""))

        if (sharedPreferences.getString(Constants.U_PROFILEPIC, "")!!.isNotEmpty()) {
            imgPath = sharedPreferences.getString(Constants.U_PROFILEPIC, "")!!

            setImage(imgPath)

        } else {

            setImage(imgPath!!)
        }

        val count = StringBuilder()
        count.append(0)
        ed_artcile_read_count?.text?.clear()
        ed_artcile_read_count?.text?.append(count)



        FirebaseFirestore.getInstance().collection("userArticleCount")
            .document(FirebaseAuth.getInstance().getUid().toString()).get().addOnSuccessListener {
                if (it.exists()) {

                    val long = it?.data?.size
                    val count = StringBuilder()
                    count.append(long)
                    ed_artcile_read_count?.text?.clear()
                    ed_artcile_read_count?.text?.append(count)
                } else {
                    val count = StringBuilder()
                    count.append(0)
                    ed_artcile_read_count?.text?.clear()
                    ed_artcile_read_count?.text?.append(count)
                }
            }
    }


}