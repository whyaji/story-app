package com.whyaji.storyapp.ui.auth.register

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.whyaji.storyapp.R
import com.whyaji.storyapp.data.remote.response.RegisterResponse
import com.whyaji.storyapp.databinding.FragmentRegisterBinding
import com.whyaji.storyapp.ui.setting.SettingActivity
import com.whyaji.storyapp.util.Resource
import com.whyaji.storyapp.util.ViewModelFactory

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private val registerViewModel: RegisterViewModel by viewModels{
        ViewModelFactory(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)

        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(RegisterFragmentDirections.actionRegisterFragmentToLoginFragment())
            }
        })

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvRegisterHaveAccount.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_registerFragment_to_loginFragment))
        binding.fabSetting.setOnClickListener{
            val intent = Intent(requireActivity(), SettingActivity::class.java)
            startActivity(intent)
        }
        binding.btRegister.setOnClickListener{ it ->
            val name = binding.edRegisterName.text.toString()
            val email = binding.edRegisterEmail.text.toString()
            val password = binding.edRegisterPassword.text.toString()

            val inputMethodManager = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(it.windowToken, 0)

            registerViewModel.register(name, email, password).observe(requireActivity()) {
                if (it != null) {
                    when(it) {
                        is Resource.Loading -> {
                            showLoading(true)
                        }
                        is Resource.Success -> {
                            showLoading(false)
                            processRegister(it.data)
                        }
                        is Resource.Error -> {
                            showLoading(false)
                            Toast.makeText(context, R.string.error_register, Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
    }

    private fun processRegister(data: RegisterResponse) {
        if (data.error) {
            Toast.makeText(requireContext(), R.string.register_failed, Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(requireContext(), R.string.register_success, Toast.LENGTH_LONG).show()
            findNavController().navigate(RegisterFragmentDirections.actionRegisterFragmentToLoginFragment())
        }
    }

    private fun showLoading(state: Boolean) {
        binding.pbCreateRegister.isVisible = state
        binding.edRegisterEmail.isInvisible = state
        binding.edRegisterName.isInvisible = state
        binding.edRegisterPassword.isInvisible = state
        binding.textView2.isInvisible = state
        binding.tvRegisterHaveAccount.isInvisible = state
        binding.btRegister.isInvisible = state
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}