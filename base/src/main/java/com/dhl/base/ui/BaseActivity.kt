package com.dhl.base.ui

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.dhl.base.R

abstract class BaseActivity : AppCompatActivity() {

    protected lateinit var titleBar: TitleBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)

        val layout = layoutInflater.inflate(R.layout.activity_base, null)
        titleBar = layout.findViewById(R.id.titlebar)
        super.setContentView(layout)
    }

    override fun setContentView(view: View) {
        setContentView(view, null)
    }

    override fun setContentView(layoutResID: Int) {
        val layout = layoutInflater.inflate(layoutResID, null)
        setContentView(layout, null)
    }

    override fun setContentView(view: View, params: ViewGroup.LayoutParams?) {
        view.layoutParams = params ?: ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        val rootView = findViewById<ViewGroup>(R.id.rootView)
        if (rootView != null) {
            rootView.addView(view)
        } else {
            super.setContentView(view)
        }
    }

    protected open fun setupLightStatus(light: Boolean) {
        val window = window ?: return
        if (light) {
            window.decorView.systemUiVisibility = window.decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        } else {
            window.decorView.systemUiVisibility = window.decorView.systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
        }
    }

}