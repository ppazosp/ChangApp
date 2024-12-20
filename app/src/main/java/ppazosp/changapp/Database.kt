package ppazosp.changapp

import android.content.Context
import android.icu.text.SimpleDateFormat
import android.util.Log
import android.widget.Toast
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import java.util.Date


const val SUPABASE_URL : String = "x"
const val SUPABASE_API_KEY : String = "x"

val supabase = createSupabaseClient(
    supabaseUrl = SUPABASE_URL,
    supabaseKey = SUPABASE_API_KEY,
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
data class Type(
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
    val id: Int,
    val user: Int,
    val type: Int,
    val place: Int,
    val title: String,
    val description: String,
    val image: String?,
    val date: String
)

@Serializable
data class InsertAdvert(
    val user: Int,
    val type: Int,
    val place: Int,
    val title: String,
    val description: String,
    val image: String?,
    val date: String
)


suspend fun fetchTypes(): List<Type>
{
    return withContext(Dispatchers.IO) {
        supabase.from("types").select().decodeList<Type>()
    }
}

suspend fun fetchPlaces(): List<Place>
{
    return withContext(Dispatchers.IO) {
        supabase.from("places").select().decodeList<Place>()
    }
}

suspend fun fetchAdverts(query: String, selectedPlace: Place?, selectedType: Type?): List<Advert> {
    return withContext(Dispatchers.IO) {
        when {
            selectedPlace?.id == -1 && selectedType?.id == -1 -> {
                supabase.from("adverts").select{
                    filter {
                        or {
                            Advert::title ilike "%$query%"
                            Advert::description ilike "%$query%"
                        }
                    }
                }.decodeList<Advert>()
            }
            selectedPlace?.id == -1 -> {
                supabase.from("adverts").select {
                    filter {
                        and {
                            Advert::type eq selectedType?.id

                            or {
                                Advert::title ilike "%$query%"
                                Advert::description ilike "%$query%"
                            }
                        }
                    }
                }.decodeList<Advert>()
            }
            selectedType?.id == -1 -> {
                supabase.from("adverts").select {
                    filter {
                        and {
                            Advert::place eq selectedPlace?.id
                            or {
                                Advert::title ilike "%$query%"
                                Advert::description ilike "%$query%" }
                        }
                    }
                }.decodeList<Advert>()
            }
            else -> {
                supabase.from("adverts").select {
                    filter {
                        and {
                            and {
                                Advert::type eq selectedType?.id
                                Advert::place eq selectedPlace?.id
                            }

                            or {
                                Advert::title ilike "%$query%"
                                Advert::description ilike "%$query%"
                            }
                        }
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

suspend fun fetchPlace(id: Int): Place
{
    return withContext(Dispatchers.IO) {
        supabase.from("places").select{filter { Place::id eq id  }}.decodeSingle()
    }
}

suspend fun fetchType(id: Int): Type
{
    return withContext(Dispatchers.IO) {
        supabase.from("types").select{filter { Type::id eq id  }}.decodeSingle()
    }
}

suspend fun fetchAdvert(id: Int): Advert
{
    return withContext(Dispatchers.IO) {
        supabase.from("adverts").select{filter { Advert::id eq id  }}.decodeSingle()
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

        val storedHashedPassword = loggedUser.password

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

suspend fun insertAdvert(context:Context, advert: InsertAdvert) {

    try{
        supabase.from("adverts").insert(advert)
    }catch (e: Exception){
        Log.e("Error", e.message.toString())
        withContext(Dispatchers.Main)
        {
            ErrorHandler.showError(context, "No se pudo publicar el anuncio")
        }
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
                        Advert::type eq sport
                    }
                }
            }
        }
    }catch (e: Exception)
    {
        withContext(Dispatchers.Main)
        {
            ErrorHandler.showError(context, "No se pudo eliminar el anuncio")
        }
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
        withContext(Dispatchers.Main)
        {
            ErrorHandler.showError(context, "No se pudo eliminar el usuario")
        }
        return false
    }

    return true
}

suspend fun fetchUserAdverts(context: Context, id: Int): List<Advert>
{
    var adverts: List<Advert> = emptyList()

    try{
        adverts = supabase.from("adverts").select {
            filter {
                Advert:: user eq id
            }
        }.decodeList<Advert>()
    }catch (e: Exception)
    {
        withContext(Dispatchers.Main)
        {
            ErrorHandler.showError(context, "No se pudieron recuperar tus anuncios")
        }
        return adverts
    }
    return adverts
}


fun getFormattedDate(): String
{
    val formatter = SimpleDateFormat("dd/MM 'a las' HH:mm")

    return formatter.format(Date())
}





