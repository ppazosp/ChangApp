package ppazosp.changapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment

class SearchFragment : Fragment() {

    private var selectedProvincia : String = ""
    private var selectedActivity : String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_search, container, false)

        val spinnerProvincias: Spinner = view.findViewById(R.id.spinner_provincia)

        val provincias = listOf(
            "--Seleccionar--",

            "A Coruña",
            "Álava",
            "Albacete",
            "Alicante",
            "Almería",
            "Asturias",
            "Barcelona",
            "Cádiz",
            "Cantabria",
            "Castellón",
            "Ceuta",
            "Ciudad-Real",
            "Córdoba",
            "Cuenca",
            "Extremadura",
            "Girona",
            "Granada",
            "Guadalajara",
            "Guipúzcoa",
            "Huelva",
            "Huesca",
            "Jaén",
            "Las Palmas de Gran Canaria",
            "León",
            "Lleida",
            "Logroño",
            "Lugo",
            "Madrid",
            "Melilla",
            "Murcia",
            "Navarra",
            "Ourense",
            "Pontevedra",
            "Salamanca",
            "Santa Cruz de Tenerife",
            "Sevilla",
            "Soria",
            "Tarragona",
            "Teruel",
            "Toledo",
            "Valencia",
            "Valladolid",
            "Vizcaya",
            "Zamora",
            "Zaragoza"
        )

        val spinnerActivities: Spinner = view.findViewById(R.id.spinner_activity)

        val activities = listOf(
            "--Seleccionar--",

            "Fútbol",
            "Baloncesto",
            "Pádel",
            "Tenis",
            "Running",
            "Ciclismo",
            "Natación",
            "Balonmano",
            "Voleibol",
            "Golf"
        )

        val adapterProvincias = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, provincias)
        adapterProvincias.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerProvincias.adapter = adapterProvincias

        val adapterActivities = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, activities)
        adapterActivities.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerActivities.adapter = adapterActivities

        val searchButton: Button = view.findViewById(R.id.button_search)

        spinnerProvincias.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                selectedProvincia = provincias[position]
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        spinnerActivities.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                selectedActivity = activities[position]
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }

        searchButton.setOnClickListener {
            val searchResultFragment: Fragment = SearchResultsFragment()
            val bundle = Bundle()
            bundle.putString(
                "selectedProvincia",
                selectedProvincia
            )
            bundle.putString(
                "selectedActivity",
                selectedActivity
            )
            searchResultFragment.arguments = bundle

            if (activity is MainActivity) {
                (activity as MainActivity?)!!.loadFragment(searchResultFragment)
            }
        }

        return view
    }
}