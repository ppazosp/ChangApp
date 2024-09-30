package ppazosp.changapp

import android.animation.ValueAnimator
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class SearchFragment : Fragment(), OnDialogDismissedListener {

    private var sports: List<Sport> = emptyList()
    private var places: List<Place> = emptyList()

    private lateinit var selectedPlace: Place
    private lateinit var selectedSport: Sport

    private lateinit var spinnerPlaces: Spinner
    private lateinit var spinnerSports: Spinner

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

        LoadingScreen.show(requireContext())

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
        spinnerSports = view.findViewById(R.id.spinner_activity)
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
            val dialogInsert = InsertAdvertDialog.newInstance(selectedPlace.id, selectedSport.id)
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

        spinnerSports.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedSport = sports[position]

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

        val adapterSports = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, sports.map { it.name })
        adapterSports.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerSports.adapter = adapterSports
        spinnerSports.setSelection(0)
    }

    private fun fillSpinners()
    {
        CoroutineScope(Dispatchers.Main).launch {
            sports = fetchSports()
            places = fetchPlaces()

            val defaultSport = Sport(id = -1, name = "--Seleccionar--")
            val defaultPlace = Place(id = -1, name = "--Seleccionar--")

            sports = listOf(defaultSport) + sports
            places = listOf(defaultPlace) + places

            if(isAdded) updateAdapters()
        }
    }

    private suspend fun search()
    {
        withContext(Dispatchers.Main)
        {
            LoadingScreen.show(requireContext())
        }

        resultsContainer.removeAllViews()

        val query = queryView.text.toString()

        val adverts: List<Advert> = fetchAdverts(query, selectedPlace, selectedSport)
        for(advert in adverts)
        {
            val user = fetchUser(advert.user)

            val resultView = LayoutInflater.from(context).inflate(R.layout.result_item, resultsContainer, false)

            val userView = resultView.findViewById<TextView>(R.id.userID)

            val titleView = resultView.findViewById<TextView>(R.id.username)
            val descriptionView = resultView.findViewById<TextView>(R.id.last_message)
            val fullnameView = resultView.findViewById<TextView>(R.id.name)
            val socialsView = resultView.findViewById<TextView>(R.id.socials)
            val placeView = resultView.findViewById<TextView>(R.id.place)
            val picView = resultView.findViewById<ImageView>(R.id.pic)

            val linerLayout = resultView.findViewById<LinearLayout>(R.id.linearLayout)

            userView.text = advert.user.toString()

            titleView.text = advert.title
            descriptionView.text = advert.description
            fullnameView.text = user.fullname
            val socialsText = "@" + user.socials
            socialsView.text = socialsText
            placeView.text = spinnerPlaces.getItemAtPosition(advert.place) as String
            if (advert.image != null ) Encripter.setImageFromBase64(picView, advert.image)

            if(advert.user == myUser.id)
            {
                linerLayout.setOnClickListener {
                    val dialogDelete = DeleteAdvertDialog.newInstance(myUser.id!!, advert.place, advert.sport)
                    dialogDelete.setOnDialogDismissedListener(this)
                    dialogDelete.show(childFragmentManager, "Eliminar anuncio")
                }
            }else
            {
                linerLayout.setOnClickListener {
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