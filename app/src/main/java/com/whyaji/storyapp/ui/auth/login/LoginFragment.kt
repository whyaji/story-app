package com.whyaji.storyapp.ui.auth.login

import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.whyaji.storyapp.R
import com.whyaji.storyapp.data.model.UserModel
import com.whyaji.storyapp.util.Resource
import com.whyaji.storyapp.data.remote.response.LoginResponse
import com.whyaji.storyapp.databinding.FragmentLoginBinding
import com.whyaji.storyapp.ui.setting.SettingActivity
import com.whyaji.storyapp.util.Preference
import com.whyaji.storyapp.util.ViewModelFactory

class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val loginViewModel: LoginViewModel by viewModels {
        ViewModelFactory(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentLoginBinding.inflate(inflater, container, false)

        when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> {
                binding.imageView.setImageResource(R.drawable.ic_story)
            }
            else -> {
                binding.imageView.setImageResource(R.drawable.ic_story_black)
            }
        }

        playAnimation()
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvLoginDontHaveAccount.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_loginFragment_to_registerFragment))
        binding.fabSetting.setOnClickListener{
            val intent = Intent(requireActivity(), SettingActivity::class.java)
            startActivity(intent)
        }
        binding.btLogin.setOnClickListener{
            val email = binding.edLoginEmail.text.toString()
            val password = binding.edLoginPassword.text.toString()
            when {
                email.isEmpty() -> {
                    binding.edLoginEmail.error = TextUtils.concat(getString(R.string.email_empty), "\u200B")
                }
                password.isEmpty() -> {
                    binding.edLoginPassword.error = TextUtils.concat(getString(R.string.password_empty), "\u200B")
                }
            }
            val inputMethodManager = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(it.windowToken, 0)

            loginViewModel.login(email, password).observe(requireActivity()) { result ->
                if (result != null) {
                    when(result) {
                        is Resource.Loading -> {
                            showLoading(true)
                        }
                        is Resource.Success -> {
                            processLogin(result.data)
                            showLoading(false)
                        }
                        is Resource.Error -> {
                            showLoading(false)
                            Toast.makeText(requireContext(), R.string.error_login, Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
    }

    private fun processLogin(data: LoginResponse) {
        when {
            data.error -> Toast.makeText(requireContext(), data.message, Toast.LENGTH_LONG).show()
            else -> {
                val preference = Preference(requireActivity())
                val userModel = UserModel(data.loginResult.userId, data.loginResult.name,
                    data.loginResult.token
                )
                preference.setUser(userModel)
                findNavController().navigate(R.id.action_loginFragment_to_mainActivity)
                requireActivity().finish()
            }
        }
    }

    private fun showLoading(state: Boolean) {
        binding.pbLogin.isVisible = state
        binding.edLoginEmail.isInvisible = state
        binding.edLoginPassword.isInvisible = state
        binding.btLogin.isInvisible = state
        binding.textView6.isInvisible = state
        binding.tvLoginDontHaveAccount.isInvisible = state
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}