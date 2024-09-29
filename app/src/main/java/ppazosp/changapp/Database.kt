package ppazosp.changapp

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
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