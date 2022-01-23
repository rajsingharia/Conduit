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
import com.example.conduit.databinding.FragmentLoginBinding
import com.example.conduit.model.UserRequestLogin
import com.example.conduit.model.UserXX
import com.example.conduit.ui.MainActivity
import com.example.conduit.util.OfflineData
import com.example.conduit.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint


class LoginFragment : Fragment() {

    private lateinit var binding:FragmentLoginBinding
    private lateinit var authViewModel: AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentLoginBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        authViewModel = (activity as MainActivity).authViewModel

        binding.loginBtn.setOnClickListener {
            val email = binding.loginEmail.text.toString()
            val password = binding.loginPassword.text.toString()
            val userRequestLogin = UserRequestLogin(UserXX(email,password))
            authViewModel.loginUser(userRequestLogin)
        }

        authViewModel.loginUser.observe(requireActivity(),{
            it?.let{ user->
                OfflineData(requireActivity()).putUserToken(user.user.token)
                Toast.makeText(requireContext(),"User Logged In ${user.user.token}",Toast.LENGTH_SHORT).show()
                val action = LoginFragmentDirections.actionLoginFragmentToMyFeedFragment()
                findNavController().navigate(action)
            }
        })

    }

}