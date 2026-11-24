
package com.budgetpath.mobile

import android.webkit.CookieManager
import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

class AuthApi {

    // This address connects the Android emulator to the Node.js server.
    private val baseUrl = "http://10.0.2.2:3002"
    private val cookieManager = CookieManager.getInstance()

    data class Result(
        val status: Int,
        val username: String? = null,
        val error: String? = null
    ) {
        val successful: Boolean
            get() = status in 200..299
    }

    fun session(): Result {
        return sendRequest("GET", "/api/auth/me")
    }

    fun login(username: String, password: String): Result {
        val data = JSONObject()
        data.put("username", username)
        data.put("password", password)
        return sendRequest("POST", "/api/auth/login", data)
    }

    fun register(username: String, password: String, confirmation: String): Result {
        val data = JSONObject()
        data.put("username", username)
        data.put("password", password)
        data.put("confirmPassword", confirmation)
        return sendRequest("POST", "/api/auth/register", data)
    }

    fun logout(): Result {
        return sendRequest("POST", "/api/auth/logout")
    }

    private fun sendRequest(
        method: String,
        path: String,
        data: JSONObject? = null
    ): Result {
        var connection: HttpURLConnection? = null

        try {
            val url = URL(baseUrl + path)
            connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = method
            connection.connectTimeout = 7000
            connection.readTimeout = 7000
            connection.setRequestProperty("Accept", "application/json")

            // Send the saved login cookie with each request.
            val savedCookie = cookieManager.getCookie(baseUrl)
            if (!savedCookie.isNullOrEmpty()) {
                connection.setRequestProperty("Cookie", savedCookie)
            }

            // Login and registration send their form data as JSON.
            if (data != null) {
                connection.doOutput = true
                connection.setRequestProperty("Content-Type", "application/json")
                connection.outputStream.use { output ->
                    output.write(data.toString().toByteArray(Charsets.UTF_8))
                }
            }

            val status = connection.responseCode

            // Save the cookie returned by the server after login/register.
            val newCookie = connection.getHeaderField("Set-Cookie")
            if (newCookie != null) {
                cookieManager.setCookie(baseUrl, newCookie)
                cookieManager.flush()
            }

            // Logout succeeds with HTTP 204, which has no JSON body.
            if (path == "/api/auth/logout" && status in 200..299) {
                cookieManager.setCookie(
                    baseUrl,
                    "budgetpath_js.sid=; Max-Age=0; Path=/"
                )
                cookieManager.flush()
                return Result(status)
            }

            val stream = if (status in 200..299) {
                connection.inputStream
            } else {
                connection.errorStream
            }

            val responseText = stream?.bufferedReader()?.use { reader ->
                reader.readText()
            } ?: ""

            val json = if (responseText.isNotEmpty()) {
                JSONObject(responseText)
            } else {
                JSONObject()
            }

            if (status in 200..299) {
                val name = json.optJSONObject("user")?.optString("username")
                if (!name.isNullOrEmpty()) {
                    return Result(status, username = name)
                }
                return Result(502, error = "Server did not return a username.")
            }

            var message = json.optString("error")
            if (message.isBlank()) {
                message = "Request failed (HTTP $status)."
            }
            return Result(status, error = message)

        } catch (e: IOException) {
            return Result(-1, error = "Cannot connect to the Node.js server.")
        } catch (e: Exception) {
            return Result(-1, error = "Authentication request failed.")
        } finally {
            connection?.disconnect()
        }
    }
}
