# IntentChooser Compose

Simple intent chooser

API SDK >= 24 

<image src="./res/demo.gif" width="300" >



## INSTALLATION

1.-Add it in your settings.gradle.kts at the end of repositories:
```
dependencyResolutionManagement {
		repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
		repositories {
			mavenCentral() //this
			maven { url 'https://jitpack.io' } or maven("https://jitpack.io")  //this
		}
	}
```
2.- 

gradle-wrapper.properties  8.6 to 8.7 

```
distributionBase=GRADLE_USER_HOME
distributionPath=wrapper/dists
distributionUrl=https\://services.gradle.org/distributions/gradle-8.7-bin.zip
zipStoreBase=GRADLE_USER_HOME
zipStorePath=wrapper/dists

```

2B.-libs.versions.toml

```
intentChooser = "1.3"

jjkit-intentchooser = { module = "com.github.Only-IceSoul:jjkit-intentchooser-compose", version.ref = "intentChooser" }

```
3.-build gradle APP

```
dependencies {


    ///
    implementation(libs.jjkit.intentchooser)
}

```


## USAGE


 [Example With Animation](./app\src\main\java\com\jjkit\sampleintentchooser/MainActivity.kt)

Example: a IntentChooser for image

```kotlin

@Composable
fun IntentChooserImage(enabled:Boolean,authorityFilerProvider:String,
                       imageName:String = "a0ImageResult",imageExt:String= "jpeg",
                       ignoreActivityInfoNames: List<String> = listOf(".document","docs"),
                       contentLayout: IntentChooser.Layout = IntentChooser.defaultLayout,
                       contentTheme: IntentChooser.Theme = IntentChooser.defaultTheme,
                       textStyle:TextStyle = LocalTextStyle.current.copy(fontSize = 13.sp),
                       top: @Composable ()->Unit = {}, bottom:@Composable ()->Unit = {},
                       onImageResult:(uri: Uri?)->Unit) {

    val ctx = LocalContext.current
    val permissionCamera = IntentChooser.rememberRequestPermission(permission = Manifest.permission.CAMERA)
    val permissionReadWrite =  IntentChooser.rememberRequestMultiplePermissions(
        permissions = arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
    )

    val launcher = rememberLauncherForActivityResult(IntentChooser.GetActivityResult()) {
        if(it.resultCode == Activity.RESULT_OK){
            val uri = IntentChooser.Helper.getImageUriFromResult(ctx,imageName,imageExt,it.intent,authorityFilerProvider)
            onImageResult(uri)
        }else{
            onImageResult(null)
        }
    }

    val data : IntentChooser.Data = remember {+
        //SET YOUR INTENTS
        IntentChooser.Builder(ctx).setIntent(IntentChooser.Helper.getCameraIntentForQuery(ctx,imageName,imageExt,authorityFilerProvider))
            .setSecondariesIntent(listOf(IntentChooser.Helper.makePhotoIntentForQuery()
            )).setIgnore(ignoreActivityInfoNames)
            .build()
    }

    IntentChooser(
        data, contentLayout,contentTheme, textStyle = textStyle,onClick = {

        if (it?.resolveInfo == null || !enabled) {
            return@IntentChooser
        }

        val ri = it.resolveInfo
        if (ri.activityInfo.name.contains("camera", true)) {
            if (permissionCamera.state.value == IntentChooser.PermissionStatus.GRANTED) {
                launcher.launch(it)
            } else {
                permissionCamera.launch()
            }
        } else if (ri.activityInfo.packageName.contains("document", true)) {

            if (permissionReadWrite.state[permissionReadWrite.permissions[0]] == IntentChooser.PermissionStatus.GRANTED) {
                launcher.launch(it)
            } else {
                permissionReadWrite.launch()
            }
        } else {
            launcher.launch(it)
            //start activity
        }

    }, top = top, bottom = bottom)

}

   
```



## LICENSE 

**Apache 2.0**

