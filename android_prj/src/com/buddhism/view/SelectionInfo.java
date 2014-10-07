/*
Copyright (C) 2013 Ray Zhou

JadeRead is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

JadeRead is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with JadeRead.  If not, see <http://www.gnu.org/licenses/>

Author: Ray Zhou
Date: 2013 06 03

 */
package com.buddhism.view;

import android.text.Spannable;
import android.text.Spanned;
import android.text.style.CharacterStyle;

/**
 * class to hold the selection information
 */
public class SelectionInfo {

  private Object mSpan;
  private int mStart;
  private int mEnd;
  private Spannable mSpannable;

  public SelectionInfo() {
    this.clear();
  }

  public SelectionInfo(CharSequence text, Object span, int start, int end) {
    this.set(text, span, start, end);
  }

  /**
   * select the {@link #getSpannable()} between the offsets {@link this.getStart()} and
   * {@link #getEnd()}
   */
  public void select() {
    this.select(this.mSpannable);
  }

  public void select(Spannable text) {
    if (text != null) {
      text.removeSpan(this.mSpan);
      text.setSpan(
          this.mSpan,
          Math.min(this.mStart, this.mEnd),
          Math.max(this.mStart, this.mEnd),
          Spanned.SPAN_INCLUSIVE_INCLUSIVE);
    }
  }

  /**
   * remove the the selection
   */
  public void remove() {
    this.remove(this.mSpannable);
  }

  public void remove(Spannable text) {
    if (text != null) {
      text.removeSpan(this.mSpan);
    }
  }

  public void clear() {
    this.mSpan = null;
    this.mSpannable = null;
    this.mStart = 0;
    this.mEnd = 0;
  }

  public void set(Object span, int start, int end) {
    this.mSpan = span;
    this.mStart = start;
    this.mEnd = end;
  }

  public void set(CharSequence text, Object span, int start, int end) {
    if (text instanceof Spannable) {
      this.mSpannable = (Spannable) text;
    }
    this.set(span, start, end);
  }

  public CharSequence getSelectedText() {
    if (this.mSpannable != null) {
      int start = Math.min(this.mStart, this.mEnd);
      int end = Math.max(this.mStart, this.mEnd);

      if (start >= 0 && end < this.mSpannable.length()) {
        return this.mSpannable.subSequence(start, end);
      }
    }
    return "";
  }

  public Object getSpan() {
    return this.mSpan;
  }

  public void setSpan(CharacterStyle span) {
    this.mSpan = span;
  }

  public int getStart() {
    return this.mStart;
  }

  /**
   * set the starting offset of the selection (inclusive)
   * @param start
   *          the starting offset. It can be larger than {@link #getEnd()}
   */
  public void setStart(int start) {
    assert (start >= 0);
    this.mStart = start;
  }

  public int getEnd() {
    return this.mEnd;
  }

  /**
   * set the ending offset of the selection (exclusive)
   * @param end
   *          the ending offset. It can be smaller than {@link #getStart()}
   */
  public void setEnd(int end) {
    assert (end >= 0);
    this.mEnd = end;
  }

  public Spannable getSpannable() {
    return this.mSpannable;
  }

  public void setSpannable(Spannable spannable) {
    this.mSpannable = spannable;
  }

}