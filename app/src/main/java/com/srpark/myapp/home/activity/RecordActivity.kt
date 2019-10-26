package com.srpark.myapp.home.activity

import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.jakewharton.rxbinding2.view.RxView
import com.srpark.myapp.R
import com.srpark.myapp.base.BaseActivity
import com.srpark.myapp.databinding.ActivityRecordBinding
import com.srpark.myapp.utils.ActivityConstant.INTENT_RECORD_DATA
import io.reactivex.android.schedulers.AndroidSchedulers
import java.util.*
import java.util.concurrent.TimeUnit

class RecordActivity : BaseActivity<ActivityRecordBinding>(), RecognitionListener {

    override val layoutResourceId: Int
        get() = R.layout.activity_record

    private lateinit var recognizer: SpeechRecognizer

    private lateinit var recordMicAnim: Animation
    private lateinit var recordRotateAnim: Animation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding.tvSearch.text = getString(R.string.search_record_ready)
        recordMicAnim = AnimationUtils.loadAnimation(this, R.anim.record_mic)
        recordRotateAnim = AnimationUtils.loadAnimation(this, R.anim.rotation)
        recognizer = SpeechRecognizer.createSpeechRecognizer(this)
        recognizer.setRecognitionListener(this)
        startListening()
        addDisposable(RxView.clicks(viewBinding.ivMic)
            .throttleFirst(1, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
            .subscribe { startListening() }
        )
    }

    private fun startListening() {
        viewBinding.ivMic.isClickable = false
        val recognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, packageName)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
        }
        recognizer.startListening(recognizerIntent)
    }

    override fun onReadyForSpeech(params: Bundle?) { // 음성 인식 준비 완료
        viewBinding.tvSearch.text = getString(R.string.search_record)
    }

    override fun onBeginningOfSpeech() { // 사용자가 말하기 시작할 때
        viewBinding.ivRotate.visibility = View.VISIBLE
        viewBinding.ivRotate.startAnimation(recordRotateAnim)
        viewBinding.ivMic.startAnimation(recordMicAnim)
    }

    override fun onEndOfSpeech() { // 사용자의 말이 끝났을 때
        viewBinding.ivRotate.visibility = View.GONE
        viewBinding.ivRotate.clearAnimation()
        viewBinding.ivMic.clearAnimation()
        recognizer.stopListening()
    }

    override fun onError(error: Int) { // 오류가 발생했을 때
        viewBinding.tvSearch.text = getString(R.string.search_record_fail)
        viewBinding.ivMic.isClickable = true
    }

    override fun onResults(results: Bundle?) { // 결과 값을 받았을 때
        val key = SpeechRecognizer.RESULTS_RECOGNITION
        val result = results?.getStringArrayList(key)
        val dataStr = result?.get(0)?.trim()
        setResult(RESULT_OK, Intent().putExtra(INTENT_RECORD_DATA, dataStr))
        finish()
    }

    override fun onRmsChanged(rmsdB: Float) { // 음성의 RMS가 바뀌었을 때
    }

    override fun onBufferReceived(buffer: ByteArray?) { // 음성 데이터의 buffer를 받을 수 있다.
    }

    override fun onPartialResults(partialResults: Bundle?) {
    }

    override fun onEvent(eventType: Int, params: Bundle?) {
    }

    override fun onDestroy() {
        recognizer.destroy()
        super.onDestroy()
    }
}