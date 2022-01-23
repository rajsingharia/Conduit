package com.example.conduit.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.conduit.R
import com.example.conduit.adapter.CommentAdapter
import com.example.conduit.databinding.FragmentArticleDetailBinding
import com.example.conduit.model.*
import com.example.conduit.ui.MainActivity
import com.example.conduit.util.Constants.NUMBER_OF_ARTICLES
import com.example.conduit.viewmodel.ArticleDetailViewModel
import com.example.conduit.viewmodel.FeedViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*


@AndroidEntryPoint
class ArticleDetailFragment : Fragment() {

    val args: ArticleDetailFragmentArgs by navArgs()

    private lateinit var binding:FragmentArticleDetailBinding
    private lateinit var feedViewModel: FeedViewModel
    private var token:String? = null
    private var liked:Boolean = false
    private var followed:Boolean = false
    private lateinit var username:String
    private lateinit var email:String
    private lateinit var celebName:String
    private lateinit var adapter:CommentAdapter
    private val articleDetailViewModel : ArticleDetailViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentArticleDetailBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val slug = args.slug
        username = (activity as MainActivity).username!!
        email = (activity as MainActivity).email!!
        feedViewModel = (activity as MainActivity).feedViewModel
        token = (activity as MainActivity).token

        articleDetailViewModel.getArticleBySlug(slug,"Token $token")
        articleDetailViewModel.getCommentForArticle(slug,"Token $token")

        adapter = CommentAdapter(username)
        binding.articleDetailRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.articleDetailRecyclerView.adapter = adapter

        articleDetailViewModel.singleArticleBySlug.observe(requireActivity(),{
            it?.let{
                setUpData(it)
            }
        })

        articleDetailViewModel.comments.observe(requireActivity(),{
            it?.let{
                setUpComments(it)
            }
        })

        binding.articleDetailLikeBtn.setOnClickListener {
            likeArticle(slug)
        }

        binding.articleDetailCommentAddBtn.setOnClickListener{
            addComment(slug)
        }


        binding.articleDetailFollowBtn.setOnClickListener {
            followUser()
        }

        adapter.setOnDeleteClickListner {
            deleteComment(slug,it)
        }


        feedViewModel.favourted.observe(requireActivity(),{
            it?.let{
                articleDetailViewModel.getArticleBySlug(slug,"Token $token")
            }
        })

        feedViewModel.postedCommentResponse.observe(requireActivity(),{
            it?.let {
                binding.articleDetailComment.clearFocus()
                binding.articleDetailComment.setText("")
                articleDetailViewModel.getCommentForArticle(slug,"Token $token")
            }
        })

        feedViewModel.followResponse.observe(requireActivity(),{
            it?.let{
                articleDetailViewModel.getArticleBySlug(slug,"Token $token")
            }
        })

        feedViewModel.deleteCommentResponse.observe(requireActivity(),{
            it?.let{
                if(it == "200" || it == "204") {
                    articleDetailViewModel.getCommentForArticle(slug,"Token $token")
                }
            }
        })



    }

    private fun deleteComment(slug: String, comment: Comment) {
        binding.articleDetailProgressBar.visibility = View.VISIBLE
        feedViewModel.deleteComment(slug,comment.id,"Token $token")
    }


    private fun likeArticle(slug: String) {
        binding.articleDetailProgressBar.visibility = View.VISIBLE
        if(liked){
            binding.articleDetailLikeBtn.setImageResource(R.drawable.ic_heart_not_liked)
            feedViewModel.unFavouriteArticle(slug, "Token $token")
        }
        else{
            binding.articleDetailLikeBtn.setImageResource(R.drawable.ic_heart)
            feedViewModel.favouriteArticle(slug, "Token $token")
        }

        feedViewModel.getMyFavouriteArticles("Token $token",NUMBER_OF_ARTICLES,username)
    }

    private fun followUser() {

        binding.articleDetailProgressBar.visibility = View.VISIBLE

        if(followed){
            feedViewModel.unFollowUser(celebName,"Token $token")
        }
        else{
            feedViewModel.followUser(celebName,"Token $token",UserXX(email,null))
        }

        feedViewModel.getMyFeed("Token $token")

    }

    private fun addComment(slug: String) {
        val comment = binding.articleDetailComment.text.toString()
        feedViewModel.postComment(slug,"Token $token", CommentRequest(CommentX(comment)))
        binding.articleDetailProgressBar.visibility = View.VISIBLE

    }

    private fun setUpComments(comments : CommentResponse) {
        binding.articleDetailProgressBar.visibility = View.GONE
        adapter.submitList(comments.comments)
    }

    private fun setUpData(article: SingleArticleResponseBySlug) {
        celebName = article.article.author.username
        binding.shimmer.stopShimmer()
        binding.userDetailConstraintLayout.visibility = View.VISIBLE
        binding.shimmer.visibility = View.INVISIBLE
        binding.articleDetailUsername.text = article.article.author.username
        val outputFormat = SimpleDateFormat("dd/MM/yyyy (HH:mm:aa)", Locale.US)
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.US)

        val date: Date = inputFormat.parse(article.article.createdAt)
        val outputText: String = outputFormat.format(date)
        binding.articleDetailDate.text = outputText
        binding.articleDetailTitle.text = article.article.title
        binding.articleDetailDescription.text = article.article.description
        binding.articleDetailBody.text = article.article.body
        binding.articleDetailLikeCount.text = article.article.favoritesCount.toString()

        //Toast.makeText(requireContext(),article.article.favorited.toString(),Toast.LENGTH_SHORT).show()
        liked=article.article.favorited
        if(!article.article.favorited){
            binding.articleDetailLikeBtn.setImageResource(R.drawable.ic_heart_not_liked)
        }
        else{
            binding.articleDetailLikeBtn.setImageResource(R.drawable.ic_heart)
        }
        followed=article.article.author.following
        if(!article.article.author.following){
            binding.articleDetailFollowBtn.text = "Follow"
        }
        else{
            binding.articleDetailFollowBtn.text = "Following"
        }

        Glide
            .with(requireContext())
            .load(article.article.author.image)
            .placeholder(R.drawable.ic_person)
            .into(binding.articleDetailProfilePic)

    }

}