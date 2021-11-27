package pro.kaspiotr.ecommercemobileapp.ui.activities

import android.content.Context
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import pro.kaspiotr.ecommercemobileapp.R
import pro.kaspiotr.ecommercemobileapp.utils.Constants

class MainActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sharedPreferences = getSharedPreferences(Constants.E_COMMERCE_MOBILE_APP_PREFERENCES, Context.MODE_PRIVATE)
        val username = sharedPreferences.getString(Constants.LOGGED_IN_USERNAME, "")!!
        tv_main.text = "Hello $username"
    }
}