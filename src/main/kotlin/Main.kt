import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.useResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.platform.Font
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.darkrockstudios.libraries.mpfilepicker.DirectoryPicker
import com.darkrockstudios.libraries.mpfilepicker.FilePicker
import com.darkrockstudios.libraries.mpfilepicker.JvmFile
import com.google.gson.JsonParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import java.awt.Dimension
import java.io.File
import java.io.FileOutputStream
import java.nio.file.Paths
import java.util.Scanner

val harmonyOsSans = FontFamily(
            Font(resource = "font/HarmonyOS_Sans_Black.ttf", weight = FontWeight.Black),
            Font(resource = "font/HarmonyOS_Sans_Black_Italic.ttf", weight = FontWeight.Black, style = FontStyle.Italic),
            Font(resource = "font/HarmonyOS_Sans_Bold.ttf", weight = FontWeight.Bold),
            Font(resource = "font/HarmonyOS_Sans_Bold_Italic.ttf", weight = FontWeight.Bold, style = FontStyle.Italic),
            Font(resource = "font/HarmonyOS_Sans_Light.ttf", weight = FontWeight.Light),
            Font(resource = "font/HarmonyOS_Sans_Light_Italic.ttf", weight = FontWeight.Light, style = FontStyle.Italic),
            Font(resource = "font/HarmonyOS_Sans_Medium.ttf", weight = FontWeight.Medium),
            Font(resource = "font/HarmonyOS_Sans_Medium_Italic.ttf", weight = FontWeight.Medium, style = FontStyle.Italic),
            Font(resource = "font/HarmonyOS_Sans_Regular.ttf", weight = FontWeight.Normal),
            Font(resource = "font/HarmonyOS_Sans_Regular_Italic.ttf", weight = FontWeight.Normal, style = FontStyle.Italic),
            Font(resource = "font/HarmonyOS_Sans_Thin.ttf", weight = FontWeight.Thin),
            Font(resource = "font/HarmonyOS_Sans_Thin_Italic.ttf", weight = FontWeight.Thin, style = FontStyle.Italic),
)

val LOGGER = LoggerFactory.getLogger("ImageDivider")

