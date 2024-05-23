package test.pecode.list

import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import test.pecode.ui.PageFragment

var numOfFragments = 1

class ViewPagerAdapter(
    private var totalNumOfFragments: Int,
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle
) : FragmentStateAdapter(fragmentManager, lifecycle) {

    fun notifyCountChanged(newNumOfFragments: Int) {
        totalNumOfFragments = newNumOfFragments
        notifyDataSetChanged()
    }

    override fun createFragment(position: Int) =
        PageFragment.newInstance(position + 1)

    override fun getItemCount() = totalNumOfFragments
}