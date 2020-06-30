package com.sck.guide

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        GuideManager.init(this, true)
        tv.setOnClickListener {
            GuideManager.begin()
                .add(GuideParams(tv, R.layout.guide_demo_bottom,"a"))
                .add(GuideParams(tv1, R.layout.guide_demo_bottom, "b",gravity = Gravity.LEFT))
                .show(this)
        }
    }
}