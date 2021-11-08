package com.example.Valiavskiy_191_351.ui.Lab6


import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.Valiavskiy_191_351
.R
import com.google.android.gms.common.util.Base64Utils.encode
import kotlinx.android.synthetic.main.fragment_lab6.*
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.math.BigInteger
import java.security.MessageDigest
import java.security.Security
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

class Lab6Fragment : Fragment() {

    private lateinit var lab6ViewModel: Lab6ViewModel
    private lateinit var keyEditText: EditText
    private lateinit var result: EditText
    private lateinit var input: EditText

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        lab6ViewModel =
            ViewModelProvider(this).get(Lab6ViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_lab6, container, false)
        keyEditText = root.findViewById(R.id.Key)
        result = root.findViewById(R.id.result)
        input = root.findViewById(R.id.input)
        val choseFile = root.findViewById<Button>(R.id.chose_file)
        val decryptFile = root.findViewById<Button>(R.id.decrypt)
        choseFile.setOnClickListener {
            val text = input.text.toString()
            val key = keyGen(keyEditText.text.toString())
            val encText = encrypt(text, key)

            val newFile = File(getOutputDirectory(), "enc.txt")

            newFile.writeText(encText)
            result.setText(encText)
        }
        decryptFile.setOnClickListener {
            openFileForDecrypt()
        }


        return root
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            requireContext(), it
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun openFileForEncrypt() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "text/plain"
        }

        startActivityForResult(intent, ENCRYPT)
    }

    private fun openFileForDecrypt() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "text/plain"

        }

        startActivityForResult(intent, DECRYPT)
    }

    private fun getOutputDirectory(): File {
        val mediaDir = requireActivity().externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else requireActivity().filesDir
    }


    private fun readTextFromUri(uri: Uri): String {
        val contentResolver = context?.contentResolver
        val stringBuilder = StringBuilder()
        contentResolver?.openInputStream(uri)?.use { inputStream ->
            BufferedReader(InputStreamReader(inputStream)).use { reader ->
                var line: String? = reader.readLine()
                while (line != null) {
                    stringBuilder.append(line + "\n")
                    line = reader.readLine()
                }
            }
        }
        return stringBuilder.toString()
    }

    private fun encrypt(strToEncrypt: String, secret_key: String): String {
        Security.addProvider(BouncyCastleProvider())

        val keyBytes: ByteArray = secret_key.toByteArray(charset("UTF8"))
        val skey = SecretKeySpec(keyBytes, "AES_256")
        val input = strToEncrypt.toByteArray(charset("UTF8"))

        synchronized(Cipher::class.java) {
            val cipher = Cipher.getInstance("AES/ECB/PKCS7Padding")
            cipher.init(Cipher.ENCRYPT_MODE, skey)

            val cipherText = ByteArray(cipher.getOutputSize(input.size))
            var ctLength = cipher.update(
                input, 0, input.size,
                cipherText, 0
            )
            ctLength += cipher.doFinal(cipherText, ctLength)

            return encode(cipherText).toString()

        }

    }


    private fun decrypt(strToDecrypt: String?, key: String): String {
        Security.addProvider(BouncyCastleProvider())

        val keyBytes: ByteArray = key.toByteArray(charset("UTF8"))
        val skey = SecretKeySpec(keyBytes, "AES_256")
        val input = org.bouncycastle.util.encoders.Base64
            .decode(strToDecrypt?.trim { it <= ' ' }?.toByteArray(charset("UTF8")))

        synchronized(Cipher::class.java) {
            val cipher = Cipher.getInstance("AES/ECB/PKCS7Padding")
            cipher.init(Cipher.DECRYPT_MODE, skey)

            val plainText = ByteArray(cipher.getOutputSize(input.size))
            var ptLength = cipher.update(input, 0, input.size, plainText, 0)
            ptLength += cipher.doFinal(plainText, ptLength)
            val decryptedString = String(plainText)
            return decryptedString.trim { it <= ' ' }
        }
    }

    private fun keyGen(key: String): String {
        val md = MessageDigest.getInstance("MD5")
        return BigInteger(1, md.digest(key.toByteArray())).toString(16).padStart(32, '0')
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == DECRYPT && resultCode == RESULT_OK) {
            data?.data?.let {
                val selectedFile = it

                val text = readTextFromUri(selectedFile)
                val key = keyGen(keyEditText.text.toString())
                val decText = decrypt(text, key)

                result.setText(decText)

                // Perform operations on the document using its URI.
            }
        } //
    }
    companion object {
        const val ENCRYPT = 111
        const val DECRYPT = 222
        private const val REQUEST_CODE_PERMISSIONS = 11
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }


}
