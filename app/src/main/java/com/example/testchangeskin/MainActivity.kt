package com.example.testchangeskin

import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.example.skin.BaseSkinActivity
import com.example.skin.SkinHelper
import com.example.skin.SkinManager
import com.example.skin.SkinValueBuilder
import com.example.testchangeskin.TSKinManager.*

class MainActivity : BaseSkinActivity() {

    private lateinit var tv2: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tv2 = findViewById(R.id.tv2)

        val skinManager: SkinManager = SkinManager.defaultInstance(this)
        setSkinManager(skinManager)


        var valueBuilder = SkinValueBuilder.acquire()
        valueBuilder.textColor(R.attr.app_skin_common_title_text_color)
        SkinHelper.setSkinValue(tv2, valueBuilder)
        valueBuilder.release()
    }

    fun btnClick1(view: View) {
        TSKinManager.changeSkin(SKIN_BLUE)
    }

    fun btnClick2(view: View) {
        TSKinManager.changeSkin(SKIN_DARK)
    }

    fun btnClick3(view: View) {
        TSKinManager.changeSkin(SKIN_WHITE)
    }
}