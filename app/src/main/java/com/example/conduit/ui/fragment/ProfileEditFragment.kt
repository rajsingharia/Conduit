package com.example.conduit.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.conduit.R
import com.example.conduit.databinding.FragmentProfileEditBinding
import com.example.conduit.model.UpdateRequestUser
import com.example.conduit.model.User
import com.example.conduit.model.UserRequestRegister
import com.example.conduit.model.UserXXX
import com.example.conduit.ui.MainActivity
import com.example.conduit.viewmodel.AuthViewModel


class ProfileEditFragment : Fragment() {

    private lateinit var binding:FragmentProfileEditBinding
    private val args: ProfileEditFragmentArgs by navArgs()
    private lateinit var authViewModel: AuthViewModel
    private lateinit var token:String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileEditBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        token = (activity as MainActivity).token!!
        authViewModel = (activity as MainActivity).authViewModel
        setupUser()

        binding.passwordEditUpdate.setOnClickListener {
            updateUser()
        }

        authViewModel.updateUser.observe(requireActivity(),{
            it?.let{
                Toast.makeText(requireContext(),"User Updated",Toast.LENGTH_SHORT).show()
                authViewModel.getCurrentUser("Token $token")
                findNavController().popBackStack()
            }
        })
    }

    private fun updateUser() {
        val image = binding.profileEditUrl.text.toString()
        val username = binding.profileEditUsername.text.toString()
        val bio = binding.profileEditBio.text.toString()
        val email = binding.profileEditEmail.text.toString()
        val password = binding.passwordEditUpdate.text.toString()

        val user = UpdateRequestUser(UserXXX(bio,email,image, password, username))
        authViewModel.updateUser("Token $token",user)
    }

    private fun setupUser() {
        binding.profileEditUrl.setText(args.image)
        binding.profileEditUsername.setText(args.username)
        binding.profileEditBio.setText(args.bio)
        binding.profileEditEmail.setText(args.email)
    }

}