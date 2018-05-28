package com.gin.multimedia

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.Surface
import android.view.SurfaceHolder
import android.view.SurfaceView

/**
 * @author: ginchen
 * @version: 1.0.0
 * @date: 2018/5/28 15:45
 * @see
 */
class MySurfaceView(context: Context, attrs: AttributeSet?, defStyle: Int) : SurfaceView(context, attrs, defStyle), SurfaceHolder.Callback {

    private var surfaceHolder: SurfaceHolder
    private lateinit var canvas: Canvas
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    constructor(context: Context) : this(context, null, 0)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    init {
        paint.color = Color.RED
        surfaceHolder = holder
        surfaceHolder.addCallback(this)
    }

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        Thread {
            canvas = holder.lockCanvas()
            canvas.drawCircle(width / 2.0f, height / 2.0f, 50.0f, paint)
            holder.unlockCanvasAndPost(canvas)
        }.start()
    }

}