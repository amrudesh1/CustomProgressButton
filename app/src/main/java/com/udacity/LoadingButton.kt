package com.udacity

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.core.content.withStyledAttributes
import kotlin.properties.Delegates


class LoadingButton @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    //Custom Attribute Text
    private var downloadingText = "Downloading"
    private var downloadText = "Download"
    private var buttonBackground = 0
    private var progressBarColor = 0
    private var textColor = 0
    private var roundProgressColor = 0


    private var widthSize = 0
    private var heightSize = 0
    private var padding = 10
    private var progress = 0
    private var interpolation: Float = 0f
    lateinit var valueAnimator: ValueAnimator

    private var stWidth = 15f

    init {
        isClickable = true
        context.withStyledAttributes(attrs, R.styleable.LoadingButton)
        {
            downloadingText = if (getString(R.styleable.LoadingButton_downloadingText) != null) {
                getString(R.styleable.LoadingButton_downloadingText).toString()
            } else {
                "Downloading"
            }
            downloadText = if (getString(R.styleable.LoadingButton_downloadText) == null) {
                "Download"
            } else {
                getString(R.styleable.LoadingButton_downloadText).toString()
            }
            textColor = getColor(R.styleable.LoadingButton_textColor, Color.WHITE)
            buttonBackground = getColor(R.styleable.LoadingButton_buttonBackground, Color.parseColor("#00aa99"))
            progressBarColor = getColor(R.styleable.LoadingButton_progressBarColor, Color.parseColor("#00404b"))
            roundProgressColor = getColor(R.styleable.LoadingButton_circleProgressColor, Color.YELLOW)
        }

    }

    private val progressPaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = progressBarColor
        style = Paint.Style.FILL
        typeface = Typeface.create("", Typeface.BOLD)
    }

    private val drawCirclePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        // Paint styles used for rendering are initialized here. This
        // is a performance optimization, since onDraw() is called
        // for every screen refresh.
        color = roundProgressColor
        style = Paint.Style.FILL
        strokeWidth = stWidth
    }


    private val rectanglePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        // Paint styles used for rendering are initialized here. This
        // is a performance optimization, since onDraw() is called
        // for every screen refresh.
        color = buttonBackground
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        typeface = Typeface.create("", Typeface.BOLD)
    }


    private val drawPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        // Paint styles used for rendering are initialized here. This
        // is a performance optimization, since onDraw() is called
        // for every screen refresh.
        color = textColor
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 65.0f

        typeface = Typeface.create("", Typeface.BOLD)
    }

    private var textToDisplay = downloadText


    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->
        if (new == ButtonState.Clicked) {
            textToDisplay = downloadingText
            startProgress(progress, true)
        } else if (new == ButtonState.Completed) {
            textToDisplay = downloadText
            invalidate()
        }
    }


    fun startAnimation() {
        buttonState = ButtonState.Clicked
        invalidate()
    }

    fun stopAnimation() {
        buttonState = ButtonState.Completed
        interpolation = 0f
        startProgress(0, false)
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

        val textRectangle = Rect()
        drawPaint.getTextBounds(textToDisplay, 0, textToDisplay.length, textRectangle)

        val rectF = RectF()
        rectF.set(((drawPaint.measureText(textToDisplay) / 1.8) + textRectangle.width()).toFloat() + 5, (mainRectangle.exactCenterY() / 2), (((drawPaint.measureText(textToDisplay) / 1.8) + textRectangle.width())).toFloat() + 100, mainRectangle.centerY().toFloat() + 50)


        canvas?.drawRect(mainRectangle, rectanglePaint)
        if (interpolation.toInt() in 1..99) {
            canvas?.drawRect(secondaryRectangle, progressPaint)
            canvas?.drawArc(rectF, 0f, ((progress * 3.6).toFloat()), false, drawCirclePaint)

        }
        canvas?.drawText(textToDisplay, mainRectangle.exactCenterX(), mainRectangle.exactCenterY() + 20, drawPaint)


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