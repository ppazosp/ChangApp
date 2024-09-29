package ppazosp.changapp

import android.content.Context
import android.util.Log
import android.widget.Toast
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable


const val SUPABASE_URL : String = "https://mapvepqvdgagccguault.supabase.co"
const val SUPABASE_API_KEY : String = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im1hcHZlcHF2ZGdhZ2NjZ3VhdWx0Iiwicm9sZSI6InNlcnZpY2Vfcm9sZSIsImlhdCI6MTcyNzAyNjQ1NSwiZXhwIjoyMDQyNjAyNDU1fQ.WT754nds11_TpqM3by8cAbbFzSjTK-yybYU3SPDalGw"

val supabase = createSupabaseClient(
    supabaseUrl = SUPABASE_URL,
    supabaseKey = SUPABASE_API_KEY
) {
    install(Postgrest)
}

@Serializable
data class InsertionUser(
    val email: String,
    val fullname: String,
    val socials: String?,
    val password: String,
)

@Serializable
data class LoginUser(
    val email: String,
    val password: String
)

@Serializable
data class User(
    val id: Int?,
    val email: String,
    val fullname: String,
    val socials: String?,
    val password: String,
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
    val image: String?
)

suspend fun fetchSports(): List<Sport>
{
    return withContext(Dispatchers.IO) {
        supabase.from("sports").select().decodeList<Sport>()
    }
}

suspend fun fetchPlaces(): List<Place>
{
    return withContext(Dispatchers.IO) {
        supabase.from("places").select().decodeList<Place>()
    }
}

suspend fun fetchAdverts(selectedPlace: Place?, selectedSport: Sport?): List<Advert> {
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

suspend fun fetchUser(id: Int): User
{
    return withContext(Dispatchers.IO) {
        supabase.from("users").select{filter { User::id eq id  }}.decodeSingle()
    }
}

suspend fun fetchUser(email: String): User
{
    return withContext(Dispatchers.IO) {
        supabase.from("users").select{filter { User::email eq email  }}.decodeSingle()
    }
}

suspend fun checkLogin(context: Context, user: LoginUser): Boolean
{
    if (user.email.isEmpty() || user.password.isEmpty()) {
        Toast.makeText(context, "email o contraseña vacíos", Toast.LENGTH_LONG).show()
        return false
    }

    try {

        val loggedUser = supabase.from("users").select {
            filter {
                User::email eq user.email
            }
        }.decodeSingle<User>()

        val storedHashedPassword = user.password

        val isPasswordValid = Encripter.validatePassword(user.password, storedHashedPassword)

        if (isPasswordValid) {
            myUser = loggedUser
            return true
        } else {
            Toast.makeText(context, "email o contraseña incorrectos", Toast.LENGTH_SHORT).show()
            return false
        }

    } catch (e: Exception) {
        Log.e("Exception", e.message.toString())
        Toast.makeText(context, "email o contraseña incorrectos", Toast.LENGTH_SHORT).show()
        return false
    }
}

suspend fun registerUser(context: Context, user: InsertionUser) : Boolean {
    try {
        supabase.from("users").insert(user)
    } catch (e: Exception) {
        if (e.message?.contains("duplicate key value", ignoreCase = true) == true) {
            ErrorHandler.showError(context, "Este correo ya está registrado")
            return false
        } else {
            Log.e("Exception", e.message.toString())
            ErrorHandler.showError(context, "No se pudo registrar el usuario")
            return false
        }
    }

    myUser = fetchUser(user.email)

    return true
}

suspend fun insertAdvert(context:Context, advert: Advert) {

    try{
        supabase.from("adverts").insert(advert)
    }catch (e: Exception){
        ErrorHandler.showError(context, "No se pudo publicar el anuncio")
    }

}

suspend fun deleteAdvert(context: Context, user: Int, place: Int, sport: Int) {

    try {
        supabase.from("adverts").delete {
            filter {
                Advert::user eq user
                and {
                    Advert::place eq place
                    and {
                        Advert::sport eq sport
                    }
                }
            }
        }
    }catch (e: Exception)
    {
        ErrorHandler.showError(context, "No se pudo eliminar el anuncio")
    }
}

suspend fun deleteUser(context: Context) : Boolean
{

    try {
        val result = supabase.from("users").delete {
            filter {
                User::id eq myUser.id
            }
        }

        if (result.data.isEmpty()) {
            ErrorHandler.showError(context, "No se encontró el usuario")
            return false
        }

    }catch (e: Exception){
        ErrorHandler.showError(context, "No se pudo eliminar el usuario")
        return false
    }

    return true
}




