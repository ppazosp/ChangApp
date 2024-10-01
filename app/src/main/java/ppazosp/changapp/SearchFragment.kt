package ppazosp.changapp

import android.animation.ValueAnimator
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import io.ktor.websocket.Frame
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class SearchFragment : Fragment(), OnDialogDismissedListener {

    private var types: List<Type> = emptyList()
    private var places: List<Place> = emptyList()

    private lateinit var selectedPlace: Place
    private lateinit var selectedType: Type

    private lateinit var spinnerPlaces: Spinner
    private lateinit var spinnerTypes: Spinner

    private lateinit var miniframe: FrameLayout

    private lateinit var fab: FloatingActionButton

    private lateinit var resultsContainer: LinearLayout

    private lateinit var resultsContainerScroll: ScrollView

    private lateinit var searchButton: Button

    private lateinit var queryView: EditText

    private var firstSearchDone = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_search, container, false)

        LoadingScreen.show(requireActivity())

        initializeVars(view)

        setUI()

        setOnClickListeners()

        LoadingScreen.hide()

        return view
    }

    private fun initializeVars(view: View)
    {
        searchButton = view.findViewById(R.id.button_search)
        miniframe = view.findViewById(R.id.miniframe)
        fab = view.findViewById(R.id.fab)
        spinnerPlaces = view.findViewById(R.id.spinner_provincia)
        spinnerTypes = view.findViewById(R.id.spinner_activity)
        resultsContainer = view.findViewById(R.id.results_container)
        resultsContainerScroll = view.findViewById(R.id.results_container_scroll)

        queryView = view.findViewById(R.id.query)
    }

    private fun setUI()
    {
        setupSpinners()
        fillSpinners()
    }

    private fun setOnClickListeners()
    {
        searchButton.setOnClickListener {

            CoroutineScope(Dispatchers.Main).launch { search() }

            if (!firstSearchDone)
            {
                animateMiniframe(miniframe)

                resultsContainerScroll.visibility = View.VISIBLE
                fab.visibility = View.VISIBLE

                firstSearchDone = true
            }
        }

        fab.setOnClickListener {
            val dialogInsert = InsertAdvertDialog.newInstance(selectedPlace.id, selectedType.id)
            dialogInsert.setOnDialogDismissedListener(this)
            dialogInsert.show(childFragmentManager, "Crear anuncio")
        }
    }

    private fun animateMiniframe(miniframe: FrameLayout) {
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
    }

    override fun onDialogDismissed() {
        CoroutineScope(Dispatchers.Main).launch { search() }
        Log.e("called", "called")
    }

    private fun setupSpinners() {

        spinnerPlaces.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedPlace = places[position]

                if(firstSearchDone) { CoroutineScope(Dispatchers.Main).launch { search() } }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        spinnerTypes.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedType = types[position]

                if (firstSearchDone) { CoroutineScope(Dispatchers.Main).launch { search() } }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun updateAdapters() {

        val adapterProvincias = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, places.map { it.name })
        adapterProvincias.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerPlaces.adapter = adapterProvincias
        spinnerPlaces.setSelection(0)

        val adapterSports = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, types.map { it.name })
        adapterSports.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerTypes.adapter = adapterSports
        spinnerTypes.setSelection(0)
    }

    private fun fillSpinners()
    {
        CoroutineScope(Dispatchers.Main).launch {
            types = fetchTypes()
            places = fetchPlaces()

            val defaultType = Type(id = -1, name = "--Seleccionar--")
            val defaultPlace = Place(id = -1, name = "--Seleccionar--")

            types = listOf(defaultType) + types
            places = listOf(defaultPlace) + places

            if(isAdded) updateAdapters()
        }
    }

    private suspend fun search()
    {
        withContext(Dispatchers.Main)
        {
            LoadingScreen.show(requireActivity())
        }

        resultsContainer.removeAllViews()

        val query = queryView.text.toString()

        val adverts: List<Advert> = fetchAdverts(query, selectedPlace, selectedType)
        for(advert in adverts)
        {
            val user = fetchUser(advert.user)

            val resultView = LayoutInflater.from(context).inflate(R.layout.result_item, resultsContainer, false)

            val userView = resultView.findViewById<TextView>(R.id.userID)

            val titleView = resultView.findViewById<TextView>(R.id.title_view)
            val typePlaceView = resultView.findViewById<TextView>(R.id.type_place_view)
            val picView = resultView.findViewById<ImageView>(R.id.pic)
            val dateView = resultView.findViewById<TextView>(R.id.dateView)

            val frameLayout = resultView.findViewById<FrameLayout>(R.id.linearLayout)

            userView.text = advert.user.toString()

            titleView.text = advert.title
            val typePlace = "${spinnerPlaces.getItemAtPosition(advert.place) as String} - ${spinnerTypes.getItemAtPosition(advert.type) as String}"
            Log.e("Error", typePlace)
            typePlaceView.text = typePlace
            if (advert.image != null ) Encripter.setImageFromBase64(picView, advert.image)
            dateView.text = advert.date

            if(advert.user == myUser.id)
            {
                frameLayout.setOnClickListener {
                    val dialogDelete = DeleteAdvertDialog.newInstance(myUser.id!!, advert.place, advert.type)
                    dialogDelete.setOnDialogDismissedListener(this)
                    dialogDelete.show(childFragmentManager, "Eliminar anuncio")
                }
            }else
            {
                frameLayout.setOnClickListener {
                    val dialogShowAdvert = ShowAdvertDialog.newInstance(advert.id)
                    dialogShowAdvert.show(childFragmentManager, "Mostrar anuncio")
                }
            }

            resultsContainer.addView(resultView)

            animateResultItem(resultView)
        }

        withContext(Dispatchers.Main)
        {
            LoadingScreen.hide()
        }
    }

    private fun animateResultItem(resultItem: View) {

        resultItem.scaleX = 0f

        resultItem.visibility = View.VISIBLE

        resultItem.animate().scaleX(1f).setDuration(1000).start()
    }

}