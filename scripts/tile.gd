extends Control
## Große, bunte Schaltfläche mit einem gezeichneten Symbol (Kinder müssen nichts lesen können).

signal pressed

var color := Color.WHITE
var icon := ""
var enabled := true
var round := false

var _down := false


func _gui_input(e: InputEvent) -> void:
	if e is InputEventMouseButton and e.button_index == MOUSE_BUTTON_LEFT:
		if e.pressed:
			_down = true
		elif _down:
			_down = false
			if enabled and Rect2(Vector2.ZERO, size).has_point(e.position):
				pressed.emit()
		queue_redraw()


func _draw() -> void:
	var r := Rect2(Vector2.ZERO, size)
	if _down and enabled:
		r = r.grow(-8)
	var short := minf(r.size.x, r.size.y)
	var sb := StyleBoxFlat.new()
	sb.bg_color = color if enabled else Color("c9ced6")
	sb.set_corner_radius_all(int(short * (0.5 if round else 0.2)))
	draw_style_box(sb, r)

	var c := r.get_center()
	var u := short / 200.0
	match icon:
		"frog":
			var g := Color("5cb85c")
			draw_circle(c + Vector2(0, 12) * u, 58 * u, g)
			for sx: float in [-1.0, 1.0]:
				var e := c + Vector2(sx * 34, -36) * u
				draw_circle(e, 26 * u, g)
				draw_circle(e, 18 * u, Color.WHITE)
				draw_circle(e, 9 * u, Color("222222"))
			draw_arc(c + Vector2(0, -2) * u, 36 * u, deg_to_rad(25), deg_to_rad(155), 16, Color("2b6b2e"), 5 * u, true)
		"note":
			var w := Color.WHITE
			draw_circle(c + Vector2(-34, 40) * u, 22 * u, w)
			draw_circle(c + Vector2(36, 26) * u, 22 * u, w)
			draw_rect(Rect2(c + Vector2(-22, -52) * u, Vector2(10, 92) * u), w)
			draw_rect(Rect2(c + Vector2(48, -66) * u, Vector2(10, 92) * u), w)
			draw_colored_polygon(PackedVector2Array([
				c + Vector2(-22, -52) * u, c + Vector2(58, -66) * u,
				c + Vector2(58, -42) * u, c + Vector2(-22, -28) * u]), w)
		"back":
			draw_colored_polygon(PackedVector2Array([
				c + Vector2(-46, 0) * u, c + Vector2(14, -52) * u, c + Vector2(14, -20) * u,
				c + Vector2(50, -20) * u, c + Vector2(50, 20) * u, c + Vector2(14, 20) * u,
				c + Vector2(14, 52) * u]), Color.WHITE)
		"hangup":
			draw_arc(c + Vector2(0, 60) * u, 70 * u, deg_to_rad(215), deg_to_rad(325), 20, Color.WHITE, 26 * u, true)
		"unknown":
			var f := ThemeDB.fallback_font
			var fs := int(110 * u)
			draw_string(f, Vector2(r.position.x, c.y + fs * 0.35), "?", HORIZONTAL_ALIGNMENT_CENTER, r.size.x, fs, Color.WHITE)
