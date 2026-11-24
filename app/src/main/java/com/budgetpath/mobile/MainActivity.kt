package com.budgetpath.mobile

import android.app.Activity
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.example.budgetpath.R

class MainActivity : Activity() {

    private lateinit var api: AuthApi
    private lateinit var screenTitle: TextView
    private lateinit var errorText: TextView
    private lateinit var checkingText: TextView
    private lateinit var authForm: View
    private lateinit var authSwitch: View
    private lateinit var confirmGroup: View
    private lateinit var accountContent: View
    private lateinit var usernameInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var confirmInput: EditText
    private lateinit var submitButton: Button
    private lateinit var switchButton: Button
    private lateinit var switchPrompt: TextView
    private lateinit var signedInText: TextView
    private lateinit var logoutButton: Button
    private var registerMode = false
    private var busy = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        api = AuthApi()
        screenTitle = findViewById(R.id.screenTitle)
        errorText = findViewById(R.id.errorText)
        checkingText = findViewById(R.id.checkingText)
        authForm = findViewById(R.id.authForm)
        authSwitch = findViewById(R.id.authSwitch)
        confirmGroup = findViewById(R.id.confirmGroup)
        accountContent = findViewById(R.id.accountContent)
        usernameInput = findViewById(R.id.usernameInput)
        passwordInput = findViewById(R.id.passwordInput)
        confirmInput = findViewById(R.id.confirmInput)
        submitButton = findViewById(R.id.submitButton)
        switchButton = findViewById(R.id.switchButton)
        switchPrompt = findViewById(R.id.switchPrompt)
        signedInText = findViewById(R.id.signedInText)
        logoutButton = findViewById(R.id.logoutButton)
        submitButton.setOnClickListener { submitCredentials() }
        switchButton.setOnClickListener {
            if (!busy) {
                usernameInput.text.clear()
                passwordInput.text.clear()
                confirmInput.text.clear()
                showForm(!registerMode)
            }
        }
        logoutButton.setOnClickListener { logout() }
        checkNetworkPermission()
    }

    private fun checkNetworkPermission() {
        if (Build.VERSION.SDK_INT >= 37 &&
            checkSelfPermission(Manifest.permission.ACCESS_LOCAL_NETWORK)
            != PackageManager.PERMISSION_GRANTED) {

            showForm(false)

            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_LOCAL_NETWORK),
                100
            )
        } else {
            checkSession()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 100) {
            if (grantResults.isNotEmpty() &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                checkSession()
            } else {
                showForm(false)
                showError("Local network permission is required.")
            }
        }
    }


    private fun checkSession() {
        authForm.visibility = View.GONE
        authSwitch.visibility = View.GONE
        accountContent.visibility = View.GONE
        checkingText.visibility = View.VISIBLE

        Thread {
            val result = api.session()

            runOnUiThread {
                if (!isFinishing && !isDestroyed) {
                    if (result.successful && result.username != null) {
                        showAccount(result.username)
                    } else {
                        showForm(false)
                        if (result.status != 401 && result.error != null) {
                            showError(result.error)
                        }
                    }
                }
            }
        }.start()
    }

    private fun showForm(register: Boolean) {
        registerMode = register

        checkingText.visibility = View.GONE
        accountContent.visibility = View.GONE
        authForm.visibility = View.VISIBLE
        authSwitch.visibility = View.VISIBLE
        errorText.visibility = View.GONE

        if (register) {
            screenTitle.text = "Register"
            confirmGroup.visibility = View.VISIBLE
            submitButton.text = "Register"
            switchPrompt.text = "Existing user?"
            switchButton.text = "Login"
        } else {
            screenTitle.text = "Login"
            confirmGroup.visibility = View.GONE
            submitButton.text = "Login"
            switchPrompt.text = "New user?"
            switchButton.text = "Register"
        }
    }

    private fun submitCredentials() {
        if (busy) return

        val name = usernameInput.text.toString().trim()
        val password = passwordInput.text.toString()
        val confirmation = confirmInput.text.toString()

        if (name.isEmpty() || password.isEmpty() ||
            (registerMode && confirmation.isEmpty())) {
            showError("All fields are required.")
            return
        }

        if (name.length > 100 || password.length > 100) {
            showError("Username or password is too long.")
            return
        }

        if (registerMode && password != confirmation) {
            showError("The passwords do not match.")
            return
        }

        val isRegistering = registerMode
        setBusy(true)
        errorText.visibility = View.GONE

        Thread {
            val result = if (isRegistering) {
                api.register(name, password, confirmation)
            } else {
                api.login(name, password)
            }

            runOnUiThread {
                if (!isFinishing && !isDestroyed) {
                    setBusy(false)
                    if (result.successful && result.username != null) {
                        usernameInput.text.clear()
                        passwordInput.text.clear()
                        confirmInput.text.clear()
                        hideKeyboard()
                        showAccount(result.username)
                    } else {
                        showError(result.error ?: "Authentication failed.")
                    }
                }
            }
        }.start()
    }

    private fun showAccount(name: String) {
        screenTitle.text = "Account"
        checkingText.visibility = View.GONE
        authForm.visibility = View.GONE
        authSwitch.visibility = View.GONE
        accountContent.visibility = View.VISIBLE
        errorText.visibility = View.GONE
        signedInText.text = "Signed in as $name."
    }

    private fun logout() {
        if (busy) return
        setBusy(true)
        errorText.visibility = View.GONE

        Thread {
            val result = api.logout()

            runOnUiThread {
                if (!isFinishing && !isDestroyed) {
                    setBusy(false)
                    if (result.successful) {
                        showForm(false)
                    } else {
                        showError(result.error ?: "Logout failed.")
                    }
                }
            }
        }.start()
    }

    private fun showError(message: String) {
        errorText.text = message
        errorText.visibility = View.VISIBLE
    }

    private fun setBusy(value: Boolean) {
        busy = value
        submitButton.isEnabled = !value
        switchButton.isEnabled = !value
        logoutButton.isEnabled = !value

        if (value) {
            submitButton.text = "Please wait..."
            logoutButton.text = "Please wait..."
        } else {
            submitButton.text = if (registerMode) "Register" else "Login"
            logoutButton.text = "Logout"
        }
    }

    private fun hideKeyboard() {
        val keyboard = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        keyboard.hideSoftInputFromWindow(passwordInput.windowToken, 0)
        passwordInput.clearFocus()
    }
}
