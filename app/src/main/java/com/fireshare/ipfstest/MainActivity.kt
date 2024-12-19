package com.fireshare.ipfstest

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.fireshare.ipfstest.ui.theme.IpfsTestTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            IpfsTestTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { paddingValues ->
                    ImageUploadComposable(Modifier.padding(paddingValues))
                }
            }
        }
    }
}

@Composable
fun ImageUploadComposable(modifier: Modifier
) {
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var uploadStatus by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
    }

    Column(
        modifier = modifier
    ) {
        Button(onClick = { launcher.launch("image/*") }) {
            Text("Select Image")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (selectedImageUri != null) {
            Text("Selected Image: ${selectedImageUri?.path}")
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                uploadStatus = "Uploading..."
                val cid = uploadToIPFS(context, selectedImageUri!!)
                uploadStatus = "Upload complete. CID: $cid"
            }) {
                Text("Upload Image")
            }

            Spacer(modifier = Modifier.height(16.dp))

            uploadStatus?.let {
                Text(it)
            }
        }
    }
}
