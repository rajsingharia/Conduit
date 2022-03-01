package com.example.conduit.ui
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.conduit.R
import com.example.conduit.data.remote.NetworkResult
import com.example.conduit.databinding.ActivityMainBinding
import com.example.conduit.util.Constants.NUMBER_OF_ARTICLES
import com.example.conduit.util.NetworkConnectivity
import com.example.conduit.util.OfflineData
import com.example.conduit.viewmodel.AuthViewModel
import com.example.conduit.viewmodel.FeedViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    //automatically injects view-model
    val feedViewModel : FeedViewModel by viewModels()
    val authViewModel : AuthViewModel by viewModels()
    var token: String? = null
    var username: String? = null
    var email: String? = null
    private lateinit var networkConnectivity :NetworkConnectivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //OfflineData(this).putUserToken("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJlbWFpbCI6IjU0MzIxQHIuY29tIiwidXNlcm5hbWUiOiJ0ZXN0Njk2OTY5IiwicGFzc3dvcmQiOiIkMmEkMTAkdzV5cS5BVWd2ZlNMMTN5UEpuMnBYTzFKWTF3b1dla3VONi5JQVhacEdDZG9mUm43V1g5YkciLCJiaW8iOm51bGwsImltYWdlIjoiaHR0cHM6Ly9hcGkucmVhbHdvcmxkLmlvL2ltYWdlcy9zbWlsZXktY3lydXMuanBlZyIsImlhdCI6MTY0MjQyMDExMCwiZXhwIjoxNjQ3NjA0MTEwfQ.zBtLcf7P8PXDo2O8_88Keb29TdJcp3Lz70jDXSG7R6M")
        token = OfflineData(this).getUserToken()

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        networkConnectivity = NetworkConnectivity(application)

        val navHostFragment = supportFragmentManager.findFragmentById(binding.fragmentContainerView.id) as NavHostFragment

        val controller = navHostFragment.navController

        binding.bottomNavigationView.setupWithNavController(controller)


        //Top level destinations
        val appBarConfiguration = AppBarConfiguration(
            topLevelDestinationIds = setOf(
                R.id.myFeedFragment,
                R.id.GlobalFeedFragment,
                R.id.accountViewPagerFragment,
                R.id.addArticleFragment,
                R.id.registerFragment,
                R.id.loginFragment
            )
        )

        binding.toolbar.setupWithNavController(controller, appBarConfiguration)



        if(token!=null){
            //If previously user exists
            val tokenFormat="Token $token"
            fetchArticles(tokenFormat)
            getCurrentUser(tokenFormat)
            getUserFeed(tokenFormat)
        }

        authViewModel.currentUser.observe(this,{
            it?.let{
                when(it){
                    is NetworkResult.Success -> {
                        OfflineData(this).putUserToken(it.data!!.user!!.token)
                        token = OfflineData(this).getUserToken()
                        feedViewModel.getMyArticles("Token $token",NUMBER_OF_ARTICLES,it.data.user!!.username)
                        username = it.data.user!!.username
                        email = it.data.user.email
                        feedViewModel.getMyFavouriteArticles("Token $token",NUMBER_OF_ARTICLES,it.data.user.username)
                    }
                    is NetworkResult.Error -> {
                        Toast.makeText(this,it.message,Toast.LENGTH_SHORT).show()
                        token = OfflineData(this).getUserToken()
                        feedViewModel.getMyArticles("Token $token",NUMBER_OF_ARTICLES,it.data!!.user!!.username)
                        username = it.data.user!!.username
                        email = it.data.user.email
                        feedViewModel.getMyFavouriteArticles("Token $token",NUMBER_OF_ARTICLES,it.data.user.username)
                    }
                }

            }
        })

        networkConnectivity.observe(this,{
            it?.let{ online->
                if(!online){
                    binding.offlineTextView.startAnimation(AnimationUtils.loadAnimation(this,R.anim.slide_down))
                    binding.offlineTextView.visibility = View.VISIBLE
                }
                else{
                    binding.offlineTextView.visibility = View.GONE
                    if(token!=null){
                        //If previously user exists
                        val tokenFormat="Token $token"
                        fetchArticles(tokenFormat)
                        getCurrentUser(tokenFormat)
                        getUserFeed(tokenFormat)
                    }
                }

            }
        })



        controller.addOnDestinationChangedListener { _, destination, _ ->

            when(destination.id){

                R.id.myFeedFragment, R.id.GlobalFeedFragment,R.id.accountViewPagerFragment,R.id.addArticleFragment -> {
                    binding.bottomNavigationView.visibility = View.VISIBLE
                    binding.toolbar.visibility = View.VISIBLE
                }

                else -> {
                    binding.bottomNavigationView.visibility = View.GONE
                    if(destination.id==R.id.splashFragment){
                        binding.toolbar.visibility = View.GONE
                    }
                    else{
                        binding.toolbar.visibility = View.VISIBLE
                    }
                }

            }

        }

    }

    private fun fetchArticles(token: String?) {
        feedViewModel.getArticles(token)
    }

    private fun getCurrentUser(token:String?){
        authViewModel.getCurrentUser(token)
    }

    private fun getUserFeed(token:String?) {
        feedViewModel.getMyFeed(token)
    }


}