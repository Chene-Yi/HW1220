package com.example.lab16_1

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
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 初始化 UI 元件
        edBook = findViewById(R.id.edBook)
        edPrice = findViewById(R.id.edPrice)
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, items)
        findViewById<ListView>(R.id.listView).adapter = adapter

        // 初始化資料庫
        dbrw = MyDBHelper(this).writableDatabase

        // 設定按鈕監聽器
        setListeners()
    }

    override fun onDestroy() {
        super.onDestroy()
        dbrw.close()
    }

    /** 設定按鈕監聽器 */
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

    /** 處理通用的資料庫操作 */
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
                    dbrw.execSQL(
                        "INSERT INTO myTable(book, price) VALUES(?, ?)",
                        arrayOf(book, price)
                    )
                    showToast("新增成功: $book, 價格: $price")
                }
                "UPDATE" -> {
                    dbrw.execSQL(
                        "UPDATE myTable SET price = ? WHERE book LIKE ?",
                        arrayOf(price, book)
                    )
                    showToast("更新成功: $book, 價格: $price")
                }
                "DELETE" -> {
                    dbrw.execSQL(
                        "DELETE FROM myTable WHERE book LIKE ?",
                        arrayOf(book)
                    )
                    showToast("刪除成功: $book")
                }
            }
            cleanEditText()
        } catch (e: Exception) {
            showToast("操作失敗: ${e.message}")
        }
    }

    /** 查詢資料庫 */
    private fun queryData() {
        val book = edBook.text.toString().trim()
        val query = if (book.isEmpty()) {
            "SELECT * FROM myTable"
        } else {
            "SELECT * FROM myTable WHERE book LIKE ?"
        }

        try {
            val cursor = if (book.isEmpty()) {
                dbrw.rawQuery(query, null)
            } else {
                dbrw.rawQuery(query, arrayOf(book))
            }

            cursor.use {
                items.clear()
                showToast("共有 ${it.count} 筆資料")
                while (it.moveToNext()) {
                    val name = it.getString(it.getColumnIndexOrThrow("book"))
                    val price = it.getInt(it.getColumnIndexOrThrow("price"))
                    items.add("書名: $name\t\t價格: $price")
                }
                adapter.notifyDataSetChanged()
            }
        } catch (e: Exception) {
            showToast("查詢失敗: ${e.message}")
        }
    }

    /** 清空輸入欄位 */
    private fun cleanEditText() {
        edBook.text.clear()
        edPrice.text.clear()
    }

    /** 顯示 Toast 訊息 */
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}
