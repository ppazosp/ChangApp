package ppazosp.changapp

import java.security.SecureRandom
import java.security.spec.KeySpec
import java.util.Base64
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

object Encripter {

    private val SALT_LENGTH: Int = 16
    private val HASH_ITERATIONS: Int = 10000
    private val HASH_LENGTH: Int = 256

    fun generateSalt(): ByteArray {
        val random = SecureRandom()
        val salt = ByteArray(SALT_LENGTH)
        random.nextBytes(salt)
        return salt
    }

    fun hashPassword(password: String, salt: ByteArray?): String {

        val spec: KeySpec = PBEKeySpec(password.toCharArray(), salt, HASH_ITERATIONS, HASH_LENGTH)
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")

        val hash = factory.generateSecret(spec).encoded

        return Base64.getEncoder().encodeToString(salt) + ":" + Base64.getEncoder()
            .encodeToString(hash)
    }

    fun validatePassword(enteredPassword: String?, storedPassword: String): Boolean {

        val parts = storedPassword.split(":".toRegex()).dropLastWhile { it.isEmpty() }
            .toTypedArray()
        val salt = Base64.getDecoder().decode(parts[0])

        val enteredHash = hashPassword(enteredPassword!!, salt)

        return enteredHash == storedPassword
    }
}