package pro.kaspiotr.ecommercemobileapp.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_address_list.*
import pro.kaspiotr.ecommercemobileapp.R
import pro.kaspiotr.ecommercemobileapp.firestore.FirestoreClass
import pro.kaspiotr.ecommercemobileapp.models.Address

class AddressListActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_address_list)
        setUpActionBar()

        tv_add_address.setOnClickListener {
            val intent = Intent(this@AddressListActivity, AddEditAddressActivity::class.java)
            startActivity(intent)
        }

        getAddressList()
    }

    private fun setUpActionBar() {
        setSupportActionBar(toolbar_address_list_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        toolbar_address_list_activity.setNavigationOnClickListener { onBackPressed() }
    }

    fun successAddressListFromFirestore(addressList: ArrayList<Address>) {
        hideProgressDialog()
        for (address in addressList) {
            Log.i("Name and Address", "${address.name} :: ${address.address}")
        }
    }

    private fun getAddressList() {
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getAddressesList(this)
    }

}