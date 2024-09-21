package ppazosp.changapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class SearchFragment : Fragment(), SearchSelectionFragment.OnSearchButtonClickListener {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_search, container, false)

        loadFragment(SearchSelectionFragment())

        return view
    }

    override fun onSearchButtonClicked(data: Bundle) {
        val searchResultFragment = SearchResultsFragment()
        searchResultFragment.arguments = data
        loadFragment(searchResultFragment)
    }

    private fun loadFragment(fragment: Fragment) {
        val transaction = childFragmentManager.beginTransaction()
        transaction.replace(R.id.frame, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
}