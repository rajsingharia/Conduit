package com.example.conduit.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.conduit.R
import com.example.conduit.adapter.ArticleAdapter
import com.example.conduit.data.remote.NetworkResult
import com.example.conduit.databinding.FragmentMyFeedBinding
import com.example.conduit.ui.MainActivity
import com.example.conduit.util.Constants.ARTICLE_TYPE_OTHER
import com.example.conduit.viewmodel.FeedViewModel

class MyFeedFragment : Fragment() {

    private lateinit var binding : FragmentMyFeedBinding
    private lateinit var feedViewModel: FeedViewModel
    private val adapter = ArticleAdapter(ARTICLE_TYPE_OTHER)
    private lateinit var token:String


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMyFeedBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        feedViewModel = (activity as MainActivity).feedViewModel
        token = (activity as MainActivity).token!!

        binding.myFeedRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.myFeedRecyclerView.adapter = adapter
        feedViewModel.myFeed.observe(requireActivity(),{
            it.data?.let{ response->
                binding.myFeedRecyclerViewSwipeToRefresh.isRefreshing = false
                when(it){
                    is NetworkResult.Loading -> {
                        binding.myFeedProgressBar.visibility = View.VISIBLE
                    }
                    is NetworkResult.Success -> {
                        binding.myFeedProgressBar.visibility = View.GONE
                        adapter.submitList(response.articles)
                    }
                    is NetworkResult.Error -> {
                        binding.myFeedProgressBar.visibility = View.GONE
                        adapter.submitList(response.articles)
                        Toast.makeText(requireContext(),it.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })

        adapter.setOnRowClickListener { articleClicked->
            val slug = articleClicked.slug
            val action = MyFeedFragmentDirections.actionMyFeedFragmentToArticleDetailFragment(slug)
            findNavController().navigate(action)
        }

        adapter.setOnLikeClickListner { articleClicked->
            val slug = articleClicked.slug
            val liked = articleClicked.favorited
            favouriteArticle(slug,liked)
        }

        feedViewModel.favourted.observe(requireActivity(),{
            it.let{
                feedViewModel.getMyFeed("Token $token")
            }
        })

        binding.myFeedRecyclerViewSwipeToRefresh.setOnRefreshListener {
            feedViewModel.getMyFeed("Token $token")
        }

    }

    private fun favouriteArticle(slug: String, liked: Boolean) {
        binding.myFeedProgressBar.visibility = View.VISIBLE

        if(liked){
            feedViewModel.unFavouriteArticle(slug, "Token $token")
        }
        else{
            feedViewModel.favouriteArticle(slug, "Token $token")
        }
    }

}