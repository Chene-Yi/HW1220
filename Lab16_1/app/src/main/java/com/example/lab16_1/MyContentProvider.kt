package com.example.lab16_1

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.net.Uri

class MyContentProvider : ContentProvider() {

    private lateinit var dbrw: SQLiteDatabase

    companion object {
        private const val TABLE_NAME = "myTable"
        private const val AUTHORITY = "com.example.lab16"
        private const val CONTENT_URI_PREFIX = "content://$AUTHORITY"
    }

    override fun onCreate(): Boolean {
        val context = context ?: return false
        dbrw = MyDBHelper(context).writableDatabase
        return true
    }

    /** 插入資料 */
    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        return try {
            values ?: return null
            val rowId = dbrw.insert(TABLE_NAME, null, values)
            if (rowId > 0) {
                Uri.parse("$CONTENT_URI_PREFIX/$rowId")
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /** 更新資料 */
    override fun update(
        uri: Uri, values: ContentValues?, selection: String?,
        selectionArgs: Array<String>?
    ): Int {
        return try {
            if (values == null || selection == null) return 0
            dbrw.update(TABLE_NAME, values, "book=?", arrayOf(selection))
        } catch (e: Exception) {
            e.printStackTrace()
            0
        }
    }

    /** 刪除資料 */
    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        return try {
            if (selection == null) return 0
            dbrw.delete(TABLE_NAME, "book=?", arrayOf(selection))
        } catch (e: Exception) {
            e.printStackTrace()
            0
        }
    }

    /** 查詢資料 */
    override fun query(
        uri: Uri, projection: Array<String>?, selection: String?,
        selectionArgs: Array<String>?, sortOrder: String?
    ): Cursor? {
        return try {
            dbrw.query(
                TABLE_NAME,
                projection,
                if (selection != null) "book=?" else null,
                if (selection != null) arrayOf(selection) else null,
                null,
                null,
                sortOrder
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun getType(uri: Uri): String? = null
}
