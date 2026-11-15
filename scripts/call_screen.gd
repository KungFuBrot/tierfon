extends Control
## Der „Videoanruf“: erst klingelt es, dann nimmt das Tier ab.

signal hangup

const Tile := preload("res://scripts/tile.gd")
const ANIMALS := {
	"frog": preload("res://scripts/frog.gd"),
}

var animal_id := "frog"

var _animal: Animal
var _ring_left := 1.8
var _ring: AudioStreamPlayer
var _t := 0.0


func _ready() -> void:
	set_anchors_and_offsets_preset(Control.PRESET_FULL_RECT)
	Mic.start()
	_ring = AudioStreamPlayer.new()
	_ring.stream = VoiceSynth.ring()
	add_child(_ring)
	_ring.play()

	var btn := Tile.new()
	btn.color = Color("e5484d")
	btn.icon = "hangup"
	btn.round = true
	btn.anchor_left = 0.38
	btn.anchor_right = 0.62
	btn.anchor_top = 0.845
	btn.anchor_bottom = 0.98
	btn.pressed.connect(func() -> void: hangup.emit())
	add_child(btn)


func _exit_tree() -> void:
	Mic.stop()


func _process(delta: float) -> void:
	_t += delta
	if _animal == null:
		_ring_left -= delta
		if _ring_left <= 0.0:
			_ring.stop()
			_animal = ANIMALS[animal_id].new()
			add_child(_animal)
			move_child(_animal, 0)
	if _animal:
		_animal.position = Vector2(size.x * 0.5, size.y * 0.47)
		_animal.scale = Vector2.ONE * (size.x / 720.0)
	queue_redraw()


func _gui_input(e: InputEvent) -> void:
	if _animal and e is InputEventMouseButton and e.pressed:
		_animal.poke()


func _draw() -> void:
	# Himmel, Sonne, Wiese
	draw_rect(Rect2(Vector2.ZERO, size), Color("bfe6ff"))
	draw_circle(Vector2(size.x * 0.82, size.y * 0.1), size.x * 0.09, Color("ffe066"))
	draw_rect(Rect2(0, size.y * 0.6, size.x, size.y * 0.4), Color("8fd18a"))
	if _animal == null:
		# Klingeln: pulsierende Ringe
		var c := size * 0.5
		for i in 3:
			var p := fmod(_t * 0.9 + i / 3.0, 1.0)
			draw_arc(c, size.x * (0.12 + 0.3 * p), 0, TAU, 48, Color(1, 1, 1, 1.0 - p), 10, true)
		draw_circle(c, size.x * 0.12, Color.WHITE)
