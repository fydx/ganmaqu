package com.sssta.ganmaqu;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ArrayAdapter;

import com.mobeta.android.dslv.DragSortListView;

public class RouteReviewActivity extends ListActivity {
	private ArrayAdapter<String> adapter;

	private List<place> places;
	private ArrayList<String> list;
	private DragSortListView.DropListener onDrop = new DragSortListView.DropListener() {
		@Override
		public void drop(int from, int to) {
			String item = adapter.getItem(from);

			adapter.notifyDataSetChanged();
			adapter.remove(item);
			adapter.insert(item, to);
		}
	};

	private DragSortListView.RemoveListener onRemove = new DragSortListView.RemoveListener() {
		@Override
		public void remove(int which) {
			// adapter.remove(adapter.getItem(which));
			adapter.notifyDataSetChanged();
		}
	};

	private DragSortListView.DragScrollProfile ssProfile = new DragSortListView.DragScrollProfile() {
		@Override
		public float getSpeed(float w, long t) {
			if (w > 0.8f) {
				// Traverse all views in a millisecond
				return ((float) adapter.getCount()) / 0.001f;
			} else {
				return 10.0f * w;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_route_review);
		DragSortListView lv = (DragSortListView) getListView();
		lv.setDropListener(onDrop);
		lv.setRemoveListener(onRemove);
		lv.setDragScrollProfile(ssProfile);
		// places = getIntent()

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.route_review, menu);
		return true;
	}

}
