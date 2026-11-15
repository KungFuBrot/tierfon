package de.kungfubrot.tierfon;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

/** Der Musikplayer: Liste der Lieder und drei große Tasten. */
public class MusicActivity extends BaseActivity {
    private Player player;
    private TileView playButton;
    private TextView empty;
    private ListView list;
    private boolean askedPermission;
    private int shownIndex = -2;
    private boolean shownPlaying;

    private final BaseAdapter adapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return player.tracks.size();
        }

        @Override
        public Object getItem(int i) {
            return player.tracks.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View old, ViewGroup parent) {
            TextView t = old instanceof TextView ? (TextView) old : new TextView(MusicActivity.this);
            t.setText(Player.title(player.tracks.get(i)));
            t.setTextSize(24);
            t.setSingleLine(true);
            t.setPadding(36, 34, 36, 34);
            boolean current = i == player.index;
            t.setTextColor(current ? 0xFFFFFFFF : 0xFF4A3520);
            t.setBackgroundColor(current ? 0xFFFF9F43 : (i % 2 == 0 ? 0xFFFFF1E0 : 0xFFFFE2C2));
            return t;
        }
    };

    private final Runnable tick = new Runnable() {
        @Override
        public void run() {
            refresh();
            list.postDelayed(this, 400);
        }
    };

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        player = Player.get(this);
        FracLayout root = new FracLayout(this);
        root.setBackgroundColor(0xFFFFE2C2);

        TileView back = new TileView(this, 0xFF7A8794, "back");
        back.round = true;
        back.setOnClickListener(v -> finish());
        root.add(back, 0.04f, 0.02f, 0.24f, 0.1325f);

        list = new ListView(this);
        list.setAdapter(adapter);
        list.setDividerHeight(0);
        list.setOnItemClickListener((p, v, i, id) -> {
            player.play(i);
            refresh();
        });
        root.add(list, 0.04f, 0.15f, 0.96f, 0.78f);

        empty = new TextView(this);
        empty.setText("Noch keine Musik.\nMP3-Dateien in den Ordner „Music“ kopieren.");
        empty.setTextSize(22);
        empty.setTextColor(0xFF4A3520);
        empty.setGravity(Gravity.CENTER);
        root.add(empty, 0.08f, 0.15f, 0.92f, 0.78f);

        TileView prev = new TileView(this, 0xFFFF9F43, "prev");
        prev.round = true;
        prev.setOnClickListener(v -> player.play(player.index - 1));
        root.add(prev, 0.06f, 0.83f, 0.30f, 0.965f);

        playButton = new TileView(this, 0xFFE8590C, "play");
        playButton.round = true;
        playButton.setOnClickListener(v -> {
            player.toggle();
            refresh();
        });
        root.add(playButton, 0.36f, 0.81f, 0.64f, 0.9675f);

        TileView next = new TileView(this, 0xFFFF9F43, "next");
        next.round = true;
        next.setOnClickListener(v -> player.play(player.index + 1));
        root.add(next, 0.70f, 0.83f, 0.94f, 0.965f);

        setContentView(root);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            player.scan();
        } else if (!askedPermission) {
            askedPermission = true;
            requestPermissions(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
        shownIndex = -2;
        list.post(tick);
    }

    @Override
    protected void onPause() {
        super.onPause();
        list.removeCallbacks(tick);
    }

    private void refresh() {
        boolean playing = player.playing();
        if (player.index == shownIndex && playing == shownPlaying) return;
        shownIndex = player.index;
        shownPlaying = playing;
        empty.setVisibility(player.tracks.isEmpty() ? View.VISIBLE : View.GONE);
        playButton.icon = playing ? "pause" : "play";
        playButton.invalidate();
        adapter.notifyDataSetChanged();
    }
}
