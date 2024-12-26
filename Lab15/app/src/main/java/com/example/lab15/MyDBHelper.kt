package com.example.lab15

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

// 自訂建構子並繼承 SQLiteOpenHelper 類別
class MyDBHelper(
    context: Context,
    name: String = DB_NAME,
    factory: SQLiteDatabase.CursorFactory? = null,
    version: Int = VERSION
) : SQLiteOpenHelper(context, name, factory, version) {

    companion object {
        private const val DB_NAME = "myDatabase" // 資料庫名稱
        private const val VERSION = 1 // 資料庫版本

        // 表格名稱與欄位名稱
        private const val TABLE_NAME = "myTable"
        private const val COLUMN_BOOK = "book"
        private const val COLUMN_PRICE = "price"

        // 建立資料表的 SQL 語句
        private const val SQL_CREATE_TABLE = """
            CREATE TABLE $TABLE_NAME (
                $COLUMN_BOOK TEXT PRIMARY KEY,
                $COLUMN_PRICE INTEGER NOT NULL
            )
        """

        // 刪除資料表的 SQL 語句
        private const val SQL_DROP_TABLE = "DROP TABLE IF EXISTS $TABLE_NAME"
    }

    override fun onCreate(db: SQLiteDatabase) {
        // 建立資料表
        db.execSQL(SQL_CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // 根據版本差異進行升級邏輯處理
        if (oldVersion < newVersion) {
            db.execSQL(SQL_DROP_TABLE)
            onCreate(db)
        }
    }

    // 提供一個清除資料表的方法（選擇性）
    fun clearTable() {
        writableDatabase.execSQL("DELETE FROM $TABLE_NAME")
    }

    // 提供一個檢查資料表是否存在的方法（選擇性）
    fun isTableExists(): Boolean {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT name FROM sqlite_master WHERE type='table' AND name=?",
            arrayOf(TABLE_NAME)
        )
        val exists = cursor.count > 0
        cursor.close()
        return exists
    }
}
