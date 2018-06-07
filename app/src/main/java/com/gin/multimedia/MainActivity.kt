package com.gin.multimedia

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.gin.multimedia.audio.view.*
import com.gin.multimedia.databinding.ActivityMainBinding
import com.gin.multimedia.video.CameraPreviewActivity
import com.gin.multimedia.video.CameraPreviewActivity2

class MainActivity : AppCompatActivity() {

    private val data =
            mapOf(Pair("AudioRecord", AudioRecordActivity::class.java),
                    Pair("AudioTrack", AudioPlayerActivity::class.java),
                    Pair("WaveRecord", WaveRecordActivity::class.java),
                    Pair("AmrEncoder", MediaEncoderActivity::class.java),
                    Pair("AmrDecoder", AmrPlayerActivity::class.java),
                    Pair("CameraPreview(SurfaceView)", CameraPreviewActivity::class.java),
                    Pair("CameraPreview(TextureView)", CameraPreviewActivity2::class.java)
                    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        binding.data = data
        binding.context = this
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    external fun stringFromJNI(): String

    companion object {

        // Used to load the 'native-lib' library on application startup.
        init {
            System.loadLibrary("native-lib")
        }
    }
}
