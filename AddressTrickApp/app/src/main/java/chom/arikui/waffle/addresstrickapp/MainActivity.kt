package chom.arikui.waffle.addresstrickapp

import android.Manifest.permission.READ_CONTACTS
import android.Manifest.permission.WRITE_CONTACTS
import android.content.ContentValues
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.database.ContentObserver
import android.database.Cursor
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.BaseColumns
import android.provider.ContactsContract
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_main.*
import java.util.jar.Manifest

class MainActivity : AppCompatActivity() {

    companion object {
        private const val ADDRESS_READ_PERMISSION_CODE = 1
        private const val ADDRESS_WRITE_PERMISSION_CODE = 2
    }

    private val listAddressData = mutableListOf<AddressData>()
    private val listAddressAdapter = AddressListAdapter(this, listAddressData)
    private val uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI

    private val mContentObserver = object : ContentObserver(Handler()) {
        override fun onChange(selfChange: Boolean) {
            super.onChange(selfChange)
            renewList()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkAddressReadPermission()
    }

    private fun getContactCursor(): Cursor? {
        //"content://com.android.contacts/data/phones"
        val projection = arrayOf(
            ContactsContract.CommonDataKinds.Phone._ID,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER
        )
        return contentResolver.query(uri, projection, null, null, null)
    }

    private fun renewList() {
        listAddressData.clear()
        val cursor = getContactCursor() ?: return
        while (cursor.moveToNext()) {
            val id = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone._ID))
            val name = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
            val phoneNumber = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER))
            listAddressData.add(AddressData(id, name, phoneNumber))
        }
        listAddressAdapter.notifyDataSetChanged()
    }

    private fun startApp() {
        renewList()
        list_address.adapter = listAddressAdapter

        button_trick.setOnClickListener {
            val cv = ContentValues()
            cv.put(ContactsContract.Data.DATA1, "hehehehe")
            cv.put(ContactsContract.Data.DATA2, "777")
            contentResolver.insert(uri, cv)
        }

        contentResolver.registerContentObserver(uri, true, mContentObserver)
    }

    private fun checkAddressReadPermission() {
        if (ActivityCompat.checkSelfPermission(this, READ_CONTACTS) == PERMISSION_GRANTED) {
            checkAddressWritePermission()
        } else {
            requestAddressReadPermission()
        }
    }

    private fun requestAddressReadPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(READ_CONTACTS), ADDRESS_READ_PERMISSION_CODE)
    }

    private fun checkAddressWritePermission() {
        if (ActivityCompat.checkSelfPermission(this, WRITE_CONTACTS) == PERMISSION_GRANTED) {
            startApp()
        } else {
            requestAddressWritePermission()
        }
    }

    private fun requestAddressWritePermission() {
        ActivityCompat.requestPermissions(this, arrayOf(WRITE_CONTACTS), ADDRESS_WRITE_PERMISSION_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            ADDRESS_READ_PERMISSION_CODE -> {
                if (ActivityCompat.checkSelfPermission(this, READ_CONTACTS) == PERMISSION_GRANTED) {
                    checkAddressWritePermission()
                } else {
                    finish()
                }
            }
            ADDRESS_WRITE_PERMISSION_CODE -> {
                if (ActivityCompat.checkSelfPermission(this, WRITE_CONTACTS) == PERMISSION_GRANTED) {
                    startApp()
                } else {
                    finish()
                }
            }
        }
    }
}
