package ppazosp.changapp

import android.animation.ValueAnimator
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Int,
    val fullname: String,
    val socials: String
)

@Serializable
data class Sport(
    val id: Int,
    val name: String
)

@Serializable
data class Place(
    val id: Int,
    val name: String
)

@Serializable
data class Advert(
    val user: Int,
    val sport: Int,
    val place: Int,
    val title: String,
    val description: String,
    val image: String
)

class SearchFragment : Fragment() {

    private var sports: List<Sport> = emptyList()
    private var places: List<Place> = emptyList()

    private var spinnerPlaces: Spinner? = null
    private var spinnerSports: Spinner? = null

    private var selectedPlace: Place? = null
    private var selectedSport: Sport? = null
    
    private var resultsContainer: LinearLayout? = null

    private var firstSearchDone = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_search, container, false)

        val searchButton: Button = view.findViewById(R.id.button_search)
        val miniframe: FrameLayout = view.findViewById(R.id.miniframe)
        val fab: FloatingActionButton = view.findViewById(R.id.fab)

        spinnerPlaces = view.findViewById(R.id.spinner_provincia)
        spinnerSports = view.findViewById(R.id.spinner_activity)
        
        resultsContainer = view.findViewById(R.id.results_container)


        setupSpinners()
        fetchSpinners()

        searchButton.setOnClickListener {

            CoroutineScope(Dispatchers.Main).launch { search() }

            animateMiniframe(miniframe)

            fab.bringToFront()

            searchButton.visibility = View.GONE
            firstSearchDone = true
        }

        fab.setOnClickListener {
            val dialogInsert = InsertAdvertDialog()
            dialogInsert.show(requireActivity().supportFragmentManager, "Crear anuncio")

        }

        return view
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

    private fun setupSpinners() {

        spinnerPlaces?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedPlace = places[position]

                if(firstSearchDone) { CoroutineScope(Dispatchers.Main).launch { search() } }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        spinnerSports?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
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
        spinnerPlaces?.adapter = adapterProvincias
        spinnerPlaces?.setSelection(0)

        val adapterSports = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, sports.map { it.name })
        adapterSports.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerSports?.adapter = adapterSports
        spinnerSports?.setSelection(0)
    }

    private fun fetchSpinners()
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

    private suspend fun fetchSports(): List<Sport>
    {
        return withContext(Dispatchers.IO) {
            supabase.from("sports").select().decodeList<Sport>()
        }
    }

    private suspend fun fetchPlaces(): List<Place>
    {
        return withContext(Dispatchers.IO) {
            supabase.from("places").select().decodeList<Place>()
        }
    }

    private suspend fun fetchAdverts(selectedPlace: Place?, selectedSport: Sport?): List<Advert> {
        return withContext(Dispatchers.IO) {
            when {
                selectedPlace?.id == -1 && selectedSport?.id == -1 -> {
                    supabase.from("adverts").select().decodeList<Advert>()
                }
                selectedPlace?.id == -1 -> {
                    supabase.from("adverts").select {
                        filter {
                            Advert::sport eq selectedSport?.id
                        }
                    }.decodeList<Advert>()
                }
                selectedSport?.id == -1 -> {
                    supabase.from("adverts").select {
                        filter {
                            Advert::place eq selectedPlace?.id
                        }
                    }.decodeList<Advert>()
                }
                else -> {
                    supabase.from("adverts").select {
                        filter {
                            Advert::place eq selectedPlace?.id
                            and { Advert::sport eq selectedSport?.id }
                        }
                    }.decodeList<Advert>()
                }
            }
        }
    }

    private suspend fun fetchUser(id: Int): User
    {
        return withContext(Dispatchers.IO) {
            supabase.from("users").select{filter { User::id eq id  }}.decodeSingle()
        }
    }

    private suspend fun search()
    {
        resultsContainer?.removeAllViews()

        val adverts: List<Advert> = fetchAdverts(selectedPlace, selectedSport)
        for(advert in adverts)
        {
            val user = fetchUser(advert.user)

            val resultView = LayoutInflater.from(context).inflate(R.layout.result_item, resultsContainer, false)
            val titleView = resultView.findViewById<TextView>(R.id.title)
            val descriptionView = resultView.findViewById<TextView>(R.id.title_input)
            val fullnameView = resultView.findViewById<TextView>(R.id.name)
            val socialsView = resultView.findViewById<TextView>(R.id.socials)
            val picView = resultView.findViewById<ImageView>(R.id.pic)

            titleView.text = advert.title
            descriptionView.text = advert.description
            fullnameView.text = user.fullname
            val socialsText = "@" + user.socials
            socialsView.text = socialsText
            context?.let {
                Glide.with(it)
                    .load(advert.image)
                    .into(picView)
            }

            copyTextOnTouch(resultView.findViewById(R.id.socials))
            resultsContainer?.addView(resultView)
        }
    }

    private fun copyTextOnTouch(textView: TextView) {
        textView.setOnClickListener {
            val clipboard = textView.context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Copied Text", textView.text.toString())
            clipboard.setPrimaryClip(clip)

            Toast.makeText(textView.context, "Text copied to clipboard!", Toast.LENGTH_SHORT).show()
        }
    }


}