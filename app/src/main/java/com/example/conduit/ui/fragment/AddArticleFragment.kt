package com.example.conduit.ui.fragment
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.conduit.databinding.FragmentAddArticleBinding
import com.example.conduit.model.ArticleRequest
import com.example.conduit.model.ArticleX
import com.example.conduit.ui.MainActivity
import com.example.conduit.util.Constants.NUMBER_OF_ARTICLES
import com.example.conduit.viewmodel.AddArticleViewModel
import com.example.conduit.viewmodel.FeedViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddArticleFragment : Fragment() {

    private lateinit var binding:FragmentAddArticleBinding
    private val addArticleViewModel: AddArticleViewModel by viewModels()
    private lateinit var feedViewModel:FeedViewModel
    private var token:String? = null
    private var username:String? = null
    private var articleTagList = mutableListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddArticleBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        feedViewModel = (activity as MainActivity).feedViewModel
        token = (activity as MainActivity).token
        username = (activity as MainActivity).username

        binding.articleTagAddButton.setOnClickListener {
            val previousTag = binding.articleTagList.text
            binding.articleTagList.text = "$previousTag  ${binding.articleSingleTag.text.toString()}"
            articleTagList.add(binding.articleSingleTag.text.toString())
            binding.articleSingleTag.text?.clear()
        }

        binding.addArticle.setOnClickListener {
            val body = binding.articleBody.text.toString()
            val title = binding.articleTitle.text.toString()
            val description = binding.articleDescription.text.toString()
            //val taglist = listOf(binding.articleTagList.text.toString())
            val article = ArticleRequest(ArticleX(body,description,articleTagList.toList(),title))
            addArticleViewModel.createArticle("Token $token",article)
        }

        getFeedIfAdded()

    }

    private fun getFeedIfAdded() {
        addArticleViewModel.articleAdded.observe(requireActivity(),{
            it?.let{
                feedViewModel.getMyArticles("Token $token",NUMBER_OF_ARTICLES,username)
                feedViewModel.getArticles("Token $token")
                Toast.makeText(requireContext(),"Article Added", Toast.LENGTH_SHORT).show()
                binding.articleSingleTag.text?.clear()
                binding.articleTitle.text?.clear()
                binding.articleDescription.text?.clear()
                binding.articleBody.text?.clear()
                binding.articleTagList.text = ""
                binding.articleTagList.clearFocus()
                binding.articleSingleTag.clearFocus()
                binding.articleTitle.clearFocus()
                binding.articleDescription.clearFocus()
                binding.articleBody.clearFocus()
            }
        })

        addArticleViewModel.articleAddError.observe(requireActivity(),{
            it?.let{
                Toast.makeText(requireContext(),it,Toast.LENGTH_SHORT).show()
                binding.articleTagList.clearFocus()
                binding.articleSingleTag.clearFocus()
                binding.articleTitle.clearFocus()
                binding.articleDescription.clearFocus()
                binding.articleBody.clearFocus()
            }
        })
    }

}