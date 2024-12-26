package com.example.lab17

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import okhttp3.*
import java.io.IOException

class MainActivity : AppCompatActivity() {
    private lateinit var btnQuery: Button

    // 單例 OkHttpClient，避免多次實例化
    private val client: OkHttpClient by lazy { OkHttpClient() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 設定邊緣安全區域
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 綁定 UI 元件
        btnQuery = findViewById(R.id.btnQuery)
        btnQuery.setOnClickListener {
            btnQuery.isEnabled = false // 防止重複點擊
            setRequest()
        }
    }

    /** 發送 API 請求 */
    private fun setRequest() {
        val url = "https://api.italkutalk.com/api/air"
        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    runOnUiThread {
                        btnQuery.isEnabled = true
                        showToast("伺服器錯誤: ${response.code}")
                    }
                    return
                }

                val json = response.body?.string()
                if (json.isNullOrEmpty()) {
                    runOnUiThread {
                        btnQuery.isEnabled = true
                        showToast("空的回傳結果")
                    }
                    return
                }

                try {
                    val myObject = Gson().fromJson(json, MyObject::class.java)
                    if (myObject.result?.records.isNullOrEmpty()) {
                        runOnUiThread {
                            btnQuery.isEnabled = true
                            showToast("未獲取到有效的資料")
                        }
                        return
                    }
                    showDialog(myObject)
                } catch (e: JsonSyntaxException) {
                    runOnUiThread {
                        btnQuery.isEnabled = true
                        showToast("JSON 解析錯誤: ${e.message}")
                        Log.e("JSON_ERROR", e.message ?: "Unknown error")
                    }
                } catch (e: Exception) {
                    runOnUiThread {
                        btnQuery.isEnabled = true
                        showToast("發生未預期的錯誤: ${e.message}")
                        Log.e("UNEXPECTED_ERROR", e.message ?: "Unknown error")
                    }
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    btnQuery.isEnabled = true
                    showToast("網路請求失敗: ${e.localizedMessage}")
                    Log.e("NETWORK_ERROR", e.localizedMessage ?: "Unknown error")
                }
            }
        })
    }

    /** 顯示 API 結果 */
    private fun showDialog(myObject: MyObject) {
        val items = myObject.result.records.map {
            val siteName = it.siteName ?: "未知地區"
            val status = it.status ?: "未知狀態"
            "地區：$siteName, 狀態：$status"
        }

        runOnUiThread {
            btnQuery.isEnabled = true

            if (items.isEmpty()) {
                showToast("沒有找到任何資料")
                Log.e("API_RESPONSE", "無效的資料：${myObject.result.records}")
            } else {
                AlertDialog.Builder(this@MainActivity)
                    .setTitle("臺北市空氣品質")
                    .setAdapter(
                        ArrayAdapter(this, android.R.layout.simple_list_item_1, items),
                        null
                    )
                    .show()
            }
        }
    }

    /** 顯示 Toast 訊息 */
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}
