package chom.arikui.waffle.makecontentprovidertest

import android.content.Context
import android.database.DatabaseErrorHandler
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns._ID
import android.util.Log
import chom.arikui.waffle.makecontentprovidertest.MyAddressContract.Companion.TABLE_NAME
import chom.arikui.waffle.makecontentprovidertest.MyAddressContract.MyAddress.Companion.DISPLAY_NAME
import chom.arikui.waffle.makecontentprovidertest.MyAddressContract.MyAddress.Companion.EMAIL_ADDRESS
import chom.arikui.waffle.makecontentprovidertest.MyAddressContract.MyAddress.Companion.PHONE_NUMBER

class MyAddressDbHelper(context: Context, name: String, factory: SQLiteDatabase.CursorFactory?, version: Int, errorHandler: DatabaseErrorHandler) :
    SQLiteOpenHelper(context, name, factory, version, errorHandler) {

    companion object {
        const val TAG = "MyAddressDbHelper"
        const val DB_NAME = "MyAddress.db"
        const val DB_VERSION = 6
        const val SQL_CREATE_TABLE = "CREATE TABLE $TABLE_NAME (\n $_ID INTEGER PRIMARY KEY AUTOINCREMENT,\n$DISPLAY_NAME TEXT,\n$PHONE_NUMBER TEXT,\n$EMAIL_ADDRESS TEXT);"
        val SQL_INSERT_INITIAL_DATA = arrayOf(
            "INSERT INTO $TABLE_NAME ($DISPLAY_NAME, $PHONE_NUMBER, $EMAIL_ADDRESS) VALUES ('waffle','09012345678','waffle.com')",
            "INSERT INTO $TABLE_NAME ($DISPLAY_NAME, $PHONE_NUMBER, $EMAIL_ADDRESS) VALUES ('meron','08098765432','meron.com')"
            )
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.beginTransaction()
        try {
            db?.execSQL(SQL_CREATE_TABLE)
            for (sql in SQL_INSERT_INITIAL_DATA) {
                db?.execSQL(sql)
            }
            db?.setTransactionSuccessful()
        } finally {
            db?.endTransaction()
        }
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        Log.i(TAG, "onUpgrade oldVersion=${oldVersion}, newVersion=${newVersion}")
        db?.execSQL( "drop table $TABLE_NAME" )
        onCreate(db)
    }

    override fun onOpen(db: SQLiteDatabase?) {
        super.onOpen(db)
        Log.i(TAG, "ON_OPEN!!")
    }
}