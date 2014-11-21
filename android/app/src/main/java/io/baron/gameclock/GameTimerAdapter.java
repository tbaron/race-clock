package io.baron.gameclock;

import android.content.Context;
import android.os.Handler;
import android.view.*;
import android.widget.*;

import java.util.*;

public class GameTimerAdapter extends BaseAdapter {
	class GameTimerViewState {
		public GameTimer timer;
		public TextView timeView;
		public TextView titleView;
		public TextView nameView;
		public ImageButton buttonView;
	}

	private final ListView listView;
	private final Context context;
	private final List<GameTimer> timers;
	private final Handler handler = new Handler();
	private Runnable runnable;

	GameTimerAdapter(ListView listView, List<GameTimer> timers, Context context) {
		this.listView = listView;
		this.timers = timers;
		this.context = context;
	}

	public void onResume() {
		if (runnable != null) {
			handler.removeCallbacks(runnable);
		}

		runnable = new Runnable() {
			@Override
			public void run() {
				refreshTimers();

				handler.postDelayed(runnable, 100);
			}
		};
		runnable.run();
	}

	public void onPause() {
		handler.removeCallbacks(runnable);
		runnable = null;
	}

	private void refreshTimers() {
		int first = listView.getFirstVisiblePosition();
		int last = listView.getLastVisiblePosition();

		if (last >= getCount()) {
			last = getCount() - 1;
		}

		for (int i = first; i <= last; i++) {
			View child = listView.getChildAt(i);

			if (child == null) {
				continue;
			}

			GameTimerViewState state = (GameTimerViewState) child.getTag(R.id.GameTimerTag);

			refreshTimeView(state);
		}
	}

	private void refreshTimeView(GameTimerViewState state) {
		Date end = state.timer.stop;
		Date started = state.timer.start;
		if (end == null) end = new Date();
		if (started == null) started = new Date();

		long diff = end.getTime() - started.getTime();
		state.timeView.setText(formatTime(diff));
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
		GameTimerViewState state;

		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.timer, parent, false);
			state = new GameTimerViewState();
			state.titleView = (TextView) convertView.findViewById(R.id.timerTitle);
			state.nameView = (TextView) convertView.findViewById(R.id.timerName);
			state.timeView = (TextView) convertView.findViewById(R.id.timerTime);
			state.buttonView = (ImageButton) convertView.findViewById(R.id.button);

			convertView.setTag(R.id.GameTimerTag, state);
		} else {
			state = (GameTimerViewState) convertView.getTag(R.id.GameTimerTag);
		}

		GameTimer timer = getItem(position);
		state.timer = timer;

		state.titleView.setText(timer.title);
		state.nameView.setText(timer.name);

		boolean isRunning = timer.start != null && timer.stop == null;
		state.buttonView.setImageResource(isRunning ? R.drawable.ic_action_stop : R.drawable.ic_action_play);
		state.buttonView.setOnClickListener(pauseBtnOnClick);

		state.timeView.setTextColor(context.getResources().getColor(isRunning ? R.color.timer_running : R.color.timer_stopped));

		refreshTimeView(state);

		return convertView;
	}

	private View.OnClickListener pauseBtnOnClick = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			int position = listView.getPositionForView(v);

			if (position == ListView.INVALID_POSITION) {
				return;
			}

			GameTimer timer = getItem(position);

			boolean isRunning = timer.start != null && timer.stop == null;

			if (isRunning) {
				timer.stop = new Date();
			} else {
				timer.start = new Date();
				timer.stop = null;
			}

			TimerCollection.instance.postUpdate(timer);

			notifyDataSetChanged();
		}
	};

	private String formatTime(long diff) {
		diff /= 10;

		int fraction = (int) (diff % 100);
		diff /= 100;
		int seconds = (int) (diff % 60);
		diff /= 60;
		int minutes = (int) (diff % 60);
		diff /= 60;
		int hours = (int) (diff % 24);

		StringBuilder builder = new StringBuilder();

		if (hours > 0) {
			builder.append(String.format(Locale.getDefault(), "%d:", hours));
		}

		builder.append(String.format(Locale.getDefault(), "%02d:%02d.%02d", minutes, seconds, fraction));

		return builder.toString();
	}
}
