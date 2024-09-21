package ppazosp.changapp

import android.animation.ValueAnimator
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.Spinner
import androidx.fragment.app.Fragment

class SearchFragment : Fragment() {

    private var selectedProvincia: String = ""
    private var selectedActivity: String = ""

    private var firstSearchDone = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_search, container, false)

        val spinnerProvincias: Spinner = view.findViewById(R.id.spinner_provincia)
        val spinnerActivities: Spinner = view.findViewById(R.id.spinner_activity)
        val searchButton: Button = view.findViewById(R.id.button_search)
        val miniframe: FrameLayout = view.findViewById(R.id.miniframe)
        val resultsContainer: LinearLayout = view.findViewById(R.id.results_container)

        setupSpinner(spinnerProvincias, spinnerActivities)

        searchButton.setOnClickListener {
            //resultsContainer.visibility = View.VISIBLE

            //val resultView = LayoutInflater.from(context).inflate(R.layout.result_item, resultsContainer, false)
            //resultsContainer.addView(resultView)

            val screenHeight = resources.displayMetrics.heightPixels

            val topMargin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 80F, resources.displayMetrics).toInt()
            val bottomMargin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40F, resources.displayMetrics).toInt()

            val desiredHeight = screenHeight - (topMargin + bottomMargin)

            val params = miniframe.layoutParams
            params.height = desiredHeight

            ValueAnimator.ofInt(miniframe.height, desiredHeight).apply {
                duration = 1000
                addUpdateListener {

                    params.height = it.animatedValue as Int
                    miniframe.layoutParams = params
                }
                start()
            }

            searchButton.visibility = View.GONE
            firstSearchDone = true
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

                if(firstSearchDone) search(selectedProvincia, selectedActivity)
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        spinnerActivities.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                selectedActivity = activities[position]

                if (firstSearchDone) search(selectedProvincia, selectedActivity)
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun search(selectedProvincia: String, selectedActivity: String)
    {

    }
}