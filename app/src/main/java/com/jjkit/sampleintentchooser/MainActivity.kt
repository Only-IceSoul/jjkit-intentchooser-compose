package com.jjkit.sampleintentchooser

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.jjkit.sampleintentchooser.ui.theme.SampleintentchooserTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SampleintentchooserTheme {

                val show = remember {
                    mutableStateOf(false)
                }
                val interactionEnabled = remember {
                    mutableStateOf(true)
                }

                val showAnim = remember {
                    mutableStateOf(false)
                }
                val imageBitmap: MutableState<ImageBitmap?> = remember {
                    mutableStateOf(null)
                }
                val ctx = LocalContext.current
                val density = LocalDensity.current
                val bottom = with(density){ WindowInsets.systemBars.getBottom(density).toDp() }

//                Box {
//
//
//                    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
//                        Column(modifier = Modifier
//                            .padding(innerPadding)
//                            .fillMaxSize(),
//                            horizontalAlignment = Alignment.CenterHorizontally,
//                            verticalArrangement = Arrangement.Center) {
//                            if(imageBitmap.value != null) Image(bitmap = imageBitmap.value!!, contentDescription ="image" )
//                            Button(onClick = {
//                                show.value = true
//
//                            }) {
//                                Text(text = "Press me")
//                            }
//                        }
//                    }
//
//                    if(show.value){
//                       Box (modifier = Modifier
//                           .fillMaxSize()
//                           .background(Color.Black.copy(alpha = 0.3f))
//                           .clickable(
//                               enabled = true,
//                               onClickLabel = "onClickLabel",
//                               onClick = {
//                                   if (interactionEnabled.value) {
//                                       showAnim.value = false
//                                       interactionEnabled.value = false
//                                   }
//                               },
//                               role = null,
//                               indication = null,
//                               interactionSource = remember { MutableInteractionSource() }
//                           )
//                       ){
//
//
//                          Box(modifier = Modifier.align(Alignment.BottomCenter)) {
//
//                              LaunchedEffect(Unit) {
//                                  showAnim.value = true
//                              }
//
//                              AnimatedVisibility(
//                                  visible = showAnim.value,
//                                  enter = slideInVertically {
//                                      // Slide in from
//                                      with(density) { 200.dp.roundToPx() }
//                                  } + expandVertically(
//                                      animationSpec =
//                                          spring(
//                                              stiffness = 800f,
//                                          ),
//                                      expandFrom = Alignment.Bottom
//                                  ) + fadeIn(
//                                      // Fade in with the initial alpha of 0.3f.
//                                      initialAlpha = 0.3f
//                                  ),
//                                  exit = slideOutVertically{
//                                      with(density) { 200.dp.roundToPx() }
//                                  }  + fadeOut()
//                              ) {
//
//                                  DisposableEffect(Unit){
//                                      onDispose {
//                                          //reset
//                                       show.value = false
//                                       interactionEnabled.value = true
//                                      }
//                                  }
//
//
//                                  IntentChooserImage(
//                                      enabled = interactionEnabled.value,
//                                      authorityFilerProvider = "${ctx.packageName}.fileprovider",
//                                      top = {
//                                            Row(modifier = Modifier.fillMaxWidth()
//                                                .padding(15.dp),
//                                                horizontalArrangement = Arrangement.Center) {
//                                                Text(text = "PICK A IMAGE")
//                                            }
//                                      },
//                                      bottom = {
//                                          Spacer(modifier = Modifier
//                                              .height(bottom)
//                                              .fillMaxWidth())
//                                      }
//                                  ) {
//                                      //if null show error
//                                      imageBitmap.value =
//                                          IntentChooser.Helper.getBitmapSampling(ctx, it, 500, 500)
//                                              ?.asImageBitmap()
//                                      showAnim.value = false
//                                  }
//
//                              }
//                          }
//                       }
//                    }
//
//                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SampleintentchooserTheme {
        Greeting("Android")
    }
}