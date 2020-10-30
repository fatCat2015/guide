package com.sck.guide

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.ContentFrameLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import kotlinx.android.synthetic.main.activity_guide.*
import java.util.*
import kotlin.collections.ArrayList

internal class GuideActivity : AppCompatActivity() {


    private var guideList=LinkedList<GuideParams>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation=GuideManager.screenOrientation
        transparentStatusBar(this)
        parseIntent()
        setContentView(R.layout.activity_guide)
        clGuideCover.setBackgroundColor(GuideManager.guideBgColor)
        showGuide()
        setListeners()
    }

    private fun parseIntent(){
        var guideParams=intent.getSerializableExtra("guideParams") as ArrayList<GuideParams>
        guideList.clear()
        guideList.addAll(guideParams)
    }

    private fun showGuide(){
        if(guideList.isNotEmpty()){
            var guideParam=guideList.pop()
            clGuideCover.removeAllViews()
            showGuideLayout(guideParam)
            GuideManager.saveShowState(guideParam)
        }
    }

    private fun showGuideLayout(guideParams:GuideParams){
        runCatching {
            LayoutInflater.from(this).inflate(guideParams.guideLayoutResId,clGuideCover)
            val guideView=clGuideCover.getChildAt(0)
            offsetGuideView(guideParams,guideView)
        }.onFailure {
            if(GuideManager.debugModel){
                Log.i(TAG, "showGuideLayout onFailure: ${it.message}")
            }
        }

    }

    private fun offsetGuideView(guideParams:GuideParams,guideView:View){
        guideView.alpha=0F
        guideView.post {
            val ivHighLight=guideView.findViewById<ImageView>(R.id.ivHighlight)
            setIvHighLight(guideParams,ivHighLight)
            ivHighLight.post{
                val outLocation=IntArray(2)
                ivHighLight.getLocationOnScreen(outLocation)
                val xOffset=guideParams.leftOffset-(outLocation[0]+guideParams.padding[0])
                val yOffset=guideParams.topOffset-(outLocation[1]+guideParams.padding[1])
                guideView.translationX=xOffset.toFloat()
                guideView.translationY=yOffset.toFloat()
                guideView.alpha=1F
            }
        }
    }

    private fun setIvHighLight(guideParams:GuideParams,ivHighlight: ImageView){
        ivHighlight.setPadding(guideParams.padding[0],guideParams.padding[1],guideParams.padding[2],guideParams.padding[3])
        ivHighlight.background=GradientDrawable().apply {
            setColor(Color.WHITE)
            cornerRadius=guideParams.radius
        }
        guideParams.highlightImage?.let{imageBytes ->
            var bitmap=BitmapFactory.decodeByteArray(imageBytes,0,imageBytes.size)
            var imageDrawable= RoundedBitmapDrawableFactory.create(resources,bitmap)
            imageDrawable.cornerRadius=guideParams.radius
            ivHighlight.setImageDrawable(imageDrawable)
        }
    }



    private fun setListeners(){
        clGuideCover.setOnClickListener {
            if(GuideManager.cancelableOnTouchOutside){
                handleIvHighlightClick()
            }
        }
    }

    private fun handleIvHighlightClick(){
        if(guideList.isEmpty()){
            finish()
        }else{
            showGuide()
        }
    }



    companion object{
        fun open(activity: Activity,guideParams: ArrayList<GuideParams>){
            var intent=Intent(activity, GuideActivity::class.java)
            intent.putExtra("guideParams",guideParams)
            activity.startActivity(intent)
            activity.overridePendingTransition(R.anim.guide_enter,0)
        }

        const val TAG="GuideActivity"

    }

    override fun finish() {
        super.finish()
        overridePendingTransition(0,R.anim.guide_exit)
    }

    override fun onBackPressed() {
        if(GuideManager.cancelableOnTouchOutside){
            handleIvHighlightClick()
        }
    }

    private fun transparentStatusBar(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            activity.window
                .addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            val option = (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
            activity.window.decorView.systemUiVisibility = option
            activity.window.statusBarColor =ContextCompat.getColor(this,android.R.color.transparent)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            activity.window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        }
    }
}