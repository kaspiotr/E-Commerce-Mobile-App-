package pro.kaspiotr.ecommercemobileapp.ui.fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_products.*
import pro.kaspiotr.ecommercemobileapp.R
import pro.kaspiotr.ecommercemobileapp.firestore.FirestoreClass
import pro.kaspiotr.ecommercemobileapp.models.Product
import pro.kaspiotr.ecommercemobileapp.ui.activities.AddProductActivity
import pro.kaspiotr.ecommercemobileapp.ui.adapters.MyProductsListAdapter

class ProductsFragment : BaseFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_products, container, false)

        return root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.add_product_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        when (id) {
            R.id.action_add_product -> {
                startActivity(Intent(activity, AddProductActivity::class.java))
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        getProductsListFromFireStore()
    }

    fun successProductsListFromFireStore(productList: ArrayList<Product>) {
        hideProgressDialog()

        if (productList.size > 0) {
            rv_my_product_items.visibility = View.VISIBLE
            tv_no_products_found.visibility = View.GONE

            rv_my_product_items.layoutManager = LinearLayoutManager(activity)
            rv_my_product_items.setHasFixedSize(true)
            val adapterProducts = MyProductsListAdapter(requireActivity(), productList, this)
            rv_my_product_items.adapter = adapterProducts
        } else {
            rv_my_product_items.visibility = View.GONE
            tv_no_products_found.visibility = View.VISIBLE
        }
    }

    private fun getProductsListFromFireStore() {
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getProductsList(this)
    }

    fun deleteProduct(productID: String) {
        showAlertDialogToDeleteProduct(productID)
    }

    fun productDeleteSuccess() {
        hideProgressDialog()

        Toast.makeText(
            requireActivity(),
            resources.getString(R.string.product_delete_success_message),
            Toast.LENGTH_SHORT
        ).show()

        getProductsListFromFireStore()
    }

    private fun showAlertDialogToDeleteProduct(productID: String) {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(resources.getString(R.string.delete_dialog_title))
        builder.setMessage(resources.getString(R.string.delete_dialog_message))
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        builder.setPositiveButton(resources.getString(R.string.yes)) { dialogInterface, _ ->
            showProgressDialog(resources.getString(R.string.please_wait))

            FirestoreClass().deleteProduct(this, productID)

            dialogInterface.dismiss()
        }

        builder.setNegativeButton(resources.getString(R.string.no)) { dialogInterface, _ ->
            dialogInterface.dismiss()
        }

        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

}