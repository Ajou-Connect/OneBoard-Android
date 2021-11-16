package kr.khs.oneboard.views

import android.content.Context
import android.content.res.Configuration
import android.graphics.Rect
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.LinearLayout
import kr.khs.oneboard.R
import timber.log.Timber
import us.zoom.sdk.ZoomVideoSDK

class KeyBoardLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {
    interface KeyBoardListener {
        fun onKeyBoardChange(isShow: Boolean, height: Int, inputHeight: Int)
    }

    private lateinit var listener: KeyBoardListener
    lateinit var inputText: EditText
    private var mHeight = 0
    private var keyboardHeight = 0
    private var isKeyBoardShow = false
    var scale = 0f
    private lateinit var chatInputGroup: View
    private lateinit var btnSend: View

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        init()
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        mHeight = bottom - top
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        dismissChat(true)
        updateLayout()
    }

    fun init() {
        inputText = findViewById(R.id.chat_input)
        chatInputGroup = findViewById(R.id.chat_input_group)
        btnSend = findViewById(R.id.btn_send)
//        val display =
//            (context.getSystemService(Service.WINDOW_SERVICE) as WindowManager).defaultDisplay
        val display = this.display
        val metrics = DisplayMetrics()
        display.getRealMetrics(metrics)
//        display.getMetrics(metrics)

        scale = metrics.density
        viewTreeObserver.addOnGlobalLayoutListener(layoutListener)
        inputText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                if (s.isNotEmpty()) {
                    if (btnSend.visibility != VISIBLE) {
                        btnSend.visibility = VISIBLE
                    }
                } else {
                    if (btnSend.visibility == VISIBLE) {
                        btnSend.visibility = GONE
                    }
                }
            }
        })
        btnSend.setOnClickListener {
            val content = inputText.getText().toString().trim { it <= ' ' }
            ZoomVideoSDK.getInstance().chatHelper.sendChatToAll(content)
            inputText.setText("")
        }
    }

    var layoutListener: OnGlobalLayoutListener = object : OnGlobalLayoutListener {
        private var wasOpened = false
        override fun onGlobalLayout() {
            val height = height
            val r = Rect()
            getWindowVisibleDisplayFrame(r)
            val heightDiff = height - r.height()
            val isOpen = heightDiff > height * KEYBOARD_MIN_HEIGHT_RATIO
            if (isOpen == wasOpened) {
                // keyboard state has not changed
                return
            }
            wasOpened = isOpen
            if (isOpen) {
                keyboardHeight = heightDiff
                onKeyboardShow(heightDiff)
            } else {
                onKeyboardHidden()
            }
            //            Log.w("KeyBoardLayout", String.format("keyboard height: %d  height:%d heightDiff=%d", keyboardHeight, height, heightDiff));
        }
    }

    fun setKeyBoardListener(listener: KeyBoardListener) {
        this.listener = listener
    }

    private fun onKeyboardShow(height: Int) {
        if (height > 0) {
            keyboardHeight = height
            updateLayout()
        }
        keyboardHeight = height
        listener.onKeyBoardChange(true, keyboardHeight, inputText.height)
        isKeyBoardShow = true
        Timber.w("onKeyboardShow:$height:$keyboardHeight")
    }

    private fun onKeyboardHidden() {
        visibility = INVISIBLE
        listener.onKeyBoardChange(false, keyboardHeight, inputText.height)
        isKeyBoardShow = false
        //        getViewTreeObserver().removeGlobalOnLayoutListener(layoutListener);
        Timber.w("onKeyboardHidden:$keyboardHeight")
    }

    private fun updateLayout() {
        val orientation = context.resources.configuration.orientation
        val params: LayoutParams
        var inputHeight = inputText.height
        if (inputHeight <= 0) {
            inputHeight = (36 * scale).toInt()
        }
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            params = chatInputGroup.layoutParams as LayoutParams
            val height = if (height > width) width else height
            params.topMargin = height - inputHeight - (12 * scale).toInt()
        } else {
            params = chatInputGroup.layoutParams as LayoutParams
            val height = if (height > width) height else width
            if (keyboardHeight > 0) {
                params.topMargin = height - keyboardHeight - inputHeight - (12 * scale).toInt()
            } else {
                params.topMargin = (12 * scale).toInt()
            }
        }
        chatInputGroup.layoutParams = params
        Timber.w("update layout params.topMargin: ${params.topMargin}, orientation: $orientation, height: $height, width: $width")
//        Log.w(
//            "KeyBoardLayout",
//            String.format(
//                "updateLayout params.topMargin : %d ",
//                params.topMargin
//            ) + " orientation:" + orientation
//                    + "  height:" + height + " width：" + width
//        )
    }

    fun showChat() {
        inputText.alpha = 0.0f
        postDelayed({ inputText.alpha = 1.0f }, 380)
        viewTreeObserver.addOnGlobalLayoutListener(layoutListener)
        visibility = VISIBLE
        inputText.requestFocus()
        val inputMethod =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethod.showSoftInput(inputText, InputMethodManager.SHOW_FORCED)
    }

    fun isKeyBoardShow(): Boolean {
        return visibility == VISIBLE
    }

    fun dismissChat(forceHidden: Boolean) {
        val inputMethod =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethod.hideSoftInputFromWindow(inputText.windowToken, 0)
        if (forceHidden) {
            postDelayed({ visibility = INVISIBLE }, 380)
        }
    }

    companion object {
        private const val KEYBOARD_MIN_HEIGHT_RATIO = 0.15
    }
}
