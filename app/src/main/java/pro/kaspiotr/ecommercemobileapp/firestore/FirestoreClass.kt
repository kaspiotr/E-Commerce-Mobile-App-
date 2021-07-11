package pro.kaspiotr.ecommercemobileapp.firestore

import android.app.Activity
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import pro.kaspiotr.ecommercemobileapp.activities.LoginActivity
import pro.kaspiotr.ecommercemobileapp.activities.RegisterActivity
import pro.kaspiotr.ecommercemobileapp.models.User
import pro.kaspiotr.ecommercemobileapp.utils.Constants

class FirestoreClass {
    private val mFirestore = FirebaseFirestore.getInstance()

    fun registerUser(activity: RegisterActivity, userInfo: User) {
        // The 'users' is the collection name. If the collection is already created that it will not create the same one.
        mFirestore.collection(Constants.USERS)
            // Document ID for users fields. Here the document it is the User ID.
            .document(userInfo.id)
            // Here the userInfo are Field and the SetOptions is set to merge. It is for if we wants to merge later
            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {
                // Here call a function of base activity for transferring the result to it.
                activity.userRegistrationSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while registering the user.",
                    e
                )
            }
    }

    fun getCurrentUserID(): String {
        // An Instance of currentUser using FirebaseAuth
        val currentUser = FirebaseAuth.getInstance().currentUser

        // A variable to assign the currentUserId if it is not null or else it will be blank
        var currentUserID = ""
        if (currentUser != null) {
            currentUserID = currentUser.uid
        }

        return currentUserID;
    }

    fun getUserDetails(activity: Activity) {
        // Here we pass the collection name from which we want to retrieve data
        mFirestore.collection(Constants.USERS)
            // The document ID to get the fields of user
            .document(getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->
                Log.i(activity.javaClass.simpleName, document.toString())

                // Here we have received the document snapshot which is covered into the User Data model object
                val user = document.toObject(User::class.java)!!

                when (activity) {
                    is LoginActivity -> {
                        // Call a function of base activity for transferring the result to it
                        activity.userLoggedInSuccess(user)
                    }
                }
            }
            .addOnFailureListener { e ->

            }
    }
}