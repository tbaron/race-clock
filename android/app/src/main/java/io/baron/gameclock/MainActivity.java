package io.baron.gameclock;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.*;
import android.widget.*;

import java.util.*;


public class MainActivity extends Activity {

	static class GameTimer {
		public String id;
		public String title;
		public String name;
		public Date started;
		public Date stopped;
	}

	static class GameTimerAdapter extends BaseAdapter {
		private List<GameTimer> timers;
		private final Context context;

		GameTimerAdapter(List<GameTimer> timers, Context context) {
			this.timers = timers;
			this.context = context;
		}

		@Override
		public int getCount() {
			return timers.size();
		}

		@Override
		public GameTimer getItem(int position) {
			return timers.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			GameTimer timer = getItem(position);

			if (convertView == null) {
				convertView = LayoutInflater.from(context).inflate(R.layout.timer, parent, false);
			}

			TextView title = (TextView) convertView.findViewById(R.id.timerTitle);
			TextView name = (TextView) convertView.findViewById(R.id.timerName);
			TextView time = (TextView) convertView.findViewById(R.id.timerTime);

			title.setText(timer.title);
			name.setText(timer.name);

			long diff = timer.stopped.getTime() - timer.started.getTime();
			time.setText(formatTime(diff));
		}

		private String formatTime(long diff) {
			diff /= 1000;

			int seconds = (int) (diff % 60);
			diff /= 60;
			int minutes = (int) (diff % 60);
			diff /= 60;
			int hours = (int) (diff % 24);

			StringBuilder builder = new StringBuilder();

			if (hours > 0) {
				builder.append(String.format(Locale.getDefault(), "%2d:", hours));
			}

			builder.append(String.format(Locale.getDefault(), "%2d:%2d", minutes, seconds));

			return builder.toString();
		}
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (hasFocus) {
			findViewById(R.id.activity).setSystemUiVisibility(
				View.SYSTEM_UI_FLAG_LAYOUT_STABLE
					| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
					| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
					| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
					| View.SYSTEM_UI_FLAG_FULLSCREEN
					| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
		}
	}
}
