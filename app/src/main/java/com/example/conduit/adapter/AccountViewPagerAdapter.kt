package com.example.conduit.adapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.conduit.ui.fragment.LikedFragment
import com.example.conduit.ui.fragment.MyArticleFragment
import com.example.conduit.util.Constants.LIKED_ARTICLE_PAGE_INDEX
import com.example.conduit.util.Constants.MY_ARTICLE_PAGE_INDEX

class AccountViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    private val tabFragmentsCreators: Map<Int, () -> Fragment> = mapOf(
        MY_ARTICLE_PAGE_INDEX to { MyArticleFragment() },
        LIKED_ARTICLE_PAGE_INDEX to { LikedFragment() }
    )

    override fun getItemCount() = tabFragmentsCreators.size

    override fun createFragment(position: Int): Fragment {
        return tabFragmentsCreators[position]?.invoke() ?: throw IndexOutOfBoundsException()
    }
}