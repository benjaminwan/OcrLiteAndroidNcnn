package com.benjaminwan.ocr.onnxtoncnn

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import com.afollestad.assent.Permission
import com.afollestad.assent.askForPermissions
import com.afollestad.assent.isAllGranted
import com.afollestad.assent.rationale.createDialogRationale
import com.benjaminwan.ocr.onnxtoncnn.dialog.DebugDialog
import com.benjaminwan.ocr.onnxtoncnn.dialog.TextResultDialog
import com.benjaminwan.ocr.onnxtoncnn.utils.showToast
import com.benjaminwan.ocrlibrary.OcrEngine
import com.benjaminwan.ocrlibrary.OcrResult
import com.bumptech.glide.Glide
import com.uber.autodispose.android.lifecycle.autoDisposable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_camera.*
import kotlin.math.max

class CameraActivity : AppCompatActivity(), View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    private lateinit var ocrEngine: OcrEngine
    private var ocrResult: OcrResult? = null

    private var preview: Preview? = null
    private var imageCapture: ImageCapture? = null
    private var camera: Camera? = null
    private lateinit var viewFinder: PreviewView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
        ocrEngine = OcrEngine(applicationContext)
        clearBtn.setOnClickListener(this)
        detectBtn.setOnClickListener(this)
        resultBtn.setOnClickListener(this)
        debugBtn.setOnClickListener(this)
        updatePadding(ocrEngine.padding)
        updateBoxScoreThresh((ocrEngine.boxScoreThresh * 100).toInt())
        updateBoxThresh((ocrEngine.boxThresh * 100).toInt())
        updateMinArea(ocrEngine.miniArea.toInt())
        updateScaleWidth((ocrEngine.scaleWidth * 10).toInt())
        updateScaleHeight((ocrEngine.scaleHeight * 10).toInt())
        paddingSeekBar.setOnSeekBarChangeListener(this)
        boxScoreThreshSeekBar.setOnSeekBarChangeListener(this)
        boxThreshSeekBar.setOnSeekBarChangeListener(this)
        minAreaSeekBar.setOnSeekBarChangeListener(this)
        scaleSeekBar.setOnSeekBarChangeListener(this)
        scaleWidthSeekBar.setOnSeekBarChangeListener(this)
        scaleHeightSeekBar.setOnSeekBarChangeListener(this)
        viewFinder = findViewById(R.id.viewFinder)
        cameraLensView.postDelayed({
            updateScale(100)
        }, 500)
    }

    override fun onResume() {
        super.onResume()
        val rationaleHandler = createDialogRationale(R.string.app_permission) {
            onPermission(
                Permission.CAMERA, "请点击允许"
            )
        }

        if (!isAllGranted(Permission.CAMERA)) {
            askForPermissions(
                Permission.CAMERA,
                rationaleHandler = rationaleHandler
            ) { result ->
                val permissionGranted: Boolean =
                    result.isAllGranted(
                        Permission.CAMERA
                    )
                if (!permissionGranted) {
                    showToast("未获取权限，应用无法正常使用！")
                } else {
                    startCamera()
                }
            }
        } else {
            startCamera()
        }
    }

    override fun onClick(view: View?) {
        view ?: return
        when (view.id) {
            R.id.clearBtn -> {
                clearLastResult()
            }
            R.id.detectBtn -> {
                val width = cameraLensView.measuredWidth * 9 / 10
                val height = cameraLensView.measuredHeight * 9 / 10
                val scale = scaleSeekBar.progress.toFloat() / 100.toFloat()
                val maxSize = max(width, height)
                val reSize = (scale * maxSize).toInt()
                detect(reSize)
            }
            R.id.resultBtn -> {
                val result = ocrResult ?: return
                TextResultDialog.instance
                    .setTitle("识别结果")
                    .setContent(result.strRes)
                    .show(supportFragmentManager, "TextResultDialog")
            }
            R.id.debugBtn -> {
                val result = ocrResult ?: return
                DebugDialog.instance
                    .setTitle("调试信息")
                    .setResult(result)
                    .show(supportFragmentManager, "DebugDialog")
            }
            else -> {
            }
        }
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        seekBar ?: return
        when (seekBar.id) {
            R.id.scaleSeekBar -> {
                updateScale(progress)
            }
            R.id.paddingSeekBar -> {
                updatePadding(progress)
            }
            R.id.boxScoreThreshSeekBar -> {
                updateBoxScoreThresh(progress)
            }
            R.id.boxThreshSeekBar -> {
                updateBoxThresh(progress)
            }
            R.id.minAreaSeekBar -> {
                updateMinArea(progress)
            }
            R.id.scaleWidthSeekBar -> {
                updateScaleWidth(progress)
            }
            R.id.scaleHeightSeekBar -> {
                updateScaleHeight(progress)
            }
            else -> {
            }
        }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {
    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
    }

    private fun updateScale(progress: Int) {
        val width = cameraLensView.measuredWidth * 9 / 10
        val height = cameraLensView.measuredHeight * 9 / 10
        val scale = progress.toFloat() / 100.toFloat()
        val maxSize = max(width, height)
        val reSize = (scale * maxSize).toInt()
        Log.i(TAG, "======$width,$height,$scale,$maxSize,$reSize")
        scaleTv.text = "Size:$reSize(${scale * 100}%)"
    }

    private fun updatePadding(progress: Int) {
        paddingTv.text = "Padding:$progress"
        ocrEngine.padding = progress
    }

    private fun updateBoxScoreThresh(progress: Int) {
        val thresh = progress.toFloat() / 100.toFloat()
        boxScoreThreshTv.text = "${getString(R.string.box_score_thresh)}:$thresh"
        ocrEngine.boxScoreThresh = thresh
    }

    private fun updateBoxThresh(progress: Int) {
        val thresh = progress.toFloat() / 100.toFloat()
        boxThreshTv.text = "BoxThresh:$thresh"
        ocrEngine.boxThresh = thresh
    }

    private fun updateMinArea(progress: Int) {
        minAreaTv.text = "${getString(R.string.min_area)}:$progress"
        ocrEngine.miniArea = progress.toFloat()
    }

    private fun updateScaleWidth(progress: Int) {
        val scale = progress.toFloat() / 10.toFloat()
        scaleWidthTv.text = "${getString(R.string.box_scale_width)}:$scale"
        ocrEngine.scaleWidth = scale
    }

    private fun updateScaleHeight(progress: Int) {
        val scale = progress.toFloat() / 10.toFloat()
        scaleHeightTv.text = "${getString(R.string.box_scale_height)}:$scale"
        ocrEngine.scaleHeight = scale
    }

    private fun showLoading() {
        loadingImg.visibility = View.VISIBLE
        Glide.with(this).load(R.drawable.loading_anim).into(loadingImg)
    }

    private fun hideLoading() {
        loadingImg.visibility = View.GONE
    }

    private fun clearLastResult() {
        cameraLensView.cameraLensBitmap = null
        timeTV.text = ""
        ocrResult = null
    }

    private fun detect(reSize: Int) {
        Single.fromCallable {
            val src = cameraLensView.cropCameraLensRectBitmap(viewFinder.bitmap, false)
            val boxImg: Bitmap = Bitmap.createBitmap(
                src.width, src.height, Bitmap.Config.ARGB_8888
            )
            Log.i(TAG, "selectedImg=${src.height},${src.width} ${src.config}")
            ocrEngine.detect(src, boxImg, reSize)
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { showLoading() }
            .doFinally { hideLoading() }
            .autoDisposable(this)
            .subscribe { t1, t2 ->
                ocrResult = t1
                timeTV.text = "识别时间:${t1.detectTime.toInt()}ms"
                cameraLensView.cameraLensBitmap = t1.boxImg
            }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener(Runnable {
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            preview = Preview.Builder()
                .setTargetAspectRatio(AspectRatio.RATIO_4_3)
                .build()

            val imageCaptureBuilder = ImageCapture.Builder()
            imageCaptureBuilder.setTargetAspectRatio(AspectRatio.RATIO_4_3)
            imageCapture = imageCaptureBuilder.build()

            // Select back camera
            val cameraSelector =
                CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build()

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)
                preview?.setSurfaceProvider(viewFinder.createSurfaceProvider())
            } catch (exc: Exception) {
                Log.e("Use case binding failed", exc.message.toString())
            }

        }, ContextCompat.getMainExecutor(this))
    }

    companion object {
        const val REQUEST_SELECT_IMAGE = 666
        const val TAG = "OcrLite"
    }


}