package com.example.android_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.android_app.ui.theme.Android_appTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Android_appTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MapClientUI(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun MapClientUI(modifier: Modifier = Modifier) {
    var leftTopX by remember { mutableStateOf("") }
    var leftTopY by remember { mutableStateOf("") }
    var rightBottomX by remember { mutableStateOf("") }
    var rightBottomY by remember { mutableStateOf("") }
    var mapText by remember { mutableStateOf("Map will appear here") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Input fields for coordinates
        Text(text = "Enter Coordinates:")

        BasicTextField(
            value = leftTopX,
            onValueChange = { leftTopX = it },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            singleLine = true,
            decorationBox = { innerTextField ->
                if (leftTopX.isEmpty()) Text("Left Top X")
                innerTextField()
            }
        )

        BasicTextField(
            value = leftTopY,
            onValueChange = { leftTopY = it },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            singleLine = true,
            decorationBox = { innerTextField ->
                if (leftTopY.isEmpty()) Text("Left Top Y")
                innerTextField()
            }
        )

        BasicTextField(
            value = rightBottomX,
            onValueChange = { rightBottomX = it },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            singleLine = true,
            decorationBox = { innerTextField ->
                if (rightBottomX.isEmpty()) Text("Right Bottom X")
                innerTextField()
            }
        )

        BasicTextField(
            value = rightBottomY,
            onValueChange = { rightBottomY = it },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            singleLine = true,
            decorationBox = { innerTextField ->
                if (rightBottomY.isEmpty()) Text("Right Bottom Y")
                innerTextField()
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Button to send request
        Button(onClick = {
            // Add your logic to send a request with these coordinates
            mapText = "Request sent with coordinates: $leftTopX, $leftTopY, $rightBottomX, $rightBottomY"
        }) {
            Text("Send Request")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Display map fragment or placeholder text
        Text(text = mapText)
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewMapClientUI() {
    Android_appTheme {
        MapClientUI()
    }
}