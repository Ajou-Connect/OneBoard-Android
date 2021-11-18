package kr.khs.oneboard.views

import android.content.Context
import android.util.AttributeSet
import com.google.android.material.checkbox.MaterialCheckBox
import kr.khs.oneboard.R
import timber.log.Timber

class ThreeStateCheckBox @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : MaterialCheckBox(context, attrs, defStyleAttr) {
    private var textUnchecked: String? = null
    private var textIndeterminate: String? = null
    private var textChecked: String? = null

    companion object {
        const val STATE_UNCHECKED = 0
        const val STATE_INDETERMINATE = 1
        const val STATE_CHECKED = 2
    }

    init {
        attrs?.let {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ThreeStateCheckBox)

            state = typedArray.getInt(R.styleable.ThreeStateCheckBox_state, STATE_UNCHECKED)

            textUnchecked =
                typedArray.getString(R.styleable.ThreeStateCheckBox_text_state_unchecked)
            textIndeterminate =
                typedArray.getString(R.styleable.ThreeStateCheckBox_text_state_indeterminate)
            textChecked = typedArray.getString(R.styleable.ThreeStateCheckBox_text_state_checked)
            isClickable = typedArray.getBoolean(R.styleable.ThreeStateCheckBox_clickable, false)

            changeText(state)
            changeDrawable(state)

            Timber.d("state: $state")
            Timber.d("textUnChecked : $textUnchecked")
            Timber.d("textIndeterminate: $textIndeterminate")
            Timber.d("textChecked : $textChecked")

            typedArray.recycle()

            initComponent()
        }
    }

    var state: Int = STATE_UNCHECKED
        set(value) {
            if (field == value) return

            Timber.d("state changed : $field -> $value")
            field = value

            isChecked = when (value) {
                STATE_UNCHECKED -> false
                STATE_INDETERMINATE -> true
                STATE_CHECKED -> true
                else -> throw IllegalStateException("$value is not a valid state for ${this.javaClass.name}")
            }

            changeText(field)
            changeDrawable(field)

            refreshDrawableState()

            onStateChanged?.let { it(this, value) }
        }

    var onStateChanged: ((ThreeStateCheckBox, Int) -> Unit)? = null

    private fun initComponent() {
        Timber.d("initComponent()")
        setOnCheckedChangeListener { _, _ ->
            state = when (state) {
                STATE_UNCHECKED -> STATE_CHECKED
                STATE_CHECKED -> STATE_INDETERMINATE
                STATE_INDETERMINATE -> STATE_UNCHECKED
                else -> -1
            }
        }
    }

    private fun changeText(state: Int) {
        text = when (state) {
            STATE_UNCHECKED -> textUnchecked
            STATE_INDETERMINATE -> textIndeterminate
            STATE_CHECKED -> textChecked
            else -> ""
        }
    }

    private fun changeDrawable(state: Int) {
        setButtonDrawable(
            when (state) {
                STATE_UNCHECKED -> R.drawable.ic_attendance_f
                STATE_INDETERMINATE -> R.drawable.ic_attendance_b
                STATE_CHECKED -> R.drawable.ic_attendance_a
                else -> throw Exception("Unknown state")
            }
        )
    }
}