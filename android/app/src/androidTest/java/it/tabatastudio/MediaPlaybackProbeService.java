// DOC L0001: Assegna il file al namespace it.tabatastudio, distinto dai package UI, dominio, dati e servizio.
package it.tabatastudio;

// DOC L0003: Rende disponibili le dipendenze esplicite di questa riga: import android.app.*;. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import android.app.*;
// DOC L0004: Rende disponibili le dipendenze esplicite di questa riga: import android.content.Intent;. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import android.content.Intent;
// DOC L0005: Rende disponibili le dipendenze esplicite di questa riga: import android.content.pm.ServiceInfo;. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import android.content.pm.ServiceInfo;
// DOC L0006: Rende disponibili le dipendenze esplicite di questa riga: import android.content.res.AssetFileDescriptor;. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import android.content.res.AssetFileDescriptor;
// DOC L0007: Rende disponibili le dipendenze esplicite di questa riga: import android.media.*;. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import android.media.*;
// DOC L0008: Rende disponibili le dipendenze esplicite di questa riga: import android.os.*;. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import android.os.*;
// DOC L0009: Rende disponibili le dipendenze esplicite di questa riga: import java.util.ArrayList;. Il modulo chiamante resta responsabile del loro uso nel flusso descritto sopra.
import java.util.ArrayList;

