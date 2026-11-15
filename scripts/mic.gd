extends Node
## Misst nur die Lautstärke am Mikrofon. Es wird nichts aufgenommen oder gespeichert.

var level := 0.0
## Solange ein Tier selbst spricht, wird das Mikrofon ignoriert (sonst hört es sich selbst).
var paused := false:
	set(value):
		paused = value
		if _capture:
			_capture.clear_buffer()
		level = 0.0

var _floor := 0.02
var _capture: AudioEffectCapture
var _player: AudioStreamPlayer


func _ready() -> void:
	var idx := AudioServer.bus_count
	AudioServer.add_bus(idx)
	AudioServer.set_bus_name(idx, "Mic")
	_capture = AudioEffectCapture.new()
	AudioServer.add_bus_effect(idx, _capture)
	# Das Mikrofon soll nicht aus dem Lautsprecher kommen.
	var silence := AudioEffectAmplify.new()
	silence.volume_db = -80.0
	AudioServer.add_bus_effect(idx, silence)
	_player = AudioStreamPlayer.new()
	_player.stream = AudioStreamMicrophone.new()
	_player.bus = "Mic"
	add_child(_player)
	get_tree().on_request_permissions_result.connect(_on_permission)


func start() -> void:
	if OS.get_name() == "Android":
		OS.request_permission("RECORD_AUDIO")
	_player.play()


func stop() -> void:
	_player.stop()
	level = 0.0


func is_voice() -> bool:
	return not paused and level > maxf(0.04, _floor * 2.5)


func _on_permission(_permission: String, granted: bool) -> void:
	if granted and _player.playing:
		_player.stop()
		_player.play()


func _process(delta: float) -> void:
	if paused or not _player.playing:
		return
	var n := _capture.get_frames_available()
	if n > 0:
		var buf := _capture.get_buffer(n)
		var peak := 0.0
		for i in range(0, buf.size(), 4):
			peak = maxf(peak, maxf(absf(buf[i].x), absf(buf[i].y)))
		level = lerpf(level, peak, 1.0 - exp(-delta * 20.0))
	# Grundrauschen langsam mitlernen, damit es auch in lauter Umgebung funktioniert.
	var speed := 0.5 if level < _floor * 2.5 else 0.05
	_floor = clampf(lerpf(_floor, level, delta * speed), 0.005, 0.3)
