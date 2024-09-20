package ppazosp.changapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment

class SearchFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_search, container, false)

        val spinnerProvincias: Spinner = view.findViewById(R.id.spinner_cities)


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

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, provincias)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerProvincias.adapter = adapter

        spinnerProvincias.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val provinciaSeleccionada = provincias[position]
                Toast.makeText(requireContext(), "Selected: $provinciaSeleccionada", Toast.LENGTH_SHORT).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        return view
    }
}