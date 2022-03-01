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
import com.example.conduit.databinding.FragmentMyArticleBinding
import com.example.conduit.ui.MainActivity
import com.example.conduit.util.Constants.ARTICLE_TYPE_MY
import com.example.conduit.util.Constants.NUMBER_OF_ARTICLES
import com.example.conduit.viewmodel.FeedViewModel


class MyArticleFragment : Fragment() {

    private lateinit var binding: FragmentMyArticleBinding
    private val adapter = ArticleAdapter(ARTICLE_TYPE_MY)
    private lateinit var feedViewModel: FeedViewModel
    private lateinit var token:String
    private lateinit var authorname:String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMyArticleBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        token = (activity as MainActivity).token!!
        authorname = (activity as MainActivity).username!!
        feedViewModel = (activity as MainActivity).feedViewModel
        binding.myArticleRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.myArticleRecyclerView.adapter = adapter
        feedViewModel.myArticle.observe(requireActivity(),{
            it.data?.let{ response->
                binding.myArticleRecyclerViewSwipeToRefresh.isRefreshing = false
                when(it){
                    is NetworkResult.Error -> {
                        binding.myArticlesProgressBar.visibility = View.GONE
                        adapter.submitList(response.articles)
                        Toast.makeText(requireContext(),it.message, Toast.LENGTH_SHORT).show()
                    }
                    is NetworkResult.Success -> {
                        binding.myArticlesProgressBar.visibility = View.GONE
                        adapter.submitList(response.articles)
                    }
                    is NetworkResult.Loading -> {
                        binding.myArticlesProgressBar.visibility = View.VISIBLE
                    }
                }
            }
        })

        feedViewModel.favourted.observe(requireActivity(),{
            it.let{
                feedViewModel.getMyArticles("Token $token",NUMBER_OF_ARTICLES,authorname)
            }
        })

        adapter.setOnRowClickListener { articleClicked->
            val slug = articleClicked.slug
            val action = AccountViewPagerFragmentDirections.actionAccountViewPagerFragmentToArticleDetailFragment(slug)
            requireParentFragment().findNavController().navigate(action)
        }

        adapter.setOnEditClickListner { articleClicked->
            val slug = articleClicked.slug
            val title = articleClicked.title
            val description = articleClicked.description
            val body = articleClicked.body
            val tags = articleClicked.tagList
            val action = AccountViewPagerFragmentDirections.actionAccountViewPagerFragmentToEditArticleFragment(slug,title, description, body)
            requireParentFragment().findNavController().navigate(action)
        }

        adapter.setOnLikeClickListner { articleClicked->
            val slug = articleClicked.slug
            val liked = articleClicked.favorited
            favouriteArticle(slug,liked)
        }

        binding.myArticleRecyclerViewSwipeToRefresh.setOnRefreshListener {
            feedViewModel.getMyArticles("Token $token",NUMBER_OF_ARTICLES,authorname)
        }

    }
    private fun favouriteArticle(slug: String, liked: Boolean) {
        binding.myArticlesProgressBar.visibility = View.VISIBLE

        if(liked){
            feedViewModel.unFavouriteArticle(slug, "Token $token")
        }
        else{
            feedViewModel.favouriteArticle(slug, "Token $token")
        }
    }

}