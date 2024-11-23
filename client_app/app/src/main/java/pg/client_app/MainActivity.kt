package pg.client_app

//import androidx.compose.material.
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
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


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Client_appTheme {
                Surface(color = Color.Gray) {
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
                    modifier = Modifier.weight(1F).padding(1.dp)

                )
                TextField(
                    value = leftTopY,
                    onValueChange = { leftTopY = it },
                    label = { Text("Lewy Górny Y") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1F).padding(1.dp)

                )
            }

            Row {
                TextField(
                    value = rightBottomX,
                    onValueChange = { rightBottomX = it },
                    label = { Text("Prawy Dolny X") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1F).padding(1.dp)
                )
                TextField(
                    value = rightBottomY,
                    onValueChange = { rightBottomY = it },
                    label = { Text("Prawy Dolny Y") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1F).padding(1.dp)
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
                ToastMessage("Uzupełnij wszystkie pola")
                showToast = false
            }

            // Displaying the result
            if (imageBase64 != null) {
                Canvas(modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)) {
                    // TODO: Decode Base64 and display the image
                }
            }
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
        val url = "http://yourserver.com/Service?wsdl"

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

        val httpTransport = HttpTransportSE(url)

        // Use coroutine for background execution
        kotlinx.coroutines.GlobalScope.launch(Dispatchers.IO) {
            try {
                httpTransport.call(soapAction, envelope)
                val response = envelope.response as? String
                withContext(Dispatchers.Main) {
                    onResponse(response)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    onResponse(null)
                }
            }
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