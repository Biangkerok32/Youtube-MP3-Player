package com.example.daron.youtubemp3player;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.MediaController;


import java.io.IOException;


public class PlayerFragment extends Fragment implements MediaController.MediaPlayerControl, MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener {

    private MediaPlayer mediaPlayer;
    private MediaController controller;
    String url;
    private Handler handler = new Handler();


    // The API I am using doesn't allow you to get the duration of the file being streamed
    public void onPrepared(MediaPlayer mediaPlayer) {
        controller.setMediaPlayer(this);
        if (getActivity() != null)
            controller.setAnchorView(getActivity().findViewById(android.R.id.content));

        handler.post(new Runnable() {
            public void run() {
                controller.setEnabled(true);
                controller.show(0);
                //controller.requestFocus();

            }
        });
    }


    public void loadMedia(String url, Context context) {
        if (mediaPlayer == null) {
            this.url = url;
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.setOnErrorListener(this);
            controller = new MediaController(context) {
                @Override
                public void hide() {
                }
            };
            try {
                mediaPlayer.setDataSource(url);
                mediaPlayer.prepare();
                mediaPlayer.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void start() {
        mediaPlayer.start();
    }

    @Override
    public void pause() {
        mediaPlayer.pause();
    }

    @Override
    public int getDuration() {
        return mediaPlayer.getDuration();
    }

    @Override
    public int getCurrentPosition() {
        try {
            return mediaPlayer.getCurrentPosition();
        } catch (Exception ex) {
            return 0;
        }
    }

    @Override
    public void seekTo(int pos) {
        mediaPlayer.seekTo(pos);
    }

    @Override
    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.e("onError triggered", "Code: " + String.valueOf(what) + " extra: " + String.valueOf(extra));
        mp.reset();
        return false;
    }

    @Override
    public void onDestroy() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
        super.onDestroy();
    }
}
