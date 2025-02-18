package com.example.invoiceandroidapp

import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.UnderlineSpan
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.invoiceandroidapp.Connection.CrudApp
import com.example.invoiceandroidapp.Connection.RetrofitClient
import com.example.invoiceandroidapp.data.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import retrofit2.Retrofit


class SignupActivity : AppCompatActivity() {

    private lateinit var retrofit: Retrofit
    private lateinit var crudApp: CrudApp

    fun injectCrudApp(crudApp: CrudApp) {
        this.crudApp = crudApp
    }

    val mainScope: CoroutineScope = CoroutineScope(Job() + Dispatchers.Main)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        retrofit = RetrofitClient.create()
        val crud = retrofit.create(CrudApp::class.java)
        crudApp = retrofit.create(CrudApp::class.java)

        val tvlogin = findViewById<TextView>(R.id.Loginhere)
        val underlinedText = "Login Here."
        val content = SpannableString(underlinedText)
        content.setSpan(UnderlineSpan(), 0, underlinedText.length, Spanned.SPAN_INCLUSIVE_INCLUSIVE)

        val password = findViewById<EditText>(R.id.password)
        val confirmPassword = findViewById<EditText>(R.id.confirm_password)
        val tvmatch = findViewById<TextView>(R.id.matcherror)
        confirmPassword.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val confirmPassword = s.toString()
                val password = password.text.toString()
                if (confirmPassword != password) {
                    tvmatch.text = "Passwords do not match!"
                } else {
                    tvmatch.text = null
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })


        tvlogin.text = content
        tvlogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }


        findViewById<Button>(R.id.btsignup).setOnClickListener {
            mainScope.launch {
                val userName = findViewById<EditText>(R.id.username)
                val email = findViewById<EditText>(R.id.email)
                val password = findViewById<EditText>(R.id.password)
                val confirmPassword = findViewById<EditText>(R.id.confirm_password)

                if (userName.text.isNullOrBlank() || email.text.isNullOrBlank() ||
                    password.text.isNullOrBlank() || confirmPassword.text.isNullOrBlank()) {
                    Toast.makeText(this@SignupActivity, "All fields are required!", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                if (password.text.toString() != confirmPassword.text.toString()) {
                    Toast.makeText(this@SignupActivity, "Passwords do not match!", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                if (!isValidPassword(password.text.toString())) {
                    return@launch
                }
                if (!isValidEmail(email.text.toString())) {
                    Toast.makeText(this@SignupActivity, "Invalid email format!", Toast.LENGTH_SHORT).show()
                    return@launch
                }
                val user = User(userName.text.toString(),email.text.toString(),password.text.toString())
                val response = crudApp.createUser(user)

                if (response.isSuccessful) {
                    val statusCode = response.body() ?: -1
                    Log.i("@Responsecode","res:$statusCode")

                    when (statusCode) {
                        1 -> {
                            Toast.makeText(this@SignupActivity, "User created successfully!", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this@SignupActivity, LoginActivity::class.java)
                            startActivity(intent)
                        }

                        else -> Toast.makeText(this@SignupActivity, "An unknown conflict occurred.", Toast.LENGTH_SHORT).show()
                    }

                } else {
                    val errorBody = response.errorBody()?.string() ?: "0"
                    val ercode = Integer.parseInt(errorBody)
                    when(ercode){
                        3 -> Toast.makeText(this@SignupActivity, "Username already exists!", Toast.LENGTH_SHORT).show()
                        2 -> Toast.makeText(this@SignupActivity, "Email already exists!", Toast.LENGTH_SHORT).show()
                        else -> Log.i("@usercreation","unexpected error code")
                    }
                }


            }
        }
    }

    fun isValidEmail(email: String): Boolean {
        val emailPattern = "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}"
        val pattern = Regex(emailPattern)
        return pattern.matches(email)
    }



    fun isValidPassword(password: String): Boolean {
        val hasMinLength = password.length >= 8
        val hasUpperCase = password.any { it.isUpperCase() }
        val hasLowerCase = password.any { it.isLowerCase() }
        val hasNumber = password.any { it.isDigit() }
        val hasSpecialChar = password.isSpecialChar()

        if (!hasMinLength) {
            Toast.makeText(this@SignupActivity, "Password must be at least 8 characters!", Toast.LENGTH_SHORT).show()
            return false
        }

        if (!hasUpperCase) {
            Toast.makeText(this@SignupActivity, "Password must contain at least one uppercase letter (A-Z)", Toast.LENGTH_SHORT).show()
            return false
        }

        if (!hasLowerCase) {
            Toast.makeText(this@SignupActivity, "Password must contain at least one lowercase letter (a-z)", Toast.LENGTH_SHORT).show()
            return false
        }

        if (!hasNumber) {
            Toast.makeText(this@SignupActivity, "Password must contain at least one number (0-9)", Toast.LENGTH_SHORT).show()
            return false
        }

        if (!hasSpecialChar) {
            Toast.makeText(this@SignupActivity, "Password must contain at least one special character (!@#$%^&*)", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun String.isSpecialChar(): Boolean {
        val specialChars = "!@#$%^&*"
        return any { it in specialChars }
    }
}