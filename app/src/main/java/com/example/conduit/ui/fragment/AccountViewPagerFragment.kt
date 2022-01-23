package com.example.conduit.ui.fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.conduit.R
import com.example.conduit.adapter.AccountViewPagerAdapter
import com.example.conduit.databinding.FragmentAccountViewPagerBinding
import com.example.conduit.model.UserX
import com.example.conduit.ui.MainActivity
import com.example.conduit.util.Constants.LIKED_ARTICLE_PAGE_INDEX
import com.example.conduit.util.Constants.MY_ARTICLE_PAGE_INDEX
import com.example.conduit.viewmodel.AuthViewModel
import com.google.android.material.tabs.TabLayoutMediator


class AccountViewPagerFragment : Fragment() {

    private lateinit var binding: FragmentAccountViewPagerBinding
    private lateinit var adapter :AccountViewPagerAdapter
    private lateinit var authViewModel: AuthViewModel
    private lateinit var user: UserX

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAccountViewPagerBinding.inflate(inflater,container,false)
        adapter = AccountViewPagerAdapter(this)
        val tabLayout = binding.tabLayout
        val viewPager = binding.viewPager2
        authViewModel = (activity as MainActivity).authViewModel

        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.setIcon(getTabIcon(position))
            tab.text = getTabTitle(position)
        }.attach()


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUser()

        binding.accountProfileEditBtn.setOnClickListener {
            val profile = user.image
            val username = user.username
            val bio = user.bio
            val email = user.email
            val action = AccountViewPagerFragmentDirections.actionAccountViewPagerFragmentToProfileEditFragment(profile,username,bio,email)
            findNavController().navigate(action)
        }
    }

    private fun setupUser() {
        authViewModel.currentUser.observe(requireActivity(),{
            it?.let{ user->
                this.user=user.data!!.user
                binding.accountProgressBar.visibility = View.GONE
                binding.accountUserName.text = user.data!!.user.username
                binding.accountBio.text = user.data.user.bio
                Glide
                    .with(requireContext())
                    .load(user.data.user.image)
                    .placeholder(R.drawable.ic_person)
                    .circleCrop()
                    .into(binding.accountUserProfilePic)
            }
        })
    }

    private fun getTabIcon(position: Int): Int {
        return when (position) {
            MY_ARTICLE_PAGE_INDEX -> R.drawable.ic_article
            LIKED_ARTICLE_PAGE_INDEX -> R.drawable.ic_like
            else -> throw IndexOutOfBoundsException()
        }
    }

    private fun getTabTitle(position: Int): String? {
        return when (position) {
            MY_ARTICLE_PAGE_INDEX -> getString(R.string.my_article)
            LIKED_ARTICLE_PAGE_INDEX -> getString(R.string.liked_article)
            else -> null
        }
    }


}