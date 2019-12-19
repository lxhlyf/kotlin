package com.example.network.util

import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.io.PrintWriter
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

object HttpUrlConUtils {

    fun sendGetRequest(url: String, charset: String = "utf-8"): String {
        var resultBuffer = StringBuffer()
        var httpURLConnection = URL(url).openConnection() as HttpURLConnection
        httpURLConnection.setRequestProperty("Accept-Charset", charset)
        httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
        //用于欺骗服务器
        httpURLConnection.setRequestProperty("user-agent",
                "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)")
        if (httpURLConnection.responseCode >= 300) throw Exception("HTTP Request is not success, Response code is ${httpURLConnection.responseCode}")
        val inputStream = httpURLConnection.inputStream
        val inputStreamReader = InputStreamReader(inputStream, charset)
        val reader = BufferedReader(inputStreamReader)
        reader.use { r ->
            var temp = r.readLine()
            if (temp != null) resultBuffer.append(temp)
        }
        reader.close()
        inputStreamReader.close()
        inputStream.close()
        return resultBuffer.toString()
    }

    fun sendPostRequest(reqBody: String, url: String, charset: String = "utf-8"): String {
        val bufferResult = StringBuffer()
        val conn = URL(url).openConnection()
        conn.setRequestProperty("accept", "*/*")
        conn.setRequestProperty("connection", "Keep-Alive")
        conn.setRequestProperty("user-agent",
                "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)")
        // 发送POST请求必须设置如下两行
        conn.setDoOutput(true)
        conn.setDoInput(true)

        val out = PrintWriter(OutputStreamWriter(conn.outputStream, charset))
        //发送请求
        out.print(reqBody)
        //flush 输出流缓冲
        out.flush()
        val inStream = BufferedReader(InputStreamReader(conn.inputStream, charset))
        inStream.use { r ->
            val temp = r.readLine()
            if (temp != null) bufferResult.append(temp)
        }
        out.close()
        inStream.close()

        return bufferResult.toString()
    }

    //拼接参数
    fun getParamStr (params: Map<String, String>): String {
        val str = StringBuffer()
        var temp = ""
        params.keys.map {k ->
            temp = k + "=" + URLEncoder.encode(params.get(k), "utf-8") + "&"
            str.append(temp)
        }
        return str.toString().substring(0, str.toString().count() - 1)
    }
}