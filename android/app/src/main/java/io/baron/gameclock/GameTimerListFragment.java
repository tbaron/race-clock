package io.baron.gameclock;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.*;
import android.widget.*;

public class GameTimerListFragment extends ListFragment {
	private GameTimerAdapter adapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_timer_list, container, false);

		adapter = new GameTimerAdapter((ListView) view.findViewById(android.R.id.list), TimerCollection.instance.timers, getActivity());

		setListAdapter(adapter);

		return view;
	}

	@Override
	public void onStart() {
		super.onStart();

		getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
				getActivity().startActionMode(new ActionMode.Callback() {
					@Override
					public boolean onCreateActionMode(ActionMode mode, Menu menu) {
						MenuInflater inflater = mode.getMenuInflater();
						inflater.inflate(R.menu.action_timer, menu);
						return true;
					}

					@Override
					public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
						return false;
					}

					@Override
					public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
						switch (item.getItemId()) {
							case R.id.action_delete:

								TimerCollection.instance.postDelete(adapter.getItem(position));
								TimerCollection.instance.timers.remove(position);

								adapter.notifyDataSetChanged();

								mode.finish();

								return true;
						}

						return false;
					}

					@Override
					public void onDestroyActionMode(ActionMode mode) {
					}
				});

				return true;
			}
		});
	}

	@Override
	public void onPause() {
		super.onPause();

		adapter.onPause();

		TimerCollection.instance.save();
	}

	@Override
	public void onResume() {
		super.onResume();

		TimerCollection.instance.restore();

		adapter.onResume();
	}
}
