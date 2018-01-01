package com.imploded.minaturer.fragments

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import com.imploded.minaturer.R
import com.imploded.minaturer.adapters.StopsAdapter
import com.imploded.minaturer.interfaces.OnFragmentInteractionListener
import com.imploded.minaturer.interfaces.SettingsInterface
import com.imploded.minaturer.utils.MinaTurerApp
import com.imploded.minaturer.utils.afterTextChanged
import com.imploded.minaturer.viewmodel.FindStopsViewModel
import org.jetbrains.anko.support.v4.alert
import kotlin.concurrent.fixedRateTimer

class FindStopFragment : Fragment() {

    private val appSettings: SettingsInterface by lazy {
        MinaTurerApp.prefs
    }
    private val viewModel: FindStopsViewModel = FindStopsViewModel()
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: StopsAdapter
    private var mListener: OnFragmentInteractionListener? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        (activity as AppCompatActivity).supportActionBar!!.title = getString(R.string.find_stop)
        val view = inflater!!.inflate(R.layout.fragment_find_stop, container, false)
        val editText = view.findViewById<EditText>(R.id.editTextSearch)
        editText.afterTextChanged {
            fixedRateTimer("timer", false, 0, 750, {
                this.cancel()
                val filter = editText.text.toString()
                if (viewModel.updateFiltering(filter)) {
                    activity.runOnUiThread{
                        if (!viewModel.isSearching) {
                            viewModel.isSearching = true
                            viewModel.getStops(::updateAdapter)
                        }
                    }
                    /*
                    context.runOnUiThread {
                    }*/
                }
            })
        }
        adapter = createAdapter()
        recyclerView = view.findViewById(R.id.recyclerViewStops)
        recyclerView.layoutManager = LinearLayoutManager(this.context)
        recyclerView.adapter = adapter
        showHint()

        return view
    }

    private fun updateAdapter() {
        adapter.updateItems { viewModel.locations.locationList.stopLocations }
        adapter.notifyDataSetChanged()
        recyclerView.scrollToPosition(0)
        viewModel.isSearching = false
    }


    private fun createAdapter(): StopsAdapter {
        return StopsAdapter({
            viewModel.addStop(it)
            mListener?.onStopAdded(it.name)
            activity.supportFragmentManager.popBackStack()
        })
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            mListener = context
        } else {
            throw RuntimeException(context!!.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    private fun showHint() {
        val settings = appSettings.loadSettings()
        if (settings.FindStopHintShown) return
        alert(getString(R.string.find_stop_page_hint), getString(R.string.tip)) {
            positiveButton(getString(R.string.got_it)) {
                settings.FindStopHintShown = true
                appSettings.saveSettings(settings)
            }
        }.show()
    }

    companion object {
        fun newInstance(): FindStopFragment {
            return FindStopFragment()
        }
    }
}
