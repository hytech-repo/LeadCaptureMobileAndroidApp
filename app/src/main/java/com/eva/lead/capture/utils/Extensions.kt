package com.eva.lead.capture.utils


/**
 *
 * Created by Laxmi Kant Joshi on 19/08/2025
 *
 * */

import android.app.Dialog
import android.content.Context
import android.content.res.Configuration
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.util.Patterns
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.PopupWindow
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.graphics.ColorUtils
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.airbnb.lottie.LottieAnimationView
import com.eva.lead.capture.R
import com.eva.lead.capture.ui.base.BaseActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlin.coroutines.CoroutineContext

fun <T : Any, L : LiveData<T>> LifecycleOwner.observe(liveData: L, body: (T) -> Unit) =
    liveData.observe(this, Observer(body))

fun <L : LiveData<Exception>> LifecycleOwner.failure(liveData: L, body: (Exception?) -> Unit) =
    liveData.observe(this, Observer(body))

inline fun <T> Flow<T>.collectWithLifecycle(
    lifecycleOwner: LifecycleOwner,
    state: Lifecycle.State = Lifecycle.State.STARTED,
    crossinline collector: suspend (T) -> Unit,
) {
    lifecycleOwner.lifecycleScope.launch {
        lifecycleOwner.repeatOnLifecycle(state) {
            this@collectWithLifecycle.collect { collector(it) }
        }
    }
}

internal fun Fragment.handleFailure(e: Exception?) {
    val log = AppLogger(context)
    context?.showToast(e?.message ?: "", ToastType.ERROR)
    when (e) {
//        is WebServiceFailure.NoNetworkFailure -> showErrorToast("No internet connection!")
//        is WebServiceFailure.NetworkTimeOutFailure, is WebServiceFailure.NetworkDataFailure -> showErrorToast("Uh Oh! Please Try Again")
//        else -> showErrorToast("Oops! Something Went Wrong")
    }
//    log.v("handleFailure: ", "OUT")
}

fun Context.getExternalFolderPath(name: String): File {
    val dir = File(getExternalFilesDir(null), name)
    if (!dir.exists()) {
        dir.mkdirs()
    }
    return dir
}

fun Context.showToast(
    msg: String,
    toastType: ToastType = ToastType.SUCCESS,
    length: Int = Toast.LENGTH_SHORT,
) {
    CustomToast.show(applicationContext, msg, toastType, length)
}

fun Context.showToast(
    strResId: Int,
    toastType: ToastType = ToastType.SUCCESS,
    length: Int = Toast.LENGTH_SHORT,
) {
    CustomToast.show(applicationContext, getString(strResId), toastType, length)
}

fun Context.showToast(strResId: Int, length: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, getString(strResId), length).show()
}

fun Context.isInternetAvailable(): Boolean {
    val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val networkInfo = connectivityManager.activeNetworkInfo
    return networkInfo?.isConnected == true
}

fun Context.hasInternet(): Boolean {
    val connectivityManager =
        getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = connectivityManager.activeNetwork ?: return false
    val networkCapabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

    // Check if we are connected to WiFi and has INTERNET capability
    val hasInternetCapability =
        networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)

    val hasValidated =
        networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)

    // ✅ hasInternetCapability = the network *claims* to provide internet
    // ✅ hasValidated = Android has *actually validated* internet access (ping to Google servers)
    return hasInternetCapability && hasValidated
}

fun Context.isLocationGPSEnable(): Boolean {
    val locationManager =
        getSystemService(Context.LOCATION_SERVICE) as LocationManager
    return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
}

fun Context.removePrefData() {
//    val prefManger = PrefManager(this)
//    prefManger.remove("device_detail")
//    prefManger.remove("user_id")
}

fun Context.showPopupDialog(layoutRes: Int, ancherView: View): PopupWindow {
    val customView = (this as AppCompatActivity).layoutInflater.inflate(layoutRes, null)
    val popupWindow = PopupWindow(
        customView,
        ViewGroup.LayoutParams.WRAP_CONTENT,
        ViewGroup.LayoutParams.WRAP_CONTENT,
        true // Set true for focusable PopupWindow
    )
    val params = FrameLayout.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.MATCH_PARENT
    )

    val dimOverlay = View(this)
    dimOverlay.setBackgroundResource(R.drawable.dim_overlay)
    dimOverlay.setOnClickListener { // Dismiss the popup menu when overlay is clicked
        popupWindow.dismiss()
        (dimOverlay.parent as ViewGroup).removeView(dimOverlay)
    }
    (window.decorView.rootView as ViewGroup).addView(dimOverlay, params)
    popupWindow.setOnDismissListener {
        (dimOverlay.parent as ViewGroup).removeView(dimOverlay)
    }
    popupWindow.showAsDropDown(ancherView)
    return popupWindow
}

