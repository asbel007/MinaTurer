package com.imploded.minaturer.viewmodel

import com.google.gson.Gson
import com.imploded.minaturer.interfaces.FindStopsViewModelInterface
import com.imploded.minaturer.interfaces.SettingsInterface
import com.imploded.minaturer.interfaces.WebServiceInterface
import com.imploded.minaturer.model.LocationContainer
import com.imploded.minaturer.model.LocationList
import com.imploded.minaturer.model.StopLocation
import com.imploded.minaturer.model.UiStop
import com.imploded.minaturer.utils.fromJson
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import org.jetbrains.anko.coroutines.experimental.bg
import java.util.*
import javax.inject.Inject

class FindStopsViewModel @Inject constructor(private val webservice: WebServiceInterface, private val settings: SettingsInterface) : FindStopsViewModelInterface {

    private var filterString = ""

    override var isSearching = false
    override var locations: LocationContainer = LocationContainer(LocationList())

    override fun updateFiltering(data: String): Boolean {
        if (data.length >= 3) {
            if (filterString != data) {
                filterString = data
                return true
            }
        }
        else {
            if (!filterString.isEmpty()) {
                filterString = ""
                return true
            }
        }
        return false
    }

    override fun getStops(updateFun: (() -> Unit)) = async(UI) {
        isSearching = true
        val searchTask = bg { webservice.getLocationsByNameTl(filterString) }
        locations = searchTask.await()
        updateFun()
    }

    override fun addStop(stop: StopLocation) {
        val activeSettings = settings.loadSettings()
        if (activeSettings.StopsList.isEmpty()) {
            val stops = listOf(UiStop(stop.name, stop.id, stop.lat, stop.lon))
            activeSettings.StopsList = Gson().toJson(stops)
        }
        else {
            val stops = Gson().fromJson<ArrayList<UiStop>>(activeSettings.StopsList)
            if (!stops.any { s -> s.id.equals(stop.id, true) }) {
                stops.add(UiStop(stop.name, stop.id, stop.lat, stop.lon))
                activeSettings.StopsList = Gson().toJson(stops)
            }
        }
        settings.saveSettings(activeSettings)
    }

}