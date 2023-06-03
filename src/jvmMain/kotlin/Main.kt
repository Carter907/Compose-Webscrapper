import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import kotlinx.coroutines.*
import kotlinx.coroutines.async
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.nio.file.FileSystems
import java.nio.file.Files
import java.util.concurrent.CompletableFuture

@Composable
@Preview
fun App() {

    var inputURL by remember { mutableStateOf("") }
    var outputText by remember { mutableStateOf("") }
    var loadingAnimation by remember { mutableStateOf(false) }
    MaterialTheme {

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceAround,
            horizontalAlignment = Alignment.CenterHorizontally

        ) {
            OutlinedTextField(
                label = {
                    Icon(Icons.Rounded.Search, contentDescription = "search field")
                },
                placeholder = {
                    Text("http....")
                },
                value = inputURL,
                onValueChange = { inputURL = it }
            )
            Surface(
                elevation = 20.dp
            ) {
                Text(
                    outputText, modifier = Modifier
                        .size(400.dp)
                        .verticalScroll(
                            rememberScrollState()
                        )
                );

            }

            AnimatedVisibility(
                visible = loadingAnimation,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                val transition = rememberInfiniteTransition()
                val rotation by transition.animateFloat(
                    initialValue = 0f,
                    targetValue = 360f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(1000, easing = LinearEasing),
                        repeatMode = RepeatMode.Restart
                    )
                )


                Icon(
                    Icons.Rounded.Refresh,
                    contentDescription = "loading animation",
                    modifier = Modifier.rotate(rotation)
                )

            }
            Button(onClick = {
                loadingAnimation = true;
                getHtml(url = inputURL, onHtmlReceived = {
                    outputText = it;
                    loadingAnimation = false
                })
            }) {
                Text("Scrape Web")
            }
        }

    }


}

fun getHtml(url: String, onHtmlReceived: (String) -> Unit) {

    GlobalScope.launch(Dispatchers.IO) {
        URL(url).openConnection().inputStream.let {
            BufferedReader(InputStreamReader(it))
        }.use {
            onHtmlReceived.invoke(it.readLines().joinToString("\n"));
        }

    }
}


fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        App()
    }
}
