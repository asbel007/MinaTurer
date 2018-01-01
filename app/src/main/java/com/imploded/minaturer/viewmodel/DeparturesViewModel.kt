package com.imploded.minaturer.viewmodel

import com.imploded.minaturer.interfaces.WebServiceInterface
import com.imploded.minaturer.model.*
import com.imploded.minaturer.repository.WebServiceRepository
import com.imploded.minaturer.utils.etaTime
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import org.jetbrains.anko.coroutines.experimental.bg

class DeparturesViewModel {

    private val webservice: WebServiceInterface = WebServiceRepository()
    var uiDepartures: List<UiDeparture> = listOf()

    private fun itemIsFiltered(departure: Departure, filterList: List<FilteredDeparture>) : Boolean {
        return filterList
                .any { f -> f.shortName.equals(departure.sname, true) && f.direction.equals(departure.direction, true) }
    }
    private fun lineIsFiltered(departure: Departure, filterList: List<String>) : Boolean {
        return filterList.any { f -> f.equals(departure.sname, true) }
    }

    fun getDepartures(stopId: String, updateFun: (() -> Unit)) = async(UI) {
        val tokenTask = bg { webservice.getToken() }
        tokenTask.await()
        val searchTask = bg { webservice.getDepartures(stopId) }
        val departures = searchTask.await()

        val filteredLines = FilteredLines.filterlistForStop(stopId)
        val departureResult = if (filteredLines.count() > 0) {
            departures.departureBoard.departures
                    .filter { d -> !lineIsFiltered(d, filteredLines) }
        } else {
            departures.departureBoard.departures
        }
        val filtered = FilteredDepartures.filterlistForStop(stopId)
        if (filtered.count() > 0) {
            uiDepartures = departureResult
                    .filter { d -> !itemIsFiltered(d, filtered) }
                    .map { d ->
                        UiDeparture(d.name, d.sname, d.time, d.date, d.fgColor, d.bgColor, d.stop, d.rtTime.etaTime(), d.direction, d.stopid)
                    }
        }
        else {
            uiDepartures = departureResult
                    .map { d ->
                        UiDeparture(d.name, d.sname, d.time, d.date, d.fgColor, d.bgColor, d.stop, d.rtTime.etaTime(), d.direction, d.stopid)
                    }
        }

        updateFun()
    }

    fun filtersActive(stopId: String): Boolean = FilteredDepartures.filterCountForStop(stopId)> 0 || FilteredLines.filterCountForStop(stopId) > 0

}