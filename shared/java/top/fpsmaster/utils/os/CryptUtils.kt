package top.fpsmaster.utils.os

import java.nio.charset.StandardCharsets
import java.security.Key
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

object CryptUtils {
    private const val ALGORITHM = "AES"
    private const val TRANSFORMATION = "AES/ECB/PKCS5Padding"
    fun encrypt(text: String, key: String): String {
        val secretKey = generateKey(key)
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        val encryptedBytes = cipher.doFinal(text.toByteArray(StandardCharsets.UTF_8))
        return Base64.getEncoder().encodeToString(encryptedBytes)
    }

    fun decrypt(encryptedText: String?, key: String): String {
        val secretKey = generateKey(key)
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.DECRYPT_MODE, secretKey)
        val encryptedBytes = Base64.getDecoder().decode(encryptedText)
        val decryptedBytes = cipher.doFinal(encryptedBytes)
        return String(decryptedBytes, StandardCharsets.UTF_8)
    }

    private fun generateKey(key: String): Key {
        val keyBytes = getMD5Hash(key)!!.toByteArray(StandardCharsets.UTF_8)
        return SecretKeySpec(keyBytes, ALGORITHM)
    }

    private fun getMD5Hash(input: String): String? {
        return try {
            val md = MessageDigest.getInstance("MD5")
            val messageDigest = md.digest(input.toByteArray())
            val hexString = StringBuilder()
            for (b in messageDigest) {
                hexString.append(String.format("%02x", b.toInt() and 0xFF))
            }
            hexString.toString().lowercase(Locale.getDefault())
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
            null
        }
    }

    fun getSHA256Hash(input: String): String? {
        return try {
            val sha256Digest = MessageDigest.getInstance("SHA-256")
            val hash = sha256Digest.digest(input.toByteArray())
            val hexString = StringBuilder()
            for (b in hash) {
                val hex = Integer.toHexString(0xff and b.toInt())
                if (hex.length == 1) hexString.append('0')
                hexString.append(hex)
            }
            hexString.toString()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
            null
        }
    }
}
