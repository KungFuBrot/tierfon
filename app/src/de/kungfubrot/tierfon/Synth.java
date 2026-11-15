package de.kungfubrot.tierfon;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

import java.util.Random;

/** Erzeugt alle Töne rechnerisch. So braucht das Projekt keine fremden Tondateien. */
final class Synth {
    static final int RATE = 22050;

    private Synth() {}

    /** Ein fertig geladener Ton, der mit wechselnder Tonhöhe abgespielt werden kann. */
    static final class Clip {
        private final AudioTrack track;
        private final float seconds;

        Clip(short[] data) {
            track = new AudioTrack(AudioManager.STREAM_MUSIC, RATE,
                AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT,
                data.length * 2, AudioTrack.MODE_STATIC);
            track.write(data, 0, data.length);
            seconds = data.length / (float) RATE;
        }

        /** Spielt den Ton ab und liefert seine Dauer in Sekunden. */
        float play(float pitch) {
            try {
                if (track.getPlayState() != AudioTrack.PLAYSTATE_STOPPED) track.stop();
                track.reloadStaticData();
                track.setPlaybackRate((int) (RATE * pitch));
                track.play();
            } catch (IllegalStateException ignored) {
            }
            return seconds / pitch;
        }

        void stop() {
            try {
                track.stop();
            } catch (IllegalStateException ignored) {
            }
        }

        void release() {
            stop();
            track.release();
        }
    }

    private static float range(Random r, float a, float b) {
        return a + r.nextFloat() * (b - a);
    }

    /** Eine Frosch-Silbe: ein knarrender Ton, der leicht in der Höhe gleitet. */
    static short[] croak(Random r) {
        float dur = range(r, 0.16f, 0.38f);
        float f0 = range(r, 170f, 250f);
        float glide = range(r, -0.15f, 0.7f);
        float pulse = range(r, 26f, 42f);
        int n = (int) (dur * RATE);
        short[] s = new short[n];
        double ph = 0;
        for (int i = 0; i < n; i++) {
            double t = i / (double) RATE;
            double u = t / dur;
            ph += 2 * Math.PI * f0 * (1 + glide * u) / RATE;
            double env = Math.pow(Math.sin(Math.PI * u), 0.6);
            double am = 0.35 + 0.65 * Math.pow(0.5 + 0.5 * Math.sin(2 * Math.PI * pulse * t), 2);
            double w = Math.sin(ph) + 0.55 * Math.sin(2 * ph) + 0.35 * Math.sin(3 * ph)
                + 0.2 * Math.sin(5 * ph);
            s[i] = (short) (Math.max(-1, Math.min(1, 0.42 * env * am * w)) * 32767);
        }
        return s;
    }

    /** Klingelton: zwei kurze Triller. */
    static short[] ring() {
        int n = (int) (1.6 * RATE);
        short[] s = new short[n];
        for (int i = 0; i < n; i++) {
            double t = i / (double) RATE;
            boolean on = t < 0.55 || (t > 0.8 && t < 1.35);
            double f = (t * 14) % 1.0 < 0.5 ? 660 : 830;
            s[i] = on ? (short) (0.3 * Math.sin(2 * Math.PI * f * t) * 32767) : 0;
        }
        return s;
    }
}
