package ppazosp.changapp

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter(fa: FragmentActivity?) : FragmentStateAdapter(fa!!) {
    override fun createFragment(position: Int): Fragment {

        return when (position) {
            0 -> SearchFragment()
            1 -> ChatsFragment()
            2 -> ProfileFragment()
            else -> {SearchFragment()}
        }
    }

    override fun getItemCount(): Int {
        return 3
    }
}