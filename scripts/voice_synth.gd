class_name VoiceSynth
## Erzeugt alle Töne rechnerisch. So braucht das Projekt keine fremden Tondateien.

const RATE := 22050


static func to_wav(samples: PackedFloat32Array) -> AudioStreamWAV:
	var bytes := PackedByteArray()
	bytes.resize(samples.size() * 2)
	for i in samples.size():
		bytes.encode_s16(i * 2, int(clampf(samples[i], -1.0, 1.0) * 32767.0))
	var wav := AudioStreamWAV.new()
	wav.format = AudioStreamWAV.FORMAT_16_BITS
	wav.mix_rate = RATE
	wav.stereo = false
	wav.data = bytes
	return wav


## Eine Frosch-Silbe: ein knarrender Ton, der leicht in der Höhe gleitet.
static func croak(rng: RandomNumberGenerator) -> AudioStreamWAV:
	var dur := rng.randf_range(0.16, 0.38)
	var f0 := rng.randf_range(170.0, 250.0)
	var glide := rng.randf_range(-0.15, 0.7)
	var pulse := rng.randf_range(26.0, 42.0)
	var n := int(dur * RATE)
	var s := PackedFloat32Array()
	s.resize(n)
	var ph := 0.0
	for i in n:
		var t := float(i) / RATE
		var u := t / dur
		ph += TAU * f0 * (1.0 + glide * u) / RATE
		var env := pow(sin(PI * u), 0.6)
		var am := 0.35 + 0.65 * pow(0.5 + 0.5 * sin(TAU * pulse * t), 2.0)
		var w := sin(ph) + 0.55 * sin(2.0 * ph) + 0.35 * sin(3.0 * ph) + 0.2 * sin(5.0 * ph)
		s[i] = 0.42 * env * am * w
	return to_wav(s)


## Klingelton: zwei kurze Triller.
static func ring() -> AudioStreamWAV:
	var dur := 1.6
	var n := int(dur * RATE)
	var s := PackedFloat32Array()
	s.resize(n)
	for i in n:
		var t := float(i) / RATE
		var on := 1.0 if (t < 0.55 or (t > 0.8 and t < 1.35)) else 0.0
		var f := 660.0 if fmod(t * 14.0, 1.0) < 0.5 else 830.0
		s[i] = 0.3 * on * sin(TAU * f * t)
	return to_wav(s)
