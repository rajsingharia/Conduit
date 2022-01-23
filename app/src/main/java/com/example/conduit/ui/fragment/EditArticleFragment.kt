package com.example.conduit.ui.fragment
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.conduit.databinding.FragmentEditArticleBinding
import com.example.conduit.model.ArticleRequest
import com.example.conduit.model.ArticleX
import com.example.conduit.ui.MainActivity
import com.example.conduit.util.Constants.NUMBER_OF_ARTICLES
import com.example.conduit.data.remote.NetworkResult
import com.example.conduit.viewmodel.FeedViewModel
import com.example.conduit.viewmodel.UpdateAndDeleteArticleViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class EditArticleFragment : Fragment() {

    private lateinit var binding:FragmentEditArticleBinding
    private val args: EditArticleFragmentArgs by navArgs()
    private val updateAndDeleteArticleViewModel: UpdateAndDeleteArticleViewModel by viewModels()
    private lateinit var feedViewModel: FeedViewModel
    private lateinit var token:String
    private lateinit var username:String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditArticleBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        feedViewModel = (activity as MainActivity).feedViewModel
        token = (activity as MainActivity).token!!
        username = (activity as MainActivity).username!!
        setupArticle()
        binding.articleEditPublish.setOnClickListener {
            updateArticle()
        }

        binding.articleEditDelete.setOnClickListener {
            deleteArticle()
        }

        updateAndDeleteArticleViewModel.updateArticleResponse.observe(requireActivity(),{
            it?.let{ response->
                when(response){
                    is NetworkResult.Success -> {
                        binding.editArticleProgressBar.visibility = View.GONE
                        Toast.makeText(requireContext(),"Article Updated",Toast.LENGTH_SHORT).show()
                        feedViewModel.getMyArticles("Token $token",NUMBER_OF_ARTICLES,username)
                        findNavController().popBackStack()
                    }
                    is NetworkResult.Loading -> {
                        binding.editArticleProgressBar.visibility = View.VISIBLE
                    }
                    is NetworkResult.Error -> {
                        binding.editArticleProgressBar.visibility = View.GONE
                        Toast.makeText(requireContext(),"Something Went Wrong",Toast.LENGTH_SHORT).show()
                    }
                }

            }
        })

        updateAndDeleteArticleViewModel.deleteArticleResponse.observe(requireActivity(),{
            it?.let{
                if(it == "204" || it == "200"){
                    Toast.makeText(requireContext(),"Article Deleted",Toast.LENGTH_SHORT).show()
                    feedViewModel.getMyArticles("Token $token",NUMBER_OF_ARTICLES,username)
                    updateAndDeleteArticleViewModel.deleteArticleResponse.postValue(null)
                    findNavController().popBackStack()
                }
            }
        })


    }

    private fun deleteArticle() {
        updateAndDeleteArticleViewModel.deleteArticle(args.slug,"Token $token")
    }

    private fun updateArticle() {
        val title = binding.articleEditTitle.text.toString()
        val description = binding.articleEditDescription.text.toString()
        val body = binding.articleEditBody.text.toString()
        val tags = listOf(binding.articleEditTags.text.toString())
        val articleRequest = ArticleRequest(ArticleX(body,description,tags,title))
        updateAndDeleteArticleViewModel.updateArticle(args.slug,"Token $token",articleRequest)
    }

    private fun setupArticle() {
        binding.articleEditTitle.setText(args.title)
        binding.articleEditDescription.setText(args.description)
        binding.articleEditBody.setText(args.body)
    }


}