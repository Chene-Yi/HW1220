package com.example.lab16_2

import android.content.ContentValues
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    private val items = ArrayList<String>()
    private lateinit var adapter: ArrayAdapter<String>

    // 定義 Provider 的 Uri
    private val uri = Uri.parse("content://com.example.lab16")

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

        setListeners()
    }

    /** 設定按鈕監聽器 */
    private fun setListeners() {
        findViewById<Button>(R.id.btnInsert).setOnClickListener { insertData() }
        findViewById<Button>(R.id.btnUpdate).setOnClickListener { updateData() }
        findViewById<Button>(R.id.btnDelete).setOnClickListener { deleteData() }
        findViewById<Button>(R.id.btnQuery).setOnClickListener { queryData() }
    }

    /** 檢查輸入是否有效 */
    private fun validateInput(requirePrice: Boolean = true): Boolean {
        val book = edBook.text.toString().trim()
        val price = edPrice.text.toString().trim()

        if (book.isEmpty() || (requirePrice && price.isEmpty())) {
            showToast("欄位請勿留空")
            return false
        }
        return true
    }

    /** 新增資料 */
    private fun insertData() {
        if (!validateInput()) return

        val values = ContentValues().apply {
            put("book", edBook.text.toString().trim())
            put("price", edPrice.text.toString().trim())
        }

        val contentUri = contentResolver.insert(uri, values)
        if (contentUri != null) {
            showToast("新增成功: ${edBook.text}, 價格: ${edPrice.text}")
            cleanEditText()
        } else {
            showToast("新增失敗")
        }
    }

    /** 更新資料 */
    private fun updateData() {
        if (!validateInput()) return

        val values = ContentValues().apply {
            put("price", edPrice.text.toString().trim())
        }

        val count = contentResolver.update(uri, values, edBook.text.toString().trim(), null)
        if (count > 0) {
            showToast("更新成功: ${edBook.text}, 價格: ${edPrice.text}")
            cleanEditText()
        } else {
            showToast("更新失敗")
        }
    }

    /** 刪除資料 */
    private fun deleteData() {
        if (!validateInput(requirePrice = false)) return

        val count = contentResolver.delete(uri, edBook.text.toString().trim(), null)
        if (count > 0) {
            showToast("刪除成功: ${edBook.text}")
            cleanEditText()
        } else {
            showToast("刪除失敗")
        }
    }

    /** 查詢資料 */
    private fun queryData() {
        val name = edBook.text.toString().trim()
        val selection = if (name.isEmpty()) null else "book=?"
        val selectionArgs = if (name.isEmpty()) null else arrayOf(name)

        val cursor = contentResolver.query(uri, null, selection, selectionArgs, null)

        cursor?.use {
            items.clear()
            showToast("共有 ${it.count} 筆資料")
            while (it.moveToNext()) {
                val book = it.getString(it.getColumnIndexOrThrow("book"))
                val price = it.getInt(it.getColumnIndexOrThrow("price"))
                items.add("書名: $book\t\t價格: $price")
            }
            adapter.notifyDataSetChanged()
        } ?: showToast("查詢失敗")
    }

    /** 顯示 Toast 訊息 */
    private fun showToast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show()
    }

    /** 清空輸入欄位 */
    private fun cleanEditText() {
        edBook.text.clear()
        edPrice.text.clear()
    }
}
