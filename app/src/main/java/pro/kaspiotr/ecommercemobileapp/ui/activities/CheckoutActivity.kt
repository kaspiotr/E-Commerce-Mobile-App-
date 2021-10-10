package pro.kaspiotr.ecommercemobileapp.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_checkout.*
import pro.kaspiotr.ecommercemobileapp.R
import pro.kaspiotr.ecommercemobileapp.firestore.FirestoreClass
import pro.kaspiotr.ecommercemobileapp.models.Address
import pro.kaspiotr.ecommercemobileapp.models.CartItem
import pro.kaspiotr.ecommercemobileapp.models.Order
import pro.kaspiotr.ecommercemobileapp.models.Product
import pro.kaspiotr.ecommercemobileapp.ui.adapters.CartItemsListAdapter
import pro.kaspiotr.ecommercemobileapp.utils.Constants

class CheckoutActivity : BaseActivity() {

    private var mAddressDetails: Address? = null
    private lateinit var mProductList: ArrayList<Product>
    private lateinit var mCartItemsList: ArrayList<CartItem>
    private var mSubtotal: Double = 0.0
    private var mTotalAmount: Double = 0.0
    private lateinit var mOrderDetails: Order

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)
        setUpActionBar()

        if (intent.hasExtra(Constants.EXTRA_SELECTED_ADDRESS)) {
            mAddressDetails = intent.getParcelableExtra<Address>(Constants.EXTRA_SELECTED_ADDRESS)
        }

        if (mAddressDetails != null) {
            tv_checkout_address_type.text = mAddressDetails?.type
            tv_checkout_full_name.text = mAddressDetails?.name
            tv_checkout_address.text = "${mAddressDetails!!.address}, ${mAddressDetails!!.zipCode}"
            tv_checkout_additional_note.text = mAddressDetails?.additionalNote

            if (mAddressDetails?.otherDetails!!.isNotEmpty()) {
                tv_checkout_other_details.text = mAddressDetails?.otherDetails
            }
            tv_checkout_mobile_number.text = mAddressDetails?.mobileNumber
        }

        getProductsList()

        btn_place_order.setOnClickListener {
            placeOrder()
        }
    }

    fun successProductsListFromFirestore(productsList: ArrayList<Product>) {
        mProductList = productsList
        getCartItemsList()
    }

    fun successCartItemsList(cartList: ArrayList<CartItem>) {
        hideProgressDialog()
        for (product in mProductList) {
            for (cartItem in cartList) {
                if (product.product_id == cartItem.product_id) {
                    cartItem.stock_quantity = product.stock_quantity
                }
            }
        }
        mCartItemsList = cartList

        rv_cart_list_items.layoutManager = LinearLayoutManager(this@CheckoutActivity)
        rv_cart_list_items.setHasFixedSize(true)

        val cartListAdapter = CartItemsListAdapter(this@CheckoutActivity, mCartItemsList, false)
        rv_cart_list_items.adapter = cartListAdapter

        for (item in mCartItemsList) {
            val availableQuantity = item.stock_quantity.toInt()
            if (availableQuantity > 0) {
                val price = item.price.toDouble()
                val quantity = item.cart_quantity.toInt()
                mSubtotal += (price * quantity)
            }
        }

        tv_checkout_sub_total.text = "$$mSubtotal"
        tv_checkout_shipping_charge.text = "$10.0"

        if (mSubtotal > 0) {
            ll_checkout_place_order.visibility = View.VISIBLE

            mTotalAmount = mSubtotal + 10.0
            tv_checkout_total_amount.text = "$$mTotalAmount"
        } else {
            ll_checkout_place_order.visibility = View.GONE
        }
    }

    fun orderPlacedSuccess() {
        FirestoreClass().updateAllDetails(this, mCartItemsList, mOrderDetails)
    }

    fun allDetailsUpdatedSuccessfully() {
        hideProgressDialog()
        Toast.makeText(
            this@CheckoutActivity,
            "Your order was placed successfully.",
            Toast.LENGTH_SHORT
        ).show()

        val intent = Intent(this@CheckoutActivity, DashboardActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun getProductsList() {
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getAllProductsList(this@CheckoutActivity)
    }

    private fun getCartItemsList() {
        FirestoreClass().getCartList(this@CheckoutActivity)
    }

    private fun setUpActionBar() {
        setSupportActionBar(toolbar_checkout_activity)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }
        toolbar_checkout_activity.setNavigationOnClickListener { onBackPressed() }
    }

    private fun placeOrder() {
        showProgressDialog(resources.getString(R.string.please_wait))

        if (mAddressDetails != null) {
            mOrderDetails = Order(
                FirestoreClass().getCurrentUserID(),
                mCartItemsList,
                mAddressDetails!!,
                "My order ${System.currentTimeMillis()}",
                mCartItemsList[0].image,
                mSubtotal.toString(),
                "10.0", // fixed shipping charge
                mTotalAmount.toString(),
                System.currentTimeMillis()
            )

            FirestoreClass().placeOrder(this, mOrderDetails)
        }
    }

}