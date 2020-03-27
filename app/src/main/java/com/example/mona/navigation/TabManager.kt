package com.example.mona.navigation


import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.view.isInvisible
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.example.mona.MainActivity
import com.example.mona.R
import kotlinx.android.synthetic.main.activity_main.*


class TabManager(private val mainActivity: MainActivity) {

    private val startDestinations = mapOf(
        R.id.odj_dest to R.id.odj_dest,
        R.id.map_dest to R.id.map_dest,
        R.id.list_dest to R.id.list_dest,
        R.id.collection_dest to R.id.collection_dest
    )
    private var currentTabId: Int = R.id.odj_dest
    var currentController: NavController? = null
    private var tabHistory = TabHistory().apply { push(R.id.odj_dest) }

    val navOdjController: NavController by lazy {
        mainActivity.findNavController(R.id.odjTab).apply {
            graph = navInflater.inflate(R.navigation.navigation).apply {
                startDestination = startDestinations.getValue(R.id.odj_dest)
            }
        }
    }
    private val navMapController: NavController by lazy {
        mainActivity.findNavController(R.id.mapTab).apply {
            graph = navInflater.inflate(R.navigation.navigation).apply {
                startDestination = startDestinations.getValue(R.id.map_dest)
            }
        }
    }
    private val navListController: NavController by lazy {
        mainActivity.findNavController(R.id.listTab).apply {
            graph = navInflater.inflate(R.navigation.navigation).apply {
                startDestination = startDestinations.getValue(R.id.list_dest)
            }
        }
    }
    private val navCollectionController: NavController by lazy {
        mainActivity.findNavController(R.id.collectionTab).apply {
            graph = navInflater.inflate(R.navigation.navigation).apply {
                startDestination = startDestinations.getValue(R.id.collection_dest)
            }
        }
    }

    fun isLastTab() : Boolean{
        var isEmpty = false
        if (tabHistory.size <= 1){
            isEmpty =  true
        }
        return isEmpty
    }

    private val odjTabContainer: View by lazy { mainActivity.odjTabContainer }
    private val mapTabContainer: View by lazy { mainActivity.mapTabContainer }
    private val listTabContainer: View by lazy { mainActivity.listTabContainer }
    private val collectionTabContainer: View by lazy { mainActivity.collectionTabContainer }

    fun onSaveInstanceState(outState: Bundle?) {
        outState?.putSerializable(KEY_TAB_HISTORY, tabHistory)
    }

    fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        savedInstanceState?.let {
            tabHistory = it.getSerializable(KEY_TAB_HISTORY) as TabHistory

            switchTab(mainActivity.bottom_nav_view.selectedItemId, false)
        }
    }

    fun supportNavigateUpTo(upIntent: Intent) {
        currentController?.navigateUp()
    }

    fun onBackPressed() {
        currentController?.let {
            if (it.currentDestination == null || it.currentDestination?.id == startDestinations.getValue(
                    currentTabId
                )
            ) {
                if (tabHistory.size > 1) {
                    val tabId = tabHistory.popPrevious()
                    switchTab(tabId, false)
                    mainActivity.bottom_nav_view.menu.findItem(tabId)?.isChecked = true
                } else {
                    mainActivity.finish()
                }
            }
            it.popBackStack()
        } ?: run {
            mainActivity.finish()
        }
    }

    fun switchTab(tabId: Int, addToHistory: Boolean = true) {
        currentTabId = tabId

        when (tabId) {
            R.id.odj_dest -> {
                currentController = navOdjController
                invisibleTabContainerExcept(odjTabContainer)
            }
            R.id.map_dest -> {
                currentController = navMapController
                invisibleTabContainerExcept(mapTabContainer)
            }
            R.id.list_dest -> {
                currentController = navListController
                invisibleTabContainerExcept(listTabContainer)
            }
            R.id.collection_dest -> {
                currentController = navCollectionController
                invisibleTabContainerExcept(collectionTabContainer)
            }
        }
        if (addToHistory) {
            tabHistory.push(tabId)
        }
    }

    private fun invisibleTabContainerExcept(container: View) {
        odjTabContainer.isInvisible = true
        mapTabContainer.isInvisible = true
        listTabContainer.isInvisible = true
        collectionTabContainer.isInvisible = true

        container.isInvisible = false
    }

    companion object {
        private const val KEY_TAB_HISTORY = "key_tab_history"
    }
}
