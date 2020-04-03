package com.udacity

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.DecelerateInterpolator
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0
    private var textToDisplay = "Download"
    var startAnimation: Boolean = false
    private var padding = 10// Radius of the circle.
    private var progress = 0
    private var interpolation: Float = 0f
    private val progressPaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = Color.parseColor("#00404b")
        style = Paint.Style.FILL
        typeface = Typeface.create("", Typeface.BOLD)
    }

    lateinit var valueAnimator: ValueAnimator
    private val rectanglePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        // Paint styles used for rendering are initialized here. This
        // is a performance optimization, since onDraw() is called
        // for every screen refresh.
        color = Color.parseColor("#00aa99")
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        typeface = Typeface.create("", Typeface.BOLD)
    }


    private val drawPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        // Paint styles used for rendering are initialized here. This
        // is a performance optimization, since onDraw() is called
        // for every screen refresh.
        color = Color.WHITE
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 65.0f
        typeface = Typeface.create("", Typeface.BOLD)
    }


    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->
        if (new == ButtonState.Clicked) {
            textToDisplay = "Downloading"
            startProgress(progress, true)
        } else if (new == ButtonState.Completed) {
            textToDisplay = "Download"
            invalidate()
        }
    }


    init {
        isClickable = true

    }

    fun startAnimation() {
        buttonState = ButtonState.Clicked
        invalidate()
    }

    fun stopAnimation() {
        buttonState = ButtonState.Completed
        interpolation = 0f
        invalidate()
    }


    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val mainRectangle = widthSize.minus(padding).let {
            heightSize.minus(padding).let { it1 ->
                Rect(
                        padding,  // Left
                        padding,  // Top
                        it,  // Right
                        it1 // Bottom
                )
            }
        }

        var progressEndX = (width * progress / 100f).toInt()
        if (interpolation.toInt() == 100) {
            buttonState = ButtonState.Completed
        }


        val secondaryRectangle = progressEndX.minus(padding).let {
            heightSize.minus(padding).let { it1 ->
                Rect(
                        padding,  // Left
                        padding,  // Top
                        it,  // Right
                        it1 // Bottom
                )
            }
        }
        canvas?.drawRect(mainRectangle, rectanglePaint)
        if (interpolation.toInt() < 100)
            canvas?.drawRect(secondaryRectangle, progressPaint)
        canvas?.drawText(textToDisplay, mainRectangle.exactCenterX() + 10, mainRectangle.exactCenterY() + 10, drawPaint)

    }

    private fun startProgress(progress: Int, start: Boolean) {
        if (start) {
            valueAnimator = ValueAnimator.ofFloat(0f, 100f).apply {
                duration = 3000
                interpolator = DecelerateInterpolator()
            }


            // reset progress without animating
            startProgress(0, false)
            valueAnimator.addUpdateListener {
                interpolation = it.getAnimatedValue() as Float
                startProgress(interpolation.toInt(), false)
            }
            if (!valueAnimator.isStarted) {
                valueAnimator.start()
            }
        } else {
            this.progress = progress
            postInvalidate()

        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
                MeasureSpec.getSize(w),
                heightMeasureSpec,
                0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)

    }


}