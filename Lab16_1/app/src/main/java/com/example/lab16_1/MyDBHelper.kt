package com.example.lab16_1

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class MyDBHelper(
    context: Context,
    name: String = DB_NAME,
    factory: SQLiteDatabase.CursorFactory? = null,
    version: Int = VERSION
) : SQLiteOpenHelper(context, name, factory, version) {

    companion object {
        private const val DB_NAME = "myDatabase" // 資料庫名稱
        private const val VERSION = 1 // 資料庫版本

        // 表格名稱和欄位名稱
        private const val TABLE_NAME = "myTable"
        private const val COLUMN_BOOK = "book"
        private const val COLUMN_PRICE = "price"

        // SQL 指令
        private const val SQL_CREATE_TABLE = """
            CREATE TABLE $TABLE_NAME (
                $COLUMN_BOOK TEXT PRIMARY KEY,
                $COLUMN_PRICE INTEGER NOT NULL
            )
        """

        private const val SQL_DROP_TABLE = "DROP TABLE IF EXISTS $TABLE_NAME"
    }

    /** 初始化資料庫 */
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_TABLE)
    }

    /** 資料庫升級邏輯 */
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < newVersion) {
            db.execSQL(SQL_DROP_TABLE)
            onCreate(db)
        }
    }

    /** 清空資料表 */
    fun clearTable() {
        writableDatabase.execSQL("DELETE FROM $TABLE_NAME")
    }

    /** 檢查資料表是否存在 */
    fun isTableExists(): Boolean {
        val cursor = readableDatabase.rawQuery(
            "SELECT name FROM sqlite_master WHERE type='table' AND name=?",
            arrayOf(TABLE_NAME)
        )
        val exists = cursor.count > 0
        cursor.close()
        return exists
    }

    /** 新增資料 */
    fun insertData(book: String, price: Int): Boolean {
        return try {
            writableDatabase.execSQL(
                "INSERT INTO $TABLE_NAME ($COLUMN_BOOK, $COLUMN_PRICE) VALUES (?, ?)",
                arrayOf(book, price)
            )
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /** 更新資料 */
    fun updateData(book: String, price: Int): Boolean {
        return try {
            writableDatabase.execSQL(
                "UPDATE $TABLE_NAME SET $COLUMN_PRICE = ? WHERE $COLUMN_BOOK = ?",
                arrayOf(price, book)
            )
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /** 刪除資料 */
    fun deleteData(book: String): Boolean {
        return try {
            writableDatabase.execSQL(
                "DELETE FROM $TABLE_NAME WHERE $COLUMN_BOOK = ?",
                arrayOf(book)
            )
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
