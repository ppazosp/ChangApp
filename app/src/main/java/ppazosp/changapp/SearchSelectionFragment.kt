package ppazosp.changapp

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner

class SearchSelectionFragment : Fragment() {

    private var selectedProvincia: String = ""
    private var selectedActivity: String = ""
    private var listener: OnSearchButtonClickListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_search_selection, container, false)

        val spinnerProvincias: Spinner = view.findViewById(R.id.spinner_provincia)
        val spinnerActivities: Spinner = view.findViewById(R.id.spinner_activity)
        val searchButton: Button = view.findViewById(R.id.button_search)

        setupSpinner(spinnerProvincias, spinnerActivities)

        searchButton.setOnClickListener {
            val bundle = Bundle().apply {
                putString("selectedProvincia", selectedProvincia)
                putString("selectedActivity", selectedActivity)
            }
            listener?.onSearchButtonClicked(bundle)
        }

        return view
    }

    private fun setupSpinner(spinnerProvincias: Spinner, spinnerActivities: Spinner) {

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
            "Ciudad Real",
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
            "Las Palmas",
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
            "Tenerife",
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
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Ensure that the parent fragment is an instance of SearchFragment
        if (parentFragment is SearchFragment) {
            listener = parentFragment as SearchFragment
        } else {
            throw RuntimeException("$context must implement OnSearchButtonClickListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface OnSearchButtonClickListener {
        fun onSearchButtonClicked(data: Bundle)
    }
}