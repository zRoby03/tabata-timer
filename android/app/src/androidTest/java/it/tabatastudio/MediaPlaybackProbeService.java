package it.tabatastudio;

import android.app.*;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.content.res.AssetFileDescriptor;
import android.media.*;
import android.os.*;
import java.util.ArrayList;

/** Framework-only so it can run in the test APK's own process and UID. */
public class MediaPlaybackProbeService extends Service {
    private final ArrayList<Integer> focusChanges = new ArrayList<>();
    private MediaPlayer player;
    private AudioManager audio;
    private AudioFocusRequest focus;
    private int focusResult;
    public IBinder onBind(Intent intent) { return null; }
    public void onCreate() {
        super.onCreate();
        NotificationManager manager = getSystemService(NotificationManager.class);
        NotificationChannel channel = new NotificationChannel("test-media", "Local media test", NotificationManager.IMPORTANCE_LOW);
        channel.setSound(null, null); manager.createNotificationChannel(channel);
        Notification notification = new Notification.Builder(this, "test-media").setSmallIcon(android.R.drawable.ic_media_play).setContentTitle("Local media test").setOngoing(true).build();
        if (Build.VERSION.SDK_INT >= 29) startForeground(72, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK);
        else startForeground(72, notification);
        AudioAttributes attributes = new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_MEDIA).setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build();
        audio = getSystemService(AudioManager.class);
        focus = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN).setAudioAttributes(attributes).setOnAudioFocusChangeListener(focusChanges::add).build();
        focusResult = audio.requestAudioFocus(focus);
        try (AssetFileDescriptor file = getResources().openRawResourceFd(getResources().getIdentifier("media_probe", "raw", getPackageName()))) {
            player = new MediaPlayer(); player.setAudioAttributes(attributes);
            player.setDataSource(file.getFileDescriptor(), file.getStartOffset(), file.getLength());
            player.setLooping(true); player.setVolume(.03f, .03f); player.prepare(); player.start();
        } catch (Exception e) { throw new IllegalStateException(e); }
    }
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && "STOP".equals(intent.getAction())) { stopSelf(); return START_NOT_STICKY; }
        if (intent != null) {
            ResultReceiver receiver = intent.getParcelableExtra("receiver");
            if (receiver != null) {
                Bundle result = new Bundle(); result.putBoolean("playing", player.isPlaying()); result.putInt("focus", focusResult); result.putInt("uid", android.os.Process.myUid());
                result.putIntArray("changes", focusChanges.stream().mapToInt(Integer::intValue).toArray()); receiver.send(0, result);
            }
        }
        return START_NOT_STICKY;
    }
    public void onDestroy() {
        if (player != null) player.release();
        if (focus != null) audio.abandonAudioFocusRequest(focus);
        super.onDestroy();
    }
}
