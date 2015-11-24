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
Date: 2013 04 26

 */

package com.buddhism.view;

import com.buddhism.sutra.R;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.text.style.BackgroundColorSpan;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * User: ray Date: 2013-02-01 <p/> WARNING: SelectableTextView is designed to be used inside a
 * ScrollView. The selection functionality have only been tested under the assumption that the
 * parent of the view is a ScrollView. <p/> The functionality WILL PROBABLY breaks if the TextView
 * is not inside a ScrollView.
 */
public class SelectableTextView extends TextView {

    private int mDefaultSelectionColor;

    private SelectionInfo mSelection;

    private final int[] mTempCoords = new int[2];

    private SelectionCursorController mSelectionController;

    public SelectableTextView (Context context) {
        super(context);
        this.init();
    }

    public SelectableTextView (Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    public SelectableTextView (Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.init();
    }

    public void setSelectionDrawable (Drawable drawable) {
        if (this.mSelectionController != null) {
            this.mSelectionController.setSelectionDrawable(drawable);
        }
    }

    private void init () {
        this.setTextScaleX(1.0618f);
        this.mSelection = new SelectionInfo();

        this.mSelectionController = new SelectionCursorController();

        final ViewTreeObserver observer = this.getViewTreeObserver();
        if (observer != null) {
            observer.addOnTouchModeChangeListener(this.mSelectionController);
        }

    }

    @Override
    protected void onAttachedToWindow () {
        super.onAttachedToWindow();

        // TextView will take care of the color span of the text when it's being scroll by
        // its parent ScrollView. But the cursors position is not handled. Calling
        // snapToSelection
        // will move the cursors along with the selection.
        if (this.getParent() instanceof ObservableScrollView) {
            ((ObservableScrollView) this.getParent())
                .addOnScrollChangedListener(new OnScrollChangedListener() {
                    @Override
                    public void onScrollChanged (
                        ObservableScrollView scrollView, int x, int y, int oldx,
                        int oldy) {
                        SelectableTextView.this.mSelectionController.snapToSelection();
                    }
                });
        }
    }

    public void setDefaultSelectionColor (int color) {
        this.mDefaultSelectionColor = color;
    }

    /**
     * set the selection beginning at {@code start} with the specified {@code length} for the given
     * {@code duration}
     *
     * @param color
     *     the color of the selection
     * @param start
     *     the start offset
     * @param length
     *     the length of the selection
     * @param duration
     *     the duration the selection, in second, to be last, -1 to indicate forever
     */
    public void setSelection (int color, int start, int length, int duration) {

        // make sure we are not selecting beyond the text
        int end = start + length >= this.getText().length() ? this.getText().length() - 1 : start
            + length;

        if (start + length > this.getText().length() || end <= start) {
            return;
        }

        this.mSelection =
            new SelectionInfo(this.getText(), new BackgroundColorSpan(color), start, end);
        this.mSelection.select();
        this.removeSelection(duration);
    }

    /**
     * set the selection beginning at {@code start} with the specified {@code length} for the given
     * {@code duration} with the default color
     *
     * @param start
     *     the start offset
     * @param length
     *     the length of the selection
     * @param duration
     *     the duration of the selection to be last, -1 to indicate forever
     */
    public void setSelection (int start, int length, int duration) {
        this.setSelection(this.mDefaultSelectionColor, start, length, duration);
    }

    /**
     * set the selection beginning at {@code start} with the spcified {@code length}
     *
     * @param start
     *     the starting offset
     * @param length
     *     the length of the selection
     */
    public void setSelection (int start, int length) {
        this.setSelection(start, length, -1);
    }

    /**
     * removes the current selection immediately
     */
    public void removeSelection () {
        this.mSelection.remove();
    }

    public void removeSelection (int delay) {
        this.removeSelection(this.mSelection, delay);
    }

    /**
     * remove the given span style from the main text after the specified time
     *
     * @param selection
     *     the span style to be removed
     * @param delay
     *     the milliseconds to wait before remove the style
     */
    public void removeSelection (final SelectionInfo selection, int delay) {
        if (delay >= 0) {
            Runnable runnable = new Runnable() {
                @Override
                public void run () {
                    selection.remove();
                }
            };
            this.postDelayed(runnable, delay);
        }
    }

    /**
     * show the selection cursors and select the text between the specified offset
     *
     * @param start
     *     the start offset
     * @param end
     *     the end offset
     */
    public void showSelectionControls (int start, int end) {
        assert (start >= 0);
        assert (end >= 0);
        assert (start < this.getText().length());
        assert (end < this.getText().length());

        this.mSelectionController.show(start, end);
    }

    /**
     * show the selection cursors and select the text between the specified offset without show
     * cursor
     * @param start
     * @param end
     */
    public void showSelectionWithoutHandle (int start, int end) {
        assert (start >= 0);
        assert (end >= 0);
        assert (start < this.getText().length());
        assert (end < this.getText().length());

        this.mSelectionController.show(start, end, false);
    }

    /**
     * @return the current selection information
     */
    public SelectionInfo getSelection () {
        return this.mSelection;
    }

    /**
     * @return the y position
     */
    private int getScrollYInternal () {
        int y = this.getScrollY();

        // a TextView inside a ScrollView is not scrolled, so getScrollY() returns 0.
        // We must use getScrollY() from the ScrollView instead
        if (this.getParent() instanceof ScrollView) {
            y += ((ScrollView) this.getParent()).getScrollY();

            // do this to compensate for the height of the status bar
            final int[] coords = this.mTempCoords;
            ((ScrollView) this.getParent()).getLocationInWindow(coords);
            y -= coords[1];
        }

        return y;
    }

    /**
     * @return the x position
     */
    @SuppressWarnings ("unused")
    private int getScrollXInternal () {
        int x = this.getScrollX();

        // a TextView inside a ScrollView is not scrolled, so getScrollX() returns 0.
        // We must use getScrollX() from the ScrollView instead
        if (this.getParent() instanceof ScrollView) {
            x += ((ScrollView) this.getParent()).getScrollX();
        }
        return x;
    }

    /**
     * Gets the character offset of (x, y). If (x, y) lies on the right half of the character, it
     * returns the offset of the next character. If (x, y) lies on the left half of the character,
     * it returns the offset of this character.
     *
     * @param x
     *     x coordinate relative to this TextView
     * @param y
     *     y coordinate relative to this TextView
     *
     * @return the offset at (x,y), -1 if error occurs
     */
    public int getOffset (int x, int y) {
        Layout layout = this.getLayout();
        int offset = -1;

        if (layout != null) {
            int topVisibleLine = layout.getLineForVertical(y);
            offset = layout.getOffsetForHorizontal(topVisibleLine, x);
        }

        return offset;
    }

    /**
     * Gets the character offset where (x, y) is pointing to.
     *
     * @param x
     *     x coordinate relative to this TextView
     * @param y
     *     y coordinate relative to this TextView
     *
     * @return the offset at (x, y), -1 if error occurs
     *
     * @see {@link #getOffset(int, int)}
     */
    public int getPreciseOffset (int x, int y) {
        Layout layout = this.getLayout();

        if (layout != null) {
            int topVisibleLine = layout.getLineForVertical(y);
            int offset = layout.getOffsetForHorizontal(topVisibleLine, x);

            int offset_x = (int) layout.getPrimaryHorizontal(offset);
            if (offset_x > x) {
                return layout.getOffsetToLeftOf(offset);
            }
        }
        return this.getOffset(x, y);
    }

    // //////////////////////////////////////////////
    // copied & modified from Android source code //
    // //////////////////////////////////////////////

    /**
     * Get the offset character closest to the specified absolute position.
     *
     * @param x
     *     The horizontal absolute position of a point on screen
     * @param y
     *     The vertical absolute position of a point on screen
     *
     * @return the character offset for the character whose position is closest to the specified
     * position. Returns -1 if there is no layout.
     */
    public int getOffsetFromRaw (int x, int y) {
        if (this.getLayout() == null)
            return -1;

        y -= this.getTotalPaddingTop();
        // Clamp the position to inside of the view.
        y = Math.max(0, y);
        y = Math.min(this.getHeight() - this.getTotalPaddingBottom() - 1, y);
        y += this.getScrollY();

        // if the textview is inside a scrollview, the above getScrollY() will return 0
        // even if it's scrolled. Must do getScrollY() from the ScrollView instead.
        if (this.getParent() instanceof ScrollView) {
            ScrollView scrollView = (ScrollView) this.getParent();
            y += scrollView.getScrollY();

            // do this to compensate for the height of the status bar
            final int[] coords = this.mTempCoords;
            scrollView.getLocationInWindow(coords);
            y -= coords[1];
        }

        final int line = this.getLayout().getLineForVertical(y);
        final int offset = this.getOffsetForHorizontal(line, x);
        return offset;
    }

    // //////////////////////////////////////////////
    // copied & modified from Android source code //
    // //////////////////////////////////////////////

    /**
     * Get the offset character closest to the specified absolute position. If the resulting offset
     * is too close to the previous offset, return the previous offset instead.
     *
     * @param x
     *     raw x
     * @param y
     *     raw y
     * @param previousOffset
     *     previous offset
     *
     * @return offset of the specified (x,y)
     */
    private int getHysteresisOffset (int x, int y, int previousOffset) {
        final Layout layout = this.getLayout();
        if (layout == null)
            return -1;

        y -= this.getTotalPaddingTop();
        // Clamp the position to inside of the view.
        y = Math.max(0, y);
        y = Math.min(this.getHeight() - this.getTotalPaddingBottom() - 1, y);
        y += this.getScrollYInternal();

        int line = this.getLayout().getLineForVertical(y);

        // this block is required because of how Android Layout for
        // TextView works - if 'offset' equals to the last character of a line, then
        //
        // * getLineForOffset(offset) will result the NEXT line
        // * getPrimaryHorizontal(offset) will return 0 because the next insertion point is on
        // the next line
        // * getOffsetForHorizontal(line, x) will not return the last offset of a line no
        // matter where x is
        // These are highly undesired and is worked around with the HACK BLOCK
        //
        // @see Moon+ Reader/Color Note - see how it can't select the last character of a line
        // unless you move
        // the cursor to the beginning of the next line.
        //
        // //////////////////////////////////////////////////////////////////////////////
        if (this.isEndOfLineOffset(previousOffset)) {
            // we have to minus one from the offset so that the code below to find
            // the previous line can work correctly.
            int left = (int) layout.getPrimaryHorizontal(previousOffset - 1);
            int right = (int) layout.getLineRight(line);
            int threshold = (right - left) / 2; // half the width of the last character
            if (x > right - threshold) {
                previousOffset -= 1;
            }
        }
        // /////////////////////////////////////////////////////////////////////////////////

        final int previousLine = layout.getLineForOffset(previousOffset);
        final int previousLineTop = layout.getLineTop(previousLine);
        final int previousLineBottom = layout.getLineBottom(previousLine);
        final int hysteresisThreshold = (previousLineBottom - previousLineTop) / 2;

        // If new line is just before or after previous line and y position is less than
        // hysteresisThreshold away from previous line, keep cursor on previous line.
        if (((line == previousLine + 1) && ((y - previousLineBottom) < hysteresisThreshold)) ||
            ((line == previousLine - 1) && ((previousLineTop - y) < hysteresisThreshold))) {
            line = previousLine;
        }

        int offset = this.getOffsetForHorizontal(line, x);

        // This allow the user to select the last character of a line without moving the
        // cursor to the next line. (As Layout.getOffsetForHorizontal does not return the
        // offset of the last character of the specified line)
        //
        // But this function will probably get called again immediately, must decrement the
        // offset
        // by 1 to compensate for the change made below. (see previous block)
        // /////////////////////////////////////////////////////////////////////////////
        if (offset < this.getText().length() - 1) {
            if (this.isEndOfLineOffset(offset + 1)) {
                int left = (int) layout.getPrimaryHorizontal(offset);
                int right = (int) layout.getLineRight(line);
                int threshold = (right - left) / 2; // half the width of the last character
                if (x > right - threshold) {
                    offset += 1;
                }
            }
        }
        // ////////////////////////////////////////////////////////////////////////////////

        return offset;
    }

    /**
     * Checks whether the specified offset is at the end of a line. <p/> PRECONDITION: assumes
     * layout exists and is valid
     *
     * @param offset
     *     the offset to check
     *
     * @return true if the offset is at the end of a line, false otherwise.
     */
    private boolean isEndOfLineOffset (int offset) {
        if (offset > 0) {
            return this.getLayout().getLineForOffset(offset) == this.getLayout().getLineForOffset(
                offset - 1) + 1;
        }
        return false;
    }

    // //////////////////////////////////////////////
    // copied & modified from Android source code //
    // //////////////////////////////////////////////
    private int getOffsetForHorizontal (int line, int x) {
        x -= this.getTotalPaddingLeft();
        // Clamp the position to inside of the view.
        x = Math.max(0, x);
        x = Math.min(this.getWidth() - this.getTotalPaddingRight() - 1, x);
        x += this.getScrollX();

        // if the textview is inside a scrollview, the above getScrollX() will return 0
        // even if it's scrolled. Must do getScrollX() from the ScrollView instead.
        if (this.getParent() instanceof ScrollView) {
            ScrollView scrollView = (ScrollView) this.getParent();
            x += scrollView.getScrollX();
        }

        return this.getLayout().getOffsetForHorizontal(line, x);
    }

    /**
     * get the (x,y) screen coordinates from the specified offset.
     *
     * @param offset
     *     the offset
     * @param scroll_y
     *     the scroll distant to take away
     * @param coords
     *     the returned x, y coordinate array, must have a length of 2
     */
    private void getXY (int offset, int scroll_y, int[] coords) {
        assert (coords.length >= 2);

        coords[0] = coords[1] = -1;
        Layout layout = this.getLayout();

        if (layout != null) {
            int line = layout.getLineForOffset(offset);
            int base = layout.getLineBottom(line);

            coords[0] = (int) layout.getPrimaryHorizontal(offset); // x
            coords[1] = base - scroll_y; // y
        }
    }

    /**
     * Get the (x,y) screen coordinate from the specified offset. If the specified offset is beyond
     * the end of the line, move the offset to the beginning of the next line.
     *
     * @param offset
     *     the offset
     * @param scroll_y
     *     the scroll distance to take away.
     * @param coords
     *     the returned x, y coordinate array, muust have a length of 2
     */
    private void getAdjusteStartXY (int offset, int scroll_y, int[] coords) {
        if (offset < this.getText().length()) {
            final Layout layout = this.getLayout();
            if (layout != null) {
                if (this.isEndOfLineOffset(offset + 1)) {
                    float a = layout.getPrimaryHorizontal(offset);
                    float b = layout.getLineRight(layout.getLineForOffset(offset));
                    if (a == b) {
                        // this means the we encounter a new line character, i think.
                        offset += 1;
                    }
                }
            }
        }
        this.getXY(offset, scroll_y, coords);
    }

    /**
     * Get the (x,y) screen coordinate from the specified offset. If the offset is the at the end of
     * a wrapped line, return the (x,y) at the end of that line instead of the (x, y) at the
     * beginning of the next line (which is the default behaviour for Android)
     *
     * @param offset
     *     the offset
     * @param scroll_y
     *     the scroll distance to take away
     * @param coords
     *     the returned x, y coordinate array, must have a length of 2
     */
    private void getAdjustedEndXY (int offset, int scroll_y, int[] coords) {
        if (offset > 0) {
            final Layout layout = this.getLayout();
            if (layout != null) {
                // if (this_line > prev_line) {
                if (this.isEndOfLineOffset(offset)) {
                    // if we are at the end of a line, calculate the X using getLineRight instead of
                    // getPrimaryHorizontal.
                    // (Because getPrimaryHorizontal returns 0 for offset sitting at the end of a
                    // line.
                    // getPrimaryHorizontal returns the next insertion point, which will be the next
                    // line)
                    int prev_line = layout.getLineForOffset(offset - 1);
                    float right = layout.getLineRight(prev_line);
                    int y = layout.getLineBottom(prev_line);
                    coords[0] = (int) right;
                    coords[1] = y - scroll_y;
                    return;
                }
            }
        }
        this.getXY(offset, scroll_y, coords);
    }

    public void hideCursor () {
        this.mSelectionController.hide();
    }

    // //////////////////////////////////////////////////////////////////////////
    // //////////////////////////////////////////////////////////////////////////
    // ///////////////////////////INNER CLASSES//////////////////////////////////
    // /////// copied, heavily modified and simplified from TextView ////////////
    // //////////////////////////////////////////////////////////////////////////

    /**
     * Manages the two cursors that control the selection. Internal used only. For outsider, please
     * use SelectionModifier
     */
    private class SelectionCursorController implements ViewTreeObserver.OnTouchModeChangeListener {

        /**
         * The two cursor handle that controls the selection. Note that the two cursors are allowed
         * to swap positions and thus the name of the handle has no bearing on the relative position
         * of the handle to each other. (e.g. mStartHandle can be positioned leagally at an offset
         * greater than the offset mEndHandle is resting on)
         */
        private CursorHandle mStartHandle;
        private CursorHandle mEndHandle;

        /**
         * whether the selection controller is displaying on the screen
         */
        private boolean mIsShowing;

        public SelectionCursorController () {
            this.mStartHandle = new CursorHandle(this);
            this.mEndHandle = new CursorHandle(this);
        }

        /**
         * snap the cursors to the current selection
         */
        public void snapToSelection () {

            if (this.mIsShowing) {
                int a = SelectableTextView.this.getSelection().getStart();
                int b = SelectableTextView.this.getSelection().getEnd();

                int start = Math.min(a, b);
                int end = Math.max(a, b);

                // find the corresponding handle for the start/end calculated above
                CursorHandle startHandle = start == a ? this.mStartHandle : this.mEndHandle;
                CursorHandle endHandle = end == b ? this.mEndHandle : this.mStartHandle;

                final int[] coords = SelectableTextView.this.mTempCoords;
                int scroll_y = SelectableTextView.this.getScrollYInternal();

                SelectableTextView.this.getAdjusteStartXY(start, scroll_y, coords);
                startHandle.pointTo(coords[0], coords[1]);

                SelectableTextView.this.getAdjustedEndXY(end, scroll_y, coords);
                endHandle.pointTo(coords[0], coords[1]);
            }
        }

        /**
         * show the selection cursor at the specified offsets and select the text between the
         * specified offsets.
         *
         * @param start
         *     the offset of the first cursor
         * @param end
         *     the offset of the second cursor
         */
        public void show (int start, int end) {
            show(start, end, true);
        }

        /**
         * @param start
         *     the offset of the first cursor
         * @param end
         *     the offset of the second cursor
         * @param hasHandle
         *     show cursor or not
         */
        public void show (int start, int end, boolean hasHandle) {
            int a = Math.min(start, end);
            int b = Math.max(start, end);

            final int[] coords = SelectableTextView.this.mTempCoords;
            int scroll_y = SelectableTextView.this.getScrollY();

            if (hasHandle) {
                SelectableTextView.this.getAdjusteStartXY(a, scroll_y, coords);
                this.mStartHandle.show(coords[0], coords[1]);

                SelectableTextView.this.getAdjustedEndXY(b, scroll_y, coords);
                this.mEndHandle.show(coords[0], coords[1]);
            }

            this.mIsShowing = true;
            this.select(a, b);
        }

        /**
         * hide the cursor and selection
         */
        public void hide () {
            if (this.mIsShowing) {
                SelectableTextView.this.removeSelection();
                this.mStartHandle.hide();
                this.mEndHandle.hide();
                this.mIsShowing = false;
            }
        }

        /**
         * redraw the moving cursor and update the selection if required
         *
         * @param cursorHandle
         *     the CursorHandle (LEFT or RIGHT)
         * @param x
         *     the x coordinate the cursor is pointing to on the screen (raw)
         * @param y
         *     the y coordinate the cursor is pointing to on the screen (raw)
         * @param oldx
         *     the previous x position the cursor pointed to
         * @param oldy
         *     the previous y position the cursor pointed to
         */
        public void updatePosition (CursorHandle cursorHandle, int x, int y, int oldx, int oldy) {
            if (!this.mIsShowing) {
                return;
            }

            int old_offset =
                cursorHandle == this.mStartHandle ?
                    SelectableTextView.this.getSelection().getStart() :
                    SelectableTextView.this.getSelection().getEnd();

            int offset = SelectableTextView.this.getHysteresisOffset(x, y, old_offset);

            if (offset != old_offset) {

                if (cursorHandle == this.mStartHandle) {
                    SelectableTextView.this.getSelection().setStart(offset);
                } else {
                    SelectableTextView.this.getSelection().setEnd(offset);
                }
                SelectableTextView.this.getSelection().select();
            }

            cursorHandle.pointTo(x, y);
        }

        /**
         * Select the textview between start and end. <p/> Precondition: start, end must be valid
         *
         * @param start
         *     the starting offset
         * @param end
         *     the ending offset
         */
        private void select (int start, int end) {
            SelectableTextView.this.setSelection(Math.min(start, end), Math.abs(end - start));
        }

        @SuppressWarnings ("unused")
        public boolean isShowing () {
            return this.mIsShowing;
        }

        @Override
        public void onTouchModeChanged (boolean isInTouchMode) {
            if (!isInTouchMode) {
                this.hide();
            }
        }

        public void setSelectionDrawable (Drawable drawable) {
            if (this.mStartHandle != null) {
                this.mStartHandle.setSelectionDrawable(drawable);
            }

            if (this.mEndHandle != null) {
                this.mEndHandle.setSelectionDrawable(drawable);
            }
        }
    }

    /**
     * represents a single cursor
     */
    private class CursorHandle extends View {

        /**
         * the {@link PopupWindow} containing the cursor drawable
         */
        private final PopupWindow mContainer;

        /**
         * the drawble of the cursor
         */
        private Drawable mDrawable;

        /**
         * whether the user is dragging the cursor
         */
        @SuppressWarnings ("unused")
        private boolean mIsDragging;

        /**
         * the controller that's controlling the cursor
         */
        private SelectionCursorController mController;

        /**
         * the height of the cursor
         */
        private int mHeight;

        /**
         * the width of the cursor
         */
        private int mWidth;

        /**
         * the x coordinate of the "pointer" of the cursor
         */
        private int mHotspotX;

        /**
         * the y coordinate of the "pointer" of the cursor which is usually the top, so it's zero.
         */
        private int mHotspotY;

        /**
         * Adjustment to add to the Raw x, y coordinate of the touch position to get the location of
         * where the cursor is pointing to
         */
        private int mAdjustX;
        private int mAdjustY;

        private int mOldX;
        private int mOldY;

        public CursorHandle (SelectionCursorController controller) {
            super(SelectableTextView.this.getContext());

            this.mController = controller;

            this.mDrawable = this.getResources().getDrawable(R.drawable.cursor);

            this.mContainer = new PopupWindow(this);
            this.mContainer.setClippingEnabled(false);

      /*
       * My Note getIntrinsicWidth() returns the width of the drawable after it has been
       * scaled to the current device's density e.g. if the drawable is a 15 x 20 image
       * and we load the image on a Nexus 4 (which has a density of 2.0),
       * getIntrinsicWidth() shall return 15 * 2 = 30
       */
            this.mHeight = this.mDrawable.getIntrinsicHeight();
            this.mWidth = this.mDrawable.getIntrinsicWidth();

            // the PopupWindow has an initial dimension of (0, 0)
            // must set the width/height of the popupwindow in order for it to be drawn
            this.mContainer.setWidth(this.mWidth);
            this.mContainer.setHeight(this.mHeight);

            // this is the location of where the pointer is relative to the cursor itself
            // if the left and right cursor are different, mHotspotX will need to be calculated
            // differently for each cursor. Currently, I'm using the same left and right cursor
            this.mHotspotX = this.mWidth / 2;
            this.mHotspotY = 0;

            this.invalidate();
        }

        public void setSelectionDrawable (Drawable drawable) {
            if (drawable == null) {
                return;
            }

            this.mDrawable = drawable;
        }

        @Override
        protected void onMeasure (int widthMeasureSpec, int heightMeasureSpec) {
            this.setMeasuredDimension(this.mWidth, this.mHeight);
        }

        @Override
        protected void onDraw (Canvas canvas) {
            this.mDrawable.setBounds(0, 0, this.mWidth, this.mHeight);
            this.mDrawable.draw(canvas);
        }

        @Override
        public boolean onTouchEvent (MotionEvent event) {

            int rawX = (int) event.getRawX();
            int rawY = (int) event.getRawY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                    // calculate distance from the (x,y) of the finger to where the cursor
                    // points to
                    this.mAdjustX = this.mHotspotX - (int) event.getX();
                    this.mAdjustY = this.mHotspotY - (int) event.getY();
                    this.mOldX = this.mAdjustX + rawX;
                    this.mOldY = this.mAdjustY + rawY;

                    this.mIsDragging = true;
                    break;
                }
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL: {
                    this.mIsDragging = false;
                    this.mController.snapToSelection();
                    break;
                }
                case MotionEvent.ACTION_MOVE: {
                    // calculate the raw (x, y) the cursor is POINTING TO
                    int x = this.mAdjustX + rawX;
                    int y = this.mAdjustY + rawY;

                    this.mController.updatePosition(this, x, y, this.mOldX, this.mOldY);

                    this.mOldX = x;
                    this.mOldY = y;
                    break;
                }
            }
            return true; // consume the event
        }

        public boolean isShowing () {
            return this.mContainer.isShowing();
        }

        /**
         * Show the cursor pointing to the specified point.
         *
         * @param x
         *     the x coordinate of the point relative to the TextView
         * @param y
         *     the y coordinate of the point relative to the TextView
         */
        public void show (int x, int y) {
            final int[] coords = SelectableTextView.this.mTempCoords;
            SelectableTextView.this.getLocationInWindow(coords);

            coords[0] += x - this.mHotspotX;
            coords[1] += y - this.mHotspotY;
            this.mContainer.showAtLocation(
                SelectableTextView.this,
                Gravity.NO_GRAVITY,
                coords[0],
                coords[1]);
        }

        /**
         * move the cursor to point the (x,y) location on the screen.
         *
         * @param x
         *     the x coordinate on the screen
         * @param y
         *     the y coordinate on the screen
         */
        private void pointTo (int x, int y) {
            if (this.isShowing()) {
                this.mContainer.update(x - this.mHotspotX, y - this.mHotspotY, -1, -1);
            }
        }

        /**
         * hide this cursor
         */
        public void hide () {
            this.mIsDragging = false;
            this.mContainer.dismiss();
        }

    }
}
