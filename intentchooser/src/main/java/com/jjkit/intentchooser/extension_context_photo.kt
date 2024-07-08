//package com.jjkit.intentchooser
//
//import android.app.Activity
//import android.content.ComponentName
//import android.content.Context
//import android.content.ContextWrapper
//import android.content.Intent
//import android.content.IntentSender
//import android.graphics.Bitmap
//import android.graphics.BitmapFactory
//import android.net.Uri
//import android.os.Build
//import android.os.Parcelable
//import android.provider.MediaStore
//import android.util.Log
//import androidx.core.content.FileProvider
//import java.io.BufferedInputStream
//import java.io.File
//import java.util.ArrayList
//
//
//fun IntentChooser.Helper.findActivity(ctx:Context): Activity? =
//    when (ctx) {
//        is Activity -> ctx
//        is ContextWrapper -> IntentChooser.Helper.findActivity(ctx.baseContext)
//        else -> null
//    }
//
////intent for start activity google standard intent chooser
////use with IntentChooser.GetIntent launcher
//fun IntentChooser.Helper.getIntentImageChooser(ctx:Context, title: CharSequence? = null,
//                                               imageName:String, imageExt:String,
//                                               authorityProvider: String? = null,
//                                               includeDocuments: Boolean = true,
//                                               includeCamera: Boolean = true, sender: IntentSender? = null): Intent {
//    val allIntents = ArrayList<Intent>()
//
//    // collect all camera intents if Camera permission is available
//    if (includeCamera) {
//        allIntents.addAll(if(!authorityProvider.isNullOrEmpty())getCameraIntents(ctx,imageName,imageExt,authorityProvider) else getCameraIntents(ctx,imageName,imageExt))
//    }
//
//    var galleryIntents = makePhotoIntents(ctx,Intent.ACTION_GET_CONTENT, includeDocuments)
//    if (galleryIntents.isEmpty()) {
//        // if no intents found for get-content try pick intent action (Huawei P9).
//        galleryIntents = makePhotoIntents(ctx,Intent.ACTION_PICK, includeDocuments)
//    }
//    allIntents.addAll(galleryIntents)
//
//    val target: Intent
//    if (allIntents.isEmpty()) {
//        target = Intent()
//    } else {
//        target = allIntents[allIntents.size - 1]
//        allIntents.removeAt(allIntents.size - 1)
//    }
//    var chooserIntent = Intent.createChooser(target, title)
//    // Create a chooser from the main  intent
//    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && sender != null){
//        chooserIntent  = Intent.createChooser(target, title,sender)
//    }
//
//
//    // Add all other intents
//    chooserIntent.putExtra(
//        Intent.EXTRA_INITIAL_INTENTS, allIntents.toTypedArray<Parcelable>())
//
//    return chooserIntent
//}
//
////file provider  >= api 24
//fun IntentChooser.Helper.getCameraIntents(ctx:Context, imageName:String, imageExt:String, authorityProvider: String
//): List<Intent> {
//
//    val allIntents = ArrayList<Intent>()
//    // Determine Uri of camera image to  save.
//    val outputFileUri = makeExternalCacheUri(ctx,imageName,imageExt,authorityProvider)
//
//    val captureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//    val listCam = ctx.packageManager.queryIntentActivities(captureIntent, 0)
//    for (res in listCam) {
//        val intent = Intent(captureIntent)
//        intent.component = ComponentName(res.activityInfo.packageName, res.activityInfo.name)
//        intent.setPackage(res.activityInfo.packageName)
//        if (outputFileUri != null) {
//            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri)
//        }
//        allIntents.add(intent)
//    }
//
//    return allIntents
//}
////api < 24
//fun IntentChooser.Helper.getCameraIntents(ctx: Context, imageName:String, imageExt:String
//): List<Intent> {
//
//    val allIntents = ArrayList<Intent>()
//    // Determine Uri of camera image to  save.
//    val outputFileUri = makeExternalCacheUri(ctx,imageName,imageExt)
//
//    val captureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//    val listCam = ctx.packageManager.queryIntentActivities(captureIntent, 0)
//    for (res in listCam) {
//        val intent = Intent(captureIntent)
//        intent.component = ComponentName(res.activityInfo.packageName, res.activityInfo.name)
//        intent.setPackage(res.activityInfo.packageName)
//        if (outputFileUri != null) {
//            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri)
//        }
//        allIntents.add(intent)
//    }
//
//    return allIntents
//}
//
//fun IntentChooser.Helper.getImageUriFromResult(ctx:Context, imageName:String, imageExt:String, data: Intent?): Uri? {
//    var isCamera = true
//    if (data != null && data.data != null) {
//        val action = data.action
//        isCamera = action != null && action == MediaStore.ACTION_IMAGE_CAPTURE
//    }
//    return if (isCamera || data?.data == null) makeExternalCacheUri(ctx,imageName,imageExt) else data.data
//}
//fun IntentChooser.Helper.getImageUriFromResult(ctx:Context, imageName:String, imageExt:String, data: Intent?, authorityProvider: String): Uri? {
//    var isCamera = true
//    if (data != null && data.data != null) {
//        val action = data.action
//        isCamera = action != null && action == MediaStore.ACTION_IMAGE_CAPTURE
//    }
//    return if (isCamera || data?.data == null) makeExternalCacheUri(ctx,imageName,imageExt,authorityProvider) else data.data
//}
//
//
//fun IntentChooser.Helper.getCameraIntentForQuery(ctx:Context, imageName:String, imageExt:String, authorityProvider: String): Intent {
//    val i = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//    val  outputFileUri = makeExternalCacheUri(ctx,imageName,imageExt,authorityProvider)
//    if (outputFileUri != null) {
//        i.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri)
//    }
//    return  i
//}
//
//fun IntentChooser.Helper.getCameraIntentForQuery(ctx:Context, imageName:String, imageExt:String): Intent {
//    val i = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//    val  outputFileUri = makeExternalCacheUri(ctx,imageName,imageExt)
//    if (outputFileUri != null) {
//        i.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri)
//    }
//    return  i
//}
//
//
//
//
//
////MAKE::::
//
//fun IntentChooser.Helper.makePhotoIntents(ctx:Context,action: String = Intent.ACTION_GET_CONTENT, includeDocuments: Boolean = true): List<Intent> {
//    val intents = ArrayList<Intent>()
//    val galleryIntent = if (action === Intent.ACTION_GET_CONTENT)
//        Intent(action)
//    else
//    // if no intents found for get-content try intent action pick (Huawei P9)  ACTION_PICK.
//        Intent(action, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
//
//    galleryIntent.addCategory(Intent.CATEGORY_OPENABLE)
//    galleryIntent.type = "image/*"
//    val listGallery = ctx.packageManager.queryIntentActivities(galleryIntent, 0)
//    for (res in listGallery) {
//        val intent = Intent(galleryIntent)
//        intent.component = ComponentName(res.activityInfo.packageName, res.activityInfo.name)
//        intent.setPackage(res.activityInfo.packageName)
//        intents.add(intent)
//    }
//
//    // remove documents intent
//    if (!includeDocuments) {
//        for (intent in intents) {
//            if (intent
//                    .component!!
//                    .className == "com.android.documentsui.DocumentsActivity") {
//                intents.remove(intent)
//                break
//            }
//        }
//    }
//    return intents
//}
//
//fun IntentChooser.Helper.makePhotoIntentForQuery(action:String = Intent.ACTION_GET_CONTENT ): Intent {
//    require(action ==  Intent.ACTION_GET_CONTENT || action == Intent.ACTION_PICK ){ " Action not valid (ACTION_GET_CONTENT - ACTION_PICK) "}
//    val i =  if (action === Intent.ACTION_GET_CONTENT)
//        Intent(action)
//    else
//    // if no intents found for get-content try pick intent action (Huawei P9)  ACTION_PICK.
//        Intent(action, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
//    i.addCategory(Intent.CATEGORY_OPENABLE)
//    i.type = "image/*"
//    return  i
//}
//
////component defined just query that component (queryIntentActivities)
//fun IntentChooser.Helper.makeGalleryIntent(ctx:Context) : Intent{
//    var i = makePhotoIntentForQuery()
//    var g = ctx.packageManager.queryIntentActivities(i, 0)
//    if(g.isEmpty()) {
//        //some devices not working with GET_CONTENT
//        i = makePhotoIntentForQuery(Intent.ACTION_PICK)
//        g = ctx.packageManager.queryIntentActivities(i, 0)
//    }
//    g.forEach {
//        if (it.activityInfo.packageName.contains("gallery")) {
//            i.component = ComponentName(it.activityInfo.packageName, it.activityInfo.name)
//        }
//    }
//    return i
//}
//
////api < 24
//fun IntentChooser.Helper.makeExternalCacheUri(ctx:Context, imageName:String, imageExt:String): Uri? {
//    return if(imageName.isNotEmpty() && imageExt.isNotEmpty()) Uri.fromFile(File(ctx.externalCacheDir, "$imageName.$imageExt"))
//    else throw Exception("makeExternalCacheUri: empty string")
//}
////api >= 24
//fun IntentChooser.Helper.makeExternalCacheUri(ctx:Context, imageName:String, imageExt:String, authorityProvider: String): Uri? {
//    return if(authorityProvider.isNotEmpty() && imageName.isNotEmpty() && imageExt.isNotEmpty()) FileProvider.getUriForFile(ctx,authorityProvider,
//        File(ctx.externalCacheDir, "$imageName.$imageExt")
//    ) else throw Exception("makeExternalCacheUri: empty string")
//}
//
//fun IntentChooser.Helper.makeCameraIntentForQuery(outputFileUri: Uri): Intent {
//    val i = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//    i.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri)
//    return  i
//}
//
//
//
//
//fun IntentChooser.Helper.getBitmap(ctx:Context,path: Uri): Bitmap? {
//    return  try {
//        val inputStream = BufferedInputStream(ctx.contentResolver.openInputStream(path)!!)
//        BitmapFactory.decodeStream(inputStream)
//    } catch (e: Exception) {
//        Log.e("JJKit-IntentChooser","ERROR: $e")
//        null
//    }
//}
//
//fun IntentChooser.Helper.getBitmapSampling(ctx:Context,uri: Uri?, reqWidth: Int, reqHeight:Int) : Bitmap? {
//    return  try {
//        val options = BitmapFactory.Options()
//        options.inJustDecodeBounds = true
//        var inputStream = BufferedInputStream(ctx.contentResolver.openInputStream(uri!!)!!)
//        BitmapFactory.decodeStream(inputStream,null,options)
//        options.inSampleSize = calculateInSampleSize(options,reqWidth,reqHeight)
//        options.inJustDecodeBounds = false
//        inputStream = BufferedInputStream(ctx.contentResolver.openInputStream(uri)!!)
//        BitmapFactory.decodeStream(inputStream,null,options)
//    } catch (e: Exception) {
//        Log.e("JJKit-IntentChooser","getBitmapFromUriSampling ERROR: $e")
//        null
//    }
//
//}
//
//fun IntentChooser.Helper.calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
//    // Raw height and width of image
//    val (height: Int, width: Int) = options.run { outHeight to outWidth }
//    var inSampleSize = 1
//
//    if (height > reqHeight || width > reqWidth) {
//
//        val halfHeight: Int = height / 2
//        val halfWidth: Int = width / 2
//
//        // Calculate the largest inSampleSize value that is a power of 2 and keeps both
//        // height and width larger than the requested height and width.
//        while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
//            inSampleSize *= 2
//        }
//    }
//    return inSampleSize
//}
