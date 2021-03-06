package com.imploded.minaturer.utils

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.support.annotation.ColorRes
import android.support.v4.app.Fragment
import android.support.v4.graphics.drawable.DrawableCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.imploded.minaturer.R
import com.imploded.minaturer.application.MinaTurerApp
import com.imploded.minaturer.fragments.*
import com.imploded.minaturer.model.TlDeparture
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

inline fun <reified T> Gson.fromJson(json: String) = this.fromJson<T>(json, object: TypeToken<T>() {}.type)

fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }
    })
}

fun String.toColor(): Int {
    return Color.parseColor(this)
}

fun MenuItem.tintMenuIcon(context: Context, @ColorRes color: Int) {
    val normalDrawable = this.icon
    val wrapDrawable = DrawableCompat.wrap(normalDrawable)
    @Suppress("DEPRECATION")
    DrawableCompat.setTint(wrapDrawable, context.resources.getColor(color))

    this.icon = wrapDrawable
}

fun String.etaTime(originalTime: String) : String {
    if (this.equals(originalTime, true)) return ""
    return if (this.isEmpty()) "" else this
}

fun Activity.inputMethodManager() : InputMethodManager {
    return this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
}
fun View.hideKeyboard(inputMethodManager: InputMethodManager) {
    inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
}

fun timeDifference(sourceDate: String, sourceTime: String, destDate: String, destTime: String) : Int {
    return try {
        val sourceDateTime = "$sourceDate $sourceTime:00"// sourceDate + " " + sourceTime + ":00"
        val destDatetime = "$destDate $destTime:00"// destDate + " " + destTime + ":00"
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val parsedSource = dateFormat.parse(sourceDateTime)
        val parsedDest = dateFormat.parse(destDatetime)
        (parsedDest.time - parsedSource.time).toInt() / 1000
    } catch(e: Exception) {
        0
    }
}

