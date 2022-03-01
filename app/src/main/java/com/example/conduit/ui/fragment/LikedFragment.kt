package com.example.conduit.ui.fragment
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.conduit.adapter.ArticleAdapter
import com.example.conduit.data.remote.NetworkResult
import com.example.conduit.databinding.FragmentLikedBinding
import com.example.conduit.ui.MainActivity
import com.example.conduit.util.Constants
import com.example.conduit.util.Constants.ARTICLE_TYPE_OTHER
import com.example.conduit.util.Constants.NUMBER_OF_ARTICLES
import com.example.conduit.viewmodel.FeedViewModel


class LikedFragment : Fragment() {

    private lateinit var binding:FragmentLikedBinding
    private val adapter = ArticleAdapter(ARTICLE_TYPE_OTHER)
    private lateinit var feedViewModel: FeedViewModel
    private lateinit var token:String
    private lateinit var authorname:String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLikedBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        token = (activity as MainActivity).token!!
        authorname = (activity as MainActivity).username!!
        feedViewModel = (activity as MainActivity).feedViewModel
        binding.favouritedArticleRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.favouritedArticleRecyclerView.adapter = adapter
        feedViewModel.myFavouritedArticle.observe(requireActivity(),{
            it.data?.let{ response->
                binding.favouritedArticleRecyclerViewSwipeToRefresh.isRefreshing = false
                when(it){
                    is NetworkResult.Loading -> {
                        binding.likedArticleProgressBar.visibility = View.VISIBLE
                    }
                    is NetworkResult.Success -> {
                        binding.likedArticleProgressBar.visibility = View.GONE
                        adapter.submitList(response.articles)
                    }
                    is NetworkResult.Error -> {
                        binding.likedArticleProgressBar.visibility = View.GONE
                        adapter.submitList(response.articles)
                        Toast.makeText(requireContext(),it.message,Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })

        feedViewModel.favourted.observe(requireActivity(),{
            it.let{
                feedViewModel.getMyFavouriteArticles("Token $token",
                    NUMBER_OF_ARTICLES,authorname)
            }
        })

        adapter.setOnRowClickListener { articleClicked->
            val slug = articleClicked.slug
            val action = AccountViewPagerFragmentDirections.actionAccountViewPagerFragmentToArticleDetailFragment(slug)
            requireParentFragment().findNavController().navigate(action)
        }

        adapter.setOnLikeClickListner { articleClicked->
            val slug = articleClicked.slug
            val liked = articleClicked.favorited
            favouriteArticle(slug,liked)
        }

        binding.favouritedArticleRecyclerViewSwipeToRefresh.setOnRefreshListener {
            feedViewModel.getMyFavouriteArticles("Token $token",
                NUMBER_OF_ARTICLES,authorname)
        }

    }

    private fun favouriteArticle(slug: String, liked: Boolean) {
        binding.likedArticleProgressBar.visibility = View.VISIBLE
        if(liked){
            feedViewModel.unFavouriteArticle(slug, "Token $token")
        }
        else{
            feedViewModel.favouriteArticle(slug, "Token $token")
        }
    }

}