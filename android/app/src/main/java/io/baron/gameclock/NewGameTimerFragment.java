package io.baron.gameclock;

import android.app.Fragment;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.*;
import android.widget.*;

import java.util.*;

public class NewGameTimerFragment extends Fragment {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_new, container, false);

		final TextView titleText = (TextView) view.findViewById(R.id.timerTitle);
		final TextView nameText = (TextView) view.findViewById(R.id.timerName);

		titleText.requestFocus();
		getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

		View button = view.findViewById(R.id.button);

		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String timerTitle = titleText.getText().toString().trim();
				String timerName = nameText.getText().toString().trim();

				if (TextUtils.isEmpty(timerTitle) ||
					TextUtils.isEmpty(timerName)) {

					Toast
						.makeText(getActivity(), "Enter Title and Names", Toast.LENGTH_SHORT)
						.show();
					return;
				}

				GameTimer timer = new GameTimer();
				timer.id = UUID.randomUUID();
				timer.title = timerTitle;
				timer.name = timerName;

				TimerCollection.instance.timers.add(timer);
				Collections.sort(TimerCollection.instance.timers, new Comparator<GameTimer>() {
					@Override
					public int compare(GameTimer lhs, GameTimer rhs) {
						int comparison = lhs.title.compareTo(rhs.title);

						if (comparison == 0) {
							comparison = lhs.name.compareTo(rhs.name);
						}

						return comparison;
					}
				});

				TimerCollection.instance.postUpdate(timer);

				getFragmentManager()
					.popBackStack();
			}
		});

		return view;
	}
}
