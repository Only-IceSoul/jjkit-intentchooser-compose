package com.jjkit.intentchooser

import android.Manifest
import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.ceil


object IntentChooser {

    object Helper {
        fun checkCamera(context: Context): Boolean{
            return ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        }

        fun checkReadWrite(context: Context):Boolean{
            return ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    &&  ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        }

        fun requestCamera(context: Activity, requestCode:Int){
            ActivityCompat.requestPermissions(context, arrayOf(Manifest.permission.CAMERA), requestCode)
        }

        fun requestReadWrite(context: Activity, requestCode:Int){
            ActivityCompat.requestPermissions(context, arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE), requestCode)
        }

        fun shouldShowRequestPermissionRationale(activity: Activity, permission:String): Boolean{
            return activity.shouldShowRequestPermissionRationale(permission)
        }

        fun checkPermission(ctx: Context,permission: String):Boolean{
            return ContextCompat.checkSelfPermission(ctx, permission) == PackageManager.PERMISSION_GRANTED
        }

        fun goSettingsApp(activity: Context){
            val packageUri = Uri.fromParts("package", activity.packageName, null)
            val applicationDetailsSettingsIntent = Intent()
            applicationDetailsSettingsIntent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            applicationDetailsSettingsIntent.data = packageUri
            applicationDetailsSettingsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            activity.startActivity(applicationDetailsSettingsIntent)
        }


    }

    data class ActivityResult(
        val resultCode: Int,
        val intent: Intent?
    )

    data class IntentInfo(
        val resolveInfo: ResolveInfo?,
        val intent: Intent
    )

    data class Data(
        val list:List<ResolveInfo>,
        val map: MutableMap<String, Intent>
    )

    enum class PermissionStatus{
        NONE, //init value
        ERROR, //activity is null
        GRANTED, DENY,NEVER_ASK_ME
    }

    class RequestPermission(val permission:String,private val launcher: ManagedActivityResultLauncher<String, Boolean> , val state:MutableState<PermissionStatus>){

        fun launch(options: ActivityOptionsCompat?) {
            launcher.launch(permission, options)
        }
        fun launch() {
            launcher.launch(permission)
        }
    }

    class RequestMultiplePermissionsStates(val permissions:Array<String>,private val launcher: ManagedActivityResultLauncher<Array<String>,  Map<String, @JvmSuppressWildcards Boolean>> , val states:Map<String,MutableState<PermissionStatus>>){

        fun launch(options: ActivityOptionsCompat?) {
            launcher.launch(permissions, options)
        }
        fun launch() {
            launcher.launch(permissions)
        }
    }
    class RequestMultiplePermissions(val permissions:Array<String>,private val launcher: ManagedActivityResultLauncher<Array<String>,  Map<String, @JvmSuppressWildcards Boolean>> , val state: SnapshotStateMap<String, PermissionStatus>){

        fun launch(options: ActivityOptionsCompat?) {
            launcher.launch(permissions, options)
        }
        fun launch() {
            launcher.launch(permissions)
        }
    }

    fun getPermissionStatus(ctx:Context,permission:String): PermissionStatus {
        Helper.findActivity(ctx)?.let {act->
            if (Helper.shouldShowRequestPermissionRationale(act, permission)) {
                return PermissionStatus.DENY
            } else {
                if (Helper.checkPermission(act, permission)) {
                    return PermissionStatus.GRANTED
                } else {
                    return PermissionStatus.NEVER_ASK_ME
                }
            }
        }
        return PermissionStatus.ERROR
    }

    @Composable
    fun rememberRequestPermission(permission:String) : RequestPermission {

        val ctx = LocalContext.current
        val state = remember {
            mutableStateOf(if(Helper.checkPermission(
                    ctx,
                    permission
                )
            ) PermissionStatus.GRANTED else PermissionStatus.NONE
            )
        }
        val requestPermissionLauncher =  rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()){
            state.value = getPermissionStatus(ctx,permission)
        }

        return remember(permission) {
            RequestPermission(permission, requestPermissionLauncher ,state)
        }

    }
    @Composable
    fun rememberRequestMultiplePermissionsStates(permissions:Array<String>) : RequestMultiplePermissionsStates {

        val ctx = LocalContext.current
        val map = remember {
            val m = mutableMapOf<String,MutableState<PermissionStatus>>()
            permissions.forEach {
                m[it] = mutableStateOf(if(Helper.checkPermission(
                        ctx,
                        it
                    )
                ) PermissionStatus.GRANTED else PermissionStatus.NONE
                )
            }
            m
        }
        val requestPermissionLauncher =  rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){
            permissions.forEach {
                map[it]?.value = getPermissionStatus(ctx,it)
            }
        }

        return remember {
            RequestMultiplePermissionsStates(permissions, requestPermissionLauncher ,map)
        }

    }

    @Composable
    fun rememberRequestMultiplePermissions(permissions:Array<String>) : RequestMultiplePermissions {

        val ctx = LocalContext.current
        val map = remember {
            val m = mutableStateMapOf<String, PermissionStatus>()
            permissions.forEach {
                m[it] = if(Helper.checkPermission(
                        ctx,
                        it
                    )
                ) PermissionStatus.GRANTED else PermissionStatus.NONE
            }
            m
        }
        val requestPermissionLauncher =  rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){
            permissions.forEach {
                map[it] = getPermissionStatus(ctx,it)
            }
        }

        return remember {
            RequestMultiplePermissions(permissions, requestPermissionLauncher ,map)
        }

    }

    class Builder(private val context: Context) {

        private lateinit var mIntent : Intent
        private var mListIgnore = mutableListOf<String>()
        private var mIntentSecondaryList = mutableListOf<Intent>()



        fun setIntent(intent:Intent): Builder {
            mIntent = intent
            return this
        }
        fun setIgnore(activityInfoNames: List<String>?): Builder {
            activityInfoNames?.let {
                mListIgnore.addAll(it)
            }
            return this
        }
        fun setSecondariesIntent( intents:List<Intent>?): Builder {
            intents?.let {
                mIntentSecondaryList.addAll(it)
            }
            return this
        }



//    Manifest:
//    Example Intent object and Manifest declaration for  query images intents.
//    <queries>
//      <intent>
//        <action android:name="android.intent.action.GET_CONTENT" />
//        <data android:mimeType="image/*" />
//     </intent>
//    </queries>

        fun build(): Data {

            var listIntents = context.packageManager.queryIntentActivities(mIntent, 0)

            val mapIntents =  mutableMapOf<String,Intent>()

            listIntents.forEach {
                mapIntents[it.activityInfo.name] = mIntent
            }

            mIntentSecondaryList.forEach { pIntent ->
                val list = context.packageManager.queryIntentActivities(pIntent, 0)
                list.forEach {
                    mapIntents[it.activityInfo.name] = pIntent
                }
                listIntents.addAll(list)
            }

            listIntents  = listIntents.filter {
                var v = true
                for (e in mListIgnore){
                    val pv = it.activityInfo.name.contains(e)
                    if(pv){
                        v = false
                        break
                    }
                }
                v
            }

            listIntents  = listIntents.filter {
                it.activityInfo.name != "com.android.fallback.Fallback"
            }

            return Data(listIntents,mapIntents)
        }



    }

    class GetActivityResult: ActivityResultContract<IntentInfo, ActivityResult>(){

        override fun createIntent(context: Context, input: IntentInfo): Intent {
            val itn = Intent(input.intent)
            if(input.resolveInfo != null) itn.component = createComponentName(input.resolveInfo)
            return itn
        }
        private fun createComponentName(resolveInfo: ResolveInfo) : ComponentName {
            return ComponentName(
                resolveInfo.activityInfo.packageName,
                resolveInfo.activityInfo.name
            )
        }
        override fun parseResult(resultCode: Int, intent: Intent?): ActivityResult {
            return ActivityResult(resultCode,intent)
        }
    }

    //not complete, example:
     class OffsetAnimationByScroll(private val cour:CoroutineScope,private val initialOffset:Float) : NestedScrollConnection {

        private var anim = Animatable(initialOffset)

        val offset: Float
            get() = anim.value

        fun snapTo(value: Float) {
            cour.launch {
                anim.snapTo(value)
            }
        }


        override  fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
            //up negative down positive
            val delta = available.y.toInt()
            val newOffset = anim.value + delta
            val animValue = newOffset.coerceIn(0f,initialOffset)
            snapTo(animValue)
            //consuming until q se ponga en el lugar con 0 scrollea la lisat y mueve la ventana.
            return Offset(0f, 0f)

        }
    }

    @Composable
    fun rememberOffsetAnimationByScroll( initialOffset:Float): OffsetAnimationByScroll {

        val cour = rememberCoroutineScope()

        return remember {
            OffsetAnimationByScroll(cour,initialOffset)
        }
    }

    data class Theme(
        val indicatorActiveColor: Color,
        val indicatorInactiveColor:Color
    )

    val defaultTheme = Theme(Color.Black,Color.Gray)

    data class Layout(
        val indicatorSize: Dp,
        val indicatorPadding: PaddingValues,
        val containerPadding: PaddingValues,
        val itemHeight:Dp,
        val itemPadding: PaddingValues,
        val imagePadding: PaddingValues

    )
    val defaultLayout = Layout(8.dp, PaddingValues(2.dp),PaddingValues(25.dp,10.dp,25.dp,25.dp),120.dp,PaddingValues(0.dp),PaddingValues(15.dp))


}



