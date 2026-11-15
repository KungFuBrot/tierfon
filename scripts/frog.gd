extends Animal
## Der Frosch. Gezeichnet für eine Breite von 720 Pixeln, Mittelpunkt bei (0, 0).

const GREEN := Color("5cb85c")
const DARK := Color("3d8b40")
const BELLY := Color("d9f2b4")


func _make_clip(r: RandomNumberGenerator) -> AudioStreamWAV:
	return VoiceSynth.croak(r)


func _draw() -> void:
	var breathe := sin(time * 2.0) * 6.0
	var y := -hop - attention * 18.0

	_ellipse(Vector2(0, 250), 240.0 - hop * 0.2, 36, Color(0, 0, 0, 0.15))
	# Hinterbeine
	_ellipse(Vector2(-195, 215 + y * 0.3), 95, 40, DARK)
	_ellipse(Vector2(195, 215 + y * 0.3), 95, 40, DARK)
	# Körper und Bauch
	_ellipse(Vector2(0, 60 + y), 235.0 + breathe * 0.5, 190.0 - breathe * 0.5, GREEN)
	_ellipse(Vector2(0, 135 + y), 165, 105, BELLY)
	# Schallblase bläht sich beim Quaken auf
	if mouth > 0.02:
		_ellipse(Vector2(0, 110 + y), 110.0 + mouth * 50.0, 50.0 + mouth * 45.0, Color("f2fbd8"))
	# Vorderfüße
	_ellipse(Vector2(-95, 238 + y * 0.3), 60, 26, DARK)
	_ellipse(Vector2(95, 238 + y * 0.3), 60, 26, DARK)

	# Augen: werden beim Zuhören größer
	var open := 1.0 - blink
	for sx: float in [-1.0, 1.0]:
		var c := Vector2(sx * 115.0, -115.0 + y)
		_ellipse(c, 82, 82, GREEN)
		var r := 58.0 + attention * 6.0
		_ellipse(c, r, maxf(4.0, r * open), Color.WHITE)
		if open > 0.3:
			var look := Vector2(sin(time * 0.7) * 8.0, cos(time * 0.5) * 4.0 - attention * 8.0)
			var p := 26.0 + attention * 6.0
			_ellipse(c + look, p, p * open, Color("222222"))
			_ellipse(c + look + Vector2(-8, -10), 8, 8.0 * open, Color.WHITE)

	# Wangen und Nasenlöcher
	_ellipse(Vector2(-170, 10 + y), 30, 18, Color(1, 0.5, 0.55, 0.55))
	_ellipse(Vector2(170, 10 + y), 30, 18, Color(1, 0.5, 0.55, 0.55))
	_ellipse(Vector2(-22, -50 + y), 6, 4, DARK)
	_ellipse(Vector2(22, -50 + y), 6, 4, DARK)

	# Mund: Lächeln oder offen beim Quaken
	if mouth < 0.05:
		draw_arc(Vector2(0, -120 + y), 140, deg_to_rad(25), deg_to_rad(155), 24, DARK.darkened(0.3), 9, true)
	else:
		_ellipse(Vector2(0, -5 + y), 135, 16.0 + mouth * 50.0, Color("7a1f2b"))
		_ellipse(Vector2(0, 5 + y + mouth * 30.0), 70, 8.0 + mouth * 20.0, Color("ff8fa3"))
