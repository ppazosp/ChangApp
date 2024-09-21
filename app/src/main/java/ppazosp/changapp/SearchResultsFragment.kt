package ppazosp.changapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment


class SearchResultsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_search_results, container, false)

        val bundle = arguments
        if (bundle != null) {
            val selectedProvincia = bundle.getString("selectedProvincia")

            val provinciaTextView =
                view.findViewById<TextView>(R.id.selected_provincia)
            provinciaTextView.text = selectedProvincia
        }

        return view
    }
}