@OptIn(ExperimentalFoundationApi::class)
@Composable
fun IntentChooser(data: IntentChooser.Data,
                  contentLayout: IntentChooser.Layout = IntentChooser.defaultLayout,
                  contentTheme: IntentChooser.Theme = IntentChooser.defaultTheme,
                  textStyle: TextStyle = LocalTextStyle.current.copy(fontSize = 10.sp),
                  onClick:(IntentChooser.IntentInfo?)->Unit,
                  top:@Composable ()-> Unit = {}, bottom:@Composable ()-> Unit= {}){


    val ctx = LocalContext.current
    val numPages = if(data.list.size > 8) ceil(data.list.size / 8f).toInt() else 1
    val rowCount = if(data.list.size > 4) 2 else 1

    val pagerState = rememberPagerState(pageCount = {
        numPages
    })

    Column(modifier = Modifier
        .clip(RoundedCornerShape(25.dp, 25.dp, 0.dp, 0.dp)) //first clip
        .background(Color.White)
        .clickable(
            enabled = true,
            onClickLabel = "onClickLabel",
            onClick = {},
            role = null,
            indication = null,
            interactionSource = remember { MutableInteractionSource() }
        )

    ) {
        top()
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()

        ) { page ->
            Column(modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(contentLayout.containerPadding)) {

                repeat(rowCount){contentIndex->
                    Row(modifier = Modifier.height(contentLayout.itemHeight),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically) {
                        val start = (8*page)
                        val itemRowNum = start+((contentIndex+1)*4)
                        val itemsCount = if(data.list.size >= itemRowNum) 4 else 4 - (itemRowNum - data.list.size)

                        repeat(itemsCount){
                            val listIndex = (start + it) + (contentIndex*4)
                            Column(modifier = Modifier
                                .fillMaxHeight()
                                .weight(1f)
                                .padding(contentLayout.itemPadding)
                                .clickable {
                                    val ri = data.list[listIndex]
                                    val ite = data.map[data.list[listIndex].activityInfo.name]

                                    onClick(
                                        if (ite != null) IntentChooser.IntentInfo(
                                            ri,
                                            ite
                                        ) else null
                                    )
                                },
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center) {
                                Image(modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(contentLayout.imagePadding),
                                    bitmap = data.list[listIndex].loadIcon(ctx.packageManager).toBitmap().asImageBitmap(), contentDescription = "${data.list[listIndex].loadLabel(ctx.packageManager)}" )
                                Text(text = data.list[listIndex].loadLabel(ctx.packageManager).toString(), modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Center, maxLines = 2, style = textStyle )
                            }
                        }

                        repeat(4 - itemsCount){
                            Spacer(modifier = Modifier.fillMaxHeight()
                                .weight(1f))
                        }
                    }
                }
            }

        }

        if(numPages > 1){

            Row(
                Modifier
                    .wrapContentHeight()
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(pagerState.pageCount) { iteration ->
                    val color = if (pagerState.currentPage == iteration) contentTheme.indicatorActiveColor else contentTheme.indicatorInactiveColor
                    Box(
                        modifier = Modifier
                            .padding(contentLayout.indicatorPadding)
                            .clip(CircleShape)
                            .background(color)
                            .size(contentLayout.indicatorSize)
                    )
                }
            }
        }

        bottom()
    }
}