/** Framework-only so it can run in the test APK's own process and UID. */
// DOC L0012: Dichiara il tipo o contenitore di questo modulo: Prove di regressione in MediaPlaybackProbeService.java. I campi e i metodi sono descritti nelle righe successive.
public class MediaPlaybackProbeService extends Service {
    // DOC L0013: Esegue il passaggio esplicito «private final ArrayList<Integer> focusChanges = new ArrayList<>();» nel flusso: Prove di regressione in MediaPlaybackProbeService.java.
    private final ArrayList<Integer> focusChanges = new ArrayList<>();
    // DOC L0014: Esegue il passaggio esplicito «private MediaPlayer player;» nel flusso: Prove di regressione in MediaPlaybackProbeService.java.
    private MediaPlayer player;
    // DOC L0015: Esegue il passaggio esplicito «private AudioManager audio;» nel flusso: Prove di regressione in MediaPlaybackProbeService.java.
    private AudioManager audio;
    // DOC L0016: Esegue il passaggio esplicito «private AudioFocusRequest focus;» nel flusso: Prove di regressione in MediaPlaybackProbeService.java.
    private AudioFocusRequest focus;
    // DOC L0017: Esegue il passaggio esplicito «private int focusResult;» nel flusso: Prove di regressione in MediaPlaybackProbeService.java.
    private int focusResult;
    // DOC L0018: onBind: Prepara o verifica il caso di regressione «on Bind»; le asserzioni nel corpo specificano l’esito atteso.
    public IBinder onBind(Intent intent) { return null; }
    // DOC L0019: onCreate: Prepara o verifica il caso di regressione «on Create»; le asserzioni nel corpo specificano l’esito atteso.
    public void onCreate() {
        // DOC L0020: Esegue il passaggio esplicito «super.onCreate();» nel flusso: Prove di regressione in MediaPlaybackProbeService.java.
        super.onCreate();
        // DOC L0021: Esegue il passaggio esplicito «NotificationManager manager = getSystemService(NotificationManager.class);» nel flusso: Prove di regressione in MediaPlaybackProbeService.java.
        NotificationManager manager = getSystemService(NotificationManager.class);
        // DOC L0022: Esegue il passaggio esplicito «NotificationChannel channel = new NotificationChannel("test-media", "Local media test", NotificationManager.IMPORTANCE_LOW);» nel flusso: Prove di regressione in MediaPlaybackProbeService.java.
        NotificationChannel channel = new NotificationChannel("test-media", "Local media test", NotificationManager.IMPORTANCE_LOW);
        // DOC L0023: Esegue il passaggio esplicito «channel.setSound(null, null); manager.createNotificationChannel(channel);» nel flusso: Prove di regressione in MediaPlaybackProbeService.java.
        channel.setSound(null, null); manager.createNotificationChannel(channel);
        // DOC L0024: Esegue il passaggio esplicito «Notification notification = new Notification.Builder(this, "test-media").setSmallIcon(android.R.drawable.ic_media_play).setContentTitle("Local media test").setOngoing(true).build()» nel flusso: Prove di regressione in MediaPlaybackProbeService.java.
        Notification notification = new Notification.Builder(this, "test-media").setSmallIcon(android.R.drawable.ic_media_play).setContentTitle("Local media test").setOngoing(true).build();
        // DOC L0025: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato.
        if (Build.VERSION.SDK_INT >= 29) startForeground(72, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK);
        // DOC L0026: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato.
        else startForeground(72, notification);
        // DOC L0027: Esegue il passaggio esplicito «AudioAttributes attributes = new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_MEDIA).setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build();» nel flusso: Prove di regressione in MediaPlaybackProbeService.java.
        AudioAttributes attributes = new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_MEDIA).setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build();
        // DOC L0028: Esegue il passaggio esplicito «audio = getSystemService(AudioManager.class);» nel flusso: Prove di regressione in MediaPlaybackProbeService.java.
        audio = getSystemService(AudioManager.class);
        // DOC L0029: Esegue il passaggio esplicito «focus = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN).setAudioAttributes(attributes).setOnAudioFocusChangeListener(focusChanges::add).build();» nel flusso: Prove di regressione in MediaPlaybackProbeService.java.
        focus = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN).setAudioAttributes(attributes).setOnAudioFocusChangeListener(focusChanges::add).build();
        // DOC L0030: Esegue il passaggio esplicito «focusResult = audio.requestAudioFocus(focus);» nel flusso: Prove di regressione in MediaPlaybackProbeService.java.
        focusResult = audio.requestAudioFocus(focus);
        // DOC L0031: Esegue il passaggio esplicito «try (AssetFileDescriptor file = getResources().openRawResourceFd(getResources().getIdentifier("media_probe", "raw", getPackageName()))) {» nel flusso: Prove di regressione in MediaPlaybackProbeService.java.
        try (AssetFileDescriptor file = getResources().openRawResourceFd(getResources().getIdentifier("media_probe", "raw", getPackageName()))) {
            // DOC L0032: Esegue il passaggio esplicito «player = new MediaPlayer(); player.setAudioAttributes(attributes);» nel flusso: Prove di regressione in MediaPlaybackProbeService.java.
            player = new MediaPlayer(); player.setAudioAttributes(attributes);
            // DOC L0033: Esegue il passaggio esplicito «player.setDataSource(file.getFileDescriptor(), file.getStartOffset(), file.getLength());» nel flusso: Prove di regressione in MediaPlaybackProbeService.java.
            player.setDataSource(file.getFileDescriptor(), file.getStartOffset(), file.getLength());
            // DOC L0034: Esegue il passaggio esplicito «player.setLooping(true); player.setVolume(.03f, .03f); player.prepare(); player.start();» nel flusso: Prove di regressione in MediaPlaybackProbeService.java.
            player.setLooping(true); player.setVolume(.03f, .03f); player.prepare(); player.start();
        // DOC L0035: Interrompe il percorso non valido con l’errore indicato, invece di applicare dati incoerenti.
        } catch (Exception e) { throw new IllegalStateException(e); }
    // DOC L0036: Chiude il costrutto aperto alla riga 19.
    }
    // DOC L0037: onStartCommand: Prepara o verifica il caso di regressione «on Start Command»; le asserzioni nel corpo specificano l’esito atteso.
    public int onStartCommand(Intent intent, int flags, int startId) {
        // DOC L0038: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato. Restituisce il valore calcolato o conclude il percorso della funzione.
        if (intent != null && "STOP".equals(intent.getAction())) { stopSelf(); return START_NOT_STICKY; }
        // DOC L0039: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato.
        if (intent != null) {
            // DOC L0040: Esegue il passaggio esplicito «ResultReceiver receiver = intent.getParcelableExtra("receiver");» nel flusso: Prove di regressione in MediaPlaybackProbeService.java.
            ResultReceiver receiver = intent.getParcelableExtra("receiver");
            // DOC L0041: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato.
            if (receiver != null) {
                // DOC L0042: Scrive la preferenza o la copia locale prevista, senza inviarla a un server.
                Bundle result = new Bundle(); result.putBoolean("playing", player.isPlaying()); result.putInt("focus", focusResult); result.putInt("uid", android.os.Process.myUid());
                // DOC L0043: Esegue il passaggio esplicito «result.putIntArray("changes", focusChanges.stream().mapToInt(Integer::intValue).toArray()); receiver.send(0, result);» nel flusso: Prove di regressione in MediaPlaybackProbeService.java.
                result.putIntArray("changes", focusChanges.stream().mapToInt(Integer::intValue).toArray()); receiver.send(0, result);
            // DOC L0044: Chiude il costrutto aperto alla riga 41.
            }
        // DOC L0045: Chiude il costrutto aperto alla riga 39.
        }
        // DOC L0046: Restituisce il valore calcolato o conclude il percorso della funzione.
        return START_NOT_STICKY;
    // DOC L0047: Chiude il costrutto aperto alla riga 37.
    }
    // DOC L0048: onDestroy: Prepara o verifica il caso di regressione «on Destroy»; le asserzioni nel corpo specificano l’esito atteso.
    public void onDestroy() {
        // DOC L0049: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato.
        if (player != null) player.release();
        // DOC L0050: Seleziona il ramo corrispondente alla condizione riportata, gestendo esplicitamente il caso alternativo o il ritorno anticipato.
        if (focus != null) audio.abandonAudioFocusRequest(focus);
        // DOC L0051: Esegue il passaggio esplicito «super.onDestroy();» nel flusso: Prove di regressione in MediaPlaybackProbeService.java.
        super.onDestroy();
    // DOC L0052: Chiude il costrutto aperto alla riga 48.
    }
// DOC L0053: Chiude il costrutto aperto alla riga 12.
}
