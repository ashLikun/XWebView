package com.ashlikun.xwebview.webview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import android.view.ViewParent;
import android.webkit.WebView;
import android.widget.OverScroller;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.NestedScrollingChild3;
import androidx.core.view.NestedScrollingChildHelper;
import androidx.core.view.ViewCompat;
import androidx.customview.widget.ViewDragHelper;

/**
 * 作者　　: 李坤
 * 创建时间: 2020/6/19　16:14
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：
 */
public class NestedWebView extends WebView implements NestedScrollingChild3 {
    private NestedScrollingChildHelper mScrollingChildHelper;
    private int mLastMotionY;
    private int mLastMotionX;
    /**
     * 用于跟踪触摸事件速度的辅助类，用于实现
     * fling 和其他类似的手势。
     */
    private VelocityTracker mVelocityTracker;
    /**
     * True if the user is currently dragging this ScrollView around. This is
     * not the same as 'is being flinged', which can be checked by
     * mScroller.isFinished() (flinging begins when the user lifts his finger).
     */
    private boolean mIsBeingDragged = false;
    /**
     * ID of the active pointer. This is used to retain consistency during
     * drags/flings if multiple pointers are used.（多点触控有用）
     */
    private int mActivePointerId = ViewDragHelper.INVALID_POINTER;
    private int mTouchSlop;
    private int mMinimumVelocity;
    private int mMaximumVelocity;
    private final int[] mScrollOffset = new int[2];
    private final int[] mScrollConsumed = new int[2];
    private OverScroller mScroller;
    private int mNestedYOffset;
    private int mLastScrollerY;

