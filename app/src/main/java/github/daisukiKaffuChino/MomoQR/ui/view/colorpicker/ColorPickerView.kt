package github.daisukiKaffuChino.MomoQR.ui.view.colorpicker

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.annotation.ColorInt
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.core.view.doOnLayout
import github.daisukiKaffuChino.MomoQR.R

class ColorPickerView : RelativeLayout {

    companion object {
        const val defaultColor = Color.RED
        val defaultColorModel = ColorModel.HSV
        const val defaultColorModelSwitch = true
        const val defaultActionOk = android.R.string.ok
        const val defaultActionCancel = android.R.string.cancel
    }

    @ColorInt
    var currentColor: Int
        private set

    var colorModel: ColorModel
        private set

    var colorModelSwitchEnabled: Boolean
        private set


    @StringRes
    var actionOkRes: Int
        private set

    @StringRes
    var actionCancelRes: Int
        private set

    var onSwitchColorModelListener: OnSwitchColorModelListener? = null

    constructor(context: Context) : this(
        context,
        defaultActionOk,
        defaultActionCancel,
        defaultColor,
        defaultColorModel,
        defaultColorModelSwitch,
    )

    constructor(
        context: Context,
        actionOkRes: Int,
        actionCancelRes: Int,
        @ColorInt initialColor: Int = Color.DKGRAY,
        colorModel: ColorModel,
        colorModelSwitchEnabled: Boolean,
        onSwitchColorModelListener: OnSwitchColorModelListener? = null,
    ) : super(context) {
        this.actionOkRes = actionOkRes
        this.actionCancelRes = actionCancelRes
        this.currentColor = initialColor
        this.colorModel = colorModel
        this.colorModelSwitchEnabled = colorModelSwitchEnabled
        this.onSwitchColorModelListener = onSwitchColorModelListener
        init()
    }

    private fun init() {
        inflate(context, R.layout.color_picker_view, this)
        clipToPadding = false

        val colorView: View = findViewById(R.id.color_view)
        colorView.setBackgroundColor(currentColor)


        val textColor = context.themeColor(android.R.attr.textColorSecondary)
        val thumbColor = ColorUtils.compositeColors(
            context.themeColor(android.R.attr.textColorPrimary),
            Color.GRAY
        )
        val rippleColor = context.themeColor(android.R.attr.colorControlHighlight)

        var channelViews = colorModel.channels.map {
            ChannelView(context, it, currentColor, textColor, thumbColor, rippleColor)
        }

        val seekbarChangeListener: () -> Unit = {
            currentColor = colorModel.evaluateColor(channelViews.map { it.channel })
            colorView.background = ColorDrawable(currentColor)

            when (colorModel) {
                ColorModel.HSV -> {
                    channelViews.forEach {
                        it.setTintHSV(
                            channelViews[0].channel.progress,
                            channelViews[1].channel.progress,
                            channelViews[2].channel.progress
                        )
                    }
                }
                ColorModel.RGB -> {
                    channelViews.forEach {
                        it.setTintRGB(
                            channelViews[0].channel.progress,
                            channelViews[1].channel.progress,
                            channelViews[2].channel.progress
                        )
                    }
                }
                ColorModel.AHSV -> {
                    channelViews.forEach {
                        it.setTintHSV(
                            channelViews[1].channel.progress,
                            channelViews[2].channel.progress,
                            channelViews[3].channel.progress
                        )
                    }
                }
                ColorModel.ARGB -> {
                    channelViews.forEach {
                        it.setTintRGB(
                            channelViews[1].channel.progress,
                            channelViews[2].channel.progress,
                            channelViews[3].channel.progress
                        )
                    }
                }
            }
        }

        val channelContainer = findViewById<ViewGroup>(R.id.channel_container)
        channelViews.forEach {
            val lp = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT,
                1f
            )
            channelContainer.addView(it, lp)
            it.registerListener(seekbarChangeListener)
        }

        if (colorModelSwitchEnabled) {
            doOnLayout {
                val root = findViewById<RelativeLayout>(R.id.color_picker_view)
                val view = View(context).apply {
                    background = ContextCompat.getDrawable(context, R.drawable.selectable_item_background_rounded)
                    isFocusable = true
                    isClickable = true
                    onClick {
                        colorModel = when (colorModel) {
                            ColorModel.HSV -> {
                                ColorModel.RGB
                            }
                            ColorModel.RGB -> {
                                ColorModel.HSV
                            }
                            ColorModel.ARGB -> {
                                ColorModel.AHSV
                            }
                            ColorModel.AHSV -> {
                                ColorModel.ARGB
                            }
                        }

                        channelViews.forEach { it.removeSelf() }
                        channelViews = colorModel.channels.map {
                            ChannelView(
                                context,
                                it,
                                currentColor,
                                textColor,
                                thumbColor,
                                rippleColor,
                            )
                        }
                        channelViews.forEach {
                            val lp = LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                1f
                            )
                            channelContainer.addView(it, lp)
                            it.registerListener(seekbarChangeListener)
                        }

                        onSwitchColorModelListener?.onColorModelSwitched(colorModel)
                    }
                }

                with(channelViews[0].findViewById<View>(R.id.label)) {
                    val params = LayoutParams(width, height * channelViews.size).apply {
                        leftMargin = left
                        topMargin = top
                        addRule(ALIGN_TOP, R.id.channel_container)
                        addRule(ALIGN_START, R.id.channel_container)
                    }
                    root.addView(view, params)
                }
            }
        }
    }

    internal interface ButtonBarListener {
        fun onPositiveButtonClick(color: Int)
        fun onNegativeButtonClick()
    }

    internal fun enableButtonBar(listener: ButtonBarListener) {
        with(findViewById<LinearLayout>(R.id.button_bar)) {
            val positiveButton = findViewById<Button>(R.id.positive_button).apply {
                setText(actionOkRes)
            }
            val negativeButton = findViewById<Button>(R.id.negative_button).apply {
                setText(actionCancelRes)
            }

            visibility = VISIBLE
            positiveButton.setOnClickListener { listener.onPositiveButtonClick(currentColor) }
            negativeButton.setOnClickListener { listener.onNegativeButtonClick() }
        }
    }
}