//fun Context.showProgressBar(): PopupWindow {
//    val customView = (this as MainActivity).layoutInflater.inflate(R.layout.loader_imageview, null)
//    val animationDrawable = (customView as ImageView).drawable as AnimationDrawable
//    animationDrawable.start()
//
//    val popupWindow = PopupWindow(
//        customView,
//        ViewGroup.LayoutParams.WRAP_CONTENT,
//        ViewGroup.LayoutParams.WRAP_CONTENT,
//        true // Set true for focusable PopupWindow
//    )
//    val params = FrameLayout.LayoutParams(
//        ViewGroup.LayoutParams.MATCH_PARENT,
//        ViewGroup.LayoutParams.MATCH_PARENT
//    )
//
//    val dimOverlay = View(this)
//    dimOverlay.setBackgroundResource(R.drawable.dim_overlay)
//    dimOverlay.setOnClickListener { // Dismiss the popup menu when overlay is clicked
//        popupWindow.dismiss()
//        (dimOverlay.parent as ViewGroup).removeView(dimOverlay)
//    }
//    (window.decorView.rootView as ViewGroup).addView(dimOverlay, params)
//    popupWindow.setOnDismissListener {
//        (dimOverlay.parent as ViewGroup).removeView(dimOverlay)
//    }
//    popupWindow.showAtLocation(null, Gravity.CENTER, 0, 0)
//    return popupWindow
//}

@Throws(Exception::class)
fun Context.showProgressDialog(cancellable: Boolean, animationRes: Int): Dialog {
    val customView = (this as BaseActivity).layoutInflater.inflate(R.layout.dialog_loader, null)
    val customDialog = Dialog(this)
    customDialog.setContentView(customView)
    customDialog.setCancelable(cancellable)
    val lottieView = customView.findViewById<LottieAnimationView>(R.id.lottie_animation_view)
    lottieView.setAnimation(animationRes)
    lottieView.playAnimation()
    customDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

    // Show the dialog
    customDialog.show()
    return customDialog
}

fun Context.showDialog(view: View, cancellable: Boolean = true): Dialog {
    val dialog = Dialog(this)
    dialog.setContentView(view)
    dialog.setCancelable(cancellable)
    return dialog
}

fun String.convertPatternOfDate(oldPattern: String, newPattern: String): String {
    val inputFormat = SimpleDateFormat(oldPattern, Locale.getDefault())
    val outputFormat = SimpleDateFormat(newPattern, Locale.getDefault())
    return try {
        val date = inputFormat.parse(this)
        outputFormat.format(date!!)
    } catch (et: Exception) {
        ""
    }
}

fun View.hideKeyboard() {
    val imm = this.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
    imm!!.hideSoftInputFromWindow(this.windowToken, 0)
}

fun String.isValidEmail(): Boolean {
    return Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

//fun String.convertHtmlIntoNormalText(): String {
//    val document = Jsoup.parse(this)
//    return document.text()
//}

fun FragmentActivity.getDeviceWidthPixel(): Int {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        val windowMetrics = this.windowManager.currentWindowMetrics
        val insets = windowMetrics.windowInsets
            .getInsetsIgnoringVisibility(WindowInsets.Type.systemBars())
        return windowMetrics.bounds.width() - insets.left - insets.right
    } else {
        val displayMetrics = DisplayMetrics()
        this.windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.widthPixels
    }
}

fun Long.convertIntoDate(): String {
    try {
        val date = Date(this)
        val format = SimpleDateFormat("dd MMM yyyy HH:mm:ss a")
        format.timeZone = TimeZone.getDefault()
        return format.format(date)
    } catch (e: java.lang.Exception) {
    }
    return ""
}

fun Long.convertIntoDate(patteren: String): String {
    try {
        val date = Date(this)
        val format = SimpleDateFormat(patteren)
        format.timeZone = TimeZone.getDefault()
        return format.format(date)
    } catch (e: java.lang.Exception) {
    }
    return ""
}

