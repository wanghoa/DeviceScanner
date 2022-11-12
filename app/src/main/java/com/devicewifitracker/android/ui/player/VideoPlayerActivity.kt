package com.devicewifitracker.android.ui.player

//import com.devicewifitracker.android.R
import android.content.Context
import android.content.Intent
import android.content.res.AssetFileDescriptor
import android.media.MediaPlayer
import android.media.MediaPlayer.OnCompletionListener
import android.media.MediaPlayer.OnPreparedListener
import android.net.Uri
import android.os.Bundle
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.blankj.utilcode.util.BarUtils
import com.blankj.utilcode.util.ConvertUtils
import com.devicewifitracker.android.App
import com.devicewifitracker.android.R
import com.devicewifitracker.android.base.BaseActivity
import com.devicewifitracker.android.databinding.ActivityVideoPlayerBinding
import java.io.IOException


class VideoPlayerActivity :BaseActivity<ActivityVideoPlayerBinding>() {
    private val mVideoPlaySurfaceview: SurfaceView? = null
//    private val mStartAndStop: ImageView? = null
    private var mMediaPlayer: MediaPlayer? = null
    private val mPath: String? = null
    private var isInitFinish = false
    private var mSurfaceHolder: SurfaceHolder? = null
    companion object{
        fun actionOpenAct(context: Context,url :Int) {
            val intent = Intent(context,VideoPlayerActivity::class.java).apply {
                putExtra("url",url)
            }
            context.startActivity(intent)

        }
    }

    override fun getLayoutId(): Int {
        return com.devicewifitracker.android.R.layout.activity_video_player
    }

    override fun initView(savedInstanceState: Bundle?) {
        val barHeight = BarUtils.getStatusBarHeight()//获取状态栏高度
        binding?.rootView?.setPadding(0, ConvertUtils.dp2px(55.toFloat()) - barHeight, 0, 0)
        super.initView(savedInstanceState)
        val url = intent.getIntExtra("url",R.raw.video_chuanganqi)
       val afd =  getRawFilePath(App.context,url)

        initMediaPalyer();
        initSurfaceviewStateListener(afd);
        binding.back.setOnClickListener {
            finish()
        }
      // 使用videoview 播放视频
//        setupVideo();

    }

    /**
     * 获取raw目录下的资源
     *
     * @param resId 资源id
     */

    fun getRawFilePath(context:Context,resId:Int):AssetFileDescriptor{
        return  resources.openRawResourceFd(resId)
    }
    private fun initSurfaceviewStateListener(afd: AssetFileDescriptor) {
        mSurfaceHolder =   binding.videoPlaySurfaceview.getHolder()
        mSurfaceHolder?.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                mMediaPlayer?.setDisplay(holder) //给mMediaPlayer添加预览的SurfaceHolder
//                setPlayVideo(mPath) //添加播放视频的路径
                setPlayVideo(afd) //添加播放视频的路径
            }

            override fun surfaceChanged(
                holder: SurfaceHolder,
                format: Int,
                width: Int,
                height: Int
            ) {
//                Log.e(TAG, "surfaceChanged触发: width=" + width + "height" + height)
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {}
        })
    }

    private fun initMediaPalyer() {
        mMediaPlayer = MediaPlayer()
    }

    private fun setPlayVideo(afd: AssetFileDescriptor) {
        try {
            mMediaPlayer?.setDataSource(afd) //
            mMediaPlayer?.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT) //缩放模式
            mMediaPlayer?.setLooping(true) //设置循环播放
            mMediaPlayer?.prepareAsync() //异步准备
            //            mMediaPlayer.prepare();//同步准备,因为是同步在一些性能较差的设备上会导致UI卡顿
            mMediaPlayer?.setOnPreparedListener(OnPreparedListener { mp ->

                //准备完成回调
                isInitFinish = true
                mp.start()
            })
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun startPlay() {
        if (!mMediaPlayer?.isPlaying()!!) {
            mMediaPlayer?.start()
        }
    }

    private fun stopPlay() {
        if (mMediaPlayer?.isPlaying()!!) {
            mMediaPlayer?.stop()
        }
    }

    private fun pausePlay() {
        if (mMediaPlayer?.isPlaying()!!) {
            mMediaPlayer?.pause()
        }
    }

    private fun seekTo(time: Int) {
        mMediaPlayer?.seekTo(time)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mMediaPlayer != null) {
            if (mMediaPlayer?.isPlaying()!!) {
                mMediaPlayer?.stop()
            }
            mMediaPlayer?.release()
            mMediaPlayer = null
        }
    }

    // 使用videoview 播放视频
    private fun setupVideo() {
        binding.videoView.setOnPreparedListener(OnPreparedListener { binding.videoView.start() })
        binding.videoView.setOnCompletionListener(OnCompletionListener { stopPlaybackVideo() })
        binding.videoView.setOnErrorListener(MediaPlayer.OnErrorListener { mp, what, extra ->
            stopPlaybackVideo()
            true
        })
        try {
            val uri = Uri.parse("android.resource://" + packageName + "/raw/" + R.raw.video_chuanganqi)
            binding.videoView.setVideoURI(uri)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onResume() {
        super.onResume()
        if (!binding.videoView.isPlaying()) {
            binding.videoView.resume()
        }
    }

    override fun onPause() {
        super.onPause()
        if (binding.videoView.canPause()) {
            binding.videoView.pause()
        }
    }
    private fun stopPlaybackVideo() {
        try {
            binding.videoView.stopPlayback()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

}