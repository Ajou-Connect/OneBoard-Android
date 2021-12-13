package kr.khs.oneboard.ui

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import kr.khs.oneboard.R
import kr.khs.oneboard.databinding.ActivityLoginBinding
import kr.khs.oneboard.utils.*
import kr.khs.oneboard.viewmodels.LoginViewModel

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    private val binding: ActivityLoginBinding by lazy {
        ActivityLoginBinding.inflate(
            layoutInflater
        )
    }
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        UserInfoUtil.setOnBoarding(this, true)

        initLoginButton()
        initEditTextWatcher()

        viewModel.loginYN.observe(this) {
            if (it.result == SUCCESS) {
                startActivity(
                    Intent(this, MainActivity::class.java)
                )
                UserInfoUtil.setToken(applicationContext, it.data.token)
            } else
                ToastUtil.shortToast(
                    applicationContext,
                    getString(R.string.login_error_id_pw_error)
                )
        }

        viewModel.isEmail.observe(this) {
            binding.buttonLogin.isEnabled = it && viewModel.isPassword.value!!
        }

        viewModel.isPassword.observe(this) {
            binding.buttonLogin.isEnabled = it && viewModel.isEmail.value!!
        }

        viewModel.isLoading.observe(this) {
            if (it) {
                DialogUtil.onLoadingDialog(this)
            } else {
                DialogUtil.offLoadingDialog()
            }
        }

        viewModel.toastMessage.observe(this) {
            if (it != "") {
                ToastUtil.shortToast(applicationContext, it)
                viewModel.setToastMessage()
            }
        }
    }

    private fun initEditTextWatcher() {
        binding.editTextLoginId.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                with(s.toString()) {
                    binding.editTextLoginId.error = when {
                        isEmpty() -> {
                            setEmailValid(false)
                            getString(R.string.login_error_input_email)
                        }
                        PatternUtil.checkEmailPattern(this) -> {
                            setEmailValid(true)
                            null
                        }
                        else -> {
                            setEmailValid(false)
                            getString(R.string.login_error_not_email_pattern)
                        }
                    }
                }
            }

        })

        binding.editTextLoginPw.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                binding.editTextLoginPw.error = if (s.toString().isEmpty()) {
                    setPWValid(false)
                    getString(R.string.login_error_input_password)
                } else {
                    setPWValid(true)
                    null
                }
            }

        })
    }

    private fun initLoginButton() {
        binding.buttonLogin.setOnClickListener {
            val id = binding.editTextLoginId.text.toString()
            val pw = binding.editTextLoginPw.text.toString()
            if (id.isNotBlank() && pw.isNotBlank()) {
                viewModel.login(id, pw)
            } else {
                ToastUtil.shortToast(applicationContext, "아이디, 비밀번호를 입력해주세요.")
            }
        }
    }

    fun setEmailValid(isValid: Boolean = true) {
        viewModel.isEmail.value = isValid
    }

    fun setPWValid(isValid: Boolean = true) {
        viewModel.isPassword.value = isValid
    }
}