enum class SearchFilter {
    NAME, ADDRESS, STATE, CITY, CAPACITY, PRICE, VARIETY, QUANTITY;
}

val coroutineScope = object : CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = Job() + Dispatchers.Main
}

enum class ToastType {
    SUCCESS, ERROR, WARNING, INFO
}

fun Context.getDrawableStatus(status: String?): Drawable {
    val colorRes = status?.getStatusColor() ?: R.color.status_yellow
    return this.changeDrawableBgAndStroke(
        R.drawable.bg_rounded_status,
        colorRes,
        2,
        15
    )
}

fun String.getStatusColor(): Int {
    return when (this) {
        "hot" -> R.color.toast_error_bg
        "warm" -> R.color.status_yellow
        "cold" -> R.color.status_blue
        else -> R.color.status_yellow
    }
}


//fun String.getStatusColor(): Int {
//    return when (this) {
//        "Attended" -> R.color.status_green
//        "Pending" -> R.color.status_yellow
//        "Confirmed" -> R.color.status_blue
//        "Canceled" -> R.color.status_red
//        "Waitlisted" -> R.color.status_purple
//        else -> R.color.status_yellow
//    }
//}

fun Context.changeDrawableBgAndStroke(
    drawableRes: Int,
    colorRes: Int,
    strokeWidth: Int,
    alpha: Int,
): GradientDrawable {
    val color = ContextCompat.getColor(this, colorRes)
    val fillColor = ColorUtils.setAlphaComponent(color, alpha)
    val drawable = ContextCompat.getDrawable(this, drawableRes) as GradientDrawable
    drawable.setColor(fillColor)
    drawable.setStroke(strokeWidth, color)
    drawable.mutate()
    return drawable
}

fun Context.isTablet(): Boolean {
    val screenLayout = resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK
    return screenLayout == Configuration.SCREENLAYOUT_SIZE_LARGE ||
            screenLayout == Configuration.SCREENLAYOUT_SIZE_XLARGE
}

enum class QuestionTabType {
    QUESTIONS,
    MY_QUESTIONS,
    QUICK_NOTES
}

fun EditText.moveOnInput(next: EditText?, previous: EditText?) {
    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            when {
                s?.length == 1 -> next?.requestFocus()
                s?.isEmpty() == true && before == 1 -> previous?.requestFocus()
            }
        }

        override fun afterTextChanged(s: Editable?) {}
    })
}

fun Long.formatDuration(): String {
    val minutes = (this / 1000) / 60
    val seconds = (this / 1000) % 60
    return String.format("%02d:%02d", minutes, seconds)
}

fun Int.toColor(mcontext: Context): Int {
    return ContextCompat.getColor(mcontext, this)
}

fun Context.toUri(file: File): Uri {
    return FileProvider.getUriForFile(
        this,
        "${this.applicationContext.packageName}.fileprovider",
        file
    )
}

fun Long.toLastLoginString(): String {
    val now = Calendar.getInstance()
    val then = Calendar.getInstance().apply { timeInMillis = this@toLastLoginString }

    val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
    val timeString = timeFormat.format(Date(this))

    return when {
        now.isSameDay(then) -> "Today, $timeString"
        now.isYesterday(then) -> "Yesterday, $timeString"
        else -> {
            val dateFormat = SimpleDateFormat("dd MM yyyy", Locale.getDefault())
            val dateString = dateFormat.format(Date(this))
            "$dateString, $timeString"
        }
    }
}

private fun Calendar.isSameDay(other: Calendar): Boolean {
    return this.get(Calendar.YEAR) == other.get(Calendar.YEAR) &&
            this.get(Calendar.DAY_OF_YEAR) == other.get(Calendar.DAY_OF_YEAR)
}

private fun Calendar.isYesterday(other: Calendar): Boolean {
    this.add(Calendar.DAY_OF_YEAR, -1)
    val result = this.isSameDay(other)
    this.add(Calendar.DAY_OF_YEAR, 1) // reset
    return result
}

//fun String.generateQRCode(width: Int, height: Int): Bitmap? {
//    return try {
//        val bitMatrix = MultiFormatWriter().encode(this, BarcodeFormat.QR_CODE, width, height)
//        val barcodeEncoder = BarcodeEncoder()
//        barcodeEncoder.createBitmap(bitMatrix)
//    } catch (e: Exception) {
//        e.printStackTrace()
//        null
//    }
//}