package com.jjkit.intentchooser

import android.Manifest
import android.app.Activity
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp

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

    val data : IntentChooser.Data = remember {
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

//        if (ri.activityInfo.name.contains("camera", true)) {
//            if (PermissionHelper.checkCamera(ctx)) {
//                launcher.launch(it)
//            } else {
//                permissionCamera.launch()
//            }
//        }
    }, top = top, bottom = bottom)

}