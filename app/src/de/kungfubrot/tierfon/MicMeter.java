package de.kungfubrot.tierfon;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

/** Misst nur die Lautstärke am Mikrofon. Es wird nichts aufgenommen oder gespeichert. */
final class MicMeter extends Thread {
    private static final int RATE = 16000;

    /** Solange das Tier selbst spricht, wird das Mikrofon ignoriert (sonst hört es sich selbst). */
    volatile boolean paused;
    private volatile boolean running = true;
    private volatile float level;
    private volatile float floor = 0.02f;

    boolean isVoice() {
        return !paused && level > Math.max(0.04f, floor * 2.5f);
    }

    void shutdown() {
        running = false;
    }

    @Override
    public void run() {
        int min = AudioRecord.getMinBufferSize(RATE, AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT);
        AudioRecord rec;
        try {
            rec = new AudioRecord(MediaRecorder.AudioSource.MIC, RATE,
                AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, Math.max(min, 3200));
        } catch (RuntimeException e) {
            return;
        }
        if (rec.getState() != AudioRecord.STATE_INITIALIZED) {
            rec.release();
            return;
        }
        rec.startRecording();
        short[] buf = new short[800];  // 50 Millisekunden
        while (running) {
            int n = rec.read(buf, 0, buf.length);
            if (n <= 0) continue;
            if (paused) {
                level = 0;
                continue;
            }
            int peak = 0;
            for (int i = 0; i < n; i++) peak = Math.max(peak, Math.abs(buf[i]));
            float l = level + (peak / 32768f - level) * 0.6f;
            level = l;
            // Grundrauschen langsam mitlernen, damit es auch in lauter Umgebung funktioniert.
            float speed = l < floor * 2.5f ? 0.025f : 0.0025f;
            floor = Math.max(0.005f, Math.min(0.3f, floor + (l - floor) * speed));
        }
        rec.stop();
        rec.release();
    }
}