@Composable
@Preview
fun App() {
    LOGGER.info("Starting Image Divider!")

    LOGGER.info("Starting copying image-divider tool executable file into temp folder")
    val imageDividerExecutor = File.createTempFile("image-divider", ".exe")
    imageDividerExecutor.deleteOnExit()
    LOGGER.info("Created temp file: $imageDividerExecutor")
    useResource("image-divider.exe") {
        LOGGER.info("Reading image-divider tool in UI distribution resources")
        val outputStream = FileOutputStream(imageDividerExecutor)
        val buffer = ByteArray(1024)
        var length = it.read(buffer)
        while (length != -1) {
            outputStream.write(buffer, 0, length)
            length = it.read(buffer)
        }
        it.close()
        outputStream.close()
        LOGGER.info("Writing image-divider tool to temp file successfully!")
    }

    LOGGER.info("Starting create UI")
    MaterialTheme {
        var showInputPicker by remember { mutableStateOf(false) }
        var showOutputPicker by remember { mutableStateOf(false) }

        var imageFileAbsolute by remember {
            mutableStateOf("")
        }
        var imageFile by remember {
            mutableStateOf("")
        }
        var outputDirectory by remember {
            mutableStateOf("")
        }
        var clips by remember {
            mutableStateOf(1)
        }
        var prefix by remember {
            mutableStateOf("")
        }
        var horizontally by remember {
            mutableStateOf(false)
        }
        var pngFormat by remember {
            mutableStateOf(true)
        }

        val fileType = listOf("jpg", "jpeg", "png")

        FilePicker(show = showInputPicker, fileExtensions = fileType) { file ->
            LOGGER.info("Image input picker picked a file")
            showInputPicker = false
            if (file != null && file is JvmFile) {
                LOGGER.info("Image input picker picked a jvm file")
                imageFileAbsolute = file.platformFile.absolutePath
                imageFile = file.platformFile.nameWithoutExtension
            }
        }

        DirectoryPicker(show = showOutputPicker) { file ->
            LOGGER.info("Output directory picker picked a directory")
            showOutputPicker = false
            if (file != null) {
                outputDirectory = file
            }
        }

        var showDialog by remember {
            mutableStateOf(false)
        }

        var dialogContent by remember {
            mutableStateOf("")
        }

        if (showDialog) {
            LOGGER.info("Starting to show the dialog")
            AlertDialog(
                onDismissRequest = {
                    showDialog = false
                    LOGGER.info("Dismissing the dialog")
                },
                title = {
                        Text("Image Divider")
                },
                text = {
                       Text(dialogContent)
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showDialog = false
                            LOGGER.info("Closing the dialog")
                        },
                        shape = RoundedCornerShape(5.dp)
                    ) {
                        Text(
                            text = "Close",
                            fontSize = 1.em,
                            fontFamily = harmonyOsSans,
                            fontWeight = FontWeight.Normal,
                        )
                    }
                },
                properties = DialogProperties(
                    dismissOnClickOutside = false
                )
            )
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier.width(IntrinsicSize.Max),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Image Divider",
                    fontSize = 4.em,
                    fontFamily = harmonyOsSans,
                    fontWeight = FontWeight.Black
                )
                Spacer(modifier = Modifier.height(20.dp))
                Row {
                    Column {
                        Text(
                            text = "Input Image",
                            fontSize = 1.em,
                            fontFamily = harmonyOsSans,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.align(Alignment.Start)
                        )
                        Row(
                            modifier = Modifier.height(IntrinsicSize.Max)
                        ) {
                            TextField(
                                value = imageFileAbsolute,
                                onValueChange = {
                                    imageFileAbsolute = it
                                },
                                shape = RoundedCornerShape(5.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Button(
                                onClick = {
                                    LOGGER.info("Starting picking the image input")
                                    showInputPicker = true
                                },
                                shape = RoundedCornerShape(5.dp),
                                modifier = Modifier.fillMaxHeight()
                            ) {
                                Text(
                                    text = "Select",
                                    fontSize = 1.2.em,
                                    fontFamily = harmonyOsSans,
                                    fontWeight = FontWeight.Normal,
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.width(20.dp))
                    Column {
                        Text(
                            text = "Output Directory",
                            fontSize = 1.em,
                            fontFamily = harmonyOsSans,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.align(Alignment.Start)
                        )
                        Row(
                            modifier = Modifier.height(IntrinsicSize.Max)
                        ) {
                            TextField(
                                value = outputDirectory,
                                onValueChange = {
                                    outputDirectory = it
                                },
                                shape = RoundedCornerShape(5.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Button(
                                onClick = {
                                    LOGGER.info("Starting picking the output directory")
                                    showOutputPicker = true
                                },
                                shape = RoundedCornerShape(5.dp),
                                modifier = Modifier.fillMaxHeight()
                            ) {
                                Text(
                                    text = "Select",
                                    fontSize = 1.2.em,
                                    fontFamily = harmonyOsSans,
                                    fontWeight = FontWeight.Normal,
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.height(IntrinsicSize.Max).fillMaxWidth()
                ) {
                    Column {
                        Text(
                            text = "Prefix",
                            fontSize = 1.em,
                            fontFamily = harmonyOsSans,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.align(Alignment.Start)
                        )
                        TextField(
                            value = prefix,
                            onValueChange = {
                                prefix = it
                            }
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Column {
                        Text(
                            text = "Clips",
                            fontSize = 1.em,
                            fontFamily = harmonyOsSans,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextButton(onClick = {
                                if (clips > 1)
                                    clips --
                            },
                                modifier = Modifier.fillMaxHeight().background(Color.Transparent)) {
                                Text(
                                    text = "-",
                                    fontSize = 2.em,
                                    fontFamily = harmonyOsSans,
                                    fontWeight = FontWeight.Black,
                                )
                            }
                            Spacer(modifier = Modifier.width(1.dp))
                            Text(
                                text = "$clips",
                                fontSize = 1.2.em,
                                fontFamily = harmonyOsSans,
                                fontWeight = FontWeight.Normal
                            )
                            Spacer(modifier = Modifier.width(1.dp))
                            TextButton(onClick = {
                                clips ++
                            },
                                modifier = Modifier.fillMaxHeight()) {
                                Text(
                                    text = "+",
                                    fontSize = 2.em,
                                    fontFamily = harmonyOsSans,
                                    fontWeight = FontWeight.Black,
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.width(IntrinsicSize.Max)
                        ) {
                            Text(
                                text = "Png",
                                fontSize = 1.em,
                                fontFamily = harmonyOsSans,
                                fontWeight = FontWeight.Bold
                            )
                            RadioButton(
                                selected = pngFormat,
                                onClick = {
                                    pngFormat = true
                                },
                                modifier = Modifier.fillMaxWidth()
                                    .fillMaxHeight()
                            )
                        }
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.width(IntrinsicSize.Max)
                        ) {
                            Text(
                                text = "Jpg",
                                fontSize = 1.em,
                                fontFamily = harmonyOsSans,
                                fontWeight = FontWeight.Bold
                            )
                            RadioButton(
                                selected = !pngFormat,
                                onClick = {
                                    pngFormat = false
                                },
                                modifier = Modifier.fillMaxWidth()
                                    .fillMaxHeight()
                            )
                        }
                        Spacer(modifier = Modifier.width(5.dp))
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Horizontally",
                            fontSize = 1.em,
                            fontFamily = harmonyOsSans,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.align(Alignment.Start)
                        )
                        Checkbox(
                            checked = horizontally,
                            onCheckedChange = {
                                horizontally = it
                            }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(30.dp))

                val scope = rememberCoroutineScope()

                Button(
                    onClick = {
                        LOGGER.info("Preparing dividing")
                        if (imageFileAbsolute == "") {
                            LOGGER.error("Please select an image file!")
                            dialogContent = "Please select an image file!"
                            showDialog = true
                            return@Button
                        }
                        try {
                            val absoluteImage = File(imageFileAbsolute)
                            if (!absoluteImage.exists()) {
                                LOGGER.error("The selected image file does not exist!")
                                dialogContent = "The selected image file does not exist!"
                                showDialog = true
                                return@Button
                            }
                            if (!(absoluteImage.name.endsWith(".jpg") || absoluteImage.name.endsWith(".jpeg") || absoluteImage.name.endsWith(".png"))) {
                                LOGGER.error("The selected file is not an image file!")
                                dialogContent = "The selected file is not an image file!"
                                showDialog = true
                                return@Button
                            }
                            if (imageFile == "") {
                                imageFile = absoluteImage.nameWithoutExtension
                            }
                            if (outputDirectory == "") {
                                outputDirectory = Paths.get("").toAbsolutePath().toString()
                            }
                            if (!File(outputDirectory).exists()) {
                                File(outputDirectory).mkdirs()
                            }
                        } catch (e: Exception) {
                            LOGGER.error("Error occurs!")
                            dialogContent = e.message ?: "Error occurs!"
                            showDialog = true
                            return@Button
                        }
                        LOGGER.info("Trying to get runtime")
                        val runtime = Runtime.getRuntime()
                        LOGGER.info("Trying to build image-divider command")
                        val commandBuilder = StringBuilder(imageDividerExecutor.absolutePath)
                        commandBuilder.append(" ")
                            .append("-i")
                            .append(" ")
                            .append("\"")
                            .append(imageFileAbsolute)
                            .append("\"")
                        if (outputDirectory != "")
                            commandBuilder.append(" ")
                                .append("-o")
                                .append(" ")
                                .append("\"")
                                .append(outputDirectory)
                                .append("\"")
                        commandBuilder.append(" ")
                            .append("-p")
                            .append(" ")
                        if (prefix != "")
                                commandBuilder.append(prefix)
                        else
                            commandBuilder.append(imageFile)
                        commandBuilder.append(" ")
                            .append("-clips")
                            .append(" ")
                            .append(clips)
                        if (horizontally)
                            commandBuilder.append(" ")
                                .append("-h")
                        commandBuilder.append(" ")
                            .append("-f")
                            .append(" ")
                        if (pngFormat)
                            commandBuilder.append("png")
                        else
                            commandBuilder.append("jpg")
                        try {
                            LOGGER.info("Trying to execute the command")
                            val process = runtime.exec(commandBuilder.toString())
                            LOGGER.info("Starting a new task to observe the output from the execution")
                            scope.launch(Dispatchers.IO) {
                                val reader = Scanner(process.errorStream)
                                val result = mutableListOf<String>()
                                while (true) {
                                    if (!reader.hasNextLine()) {
                                        continue
                                    }
                                    result.add(reader.nextLine())
                                    if (reader.hasNextLine())
                                        continue
                                    break
                                }
                                showDialog = true
                                val outputString = result.joinToString("\n")
                                LOGGER.info("Returning: $outputString")
                                dialogContent = if (outputString.startsWith("{") && outputString.endsWith("}")) {
                                    JsonParser.parseString(outputString).asJsonObject["msg"].asString
                                } else {
                                    outputString
                                }
                            }
                        } catch (e: Exception) {
                            LOGGER.error("Error occurs!")
                            dialogContent = e.message ?: "Error occurs!"
                            showDialog = true
                            return@Button
                        }
                    },
                    shape = RoundedCornerShape(5.dp)
                ) {
                    Text(
                        text = "Start!",
                        fontSize = 1.5.em,
                        fontFamily = harmonyOsSans,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

fun main() {
    try {
        application {
            Window(
                onCloseRequest = ::exitApplication,
                icon = painterResource("icon/image-divider-icon.ico")
            ) {
                window.minimumSize = Dimension(950, 700)
                window.title = "Image Divider"
                App()
            }
        }
    } catch (e: Exception) {
        LOGGER.error("Error occurs", e)
        e.printStackTrace()
    }
}