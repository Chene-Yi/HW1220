package com.example.lab15

import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    private val items = ArrayList<String>()
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var dbrw: SQLiteDatabase
    private lateinit var edBook: EditText
    private lateinit var edPrice: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // UI 元件初始化
        edBook = findViewById(R.id.edBook)
        edPrice = findViewById(R.id.edPrice)
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, items)
        findViewById<ListView>(R.id.listView).adapter = adapter

        // 資料庫初始化
        dbrw = MyDBHelper(this).writableDatabase

        // 設定監聽器
        setListeners()
    }

    override fun onDestroy() {
        super.onDestroy()
        dbrw.close()
    }

    private fun setListeners() {
        findViewById<Button>(R.id.btnInsert).setOnClickListener {
            handleDatabaseOperation("INSERT")
        }

        findViewById<Button>(R.id.btnUpdate).setOnClickListener {
            handleDatabaseOperation("UPDATE")
        }

        findViewById<Button>(R.id.btnDelete).setOnClickListener {
            handleDatabaseOperation("DELETE")
        }

        findViewById<Button>(R.id.btnQuery).setOnClickListener {
            queryData()
        }
    }

    private fun handleDatabaseOperation(operation: String) {
        val book = edBook.text.toString().trim()
        val price = edPrice.text.toString().trim()

        if (book.isEmpty() || (operation != "DELETE" && price.isEmpty())) {
            showToast("欄位請勿留空")
            return
        }

        try {
            when (operation) {
                "INSERT" -> {
                    dbrw.execSQL("INSERT INTO myTable(book, price) VALUES(?, ?)", arrayOf(book, price))
                    showToast("新增: $book, 價格: $price")
                }
                "UPDATE" -> {
                    dbrw.execSQL("UPDATE myTable SET price = ? WHERE book LIKE ?", arrayOf(price, book))
                    showToast("更新: $book, 價格: $price")
                }
                "DELETE" -> {
                    dbrw.execSQL("DELETE FROM myTable WHERE book LIKE ?", arrayOf(book))
                    showToast("刪除: $book")
                }
            }
            cleanEditText()
        } catch (e: Exception) {
            showToast("$operation 失敗: ${e.message}")
        }
    }

    private fun queryData() {
        val book = edBook.text.toString().trim()
        val queryString = if (book.isEmpty()) {
            "SELECT * FROM myTable"
        } else {
            "SELECT * FROM myTable WHERE book LIKE ?"
        }

        try {
            val cursor = if (book.isEmpty()) {
                dbrw.rawQuery(queryString, null)
            } else {
                dbrw.rawQuery(queryString, arrayOf(book))
            }

            cursor.use {
                items.clear()
                showToast("共有 ${it.count} 筆資料")

                while (it.moveToNext()) {
                    items.add("書名: ${it.getString(0)}\t\t\t\t價格: ${it.getInt(1)}")
                }
            }
            adapter.notifyDataSetChanged()
        } catch (e: Exception) {
            showToast("查詢失敗: ${e.message}")
        }
    }

    private fun showToast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show()
    }

    private fun cleanEditText() {
        edBook.text.clear()
        edPrice.text.clear()
    }
}
