package cn.dhl.sample.popup

import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import cn.dhl.sample.R

/**
 *
 * Author: duanhl
 * Create: 5/9/21 10:59 PM
 * Description:
 *
 */
class PopupActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view = PopupBackgroundView(this)
        val params = ViewGroup.MarginLayoutParams(920, 1000)
        params.leftMargin = 80
        params.rightMargin = 80
        view.layoutParams = params
        setContentView(view)
    }

}