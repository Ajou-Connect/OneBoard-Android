package kr.khs.oneboard.core.zoom

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.view.*
import kr.khs.oneboard.databinding.LayoutShareToolbarBinding

class ShareToolbar(private val listener: Listener, private val context: Context) {
    interface Listener {
        fun onClickStopShare()
    }

    private val windowManager: WindowManager =
        context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private val display: Display =
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R)
            context.display!!
        else
            windowManager.defaultDisplay

    var lastRawX = -1f
    var lastRawY = -1f
    private var contentBinding: LayoutShareToolbarBinding? = null

    @SuppressLint("ClickableViewAccessibility")
    private fun init() {
        contentBinding = LayoutShareToolbarBinding.inflate(
            LayoutInflater.from(context)
        )
        contentBinding?.root?.setOnClickListener { } ?: return

        val listener = object : GestureDetector.SimpleOnGestureListener() {
            override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                listener.onClickStopShare()
                destroy()
                return true
            }

            override fun onScroll(
                e1: MotionEvent,
                e2: MotionEvent,
                distanceX: Float,
                distanceY: Float
            ): Boolean {
                contentBinding?.let { contentBinding ->
                    val layoutParams =
                        contentBinding.root.layoutParams as WindowManager.LayoutParams
                    var dx = 0;
                    var dy = 0
                    if (lastRawX == -1f || lastRawY == -1f) {
                        dx = (e2.rawX - e1.rawX).toInt()
                        dy = (e2.rawY - e1.rawY).toInt()
                    } else {
                        dx = (e2.rawX - lastRawX).toInt()
                        dy = (e2.rawY - lastRawY).toInt()
                    }
                    layoutParams.x += dx
                    layoutParams.y += dy
                    lastRawX = e2.rawX
                    lastRawY = e2.rawY
                    windowManager.updateViewLayout(contentBinding.root, layoutParams)

                    return true
                }
                return true
            }
        }

        val detector = GestureDetector(context, listener)

        contentBinding?.root?.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                lastRawX = -1f
                lastRawY = -1f
            }
            detector.onTouchEvent(event)
        }
    }

    fun destroy() {
        contentBinding?.let {
            windowManager.removeView(it.root)
            contentBinding = null
        }
    }

    fun showToolbar() {
        contentBinding ?: init()

        contentBinding?.let { contentBinding ->
            contentBinding.root.measure(View.MeasureSpec.AT_MOST, View.MeasureSpec.AT_MOST)
            val layoutParams = WindowManager.LayoutParams().apply {
                type = getWindowLayoutParamsType()
                format = PixelFormat.RGBA_8888
                flags = flags or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_FULLSCREEN or
                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN

                width = WindowManager.LayoutParams.WRAP_CONTENT
                height = WindowManager.LayoutParams.WRAP_CONTENT
                gravity = Gravity.TOP or Gravity.START

                var height = contentBinding.root.height
                if (height == 0)
                    height = 150
                x = 100
                y = display.height - 100 - height
            }
            windowManager.addView(contentBinding.root, layoutParams)
        }
    }

    private fun getWindowLayoutParamsType(): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
        }
    }
}