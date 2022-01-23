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
import com.example.conduit.databinding.FragmentGlobalFeedBinding
import com.example.conduit.ui.MainActivity
import com.example.conduit.util.Constants.ARTICLE_TYPE_OTHER
import com.example.conduit.data.remote.NetworkResult
import com.example.conduit.viewmodel.FeedViewModel


class GlobalFeedFragment : Fragment() {

    private lateinit var binding : FragmentGlobalFeedBinding
    private lateinit var feedViewModel:FeedViewModel
    private lateinit var token:String
    private var isOffline = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //fetchArticles()
        binding = FragmentGlobalFeedBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        token = (activity as MainActivity).token!!
        feedViewModel = (activity as MainActivity).feedViewModel
        val adapter = ArticleAdapter(ARTICLE_TYPE_OTHER)

        binding.globalRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.globalRecyclerView.adapter = adapter

        feedViewModel.globalArticle.observe(requireActivity(),{
            it?.let{ response->
                when(response){
                    is NetworkResult.Loading -> {
                        binding.globalProgressBar.visibility = View.VISIBLE
                    }
                    is NetworkResult.Success -> {
                        adapter.submitList(response.data!!.articles)
                        binding.globalProgressBar.visibility = View.GONE
                    }
                    is NetworkResult.Error -> {
                        binding.globalProgressBar.visibility = View.GONE
                        adapter.submitList(response.data!!.articles)
                        isOffline = true
                        Toast.makeText(requireContext(),response.message,Toast.LENGTH_SHORT).show()
                    }
                }

            }
        })

        adapter.setOnRowClickListener { articleClicked->
            if(!isOffline){
                val slug = articleClicked.slug
                val action = GlobalFeedFragmentDirections.actionGlobalFeedFragmentToArticleDetailFragment(slug)
                findNavController().navigate(action)
            }
            else{
                Toast.makeText(requireContext(),"Unable to reach server!!",Toast.LENGTH_SHORT).show()
            }
        }

        adapter.setOnLikeClickListner { articleClicked->
            if(!isOffline){
                val slug = articleClicked.slug
                val liked = articleClicked.favorited
                favouriteArticle(slug, liked)
            }
            else{
                Toast.makeText(requireContext(),"Unable to reach server!!",Toast.LENGTH_SHORT).show()
            }
        }

        feedViewModel.favourted.observe(requireActivity(),{
            it.let{
                feedViewModel.getArticles("Token $token")
            }
        })

    }

    private fun favouriteArticle(slug: String, liked: Boolean) {
        binding.globalProgressBar.visibility = View.VISIBLE

        if(liked){
            feedViewModel.unFavouriteArticle(slug, "Token $token")
        }
        else{
            feedViewModel.favouriteArticle(slug, "Token $token")
        }
    }

}