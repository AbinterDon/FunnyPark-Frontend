package com.example.administrator.funnypark.beacon_game.game;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Looper;

public class RecordThread extends Thread {
    private AudioRecord mAudioRecord;
    private Handler mHandler;
    private int bufferSize = 100;
    private ChangeState mCallback;

    private static final int BUFFER_SIZE = 8000;
    private static final int MIN_VALUE = 4000;

    public RecordThread(ChangeState callback) {
        mCallback = callback;
        Looper looper = Looper.getMainLooper();
        mHandler = new Handler(looper);
        bufferSize = AudioRecord.getMinBufferSize(
                BUFFER_SIZE,
                AudioFormat.CHANNEL_CONFIGURATION_MONO,
                AudioFormat.ENCODING_PCM_16BIT);
        mAudioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
                BUFFER_SIZE,
                AudioFormat.CHANNEL_CONFIGURATION_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                bufferSize);
    }



    @Override
    public void run() {
        // TODO Auto-generated method stub
        super.run();
        mAudioRecord.startRecording();
        byte[] buffer = new byte[bufferSize];
        while(Game05.isRunning) {
            try {
                sleep(10);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            int length = mAudioRecord.read(buffer, 0, bufferSize) + 1;
            int av = 0;
            for(int i = 0; i < buffer.length; i++) {
                av += (buffer[i] * buffer[i]);
            }
            av = Integer.valueOf(av / (int) length);
            if(av > MIN_VALUE) {
                //mHandler.sendEmptyMessage(1);
                mHandler.post(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        mCallback.change();
                    }
                });
            }

        }
        mAudioRecord.stop();
        mAudioRecord.release();
    }

    public interface ChangeState {
        public void change();
    }
}