    public NestedWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mScroller = new OverScroller(getContext());
        final ViewConfiguration configuration = ViewConfiguration.get(getContext());
        mTouchSlop = configuration.getScaledTouchSlop();
        mMinimumVelocity = configuration.getScaledMinimumFlingVelocity();
        mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
        //设置支持嵌套滑动
        setNestedScrollingEnabled(true);
    }

    public NestedWebView(Context context) {
        this(context, null);
    }

    private NestedScrollingChildHelper getScrollingChildHelper() {
        if (mScrollingChildHelper == null) {
            mScrollingChildHelper = new NestedScrollingChildHelper(this);
        }
        return mScrollingChildHelper;
    }

    private void initOrResetVelocityTracker() {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        } else {
            mVelocityTracker.clear();
        }
    }

    private void initVelocityTrackerIfNotExists() {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!isNestedScrollingEnabled() || !getScrollingChildHelper().hasNestedScrollingParent(ViewCompat.TYPE_TOUCH)) {
            return super.onInterceptTouchEvent(ev);
        }
        /*
         * This method JUST determines whether we want to intercept the motion.
         * If we return true, onMotionEvent will be called and we do the actual
         * scrolling there.
         */

        /*
         * Shortcut the most recurring case: the user is in the dragging
         * state and he is moving his finger.  We want to intercept this
         * motion.
         */
        final int action = ev.getAction();
        if ((action == MotionEvent.ACTION_MOVE) && mIsBeingDragged) {
            return true;
        }

        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_MOVE: {
                /*
                 * mIsBeingDragged == false, otherwise the shortcut would have caught it. Check
                 * whether the user has moved far enough from his original down touch.
                 */

                /*
                 * Locally do absolute value. mLastMotionY is set to the y value
                 * of the down event.
                 */
                final int activePointerId = mActivePointerId;
                if (activePointerId == ViewDragHelper.INVALID_POINTER) {
                    // If we don't have a valid id, the touch down wasn't on content.
                    break;
                }

                final int pointerIndex = ev.findPointerIndex(activePointerId);
                if (pointerIndex == -1) {
                    break;
                }

                final int y = (int) ev.getY(pointerIndex);
                final int x = (int) ev.getX(pointerIndex);
                final int yDiff = Math.abs(y - mLastMotionY);
                final int xDiff = Math.abs(x - mLastMotionX);
                if (yDiff > mTouchSlop) {
                    mLastMotionY = y;
                }
                if (xDiff > mTouchSlop) {
                    mLastMotionX = x;
                }
                if (Math.abs(xDiff) < Math.abs(yDiff)) {
                    mIsBeingDragged = true;
                    initOrResetVelocityTracker();
                    mVelocityTracker.addMovement(ev);
                    mNestedYOffset = 0;
                    final ViewParent parent = getParent();
                    if (parent != null) {
                        parent.requestDisallowInterceptTouchEvent(true);
                    }
                }
                break;
            }

            case MotionEvent.ACTION_DOWN: {
                final int y = (int) ev.getY();
                final int x = (int) ev.getX();
                /*
                 * Remember location of down touch.
                 * ACTION_DOWN always refers to pointer index 0.
                 */
                mLastMotionY = y;
                mLastMotionX = x;
                mActivePointerId = ev.getPointerId(0);

                initOrResetVelocityTracker();
                mVelocityTracker.addMovement(ev);
                /*
                 * If being flinged and user touches the screen, initiate drag;
                 * otherwise don't. mScroller.isFinished should be false when
                 * being flinged. We need to call computeScrollOffset() first so that
                 * isFinished() is correct.
                 */
                mScroller.computeScrollOffset();
                mIsBeingDragged = !mScroller.isFinished();
                startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL, ViewCompat.TYPE_TOUCH);
                break;
            }

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                /* Release the drag */
                mIsBeingDragged = false;
                mActivePointerId = ViewDragHelper.INVALID_POINTER;
                recycleVelocityTracker();
                stopNestedScroll(ViewCompat.TYPE_TOUCH);
                break;
            case MotionEvent.ACTION_POINTER_UP:
                onSecondaryPointerUp(ev);
                break;
        }

        /*
         * The only time we want to intercept motion events is if we are in the
         * drag mode.
         */
        return mIsBeingDragged;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isNestedScrollingEnabled() || !getScrollingChildHelper().hasNestedScrollingParent(ViewCompat.TYPE_TOUCH)) {
            return super.onTouchEvent(event);
        }
        initVelocityTrackerIfNotExists();
        boolean eventAddedToVelocityTracker = false;
        MotionEvent vtev = MotionEvent.obtain(event);//复制一个event
        final int actionMasked = event.getActionMasked();//类似getAction
        boolean result = false;
        if (actionMasked == MotionEvent.ACTION_DOWN) {
            mNestedYOffset = 0;
        }
        vtev.offsetLocation(0, mNestedYOffset);

        switch (actionMasked) {
            case MotionEvent.ACTION_DOWN:
                if ((mIsBeingDragged = !mScroller.isFinished())) {
                    final ViewParent parent = getParent();
                    if (parent != null) {
                        parent.requestDisallowInterceptTouchEvent(true);//不让父布局拦截事件
                    }
                }

                /*
                 * If being flinged and user touches, stop the fling. isFinished
                 * will be false if being flinged.//如果在fling 的过程中用户触摸屏幕，则停止fling
                 */
                if (!mScroller.isFinished()) {
                    abortAnimatedScroll();
                }

                // Remember where the motion event started
                mLastMotionY = (int) event.getY();
                mLastMotionX = (int) event.getX();
                mActivePointerId = event.getPointerId(0);
                startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL, ViewCompat.TYPE_TOUCH);
                result = super.onTouchEvent(event);
                break;
            case MotionEvent.ACTION_MOVE:

                final int activePointerIndex = event.findPointerIndex(mActivePointerId);
                if (activePointerIndex == -1) {
                    break;
                }
                final int y = (int) event.getY(activePointerIndex);
                final int x = (int) event.getX(activePointerIndex);

                int deltaY = mLastMotionY - y;
                final int yDiff = Math.abs(y - mLastMotionY);
                final int xDiff = Math.abs(x - mLastMotionX);
                if (dispatchNestedPreScroll(0, deltaY, mScrollConsumed, mScrollOffset, ViewCompat.TYPE_TOUCH)) {
                    //纵轴位移- 被父布局消费的滑动距离
                    deltaY -= mScrollConsumed[1];
                    vtev.offsetLocation(0, mScrollOffset[1]);
                }
                if (!mIsBeingDragged && Math.abs(deltaY) > mTouchSlop && (Math.abs(xDiff) < Math.abs(yDiff))) {
                    final ViewParent parent = getParent();
                    if (parent != null) {
                        parent.requestDisallowInterceptTouchEvent(true);
                    }
                    mIsBeingDragged = true;
                }

                if (mIsBeingDragged) {
                    //上一次的坐标
                    mLastMotionY = y - mScrollOffset[1];
                    int scrolledDeltaY = 0;
                    int unconsumedY = deltaY;
                    if (Math.abs(deltaY) > 0) {
                        if (deltaY <= 0) {
                            //向顶部滑动
                            if (canScrollVertically(-1)) {
                                if (getScrollY() + deltaY < 0) {
                                    scrolledDeltaY = -getScrollY();
                                    unconsumedY = getScrollY() + deltaY;
                                    //这行不知对不对
                                    vtev.offsetLocation(0, unconsumedY);
                                    mNestedYOffset += unconsumedY;
                                } else {
                                    scrolledDeltaY = deltaY;
                                    unconsumedY = 0;
                                }
                            }
                        } else {
                            //向底部滑动
                            if (canScrollVertically(1)) {
                                if (deltaY - getTop() > 0) {
                                    scrolledDeltaY = deltaY - getTop();
                                    unconsumedY = getTop();
                                    //这行不知对不对
                                    vtev.offsetLocation(0, unconsumedY);
                                    mNestedYOffset += unconsumedY;
                                } else {
                                    scrolledDeltaY = deltaY;
                                    unconsumedY = 0;
                                }
                            }
                        }
                    }
                    mScrollConsumed[1] = 0;

                    dispatchNestedScroll(0, scrolledDeltaY, 0, unconsumedY, mScrollOffset,
                            ViewCompat.TYPE_TOUCH, mScrollConsumed);

                    mLastMotionY -= mScrollOffset[1];
                    mNestedYOffset += mScrollOffset[1];

                    vtev.offsetLocation(0, mScrollOffset[1]);
                    mNestedYOffset += mScrollOffset[1];
                }
                if (deltaY == 0 && mIsBeingDragged) {
                    result = true;
                } else {
                    result = super.onTouchEvent(vtev);
                }

                break;
            case MotionEvent.ACTION_UP:
                if (mVelocityTracker != null) {
                    mVelocityTracker.addMovement(vtev);
                }
                eventAddedToVelocityTracker = true;
                boolean flingRes = caculateV(mActivePointerId, (int) event.getY());
                mActivePointerId = ViewDragHelper.INVALID_POINTER;

                if (mVelocityTracker != null) {
                    mVelocityTracker.clear();
                }
                if (!mIsBeingDragged || !flingRes) {
                    result = super.onTouchEvent(vtev);
                }
                endDrag();
                break;
            case MotionEvent.ACTION_CANCEL:
                mActivePointerId = ViewDragHelper.INVALID_POINTER;
                if (mVelocityTracker != null) {
                    mVelocityTracker.clear();
                }
                stopNestedScroll(ViewCompat.TYPE_TOUCH);
                result = super.onTouchEvent(vtev);
                endDrag();
                break;
        }
        if (!eventAddedToVelocityTracker) {
            if (mVelocityTracker != null) {
                mVelocityTracker.addMovement(vtev);
            }
        }
        vtev.recycle();
        return result;
    }

    private void onSecondaryPointerUp(MotionEvent ev) {
        final int pointerIndex = ev.getActionIndex();
        final int pointerId = ev.getPointerId(pointerIndex);
        if (pointerId == mActivePointerId) {
            // This was our active pointer going up. Choose a new
            // active pointer and adjust accordingly.
            // TODO: Make this decision more intelligent.
            final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
            mLastMotionY = (int) ev.getY(newPointerIndex);
            mActivePointerId = ev.getPointerId(newPointerIndex);
            if (mVelocityTracker != null) {
                mVelocityTracker.clear();
            }
        }
    }


    /**
     * 处理fling 速度问题
     *
     * @param mActivePointerId
     * @param curY
     */
    private boolean caculateV(int mActivePointerId, int curY) {
        mVelocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
        int initialVelocityY = (int) mVelocityTracker.getYVelocity(mActivePointerId);
        int initialVelocityX = (int) mVelocityTracker.getXVelocity(mActivePointerId);
        if ((Math.abs(initialVelocityY) >= mMinimumVelocity) && Math.abs(initialVelocityX) < Math.abs(initialVelocityY)) {
            if (!dispatchNestedPreFling(0, -initialVelocityY)) {
                dispatchNestedFling(0, -initialVelocityY, true);
                fling(-initialVelocityY);
                return true;
            }
        }
        return false;
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (!isNestedScrollingEnabled() || !getScrollingChildHelper().hasNestedScrollingParent(ViewCompat.TYPE_TOUCH)) {
            return;
        }
        if (mScroller.isFinished()) {
            return;
        }

        mScroller.computeScrollOffset();
        final int y = mScroller.getCurrY();
        int unconsumed = y - mLastScrollerY;
        mLastScrollerY = y;

        // Nested Scrolling Pre Pass
        mScrollConsumed[1] = 0;
        dispatchNestedPreScroll(0, unconsumed, mScrollConsumed, null,
                ViewCompat.TYPE_NON_TOUCH);
        unconsumed -= mScrollConsumed[1];


        if (unconsumed != 0) {
            // Internal Scroll
            final int oldScrollY = getScrollY();
            if (canScrollVertically(unconsumed)) {
                scrollBy(0, unconsumed);
            }
            final int scrolledByMe = getScrollY() - oldScrollY;
            unconsumed -= scrolledByMe;
            // Nested Scrolling Post Pass
            mScrollConsumed[1] = 0;
            dispatchNestedScroll(0, scrolledByMe, 0, unconsumed, mScrollOffset,
                    ViewCompat.TYPE_NON_TOUCH, mScrollConsumed);
            unconsumed -= mScrollConsumed[1];
        }

        if (unconsumed != 0) {
            abortAnimatedScroll();
        }

        if (!mScroller.isFinished()) {
            ViewCompat.postInvalidateOnAnimation(this);
        } else {
            stopNestedScroll(ViewCompat.TYPE_NON_TOUCH);
        }
    }

    public void fling(int velocityY) {
        mScroller.fling(getScrollX(), getScrollY(), // start
                0, velocityY, // velocities
                0, 0, // x
                Integer.MIN_VALUE, Integer.MAX_VALUE, // y
                0, 0); // overscroll
        runAnimatedScroll(true);
    }

    private void runAnimatedScroll(boolean participateInNestedScrolling) {
        if (participateInNestedScrolling) {
            startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL, ViewCompat.TYPE_NON_TOUCH);
        } else {
            stopNestedScroll(ViewCompat.TYPE_NON_TOUCH);
        }
        mLastScrollerY = getScrollY();
        ViewCompat.postInvalidateOnAnimation(this);
    }

    private void abortAnimatedScroll() {
        mScroller.abortAnimation();
        stopNestedScroll(ViewCompat.TYPE_NON_TOUCH);
    }

    private void endDrag() {
        mIsBeingDragged = false;
        recycleVelocityTracker();
        stopNestedScroll(ViewCompat.TYPE_TOUCH);
    }

    private void recycleVelocityTracker() {
        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    @Override
    public boolean startNestedScroll(int axes, int type) {
        return getScrollingChildHelper().startNestedScroll(axes, type);
    }

    @Override
    public void stopNestedScroll(int type) {
        getScrollingChildHelper().stopNestedScroll(type);
    }

    @Override
    public boolean hasNestedScrollingParent(int type) {
        return getScrollingChildHelper().hasNestedScrollingParent(type);
    }

    @Override
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, @Nullable int[] offsetInWindow, int type) {
        return getScrollingChildHelper().dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow, type);
    }

    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, @Nullable int[] consumed, @Nullable int[] offsetInWindow, int type) {
        return getScrollingChildHelper().dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow, type);
    }

    @Override
    public void setNestedScrollingEnabled(boolean enabled) {
        getScrollingChildHelper().setNestedScrollingEnabled(enabled);
    }

    @Override
    public boolean isNestedScrollingEnabled() {
        return getScrollingChildHelper().isNestedScrollingEnabled();
    }

    @Override
    public boolean startNestedScroll(int axes) {
        return getScrollingChildHelper().startNestedScroll(axes);
    }

    @Override
    public void stopNestedScroll() {
        getScrollingChildHelper().stopNestedScroll();
    }

    @Override
    public boolean hasNestedScrollingParent() {
        return getScrollingChildHelper().hasNestedScrollingParent();
    }

    @Override
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, @Nullable int[] offsetInWindow) {
        return getScrollingChildHelper().dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow);
    }

    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, @Nullable int[] consumed, @Nullable int[] offsetInWindow) {
        return getScrollingChildHelper().dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow);
    }

    @Override
    public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed) {
        return getScrollingChildHelper().dispatchNestedFling(velocityX, velocityY, consumed);
    }

    @Override
    public boolean dispatchNestedPreFling(float velocityX, float velocityY) {
        return getScrollingChildHelper().dispatchNestedPreFling(velocityX, velocityY);
    }
    // NestedScrollingChild3

    @Override
    public void dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed,
                                     int dyUnconsumed, @Nullable int[] offsetInWindow, int type, @NonNull int[] consumed) {
        getScrollingChildHelper().dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed,
                offsetInWindow, type, consumed);
    }
}
