import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.desktop.ui.tooling.preview.Preview
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import kotlinx.coroutines.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL

@Composable
fun App() {

    var inputURL by remember { mutableStateOf("") }
    var htmlResult by remember { mutableStateOf("") }
    var loadingAnimationVisibility by remember { mutableStateOf(false) }

    MaterialTheme {

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceAround,
            horizontalAlignment = Alignment.CenterHorizontally

        ) {

            SearchTextField(value = inputURL, onValueChanged = { inputURL = it })

            HtmlOutputText(text = htmlResult)

            LoadingAnimation(visible = loadingAnimationVisibility)

            SearchButton(onSearch = {
                loadingAnimationVisibility = true;
                getHtml(url = inputURL, onHtmlReceived = {
                    htmlResult = it;
                    loadingAnimationVisibility = false
                })

            })

        }

    }
}

@Composable
fun SearchButton(onSearch: () -> Unit) {
    Button(onClick = {
        onSearch.invoke();
    }) {
        Text("Scrape Web")
    }
}

@Composable
fun HtmlOutputText(text: String) {
    Surface(
        elevation = 20.dp
    ) {
        Text(
            text, modifier = Modifier
                .size(400.dp)
                .verticalScroll(
                    rememberScrollState()
                )
        );

    }
}

@Composable
fun SearchTextField(value: String, onValueChanged: (String) -> Unit) {
    OutlinedTextField(
        value,
        onValueChanged,
        label = {
            Icon(Icons.Rounded.Search, contentDescription = "search field")
        },
        placeholder = {
            Text("http....")
        },
    )
}


@Composable
fun LoadingAnimation(visible: Boolean = false) {

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        val rotation by rememberInfiniteTransition().animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(1000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            )
        )

        LoadingAnimationIconRotation(rotation)
    }
}

@Composable
fun LoadingAnimationIconRotation(rotation: Float) {

    Icon(
        Icons.Rounded.Refresh,
        contentDescription = "loading animation",
        modifier = Modifier.rotate(rotation)
    )

}


@OptIn(DelicateCoroutinesApi::class)
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

    val icon = painterResource(resourcePath = "icon/scrubify_icon.png");

    Window(
        icon = icon,
        title = "Scrubify",
        state = WindowState(size = DpSize(800.dp, 700.dp)),
        onCloseRequest = ::exitApplication
    ) {
        App()
    }

}
