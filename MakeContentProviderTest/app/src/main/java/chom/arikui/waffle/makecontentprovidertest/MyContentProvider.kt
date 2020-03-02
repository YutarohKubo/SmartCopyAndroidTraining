package chom.arikui.waffle.makecontentprovidertest

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.DatabaseErrorHandler
import android.net.Uri
import android.provider.BaseColumns._ID
import android.util.Log
import chom.arikui.waffle.makecontentprovidertest.MyAddressContract.Companion.AUTHORITY
import chom.arikui.waffle.makecontentprovidertest.MyAddressContract.Companion.MIME_TYPE_DIR
import chom.arikui.waffle.makecontentprovidertest.MyAddressContract.Companion.MIME_TYPE_ITEM
import chom.arikui.waffle.makecontentprovidertest.MyAddressContract.Companion.TABLE_NAME
import chom.arikui.waffle.makecontentprovidertest.MyAddressDbHelper.Companion.DB_NAME
import chom.arikui.waffle.makecontentprovidertest.MyAddressDbHelper.Companion.DB_VERSION

class MyContentProvider : ContentProvider() {

    companion object {
        const val TAG = "MyContentProvider"
        val uriMatcher: UriMatcher = UriMatcher(UriMatcher.NO_MATCH).apply {
            addURI(AUTHORITY, TABLE_NAME, ROW_DIR)
            addURI(AUTHORITY, "$TABLE_NAME/#", ROW_ITEM)
        }
        val ROW_DIR = 1
        val ROW_ITEM = 2

    }

    private lateinit var mDbHelper: MyAddressDbHelper

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        var count = 0
        when (uriMatcher.match(uri)) {
            ROW_ITEM -> {
                val id = ContentUris.parseId(uri) as Int
                synchronized(mDbHelper) {
                    val db = mDbHelper.writableDatabase
                    count = db.delete(TABLE_NAME, "$_ID=?", arrayOf(id.toString()))
                    if (count > 0) {
                        context?.contentResolver?.notifyChange(uri, null)
                    }
                }
            }
            ROW_DIR -> {
                synchronized(mDbHelper) {
                    val db = mDbHelper.writableDatabase
                    count = db.delete(TABLE_NAME, selection, selectionArgs)
                    if (count > 0) {
                        context?.contentResolver?.notifyChange(uri, null)
                    }
                }
            }
            else -> {
                throw IllegalArgumentException("引数のURIが間違っています")
            }
        }
        return count
    }

    override fun getType(uri: Uri) = when (uriMatcher.match(uri)) {
        ROW_DIR -> MIME_TYPE_DIR
        ROW_ITEM -> MIME_TYPE_ITEM
        else -> null
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        val resultUri: Uri
        when (uriMatcher.match(uri)) {
            ROW_DIR -> {
                synchronized(mDbHelper) {
                    val db = mDbHelper.writableDatabase
                    val lastId = db.insert(TABLE_NAME, null, values)
                    resultUri = ContentUris.withAppendedId(uri, lastId)

                    context?.contentResolver?.notifyChange(resultUri, null)
                }
            }
            else -> {
                throw IllegalArgumentException("引数のURIが間違っています")
            }
        }
        return resultUri
    }

    override fun onCreate(): Boolean {
        Log.i(TAG, "MyContentProvider onCreate")
        mDbHelper =
            MyAddressDbHelper(context!!, DB_NAME, null, DB_VERSION, DatabaseErrorHandler { dbObj ->
                run {
                    val path = dbObj.path
                    context!!.deleteFile(path)
                }
            })
        return true
    }

    override fun query(
        uri: Uri, projection: Array<String>?, selection: String?,
        selectionArgs: Array<String>?, sortOrder: String?
    ): Cursor? {
        val cursor: Cursor
        when (uriMatcher.match(uri)) {
            ROW_DIR -> {
                synchronized(mDbHelper) {
                    val db = mDbHelper.writableDatabase
                    cursor = db.query(
                        TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                    )
                }
            }
            ROW_ITEM -> {
                synchronized(mDbHelper) {
                    val id = ContentUris.parseId(uri)
                    val db = mDbHelper.writableDatabase
                    cursor =
                        db.query(
                            TABLE_NAME,
                            projection,
                            _ID,
                            arrayOf(id.toString()),
                            null,
                            null,
                            null
                        )
                }
            }
            else -> {
                throw IllegalArgumentException("引数のURIが間違っています")
            }
        }
        return cursor
    }

    override fun update(
        uri: Uri, values: ContentValues?, selection: String?,
        selectionArgs: Array<String>?
    ): Int {
        var count = 0
        when (uriMatcher.match(uri)) {
            ROW_ITEM -> {
                val id = ContentUris.parseId(uri) as Int
                synchronized(mDbHelper) {
                    val db = mDbHelper.writableDatabase
                    count = db.update(TABLE_NAME, values, "$_ID=?", arrayOf(id.toString()))
                    if (count > 0) {
                        context?.contentResolver?.notifyChange(uri, null)
                    }
                }
            }
            ROW_DIR -> {
                synchronized(mDbHelper) {
                    val db = mDbHelper.writableDatabase
                    count = db.update(TABLE_NAME, values, selection, selectionArgs)
                    if (count > 0) {
                        context?.contentResolver?.notifyChange(uri, null)
                    }
                }
            }
            else -> {
                throw IllegalArgumentException("引数のURIが間違っています")
            }
        }
        return count
    }
}
