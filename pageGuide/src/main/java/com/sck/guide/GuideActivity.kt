package com.sck.guide

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Bundle
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

class GuideActivity : AppCompatActivity() {


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
        var guideParams=intent.getSerializableExtra("guideParams")
        if(guideParams is ArrayList<*>){
            guideList.clear()
            guideList.addAll(guideParams as ArrayList<GuideParams>)
        }
    }

    private fun showGuide(){
        var guideParams=guideList.pop()
        guideParams?.let {
            clGuideCover.removeAllViews()
            showGuideLayout(it)
            GuideManager.saveShowState(it)
        }
    }

    private fun showGuideLayout(guideParams:GuideParams){
        var guideLayoutResId=guideParams.guideLayoutResId
        if(guideLayoutResId!=0){
            var guideView=LayoutInflater.from(this).inflate(guideLayoutResId,clGuideCover,false)
            var lp=ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT,ConstraintLayout.LayoutParams.WRAP_CONTENT)
            clGuideCover.addView(guideView,lp)
            var ivHighlight=guideView.findViewById<ImageView>(R.id.ivHighlight)
            setHighlightView(guideParams,ivHighlight)
            ivHighlight.setOnClickListener {
                handleIvHighlightClick()
            }
        }
    }

    private fun setHighlightView(guideParams:GuideParams,ivHighlight:ImageView){
        var lp= ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT,ConstraintLayout.LayoutParams.WRAP_CONTENT)
        lp.topToTop=ConstraintLayout.LayoutParams.PARENT_ID
        lp.leftToLeft=ConstraintLayout.LayoutParams.PARENT_ID
        lp.leftMargin= guideParams.leftOffset-guideParams.padding[0]
        lp.topMargin= guideParams.topOffset-guideParams.padding[1]
        ivHighlight.layoutParams=lp

        ivHighlight.setPadding(guideParams.padding[0],guideParams.padding[1],guideParams.padding[2],guideParams.padding[3])
        ivHighlight.background=createHighlightViewBg(guideParams.radius)

        guideParams.highlightImage?.let{imageBytes ->
            var bitmap=BitmapFactory.decodeByteArray(imageBytes,0,imageBytes.size)
            var imageDrawable= RoundedBitmapDrawableFactory.create(resources,bitmap)
            imageDrawable.cornerRadius=guideParams.radius
            ivHighlight.setImageDrawable(imageDrawable)
        }
    }


    private fun createHighlightViewBg(radius:Float):Drawable{
        var drawable=GradientDrawable()
        drawable.setColor(Color.WHITE)
        drawable.cornerRadius=radius
        return drawable
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

    }

    override fun finish() {
        super.finish()
        overridePendingTransition(0,R.anim.guide_exit)
    }

    override fun onBackPressed() {
        if(GuideManager.cancelable){
            super.onBackPressed()
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