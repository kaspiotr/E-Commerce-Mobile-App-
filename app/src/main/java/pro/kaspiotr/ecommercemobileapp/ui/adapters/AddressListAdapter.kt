package pro.kaspiotr.ecommercemobileapp.ui.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_address_layout.view.*
import pro.kaspiotr.ecommercemobileapp.R
import pro.kaspiotr.ecommercemobileapp.models.Address
import pro.kaspiotr.ecommercemobileapp.ui.activities.AddEditAddressActivity
import pro.kaspiotr.ecommercemobileapp.ui.activities.CheckoutActivity
import pro.kaspiotr.ecommercemobileapp.utils.Constants

open class AddressListAdapter(
    private val context: Context,
    private var list: ArrayList<Address>,
    private val selectAddress: Boolean
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return MyViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_address_layout,
                parent,
                false
            )
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        if (holder is MyViewHolder) {
            holder.itemView.tv_address_full_name.text = model.name
            holder.itemView.tv_address_type.text = model.type
            holder.itemView.tv_address_details.text = "${model.address}, ${model.zipCode}"
            holder.itemView.tv_address_mobile_number.text = model.mobileNumber

            if (selectAddress) {
                holder.itemView.setOnClickListener {
                    val intent = Intent(context, CheckoutActivity::class.java)
                    context.startActivity(intent)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    private class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)

    fun notifyEditItem(activity: Activity, position: Int) {
        val intent = Intent(context, AddEditAddressActivity::class.java)
        intent.putExtra(Constants.EXTRA_ADDRESS_DETAILS, list[position])
        activity.startActivityForResult(intent, Constants.ADD_ADDRESS_REQUEST_CODE)
        notifyItemChanged(position)
    }
}