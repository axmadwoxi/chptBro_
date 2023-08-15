package com.example.chptbro

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.TextView
import android.widget.TextView.OnEditorActionListener
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.RetryPolicy
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.chptbro.Model.ReqvestModel
import com.google.android.material.textfield.TextInputEditText
import org.json.JSONArray
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    // creating variables on below line.
    lateinit var responseTV: TextView
    lateinit var questionTV: TextView
    lateinit var queryEdt: TextInputEditText

    var url = "https://api.openai.com/v1/completions"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // initializing variables on below line.
        responseTV = findViewById(R.id.idTVResponse)
        questionTV = findViewById(R.id.idTVQuestion)
        queryEdt = findViewById(R.id.idEdtQuery)


        findViewById<Button>(R.id.send_btn).setOnClickListener {// setting response tv on below line.
            responseTV.text = "Please wait.."
            // validating text
            if (queryEdt.text.toString().length > 0) {
                // calling get response to get the response.
                getResponse(queryEdt.text.toString()){
                    questionTV.text = queryEdt.text.toString()
                    getResponse(queryEdt.text.toString())
                    queryEdt.text = null

                }
            } else {
                Toast.makeText(this, "Please enter your query..", Toast.LENGTH_SHORT).show()
            } }

    }


    var queue: RequestQueue? = null
    private fun getResponse(query: String, errorPos: ((position: Boolean) -> Unit)? = null) {
        val name = "Bearer"

        queue = Volley.newRequestQueue(this)
//        val queue: RequestQueue = Volley.newRequestQueue(requireActivity())
        val jsonObject: JSONObject = JSONObject()
        jsonObject.put("model", "gpt-3.5-turbo")
        val list = JSONArray()
        val obj = JSONObject().apply {
            put("role", "user")
            put("content", query)
        }
        list.put(obj)
        jsonObject.put("messages", list)
        val postRequest: JsonObjectRequest =
            object : JsonObjectRequest(Method.POST,"https://api.openai.com/v1/chat/completions", jsonObject,
                Response.Listener { response ->
                    errorPos?.invoke(false)
                    val res =
                        response.getJSONArray("choices").getJSONObject(0).getJSONObject("message")
                    val resultMsg = ReqvestModel(msg = res.getString("content").trim())
                    responseTV.text = resultMsg.msg
                },
                // adding on error listener
                Response.ErrorListener { error ->

                    Toast.makeText(this, "xatalik bor:${error.message}", Toast.LENGTH_SHORT).show()

                }) {
                override fun getHeaders(): MutableMap<String, String> {
                    val params: MutableMap<String, String> = HashMap()
                    // adding headers on below line.
                    params["Content-Type"] = "application/json"
                    params["Authorization"] = "Bearer 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"
                    return params;
                }
            }

        // on below line adding retry policy for our request.
        postRequest.retryPolicy = object : RetryPolicy {
            override fun getCurrentTimeout(): Int {
                return 50000
            }

            override fun getCurrentRetryCount(): Int {
                return 50000
            }

            @Throws(VolleyError::class)
            override fun retry(error: VolleyError) {
            }
        }
        queue!!.add(postRequest)
    }
}
