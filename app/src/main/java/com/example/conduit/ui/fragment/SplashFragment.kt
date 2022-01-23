package com.example.conduit.ui.fragment
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.conduit.databinding.FragmentSplashBinding
import com.example.conduit.ui.MainActivity
import com.example.conduit.viewmodel.FeedViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@AndroidEntryPoint
class SplashFragment : Fragment() {

    private lateinit var binding : FragmentSplashBinding
    private lateinit var token:String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentSplashBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        token = (activity as MainActivity).token!!
        lifecycleScope.launch {

            while(true){
                delay(3000)
                checkForUser()
                break
            }

        }
    }

    private fun checkForUser() {
        if(token==null){
            val action = SplashFragmentDirections.actionSplashFragmentToRegisterFragment()
            findNavController().navigate(action)
        }
        else{
            val action = SplashFragmentDirections.actionSplashFragmentToMyFeedFragment()
            findNavController().navigate(action)
        }
    }
}