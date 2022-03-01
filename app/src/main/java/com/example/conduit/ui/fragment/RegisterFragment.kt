package com.example.conduit.ui.fragment
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.conduit.R
import com.example.conduit.databinding.FragmentRegisterBinding
import com.example.conduit.model.User
import com.example.conduit.model.UserRequestRegister
import com.example.conduit.ui.MainActivity
import com.example.conduit.util.OfflineData
import com.example.conduit.viewmodel.AuthViewModel
import com.example.conduit.viewmodel.FeedViewModel
import dagger.hilt.android.AndroidEntryPoint


class RegisterFragment : Fragment() {

    private lateinit var binding : FragmentRegisterBinding
    private lateinit var authViewModel: AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentRegisterBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        authViewModel = (activity as MainActivity).authViewModel

        binding.loginBtn.setOnClickListener {
            val action = RegisterFragmentDirections.actionRegisterFragmentToLoginFragment()
            findNavController().navigate(action)
        }

        binding.registerBtn.setOnClickListener {
            val email = binding.registerEmail.text.toString()
            val username = binding.registerUsername.text.toString()
            val password = binding.registerPassword.text.toString()
            val userRequest = UserRequestRegister(User(email,password,username))
            authViewModel.signUpUser(userRequest)

        }

        authViewModel.error.observe(requireActivity(),{
            it?.let{
                Toast.makeText(requireContext(),it,Toast.LENGTH_SHORT).show()
            }
        })

        authViewModel.signupUser.observe(requireActivity(),{
            it?.let { user->
                OfflineData(requireActivity()).putUserToken(user.user!!.token)
                Toast.makeText(requireContext(),"User Registered In",Toast.LENGTH_SHORT).show()
                val action = RegisterFragmentDirections.actionRegisterFragmentToLoginFragment()
                findNavController().navigate(action)
            }
        })

    }

}