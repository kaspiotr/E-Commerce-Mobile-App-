package pro.kaspiotr.ecommercemobileapp.ui.activities

import android.os.Bundle
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.activity_product_details.*
import pro.kaspiotr.ecommercemobileapp.R
import pro.kaspiotr.ecommercemobileapp.firestore.FirestoreClass
import pro.kaspiotr.ecommercemobileapp.models.Product
import pro.kaspiotr.ecommercemobileapp.utils.Constants
import pro.kaspiotr.ecommercemobileapp.utils.GlideLoader

class ProductDetailsActivity : BaseActivity() {

    private var mProductId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_details)
        setupActionBar()

        if (intent.hasExtra(Constants.EXTRA_PRODUCT_ID)) {
            mProductId = intent.getStringExtra(Constants.EXTRA_PRODUCT_ID)!!
        }
        var productOwnerId: String = ""

        if (intent.hasExtra(Constants.EXTRA_PRODUCT_OWNER_ID)) {
            productOwnerId = intent.getStringExtra(Constants.EXTRA_PRODUCT_OWNER_ID)!!
        }

        if (FirestoreClass().getCurrentUserID() == productOwnerId) {
            btn_add_to_cart.visibility = View.GONE
        } else {
            btn_add_to_cart.visibility = View.VISIBLE
        }

        getProductDetails()
    }

    private fun setupActionBar() {
        setSupportActionBar(toolbar_product_details_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        toolbar_product_details_activity.setNavigationOnClickListener { onBackPressed() }
    }

    fun getProductDetails() {
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getProductDetails(this, mProductId)
    }

    fun productDetailsSuccess(product: Product) {
        hideProgressDialog()
        GlideLoader(this@ProductDetailsActivity).loadProductPicture(
            product.image,
            iv_product_detail_image
        )
        tv_product_details_title.text = product.title
        tv_product_details_price.text = "$${product.price}"
        tv_product_details_description.text = product.description
        tv_product_details_available_quantity.text = product.stock_quantity
    }
}