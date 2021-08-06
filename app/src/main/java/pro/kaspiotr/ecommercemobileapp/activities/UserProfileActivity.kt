package pro.kaspiotr.ecommercemobileapp.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.activity_register.et_email
import kotlinx.android.synthetic.main.activity_register.et_first_name
import kotlinx.android.synthetic.main.activity_register.et_last_name
import kotlinx.android.synthetic.main.activity_user_profile.*
import pro.kaspiotr.ecommercemobileapp.R
import pro.kaspiotr.ecommercemobileapp.firestore.FirestoreClass
import pro.kaspiotr.ecommercemobileapp.models.User
import pro.kaspiotr.ecommercemobileapp.utils.Constants
import pro.kaspiotr.ecommercemobileapp.utils.GlideLoader
import java.io.IOException

class UserProfileActivity : BaseActivity(), View.OnClickListener {

    private lateinit var mUserDetails: User;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        if (intent.hasExtra(Constants.EXTRA_USER_DETAILS)) {
            mUserDetails = intent.getParcelableExtra(Constants.EXTRA_USER_DETAILS)!!
        }

        et_first_name.isEnabled = false
        et_first_name.setText(mUserDetails.firstName)

        et_last_name.isEnabled = false
        et_last_name.setText(mUserDetails.lastName)

        et_email.isEnabled = false
        et_email.setText(mUserDetails.email)

        iv_user_photo.setOnClickListener(this@UserProfileActivity)
        btn_submit.setOnClickListener(this@UserProfileActivity)
    }

    override fun onClick(view: View?) {
        if (view != null) {
            when (view.id) {
                R.id.iv_user_photo -> {
                    // Check if the permissions already allowed or we need to request for it.
                    // Firstly, the READ_EXTERNAL_STORAGE permission is checked and if it is allowed
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        Constants.showImageChooser(this)
                    } else {
                        // Request permissions to be granted to this application. These permissions must be requested in manifest, they should be
                        // granted to the app, and they should have protected level.
                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                            Constants.READ_STORAGE_PERMISSION_CODE
                        )
                    }
                }

                R.id.btn_submit -> {
                    if (validateUserProfileDetails()) {
                        val userHashMap = HashMap<String, Any>()
                        val mobileNumber = et_mobile_number.text.toString().trim { it <= ' ' }
                        val gender = if (rb_male.isChecked) {
                            Constants.MALE
                        } else {
                            Constants.FEMALE
                        }

                        if (mobileNumber.isNotEmpty()) {
                            userHashMap[Constants.MOBILE] = mobileNumber.toLong()
                        }
                        userHashMap[Constants.GENDER] = gender
                        showProgressDialog(resources.getString(R.string.please_wait))
                        FirestoreClass().updateUserProfileData(this, userHashMap)
                    }
                }
            }
        }
    }

    fun userProfileUpdateSuccess() {
        hideProgressDialog()

        Toast.makeText(this@UserProfileActivity,
            resources.getString(R.string.msg_profile_update_success),
            Toast.LENGTH_SHORT
        ).show()

        startActivity(Intent(this@UserProfileActivity, MainActivity::class.java))
        finish()
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Constants.showImageChooser(this)
        } else {
            Toast.makeText(
                this, resources.getString(R.string.read_storage_permission_denied),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.PICK_IMAGE_REQUEST_CODE) {
                if (data != null) {
                    try {
                        // The uri of selected image from phone storage.
                        val selectedImageFileUri = data.data!!
                        GlideLoader(this).loadUserPicture(selectedImageFileUri, iv_user_photo)
                    } catch (e: IOException) {
                        e.printStackTrace()
                        Toast.makeText(
                            this@UserProfileActivity,
                            resources.getString(R.string.image_selection_failed),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            // A log is printed when user close or cancel the image selection.
            Log.e("Request Cancelled", "Image selection cancelled")
        }
    }

    private fun validateUserProfileDetails(): Boolean {
        return when {
            TextUtils.isEmpty(et_mobile_number.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_mobile_number), true)
                false
            }
            else -> {
                true
            }
        }
    }
}