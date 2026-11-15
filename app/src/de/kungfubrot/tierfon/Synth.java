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

    /**
     * Allgemeiner Tierlaut. glide verschiebt die Tonhöhe über die Dauer; mit arch steigt sie
     * erst und fällt dann wieder. pulse ist ein schnelles An- und Abschwellen (Knarren, Trillern).
     */
    private static short[] tone(float dur, float f0, float glide, boolean arch, float pulse,
            float depth, double h2, double h3, double volume) {
        int n = (int) (dur * RATE);
        short[] s = new short[n];
        double ph = 0;
        for (int i = 0; i < n; i++) {
            double t = i / (double) RATE;
            double u = t / dur;
            double bend = arch ? Math.sin(Math.PI * u) : u;
            ph += 2 * Math.PI * f0 * (1 + glide * bend) / RATE;
            double env = Math.pow(Math.sin(Math.PI * u), 0.5);
            double am = 1 - depth + depth * (0.5 + 0.5 * Math.sin(2 * Math.PI * pulse * t));
            double w = Math.sin(ph) + h2 * Math.sin(2 * ph) + h3 * Math.sin(3 * ph);
            s[i] = (short) (Math.max(-1, Math.min(1, volume * env * am * w)) * 32767);
        }
        return s;
    }

    /** Igel: kurze, hohe Quiekser, die nach unten wegrutschen. */
    static short[] squeak(Random r) {
        return tone(range(r, 0.07f, 0.17f), range(r, 900f, 1300f), range(r, -0.35f, -0.1f), false,
            range(r, 40f, 60f), 0.25f, 0.3, 0.1, 0.42);
    }

    /** Brachiosaurus: ein tiefes, langes Brummen, das nach unten abfällt. */
    static short[] rumble(Random r) {
        return tone(range(r, 0.35f, 0.7f), range(r, 150f, 210f), range(r, -0.25f, -0.05f), false,
            range(r, 14f, 20f), 0.35f, 0.7, 0.5, 0.42);
    }

    /** Pterodactylus: ein hohes, flatterndes Kreischen. */
    static short[] screech(Random r) {
        return tone(range(r, 0.15f, 0.35f), range(r, 950f, 1400f), range(r, 0.2f, 0.5f), true,
            range(r, 30f, 45f), 0.8f, 0.4, 0.2, 0.36);
    }

    /** Schmatzen: drei kurze, dumpfe Bisse. */
    static short[] munch(Random r) {
        int n = (int) (0.62 * RATE);
        short[] s = new short[n];
        for (int i = 0; i < n; i++) {
            double t = i / (double) RATE;
            double u = (t % 0.2) / 0.11;
            if (u < 1) {
                double tone = Math.sin(2 * Math.PI * 190 * t * (1 - 0.3 * u));
                double noise = r.nextDouble() * 2 - 1;
                s[i] = (short) ((0.5 * tone + 0.5 * noise) * Math.sin(Math.PI * u) * 0.38 * 32767);
            }
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
