package com.dhl.base.ui

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.dhl.base.log

open class BaseFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        log { "Lifecycle -> onCreate - ${this::class.java.simpleName}" }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        log { "Lifecycle -> onAttach - ${this::class.java.simpleName}" }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        log { "Lifecycle -> onCreateView - ${this::class.java.simpleName}" }
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        log { "Lifecycle -> onViewCreated - ${this::class.java.simpleName}" }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        log { "Lifecycle -> onActivityCreated - ${this::class.java.simpleName}" }
    }

    override fun onPause() {
        super.onPause()
        log { "Lifecycle -> onPause - ${this::class.java.simpleName}" }
    }

    override fun onStart() {
        super.onStart()
        log { "Lifecycle -> onStart - ${this::class.java.simpleName}" }
    }

    override fun onResume() {
        super.onResume()
        log { "Lifecycle -> onResume - ${this::class.java.simpleName}" }
    }

    override fun onStop() {
        super.onStop()
        log { "Lifecycle -> onStop - ${this::class.java.simpleName}" }
    }

    override fun onDetach() {
        super.onDetach()
        log { "Lifecycle -> onDetach - ${this::class.java.simpleName}" }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        log { "Lifecycle -> onDestroyView - ${this::class.java.simpleName}" }
    }

    override fun onAttachFragment(childFragment: Fragment) {
        super.onAttachFragment(childFragment)
        log { "Lifecycle -> onAttachFragment:${childFragment::class.java.simpleName} - ${this::class.java.simpleName}" }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        log { "Lifecycle -> onConfigurationChanged:$newConfig - ${this::class.java.simpleName}" }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        log { "Lifecycle -> onHiddenChanged:$hidden - ${this::class.java.simpleName}" }
    }

    override fun onDestroy() {
        super.onDestroy()
        log { "Lifecycle -> onDestroy - ${this::class.java.simpleName}" }
    }

    open fun dispatchKeyEvent(event: KeyEvent?): Boolean {
        return false
    }

}
