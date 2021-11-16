package kr.khs.oneboard.core.rawdata

import android.content.Context
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.util.AttributeSet
import us.zoom.rawdatarender.RawDataBufferType
import us.zoom.rawdatarender.ZoomSurfaceViewRender
import us.zoom.sdk.ZoomVideoSDKRawDataPipeDelegate
import us.zoom.sdk.ZoomVideoSDKUser
import us.zoom.sdk.ZoomVideoSDKVideoRawData
import us.zoom.sdk.ZoomVideoSDKVideoResolution

class RawDataRenderer @JvmOverloads constructor(
    @get:JvmName("getRawContext")
    private val context: Context,
    private val attrs: AttributeSet? = null
) : ZoomSurfaceViewRender(context, attrs), ZoomVideoSDKRawDataPipeDelegate {
    companion object {
        private var handlerThread: HandlerThread? = null
        private lateinit var handler: Handler
        const val VideoAspect_Full_Filled = 0
        const val VideoAspect_Original = 1
    }

    private var user: ZoomVideoSDKUser? = null
    private var isSubscribeShare = false

    init {
        setBufferType(RawDataBufferType.BYTE_ARRAY)
        initRender()
        startRender()

        if (handlerThread == null) {
            handlerThread = HandlerThread("RawDataRenderer")
            handlerThread!!.start()
            RawDataRenderer.handler = Handler(handlerThread!!.looper)
        }
    }

    protected override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        startRender()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        stopRender()
        user?.videoPipe?.unSubscribe(this)
    }

    override fun onRawDataStatusChanged(status: ZoomVideoSDKRawDataPipeDelegate.RawDataStatus?) {
        status ?: return
        if (status == ZoomVideoSDKRawDataPipeDelegate.RawDataStatus.RawData_Off) {
            clearImage(0.0f, 0.0f, 0.0f, 1.0f)
        }
    }

    override fun onRawDataFrameReceived(rawData: ZoomVideoSDKVideoRawData) {
        val isMainThread = Thread.currentThread() == Looper.getMainLooper().thread
        if (isMainThread && rawData.canAddRef()) {
            rawData.addRef()
            handler.post {
                drawI420YUV(
                    rawData.getyBuffer(), rawData.getuBuffer(), rawData.getvBuffer(),
                    rawData.streamWidth, rawData.streamHeight, rawData.rotation, 30
                )
            }
        }
    }

    fun subscribe(
        user: ZoomVideoSDKUser?,
        resolution: ZoomVideoSDKVideoResolution,
        isShare: Boolean
    ) {
        user ?: return

        this.user = user
        if (isShare) {
            user.sharePipe.subscribe(resolution, this)
        } else {
            user.videoPipe.subscribe(resolution, this)
        }

        isSubscribeShare = isShare
    }

    fun unSubscribe() {
        user?.let { user ->
            if (isSubscribeShare) {
                user.sharePipe.unSubscribe(this)
            } else {
                user.videoPipe.unSubscribe(this)
            }
        }
    }
}