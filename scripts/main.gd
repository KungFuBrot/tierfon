extends Control
## Startbildschirm und Wechsel zwischen den Bildschirmen.
## Auf dem Telefon gibt es genau zwei Apps: „Tiere anrufen“ und „Musik“.

const Tile := preload("res://scripts/tile.gd")
const CallScreen := preload("res://scripts/call_screen.gd")

var _screen: Control
var _back := Callable()


func _ready() -> void:
	get_tree().quit_on_go_back = false
	# Hilfen für die Entwicklung: direkt einen Bildschirm öffnen und ein Bildschirmfoto speichern.
	if OS.get_environment("TIERFON_SCREEN") == "call":
		show_call("frog")
	elif OS.get_environment("TIERFON_SCREEN") == "animals":
		show_animals()
	else:
		show_home()
	var shot := OS.get_environment("TIERFON_SHOT")
	if shot != "":
		await get_tree().create_timer(3.0).timeout
		get_viewport().get_texture().get_image().save_png(shot)
		get_tree().quit()


func _notification(what: int) -> void:
	if what == NOTIFICATION_WM_GO_BACK_REQUEST and _back.is_valid():
		_back.call()


func _new_screen(bg: Color, back: Callable) -> Control:
	if _screen:
		_screen.queue_free()
	_back = back
	_screen = Control.new()
	_screen.set_anchors_and_offsets_preset(Control.PRESET_FULL_RECT)
	add_child(_screen)
	var rect := ColorRect.new()
	rect.color = bg
	rect.set_anchors_and_offsets_preset(Control.PRESET_FULL_RECT)
	_screen.add_child(rect)
	if back.is_valid():
		var b := _tile(_screen, Color("7a8794"), "back", 0.04, 0.02, 0.24, 0.1325)
		b.round = true
		b.pressed.connect(back)
	return _screen


func _tile(parent: Control, color: Color, icon: String, x0: float, y0: float, x1: float, y1: float) -> Control:
	var t := Tile.new()
	t.color = color
	t.icon = icon
	t.anchor_left = x0
	t.anchor_top = y0
	t.anchor_right = x1
	t.anchor_bottom = y1
	parent.add_child(t)
	return t


func show_home() -> void:
	var s := _new_screen(Color("fff4d6"), Callable())
	_tile(s, Color("bdeaa8"), "frog", 0.1, 0.07, 0.9, 0.48).pressed.connect(show_animals)
	_tile(s, Color("ff9f43"), "note", 0.1, 0.52, 0.9, 0.93).pressed.connect(show_music)


func show_animals() -> void:
	var s := _new_screen(Color("e3f6ff"), show_home)
	_tile(s, Color("bdeaa8"), "frog", 0.06, 0.18, 0.48, 0.52).pressed.connect(show_call.bind("frog"))
	# Igel, Waschbär und Brontosaurus kommen in Etappe 2.
	for cell: Array in [[0.52, 0.18], [0.06, 0.56], [0.52, 0.56]]:
		var t := _tile(s, Color.WHITE, "unknown", cell[0], cell[1], cell[0] + 0.42, cell[1] + 0.34)
		t.enabled = false


func show_call(animal_id: String) -> void:
	if _screen:
		_screen.queue_free()
	_back = show_animals
	var c := CallScreen.new()
	c.animal_id = animal_id
	c.hangup.connect(show_animals)
	_screen = c
	add_child(c)


func show_music() -> void:
	# Der Musikplayer kommt in Etappe 3.
	var s := _new_screen(Color("ffe2c2"), show_home)
	var t := _tile(s, Color("ff9f43"), "note", 0.2, 0.3, 0.8, 0.64)
	t.enabled = false
