package com.saikalyandaroju.kotlinnews.auth.fragments

import android.app.Activity.RESULT_OK
import android.app.ProgressDialog
import android.content.ContentResolver
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import com.bumptech.glide.RequestManager
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.gms.tasks.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.saikalyandaroju.kotlinnews.R
import com.saikalyandaroju.kotlinnews.auth.model.User
import com.saikalyandaroju.kotlinnews.utils.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_signup.*
import okhttp3.internal.wait
import java.lang.StringBuilder
import javax.inject.Inject


@AndroidEntryPoint
class SignupFragment : Fragment() {

    private val TAG = "SignupFragment"

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    @Inject
    lateinit var requestManager: RequestManager

    private lateinit var editor: SharedPreferences.Editor


    private var imageURi: Uri? = null
    private lateinit var storageReference: StorageReference
    private lateinit var firebaseFirestore: FirebaseFirestore
    private lateinit var storageTask: StorageTask<*>
    private lateinit var downloadurl: String
    private lateinit var progressDialog: ProgressDialog
    private lateinit var imagePath: String


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_signup, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        editor = sharedPreferences.edit()

        inits()

        checkAlreadyRegisteredUser()
        setUpListeners()
    }

    private fun checkAlreadyRegisteredUser() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val userId = user.uid
            firebaseFirestore.collection("Users").document(userId).get().addOnSuccessListener {

                if (it.exists()) {

                    editor.putBoolean(Constants.NEW_USER, false).commit()
                    val f_user = it.toObject(User::class.java)
                    if (f_user != null) {
                        updateUI(f_user)
                    }

                } else {
                    editor.putBoolean(Constants.NEW_USER, true).commit()
                }

            }.addOnCompleteListener {

            }.addOnFailureListener {

                editor.putBoolean(Constants.NEW_USER, true).commit()
            }

        }
    }

    private fun updateUI(user: User) {
        val x: StringBuilder = StringBuilder()
        x.append(user.name)
        nameEt.text.clear()
        nameEt.text.append(x)

        requestManager.load(user.imgPath).centerCrop().fitCenter()
            .placeholder(R.drawable.ic_baseline_face_24)
            .error(R.drawable.ic_baseline_face_24)
            .into(userImgView)
        imagePath = user.imgPath
        downloadurl = user.imageUrl
        editor.putString(Constants.U_PROFILEPIC, user.imgPath).commit()
        editor.putString(Constants.U_NAME, user.name).commit()

    }

    private fun inits() {
        downloadurl =
            Uri.parse(
                ContentResolver.SCHEME_ANDROID_RESOURCE +
                        "://" + getResources().getResourcePackageName(R.drawable.ic_launcher_background)
                        + '/' + getResources().getResourceTypeName(R.drawable.ic_launcher_background)
                        + '/' + getResources().getResourceEntryName(R.drawable.ic_launcher_background)
            ).toString();
        imagePath = downloadurl
        storageReference =
            com.google.firebase.storage.FirebaseStorage.getInstance().getReference("uploads");
        firebaseFirestore = FirebaseFirestore.getInstance();
        progressDialog = createDialog("Please wait....", false)
    }

    private fun setUpListeners() {
        userImgView.setOnClickListener(View.OnClickListener { v -> choosephoto(v) }
        )
        nameEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val sn = s.toString()
                if (sn.length > 0) {
                    nextBtn.setEnabled(true)
                } else {
                    nextBtn.setEnabled(false)
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })



        nextBtn.setOnClickListener(View.OnClickListener {
            progressDialog.show()
            val uname: String = nameEt.getText().toString()
            if (uname.length == 0) {
                progressDialog.dismiss()
                Toast.makeText(getApplicationContext(), "Name can't be empty", Toast.LENGTH_SHORT)
                    .show()
            } else {


                val user = com.saikalyandaroju.kotlinnews.auth.model.User(
                    uname,
                    downloadurl, imagePath,
                    FirebaseAuth.getInstance().uid.toString()
                )
                firebaseFirestore.collection("Users/")
                    .document(FirebaseAuth.getInstance().uid.toString()).set(user)
                    .addOnSuccessListener(
                        OnSuccessListener<Void?> {

                            navigate(uname, imagePath)

                        })
                    .addOnFailureListener(OnFailureListener { e ->
                        progressDialog.dismiss()
                        Toast.makeText(getApplicationContext(), "Error!", Toast.LENGTH_SHORT).show()
                        Log.d(TAG, e.toString())
                    })
            }
        })

    }

    private fun navigate(uname: String, downloadurl: String) {
        editor.putString(Constants.U_NAME, uname).commit()
        editor.putString(Constants.U_PROFILEPIC, downloadurl).commit()
        editor.putBoolean(Constants.PROFILE_STEP, true).commit()
        progressDialog.dismiss()

        if (sharedPreferences.getBoolean(Constants.NEW_USER, false)) {
            findNavController().navigate(R.id.action_signupFragment_to_onBoardFragment)
        } else {
            findNavController().navigate(R.id.action_signupFragment_to_mainActivity)
        }


    }


    private fun choosephoto(v: View) {
        ImagePicker.with(this)
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

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        @Nullable data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && data != null && data.data != null) {
            imageURi = data.data
            imagePath = imageURi?.path.toString()
            editor.putString(Constants.U_PROFILEPIC, imagePath).apply()
            Log.i("data", imageURi.toString())

            requestManager.load(imageURi).centerCrop().fitCenter()
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .into(userImgView);
            progressDialog.show()
            uploadImage(imageURi, imagePath)
        }
    }

    private fun uploadImage(imageURi: Uri?, imagePath: String) {
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
                    editor.putString(Constants.U_PROFILEPIC, downloadurl).apply();
                    progressDialog.dismiss()
                    Toast.makeText(context, "Sucessfully uploaded the image", Toast.LENGTH_SHORT)
                        .show()
                }
            }.addOnFailureListener {
                progressDialog.dismiss()
                Toast.makeText(context, it?.localizedMessage, Toast.LENGTH_SHORT).show()

            }

        } else {
            progressDialog.dismiss()
            Toast.makeText(context, "Error in  uploading the image ", Toast.LENGTH_SHORT).show()

        }
    }


    // to get type of file jpg/mp4/mp3 etc..
    private fun getFileExtension(uri: Uri): String? {
        val cR: ContentResolver = context?.contentResolver!!
        val mime = MimeTypeMap.getSingleton()
        return mime.getExtensionFromMimeType(cR.getType(uri))
    }

    private fun createDialog(s: String, b: Boolean): ProgressDialog {
        return ProgressDialog(context).apply {
            setCancelable(b)
            setCanceledOnTouchOutside(false)
            setMessage(s)
        }

    }


}