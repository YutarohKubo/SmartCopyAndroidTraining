package chom.arikui.waffle.usecontentprovidertest

import android.content.ContentValues
import android.database.ContentObserver
import android.database.Cursor
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.BaseColumns._ID
import android.provider.ContactsContract
import chom.arikui.waffle.usecontentprovidertest.MyAddressContract.Companion.CONTENT_URI
import chom.arikui.waffle.usecontentprovidertest.MyAddressContract.MyAddress.Companion.DISPLAY_NAME
import chom.arikui.waffle.usecontentprovidertest.MyAddressContract.MyAddress.Companion.EMAIL_ADDRESS
import chom.arikui.waffle.usecontentprovidertest.MyAddressContract.MyAddress.Companion.PHONE_NUMBER
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val listAddressData = mutableListOf<AddressData>()
    private val listAddressAdapter = AddressListAdapter(this, listAddressData)

    private val mContentObserver = object : ContentObserver(Handler()) {
        override fun onChange(selfChange: Boolean) {
            super.onChange(selfChange)
            renewList()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        renewList()
        list_address.adapter = listAddressAdapter

        button_insert.setOnClickListener {
            val cv = ContentValues()
            cv.put(DISPLAY_NAME, edit_name.text.toString())
            cv.put(PHONE_NUMBER, edit_phone_num.text.toString())
            cv.put(EMAIL_ADDRESS, edit_email.text.toString())
            contentResolver.insert(CONTENT_URI, cv)
        }

        button_delete.setOnClickListener {

        }

        contentResolver.registerContentObserver(CONTENT_URI, true, mContentObserver)
    }

    private fun getContactCursor(): Cursor? {
        //"content://com.android.contacts/data/phones"
        val uri = CONTENT_URI
        val projection = arrayOf(
            _ID,
            DISPLAY_NAME,
            PHONE_NUMBER,
            EMAIL_ADDRESS
        )
        return contentResolver.query(uri, projection, null, null, null)
    }

    private fun renewList() {
        listAddressData.clear()
        val cursor = getContactCursor() ?: return
        while (cursor.moveToNext()) {
            //ContactsContract.CommonDataKinds.Phone.CONTENT_URI
            //val id = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.CONTACT_ID))
            //val name = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
            //val phoneNumber = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER))
            //val data = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DATA))
            val id = cursor.getString(cursor.getColumnIndexOrThrow(_ID))
            val name = cursor.getString(cursor.getColumnIndexOrThrow(DISPLAY_NAME))
            val phoneNumber = cursor.getString(cursor.getColumnIndexOrThrow(PHONE_NUMBER))
            val data = cursor.getString(cursor.getColumnIndexOrThrow(EMAIL_ADDRESS))
            listAddressData.add(AddressData(id, name, phoneNumber, data))
        }
        listAddressAdapter.notifyDataSetChanged()
    }

}
