package com.saikalyandaroju.kotlinnews.auth.fragments

import android.app.Activity.RESULT_OK
import android.app.ProgressDialog
import android.content.ContentResolver
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.BitmapFactory
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
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import com.bumptech.glide.RequestManager
import com.google.android.gms.tasks.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.saikalyandaroju.kotlinnews.R
import com.saikalyandaroju.kotlinnews.model.adapters.NewsAdapter
import com.saikalyandaroju.kotlinnews.model.source.models.Article
import com.saikalyandaroju.kotlinnews.ui.activities.MainActivity
import com.saikalyandaroju.kotlinnews.utils.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_signup.*
import kotlinx.android.synthetic.main.item_article.view.*
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
    private lateinit var documentReference: DocumentReference
    private lateinit var storageTask: StorageTask<*>
    private lateinit var downloadurl: String
    private lateinit var progressDialog: ProgressDialog


    private lateinit var isReadyToNavigate: MutableLiveData<Boolean>


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
        isReadyToNavigate = MutableLiveData()
        inits()
        setUpListeners()
    }

    private fun inits() {
        downloadurl =
            Uri.parse(
                ContentResolver.SCHEME_ANDROID_RESOURCE +
                        "://" + getResources().getResourcePackageName(R.drawable.ic_launcher_background)
                        + '/' + getResources().getResourceTypeName(R.drawable.ic_launcher_background)
                        + '/' + getResources().getResourceEntryName(R.drawable.ic_launcher_background)
            ).toString();
        storageReference =
            com.google.firebase.storage.FirebaseStorage.getInstance().getReference("uploads");
        firebaseFirestore = FirebaseFirestore.getInstance();
        progressDialog = createDialog("uploading the photo....", false)
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
                    downloadurl,
                    FirebaseAuth.getInstance().uid.toString()
                )
                firebaseFirestore.collection("Users")
                    .document(FirebaseAuth.getInstance().uid.toString()).set(user)
                    .addOnSuccessListener(
                        OnSuccessListener<Void?> {

                            navigate(uname, downloadurl)

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
        findNavController().navigate(R.id.auth_nav_graph)
        activity?.finish()
    }


    private fun choosephoto(v: View) {
        val i = Intent()
        i.type = "image/*"
        i.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(i, 100)
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        @Nullable data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == RESULT_OK && data != null && data.data != null) {
            imageURi = data.data
            Log.i("data", imageURi.toString())

            requestManager.load(imageURi).centerCrop().fitCenter()
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .into(userImgView);
            progressDialog.show()
            uploadImage(imageURi)
        }
    }

    private fun uploadImage(imageURi: Uri?) {
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