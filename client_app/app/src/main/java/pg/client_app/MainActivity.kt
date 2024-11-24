package pg.client_app

//import androidx.compose.material.
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.ksoap2.SoapEnvelope
import org.ksoap2.serialization.SoapObject
import org.ksoap2.serialization.SoapSerializationEnvelope
import org.ksoap2.transport.HttpTransportSE
import pg.client_app.ui.theme.Client_appTheme
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale


class MainActivity : ComponentActivity() {

    var requestSender: ((Int, Int, Int, Int, (String?) -> Unit) -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Client_appTheme {
                Surface(color = Color.White) {
                    MapFragmentApp()
                }
            }
        }
    }

    @Composable
    fun MapFragmentApp() {
        var leftTopX by remember { mutableStateOf("") }
        var leftTopY by remember { mutableStateOf("") }
        var rightBottomX by remember { mutableStateOf("") }
        var rightBottomY by remember { mutableStateOf("") }
        var imageBase64 by remember { mutableStateOf<String?>(null) }
        var showToast by remember { mutableStateOf(false) }

        val context = LocalContext.current

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row {
                TextField(
                    value = leftTopX,
                    onValueChange = { leftTopX = it },
                    label = { Text("Lewy Górny X") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .weight(1f)
                        .padding(1.dp)
                )
                TextField(
                    value = leftTopY,
                    onValueChange = { leftTopY = it },
                    label = { Text("Lewy Górny Y") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .weight(1f)
                        .padding(1.dp)
                )
            }

            Row {
                TextField(
                    value = rightBottomX,
                    onValueChange = { rightBottomX = it },
                    label = { Text("Prawy Dolny X") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .weight(1f)
                        .padding(1.dp)
                )
                TextField(
                    value = rightBottomY,
                    onValueChange = { rightBottomY = it },
                    label = { Text("Prawy Dolny Y") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .weight(1f)
                        .padding(1.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (leftTopX.isNotEmpty() && leftTopY.isNotEmpty() && rightBottomX.isNotEmpty() && rightBottomY.isNotEmpty()) {
                        sendRequest(
                            leftTopX.toInt(),
                            leftTopY.toInt(),
                            rightBottomX.toInt(),
                            rightBottomY.toInt()
                        ) { response ->
                            imageBase64 = response
                        }
                    } else {
                        showToast = true
                    }
                },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Pobierz fragment mapy")
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (showToast) {
                Toast.makeText(context, "Uzupełnij wszystkie pola", Toast.LENGTH_SHORT).show()
                showToast = false
            }

            imageBase64?.let { base64 ->
                Base64Image(base64)
            } ?: Text(
                text = "Brak danych do wyświetlenia mapy",
                color = Color.Gray,
                modifier = Modifier.padding(16.dp)
            )
        }
    }

    @Composable
    fun ToastMessage(message: String) {
        val context = LocalContext.current
        LaunchedEffect(Unit) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    fun sendRequest(
        leftTopX: Int,
        leftTopY: Int,
        rightBottomX: Int,
        rightBottomY: Int,
        onResponse: (String?) -> Unit
    ) {
        val namespace = "http://your.namespace.com/"
        val methodName = "getMapFragment"
        val soapAction = "$namespace$methodName"
        val url = "http://10.0.2.2:3000/api/v1/map/action"

            val request = SoapObject(namespace, methodName).apply {
                addProperty("leftTopX", leftTopX)
                addProperty("leftTopY", leftTopY)
                addProperty("rightBottomX", rightBottomX)
                addProperty("rightBottomY", rightBottomY)
            }

            val envelope = SoapSerializationEnvelope(SoapEnvelope.VER11).apply {
                dotNet = false
                setOutputSoapObject(request)
            }

        val httpTransport = HttpTransportSE(url).apply {
            debug = true
        }

        kotlinx.coroutines.GlobalScope.launch(Dispatchers.IO) {
            try {
                httpTransport.call(soapAction, envelope)

                val response = envelope.bodyIn as? SoapObject
                val imageBase64 = response?.getProperty("image")?.toString()

                withContext(Dispatchers.Main) {
                    onResponse(imageBase64)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    onResponse(null)
                }
            }
        }
    }


    @Composable
    fun Base64Image(base64String: String) {
        val decodedBytes = android.util.Base64.decode(base64String, android.util.Base64.DEFAULT)
        val bitmap = android.graphics.BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)

        if (bitmap != null) {
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "Base64 Decoded Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Fit
            )
        } else {
            Text(
                text = "Nie udało się załadować obrazu",
                color = Color.Red,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
    @Preview(showBackground = true)
    @Composable
    fun GreetingPreview() {
        Client_appTheme {
            MapFragmentApp()

        }
    }
}