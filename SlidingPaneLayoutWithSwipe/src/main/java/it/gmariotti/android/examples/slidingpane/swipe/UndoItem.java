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

import android.os.Parcel;
import android.os.Parcelable;

public class UndoItem implements Parcelable {

	public String[] itemString;
	public int[] itemPosition;

	public UndoItem(String[] itemString, int[] itemPosition) {
		this.itemString = itemString;
		this.itemPosition = itemPosition;
	}

	protected UndoItem(Parcel in) {

	}

	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {

	}

	public static final Creator<UndoItem> CREATOR = new Creator<UndoItem>() {
		public UndoItem createFromParcel(Parcel in) {
			return new UndoItem(in);
		}

		public UndoItem[] newArray(int size) {
			return new UndoItem[size];
		}
	};
}