fun Fragment.title(title: String) {
    (activity as AppCompatActivity).supportActionBar?.title = title
}
fun Fragment.displayBackNavigation() {
    (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
}
fun Fragment.hideBackNavigation() {
    (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
}

fun Fragment.inject() {
    when(this) {
        is LandingPageFragment -> activity?.app?.appComponent?.inject(this)
        is FindStopFragment -> activity?.app?.appComponent?.inject(this)
        is DeparturesFragment -> activity?.app?.appComponent?.inject(this)
        is JourneyDetailsFragment -> activity?.app?.appComponent?.inject(this)
        is SettingsPageFragment -> activity?.app?.appComponent?.inject(this)
    }
}

fun RecyclerView.applyAnimation() {
    val context = this.context
    val controller = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_fall_down)
    this.layoutAnimation = controller
    this.adapter.notifyDataSetChanged()
    this.scheduleLayoutAnimation()
}

val Activity.app: MinaTurerApp
    get() = application as MinaTurerApp

const val DEFAULT_BG_COLOR = "#00a5dc"
const val DEFAULT_BG_COLOR_SL = "#d71d24"
const val DEFAULT_FG_COLOR = "#ffffff"
const val OPERATOR_VASTTRAFIK = "279"
const val OPERATOR_SL = "275"

fun String.isTram() : Boolean = this.contains("Spårväg", true)

fun TlDeparture.bgColor(): String {
    when(this.product.operatorCode)
    {
        OPERATOR_VASTTRAFIK -> {
            val productName = this.product.num.toUpperCase()
            if (this.product.name.isTram()) {
                when(productName) {
                    "1" -> return "#ffffff"
                    "2" -> return "#fff014"
                    "3" -> return "#004b85"
                    "4" -> return "#14823c"
                    "5" -> return "#eb1923"
                    "6" -> return "#fa8719"
                    "7" -> return "#7d4313"
                    "8" -> return "#872387"
                    "9" -> return "#b9e2f8"
                    "10" -> return "#b4e16e"
                    "11" -> return "#000000"
                    "13" -> return "#fee6c2"
                }
            }

            when(productName)
            {
                "ROSA" -> return "#e89dc0"
                "GRÖN" -> return "#008228"
                "GUL" -> return "#ffdd00"
                "SVAR" -> return "#000000"
                "RÖD" -> return "#cd1432"
                "LILA" -> return "#692869"
                "BLÅ" -> return "#336699"
            }
            return DEFAULT_BG_COLOR
        }
        OPERATOR_SL -> {
            when(this.product.num.toUpperCase()) {
                "1", "2", "3", "4", "5", "6", "7", "8", "9" -> return "#0089ca"

                "10", "11" -> return "#0089ca"
                "12" -> return "#778da7"
                "13", "14" -> return "#d71d24"
                "17", "18", "19" -> return "#179d4d"
                "21" -> return "#b76020"
                "22" -> return "#985141"

                "25", "26" -> return "#008f93"
                "25B" -> return "#d71d24"

                "27", "28", "29" -> return "#9f599a"

                "40", "41", "41X", "42", "42X", "43", "43X", "44", "48" -> return "#ec619f"
            }
            return DEFAULT_BG_COLOR_SL
        }
    }
    return DEFAULT_BG_COLOR
}
fun TlDeparture.fgColor(): String {
    if (this.product.operatorCode != "279") {
        return DEFAULT_FG_COLOR
    }
    val productName = this.product.num.toUpperCase()
    if (this.product.name.isTram()) {
        when(productName)
        {
            "1" -> return "#00394d"
            "2" -> return "#00394d"
            "6" -> return "#00394d"
            "9" -> return  "#00394d"
            "10" -> return "#0e629b"
            "13" -> return "#00394d"
        }
    }

    when(productName)
    {
        "GUL" -> return "#00394d"
        "SVAR" -> return "#ffffff"
    }
    return DEFAULT_FG_COLOR
}

fun String.fixTime() : String {
    return if (this.length == 8) this.substring(0, 5) else this
}

fun TlDeparture.actualTrack() : String {
    return if (!this.rtDepTrack.isNullOrEmpty()) {
        this.rtDepTrack
    } else {
        if (this.rtTrack.isNullOrEmpty()) "" else this.rtTrack
    }
}

fun String?.ensureString() : String {
    return if (this.isNullOrEmpty()) "" else this.toString()
}

private fun findIndexSeries(input: Int, power: Int, numbers: ArrayList<Int>) {
    if (input == 0) {
        return
    }
    val digit = input.rem(2)
    if (digit == 1) {
        val a = Math.pow(2.0, power.toDouble()).toInt()
        numbers.add(a)
    }
    findIndexSeries(input / 2, power + 1, numbers)
}

private fun seriesContainsNumber(input: Int, number: Int) : Boolean {
    val numbers: ArrayList<Int> = arrayListOf()
    findIndexSeries(input, 0, numbers)
    return numbers.contains(number)
}

fun Int.isExpressTrain() : Boolean = seriesContainsNumber(this, AppConstants.ExpressTrain)
fun Int.isRegionalTrain() : Boolean = seriesContainsNumber(this, AppConstants.RegionalTrain)
fun Int.isExpressBus() : Boolean = seriesContainsNumber(this, AppConstants.ExpressBus)
fun Int.isLocalTrain() : Boolean = seriesContainsNumber(this, AppConstants.LocalTrain)
fun Int.isSubway() : Boolean = seriesContainsNumber(this, AppConstants.Subway)
fun Int.isTram() : Boolean = seriesContainsNumber(this, AppConstants.Tram)
fun Int.isBus() : Boolean = seriesContainsNumber(this, AppConstants.Bus)
fun Int.isFerry() : Boolean = seriesContainsNumber(this, AppConstants.Ferry)

fun Int.toCatResource(): Int {
    when(this) {
        1, 2, 4 -> return R.drawable.ic_train_black_24dp
        3, 7 -> return R.drawable.ic_directions_bus_black_24dp
        5 -> return R.drawable.ic_subway_black_24dp
        6 -> return R.drawable.ic_tram_black_24dp
        8 -> return R.drawable.ic_directions_boat_black_24dp
    }
    return R.drawable.ic_directions_bus_black_24dp
}