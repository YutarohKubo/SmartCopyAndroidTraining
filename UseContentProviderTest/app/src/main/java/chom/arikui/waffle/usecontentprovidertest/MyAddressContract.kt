package chom.arikui.waffle.usecontentprovidertest

import android.content.ContentResolver
import android.net.Uri
import android.provider.BaseColumns

interface MyAddressContract {

    companion object {
        const val AUTHORITY = "chom.arikui.waffle.mycontentprovider"
        const val TABLE_NAME = "MyAddress"
        val CONTENT_URI: Uri = Uri.parse("${ContentResolver.SCHEME_CONTENT}://${AUTHORITY}/${TABLE_NAME}")
        const val MIME_TYPE_DIR = "vnd.android.cursor.dir/${AUTHORITY}.${TABLE_NAME}"
        const val MIME_TYPE_ITEM = "vnd.android.cursor.item/${AUTHORITY}.${TABLE_NAME}"
    }

    interface MyAddress : BaseColumns {
        companion object{
            const val DISPLAY_NAME = "name"
            const val PHONE_NUMBER = "phone_number"
            const val EMAIL_ADDRESS = "email_address"
        }
    }

}