package ppazosp.changapp

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.IOException

class SearchFragment : Fragment() {

    private var autoCompleteTextView: AutoCompleteTextView? = null
    private var recyclerView: RecyclerView? = null
    private var adapter: PlaceAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_search, container, false)

        autoCompleteTextView = view.findViewById(R.id.autocomplete_city)
        recyclerView = view.findViewById(R.id.recyclerview_suggestions)
        recyclerView?.layoutManager = LinearLayoutManager(requireContext())

        autoCompleteTextView?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.length >= 3) {
                    searchCities(s.toString())
                } else {
                    recyclerView?.visibility = View.GONE
                }
            }

            override fun afterTextChanged(s: Editable) {
            }
        })

        return view
    }

    private fun searchCities(query: String) {
        Thread {
            try {
                val places = NominatimSearch.searchCity(query)
                requireActivity().runOnUiThread {
                    if (places != null && places.isNotEmpty()) {
                        recyclerView?.visibility = View.VISIBLE
                        adapter = PlaceAdapter(places) { place: Place ->
                            Toast.makeText(
                                requireContext(),
                                "Selected: " + place.displayName,
                                Toast.LENGTH_SHORT
                            ).show()
                            recyclerView?.visibility = View.GONE
                        }
                        recyclerView?.adapter = adapter
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }.start()
    }
}