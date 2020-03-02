package chom.arikui.waffle.usecontentprovidertest

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class AddressListAdapter(private val context: Context, private val dataList: List<AddressData>) : BaseAdapter() {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val item = getItem(position) as AddressData
        var tmpConvertView = convertView
        if (tmpConvertView == null) {
            tmpConvertView = LayoutInflater.from(context).inflate(R.layout.layout_address_data_item, null)
        }
        val textAddressInfo1 = tmpConvertView!!.findViewById<TextView>(R.id.address_info1)
        val textAddressInfo2 = tmpConvertView.findViewById<TextView>(R.id.address_info2)
        val textAddressInfo3 = tmpConvertView.findViewById<TextView>(R.id.address_info3)
        val textAddressInfo4 = tmpConvertView.findViewById<TextView>(R.id.address_info4)
        textAddressInfo1.text = item.id
        textAddressInfo2.text = item.name
        textAddressInfo3.text = item.emailAddress
        textAddressInfo4.text = item.phoneNum
        return tmpConvertView
    }

    override fun getItem(position: Int): Any {
        return dataList[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getCount(): Int {
        return dataList.size
    }

}