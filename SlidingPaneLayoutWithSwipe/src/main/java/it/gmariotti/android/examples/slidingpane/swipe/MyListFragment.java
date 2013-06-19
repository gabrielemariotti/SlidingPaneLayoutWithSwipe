package it.gmariotti.android.examples.slidingpane.swipe; /*******************************************************************************

 * Copyright 2013 Gabriele Mariotti
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/

import java.util.ArrayList;
import java.util.Arrays;

import android.app.Activity;
import android.app.ListFragment;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import it.gmariotti.android.examples.slidingpane.swipe.R;

public class MyListFragment extends ListFragment   implements UndoBarController.UndoListener {

	private ArrayAdapter<String> mAdapter;
	private ListView mListView;
	
	private SwipeDismissListViewTouchListener mOnTouchListener;
	private UndoBarController mUndoBarController;

	public static String[] items = { "Item 1: xxxxxxxxxxxxxxxxx",
			"Item 2: xxxxxxxxxxxxxxxxx", "Item 3: xxxxxxxxxxxxxxxxx",
			"Item 4: xxxxxxxxxxxxxxxxx", "Item 5: xxxxxxxxxxxxxxxxx",
			"Item 6: xxxxxxxxxxxxxxxxx", "Item 7: xxxxxxxxxxxxxxxxx",
			"Item 8: xxxxxxxxxxxxxxxxx", "Item 9: xxxxxxxxxxxxxxxxx",
			"Item 10: xxxxxxxxxxxxxxxxx", "Item 11: xxxxxxxxxxxxxxxxx",
			"Item 12: xxxxxxxxxxxxxxxxx", "Item 13: xxxxxxxxxxxxxxxxx",
			"Item 14: xxxxxxxxxxxxxxxxx", "Item 15: xxxxxxxxxxxxxxxxx" };

	ListFragmentItemClickListener iItemClickListener;

	/**
	 * SwipeDismiss callback
	 * 
	 * Remove items, and show undobar
	 */
	SwipeDismissListViewTouchListener.DismissCallbacks mCallback = new SwipeDismissListViewTouchListener.DismissCallbacks() {
		@Override
		public void onDismiss(ListView listView, int[] reverseSortedPositions) {
			
			String[] itemStrings=new String[reverseSortedPositions.length];
			int[] itemPositions=new int[reverseSortedPositions.length];
			int i=0;
			
			for (int position : reverseSortedPositions) {
				String itemString=mAdapter.getItem(position);
				mAdapter.remove(itemString);
				
				itemStrings[i]=itemString;
				itemPositions[i]=position;
				i++;
			}
			mAdapter.notifyDataSetChanged();
			
			
			//Show UndoBar
			UndoItem itemUndo=new UndoItem(itemStrings,itemPositions);
			
			Resources res = getResources();
			String messageUndoBar = res.getQuantityString(R.plurals.items, reverseSortedPositions.length,reverseSortedPositions.length);
			
			mUndoBarController.showUndoBar(
                     false,
                     messageUndoBar,
                     itemUndo);
		}

		@Override
		public boolean canDismiss(int position) {
			return position <= mAdapter.getCount() - 1;
		}
	};

	/** An interface for defining the callback method */
	public interface ListFragmentItemClickListener {
		/**
		 * This method will be invoked when an item in the ListFragment is
		 * clicked
		 */
		void onListFragmentItemClick(View view, int position);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		
		if (mUndoBarController!=null)
			mUndoBarController.onRestoreInstanceState(savedInstanceState);
		
		// We use a arrayList to prevent UnsupportedOperationException
		// The ArrayAdapter, on being initialized by an array, converts the
		// array into a AbstractList (List) which cannot be modified.
		ArrayList<String> itemslist = new ArrayList<String>();
		itemslist.addAll(Arrays.asList(items));

		mAdapter = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_list_item_1, itemslist);

		setListAdapter(mAdapter);

		// Create a ListView-specific touch listener.
		mListView = getListView();
		if (mListView != null) {
			mOnTouchListener = new SwipeDismissListViewTouchListener(mListView,
					mCallback);
			mListView.setOnTouchListener(mOnTouchListener);

			// Setting this scroll listener is required to ensure that during
			// ListView scrolling,
			// we don't look for swipes.
			mListView
					.setOnScrollListener(mOnTouchListener.makeScrollListener());
			
			//UndoController
			if (mUndoBarController==null)
				mUndoBarController = new UndoBarController(getActivity().findViewById(R.id.undobar), this);
		}
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		setHasOptionsMenu(true);
		return inflater.inflate(R.layout.list_fragment, container, false);
		//return super.onCreateView(inflater, container, savedInstanceState);
	}

	/**
	 * A callback function, executed when this fragment is attached to an
	 * activity
	 */
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			/**
			 * This statement ensures that the hosting activity implements
			 * ListFragmentItemClickListener
			 */
			iItemClickListener = (ListFragmentItemClickListener) activity;
		} catch (Exception e) {
			Toast.makeText(activity.getBaseContext(), "Exception",
					Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.list, menu);
	}

	@Override
	public void onListItemClick(ListView list, View view, int position, long id) {

		/**
		 * Invokes the implementation of the method onListFragmentItemClick in
		 * the hosting activity
		 */
		iItemClickListener.onListFragmentItemClick(view, position);

	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		if (mUndoBarController!=null)
			mUndoBarController.onSaveInstanceState(outState);
    }

	
	
	/**
	 * Undo remove Action
	 */
	@Override
	public void onUndo(Parcelable token) {
		
		//Restore items in lists (use reverseSortedOrder)
		if (token!=null){
			
			UndoItem item=(UndoItem)token;
			String[] itemStrings=item.itemString;
			int[] itemPositions=item.itemPosition;
			
			if (itemStrings!=null && itemPositions!=null){
				int end=itemStrings.length;
				
				for(int i=end-1;i>=0;i--){
					
					String itemString=itemStrings[i];
					int itemPosition=itemPositions[i];
					
					mAdapter.insert(itemString,itemPosition);
					mAdapter.notifyDataSetChanged();
				}
			}
		}
		
	}

	
}
