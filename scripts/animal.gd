class_name Animal
extends Node2D
## Gemeinsames Verhalten aller Tiere: zuhören, wenn das Kind spricht, und danach zurückblabbern.
## Ein Tier leitet davon ab und liefert nur sein Aussehen (_draw) und seine Stimme (_make_clip).

enum State { IDLE, LISTEN, TALK }

var state := State.IDLE
## Werte für die Zeichnung, jeweils 0 bis 1.
var mouth := 0.0
var attention := 0.0
var blink := 0.0
## Sprunghöhe in Pixeln.
var hop := 0.0
var time := 0.0

var _rng := RandomNumberGenerator.new()
var _clips: Array[AudioStreamWAV] = []
var _player: AudioStreamPlayer
var _heard := 0.0
var _listen_time := 0.0
var _silence := 0.0
var _talk_left := 0.0
var _syllable_left := 0.0
var _gap := 0.0
var _cooldown := 0.0
var _idle_timer := 7.0
var _blink_timer := 2.0
var _hop_speed := 0.0


func _make_clip(_r: RandomNumberGenerator) -> AudioStreamWAV:
	return null


func _ready() -> void:
	_rng.randomize()
	for i in 6:
		_clips.append(_make_clip(_rng))
	_player = AudioStreamPlayer.new()
	add_child(_player)
	talk(1.2)  # Begrüßung beim Abnehmen


func _exit_tree() -> void:
	Mic.paused = false


func talk(seconds: float) -> void:
	state = State.TALK
	_talk_left = seconds
	_syllable_left = 0.0
	_gap = 0.12
	_cooldown = 0.45
	Mic.paused = true


## Antippen: kleiner Hüpfer und ein kurzer Laut.
func poke() -> void:
	if hop <= 0.0:
		_hop_speed = 900.0
	if state != State.TALK:
		talk(0.5)


func _process(delta: float) -> void:
	time += delta
	var voice := Mic.is_voice() or Input.is_key_pressed(KEY_SPACE)
	match state:
		State.IDLE:
			_heard = _heard + delta if voice else maxf(0.0, _heard - delta)
			_idle_timer -= delta
			if _heard > 0.15:
				state = State.LISTEN
				_heard = 0.0
				_listen_time = 0.0
				_silence = 0.0
			elif _idle_timer <= 0.0:
				talk(_rng.randf_range(0.5, 1.1))
		State.LISTEN:
			_listen_time += delta
			_silence = 0.0 if voice else _silence + delta
			if _silence > 0.8 or _listen_time > 8.0:
				talk(clampf(_listen_time * 0.8, 1.0, 3.5))
		State.TALK:
			_talk_left -= delta
			if _syllable_left > 0.0:
				_syllable_left -= delta
			elif _talk_left > 0.0:
				_gap -= delta
				if _gap <= 0.0:
					_play_syllable()
			else:
				_cooldown -= delta
				if _cooldown <= 0.0:
					state = State.IDLE
					_idle_timer = _rng.randf_range(6.0, 11.0)
					Mic.paused = false

	var speaking := state == State.TALK and _syllable_left > 0.0
	var mouth_target := 0.35 + 0.65 * absf(sin(time * 20.0)) if speaking else 0.0
	mouth = move_toward(mouth, mouth_target, delta * 10.0)
	attention = move_toward(attention, 1.0 if state == State.LISTEN else 0.0, delta * 5.0)

	_blink_timer -= delta
	if _blink_timer <= 0.0:
		_blink_timer = _rng.randf_range(2.0, 5.0)
	blink = clampf(1.0 - absf(_blink_timer - 0.08) / 0.08, 0.0, 1.0)

	if hop > 0.0 or _hop_speed > 0.0:
		_hop_speed -= 2600.0 * delta
		hop = maxf(0.0, hop + _hop_speed * delta)
		if hop <= 0.0:
			_hop_speed = 0.0
	queue_redraw()


func _play_syllable() -> void:
	var clip: AudioStreamWAV = _clips[_rng.randi() % _clips.size()]
	_player.stream = clip
	_player.pitch_scale = _rng.randf_range(0.85, 1.25)
	_player.play()
	_syllable_left = clip.get_length() / _player.pitch_scale
	_gap = _rng.randf_range(0.04, 0.18)


func _ellipse(c: Vector2, rx: float, ry: float, col: Color) -> void:
	var pts := PackedVector2Array()
	for i in 36:
		var a := TAU * i / 36.0
		pts.append(c + Vector2(cos(a) * rx, sin(a) * ry))
	draw_colored_polygon(pts, col)
