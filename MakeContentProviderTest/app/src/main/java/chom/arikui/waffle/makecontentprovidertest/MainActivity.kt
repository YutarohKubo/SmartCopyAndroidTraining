package chom.arikui.waffle.makecontentprovidertest

import android.content.Context
import android.database.Cursor
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.BaseColumns._ID
import android.view.View
import android.view.ViewGroup
import android.widget.CursorAdapter
import android.widget.TextView
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import chom.arikui.waffle.makecontentprovidertest.MyAddressContract.Companion.CONTENT_URI
import chom.arikui.waffle.makecontentprovidertest.MyAddressContract.MyAddress.Companion.DISPLAY_NAME
import chom.arikui.waffle.makecontentprovidertest.MyAddressContract.MyAddress.Companion.EMAIL_ADDRESS
import chom.arikui.waffle.makecontentprovidertest.MyAddressContract.MyAddress.Companion.PHONE_NUMBER
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.NullPointerException

class MainActivity : AppCompatActivity(), LoaderManager.LoaderCallbacks<Cursor> {

    companion object {
        private const val LOADER_ID = 1
        private val PROJECTIONS = arrayOf(_ID, DISPLAY_NAME, PHONE_NUMBER, EMAIL_ADDRESS)
    }

    private lateinit var mAdapter: CursorAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mAdapter = object : CursorAdapter(applicationContext, null, false) {
            override fun newView(context: Context?, cursor: Cursor?, parent: ViewGroup?) = View.inflate(context, R.layout.layout_address_data_item, null)

            override fun bindView(view: View?, context: Context?, cursor: Cursor?) {
                if (cursor == null) {
                    throw NullPointerException()
                }
                view?.findViewById<TextView>(R.id.address_info1)?.text = cursor.getString(cursor.getColumnIndexOrThrow(_ID))
                view?.findViewById<TextView>(R.id.address_info2)?.text = cursor.getString(cursor.getColumnIndexOrThrow(DISPLAY_NAME))
                view?.findViewById<TextView>(R.id.address_info3)?.text = cursor.getString(cursor.getColumnIndexOrThrow(PHONE_NUMBER))
                view?.findViewById<TextView>(R.id.address_info4)?.text = cursor.getString(cursor.getColumnIndexOrThrow(EMAIL_ADDRESS))
            }
        }
        list_view.adapter = mAdapter
        mAdapter.notifyDataSetChanged()

        LoaderManager.getInstance(this).initLoader(LOADER_ID, null, this)
    }

    override fun onResume() {
        super.onResume()
        LoaderManager.getInstance(this).initLoader(LOADER_ID, null, this)
    }

    override fun onCreateLoader(id: Int, args: Bundle?) = CursorLoader(this, CONTENT_URI, PROJECTIONS, null, null, null)

    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor?) {
        data?.setNotificationUri(contentResolver, CONTENT_URI)
        mAdapter.swapCursor(data)
        mAdapter.notifyDataSetChanged()
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {
        mAdapter.swapCursor(null)
    }
}
