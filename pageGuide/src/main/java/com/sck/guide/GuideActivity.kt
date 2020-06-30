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
import androidx.appcompat.app.AppCompatActivity
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
        setContentView(R.layout.activity_guide)
        clGuideCover.setBackgroundColor(GuideManager.guideBgColor)
        parseIntent()
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
        var guideParams=guideList.peek()
        guideParams?.let {
            showHighlightView(it)
            removeOtherGuideView()
            showGuideLayout(guideParams)
            GuideManager.saveShowState(it)
        }
    }

    private fun showHighlightView(guideParams:GuideParams){
        ivHighlight.setPadding(guideParams.padding[0],guideParams.padding[1],guideParams.padding[2],guideParams.padding[3])
        ivHighlight.background=createHighlightViewBg(guideParams.radius)
        var highlightImageLayoutParams=ivHighlight.layoutParams as ViewGroup.MarginLayoutParams
        highlightImageLayoutParams.topMargin=guideParams.topOffset-guideParams.padding[1]
        highlightImageLayoutParams.leftMargin=guideParams.leftOffset-guideParams.padding[0]
        ivHighlight.layoutParams=highlightImageLayoutParams
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


    private fun removeOtherGuideView(){
        var needRemoveViewList=ArrayList<View>()
        var childCount=clGuideCover.childCount
        for(index in 0 until childCount){
            var child=clGuideCover.getChildAt(index)
            if(child.id!=R.id.ivHighlight){
                needRemoveViewList.add(child)
            }
        }
        needRemoveViewList.forEach {
            clGuideCover.removeView(it)
        }
    }

    private fun showGuideLayout(guideParams:GuideParams){
        var guideLayoutResId=guideParams.guideLayoutResId
        if(guideLayoutResId!=0){
            var guideView=LayoutInflater.from(this).inflate(guideLayoutResId,clGuideCover,false)
            var lp=ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT,ConstraintLayout.LayoutParams.WRAP_CONTENT)
            when(guideParams.gravity){
                Gravity.LEFT ->{
                    lp.topToTop=R.id.ivHighlight
                    lp.bottomToBottom=R.id.ivHighlight
                    lp.rightToLeft=R.id.ivHighlight
                }
                Gravity.TOP ->{
                    lp.bottomToTop=R.id.ivHighlight
                    lp.leftToLeft=R.id.ivHighlight
                    lp.rightToRight=R.id.ivHighlight
                }
                Gravity.RIGHT ->{
                    lp.topToTop=R.id.ivHighlight
                    lp.bottomToBottom=R.id.ivHighlight
                    lp.leftToRight=R.id.ivHighlight
                }
                Gravity.BOTTOM ->{
                    lp.topToBottom=R.id.ivHighlight
                    lp.leftToLeft=R.id.ivHighlight
                    lp.rightToRight=R.id.ivHighlight
                }
            }
            clGuideCover.addView(guideView,lp)
        }
    }


    private fun setListeners(){
        clGuideCover.setOnClickListener {
            if(GuideManager.cancelableOnTouchOutside){
                ivHighlight.performClick()
            }
        }
        ivHighlight.setOnClickListener {
            guideList.pop()
            if(guideList.isEmpty()){
                finish()
            }else{
                showGuide()
            }

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