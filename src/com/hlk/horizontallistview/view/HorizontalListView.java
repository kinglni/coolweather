package com.hlk.horizontallistview.view;

import java.util.LinkedList;
import java.util.Queue;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.Scroller;

public class HorizontalListView extends AdapterView<ListAdapter> {  
  
    public boolean mAlwaysOverrideTouch = true;  
    protected ListAdapter mAdapter;  
    private int mLeftViewIndex = -1;  
    private int mRightViewIndex = 0;  
    protected int mCurrentX;  
    protected int mNextX;  
    private int mMaxX = Integer.MAX_VALUE;  
    private int mDisplayOffset = 0;  
    protected Scroller mScroller;  //（跑马灯）收集数据用于产生一个照片胶卷动态效果。
    private GestureDetector mGesture;  //通知用户发生的各种操作。（手势通知器）
    private Queue<View> mRemovedViewQueue = new LinkedList<View>();  //用于在进程之前存放元素。（队列）
    private OnItemSelectedListener mOnItemSelected;  
    private OnItemClickListener mOnItemClicked;  
    private OnItemLongClickListener mOnItemLongClicked;  
    private boolean mDataChanged = false;  
      
  
    public HorizontalListView(Context context, AttributeSet attrs) {  
    	//传入上下文Context和属性设置集合AttributeSet。
        super(context, attrs);  
        initView();  
    }  
      
    private synchronized void initView() {  
        mLeftViewIndex = -1;  
        mRightViewIndex = 0;  
        mDisplayOffset = 0;  
        mCurrentX = 0;  
        mNextX = 0;  
        mMaxX = Integer.MAX_VALUE;  
        mScroller = new Scroller(getContext());  
        mGesture = new GestureDetector(getContext(), mOnGesture);  
    }  
      
    @Override  
    public void setOnItemSelectedListener(AdapterView.OnItemSelectedListener listener) {  
        mOnItemSelected = listener;  
    }  
      
    @Override  
    public void setOnItemClickListener(AdapterView.OnItemClickListener listener){  
        mOnItemClicked = listener;  
    }  
      
    @Override  
    public void setOnItemLongClickListener(AdapterView.OnItemLongClickListener listener) {  
        mOnItemLongClicked = listener;  
    }  //如果返回false那么click仍然会被调用。而且是先调用Long click，然后调用click。 

//如果返回true那么click就会被吃掉，click就不会再被调用了
  
    private DataSetObserver mDataObserver = new DataSetObserver() {  
  
        @Override  
        public void onChanged() {  
            synchronized(HorizontalListView.this){  
                mDataChanged = true;  
            }  
            invalidate();  
            requestLayout();  
        }  
  
        @Override  
        public void onInvalidated() {  
            reset();  
            invalidate();  
            requestLayout();  
        }  
          
    };  
  
    @Override  
    public ListAdapter getAdapter() {  
        return mAdapter;  
    }  
  
    @Override  
    public View getSelectedView() {  
        //TODO: implement  
        return null;  
    }  
  
    @Override  
    public void setAdapter(ListAdapter adapter) { 
        if(mAdapter != null) {  
            mAdapter.unregisterDataSetObserver(mDataObserver);   //注销数据变动监听器
        }  
        mAdapter = adapter;  
        mAdapter.registerDataSetObserver(mDataObserver);  //注册数据变动监听器
        reset();  //重置刷新界面
    }  
      
    private synchronized void reset(){  
        initView();  //初始化
        removeAllViewsInLayout();  //清空视图
        requestLayout();  //当视图的边界需要改变的时候调用
    }  
  
    @Override  
    public void setSelection(int position) {  
        //TODO: implement  
    }  
      
    private void addAndMeasureChild(final View child, int viewPos) {  
        LayoutParams params = child.getLayoutParams();  
        if(params == null) {  
            params = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);  
        }  
  
        addViewInLayout(child, viewPos, params, true);  
        child.measure(MeasureSpec.makeMeasureSpec(getWidth(), MeasureSpec.AT_MOST),  
                MeasureSpec.makeMeasureSpec(getHeight(), MeasureSpec.AT_MOST));  
    }  
      
    @Override  
    protected synchronized void onLayout(boolean changed, int left, int top, int right, int bottom) {  
        super.onLayout(changed, left, top, right, bottom);  
  //onLayout方法：根据获取到的尺寸信息渲染这个view。onMeasure方法执行完后会回调onLayout方法。
 //onMeasure方法：作用是计算各控件的大小。系统在渲染页面时会调用各view的onMeasure方法，
 //各控件的onMeasure方法执行顺序是从内到外，即先调用子控件的onMeasure方法，在执行父布局的onMeasure方法
        if(mAdapter == null){  
            return;  
        }  
          
        if(mDataChanged){  
            int oldCurrentX = mCurrentX;  
            initView();  
            removeAllViewsInLayout();  
            mNextX = oldCurrentX;  
            mDataChanged = false;  
        }  
  
        if(mScroller.computeScrollOffset()){  
            int scrollx = mScroller.getCurrX();  
            mNextX = scrollx;  //x方向的位移量
        }  
          
        if(mNextX <= 0){  
            mNextX = 0;  
            mScroller.forceFinished(true);  
        }  
        if(mNextX >= mMaxX) {  
            mNextX = mMaxX;  
            mScroller.forceFinished(true);  
        }  
          
        int dx = mCurrentX - mNextX;  //
          
        removeNonVisibleItems(dx);  
        fillList(dx);  
        positionItems(dx);  
          
        mCurrentX = mNextX;  
      //根据各种条件判断dx（位移量）。同时执行刷新视图操作。
        if(!mScroller.isFinished()){  
            post(new Runnable(){  
                @Override  
                public void run() {  
                    requestLayout();  
                }  
            });  
              
        }  
    }  
      
    private void fillList(final int dx) {  
        int edge = 0;  
        View child = getChildAt(getChildCount()-1); 
        //ListView.getChildCount()(ViewGroup.getChildCount) 返回的是显示层面上的“所包含的子 View 个数”。
        // 当item个数多 要滚动时 getChildCount()是当前可见的item个数 getCount()是全部。
        //child就是显示的最后一个子视图（只是数量上的最后一个）。
        //根据INDEX获取子视图。
        if(child != null) {  
            edge = child.getRight(); //屏幕最后一个子视图的右边X坐标单位px。 
        }  
        fillListRight(edge, dx); 
        //填右边的视图
          edge = 0;  
        child = getChildAt(0);  
        if(child != null) {  
            edge = child.getLeft();  //最左边的视图（完整视图？）边距PX。
        }  
        fillListLeft(edge, dx);  
      //填左边的视图 
          
    }  
      
    private void fillListRight(int rightEdge, final int dx) {  
        while( rightEdge + dx < getWidth() &&mRightViewIndex < mAdapter.getCount()) {  
            //  rightEdge屏幕能显示的右侧，前半句判断可以不要？测试不影响使用。本句用于判断限制溢出。
            View child = mAdapter.getView(mRightViewIndex, mRemovedViewQueue.poll(), this);  
            addAndMeasureChild(child, -1);  //添加child视图到HorizontalListView
            rightEdge += child.getMeasuredWidth();  
              
            if(mRightViewIndex == mAdapter.getCount()-1) {  
                mMaxX = mCurrentX + rightEdge - getWidth();  
            }  
              
            if (mMaxX < 0) {  
                mMaxX = 0;  
            }  
            mRightViewIndex++;  
        }  
          
    }  
      
    private void fillListLeft(int leftEdge, final int dx) {  
        while(leftEdge + dx > 0 && mLeftViewIndex >= 0) {  
            View child = mAdapter.getView(mLeftViewIndex, mRemovedViewQueue.poll(), this);  
            addAndMeasureChild(child, 0);  
            leftEdge -= child.getMeasuredWidth();  
            mLeftViewIndex--;  
            mDisplayOffset -= child.getMeasuredWidth();  
        }  
    }  
      
    private void removeNonVisibleItems(final int dx) {  
        View child = getChildAt(0);  
        while(child != null && child.getRight() + dx <= 0) {  
            mDisplayOffset += child.getMeasuredWidth();  
            mRemovedViewQueue.offer(child);  
            removeViewInLayout(child);  
            mLeftViewIndex++;  
            child = getChildAt(0);  
              
        }  
          
        child = getChildAt(getChildCount()-1);  
        while(child != null && child.getLeft() + dx >= getWidth()) {  
            mRemovedViewQueue.offer(child);  
            removeViewInLayout(child);  
            mRightViewIndex--;  
            child = getChildAt(getChildCount()-1);  
        }  
    }  
      
    private void positionItems(final int dx) {  
        if(getChildCount() > 0){  
            mDisplayOffset += dx;  
            int left = mDisplayOffset;  
            for(int i=0;i<getChildCount();i++){  
                View child = getChildAt(i);  
                int childWidth = child.getMeasuredWidth();  
                child.layout(left, 0, left + childWidth, child.getMeasuredHeight());  
                left += childWidth + child.getPaddingRight();  
            }  
        }  
    }  
      
    public synchronized void scrollTo(int x) {  
        mScroller.startScroll(mNextX, 0, x - mNextX, 0);  
        requestLayout();  
    }  
      
    @Override  
    public boolean dispatchTouchEvent(MotionEvent ev) {  
        boolean handled = super.dispatchTouchEvent(ev);  
        handled |= mGesture.onTouchEvent(ev);  
        return handled;  
    }  
     /*1）public boolean dispatchTouchEvent(MotionEvent ev)这个方法用来分发TouchEvent
2）public boolean onInterceptTouchEvent(MotionEvent ev)这个方法用来拦截TouchEvent
3）public boolean onTouchEvent(MotionEvent ev)这个方法用来处理TouchEvent*/ 
    protected boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,  
                float velocityY) {  
    	/*onFling（）每秒的速度。
          onScroll（）单位时间的距离。*/
        synchronized(HorizontalListView.this){  
            mScroller.fling(mNextX, 0, (int)-velocityX, 0, 0, mMaxX, 0, 0);  
        }  
        requestLayout();  
          
        return true;  
    }  
      
    protected boolean onDown(MotionEvent e) {  
        mScroller.forceFinished(true);  
        return true;  
    }  
      
    private OnGestureListener mOnGesture = new GestureDetector.SimpleOnGestureListener() {  
  
        @Override  
        public boolean onDown(MotionEvent e) {  
            return HorizontalListView.this.onDown(e);  
        }  
  
        @Override  
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,  
                float velocityY) {  
            return HorizontalListView.this.onFling(e1, e2, velocityX, velocityY);  
        }  
  
        @Override  
        public boolean onScroll(MotionEvent e1, MotionEvent e2,  
                float distanceX, float distanceY) {  
              
            synchronized(HorizontalListView.this){  
                mNextX += (int)distanceX;  
            }  
            requestLayout();  
              
            return true;  
        }  
  
        @Override  
        public boolean onSingleTapConfirmed(MotionEvent e) {  
            for(int i=0;i<getChildCount();i++){  
                View child = getChildAt(i);  
                if (isEventWithinView(e, child)) {  
                    if(mOnItemClicked != null){  
                        mOnItemClicked.onItemClick(HorizontalListView.this, child, mLeftViewIndex + 1 + i, mAdapter.getItemId( mLeftViewIndex + 1 + i ));  
                    }  
                    if(mOnItemSelected != null){  
                        mOnItemSelected.onItemSelected(HorizontalListView.this, child, mLeftViewIndex + 1 + i, mAdapter.getItemId( mLeftViewIndex + 1 + i ));  
                    }  
                    break;  
                }  
                  
            }  
            return true;  
        }  
          
        @Override  
        public void onLongPress(MotionEvent e) {  
            int childCount = getChildCount();  
            for (int i = 0; i < childCount; i++) {  
                View child = getChildAt(i);  
                if (isEventWithinView(e, child)) {  
                    if (mOnItemLongClicked != null) {  
                        mOnItemLongClicked.onItemLongClick(HorizontalListView.this, child, mLeftViewIndex + 1 + i, mAdapter.getItemId(mLeftViewIndex + 1 + i));  
                    }  
                    break;  
                }  
  
            }  
        }  
  
        private boolean isEventWithinView(MotionEvent e, View child) {  
            Rect viewRect = new Rect();  
            int[] childPosition = new int[2];  
            child.getLocationOnScreen(childPosition);  
            int left = childPosition[0];  
            int right = left + child.getWidth();  
            int top = childPosition[1];  
            int bottom = top + child.getHeight();  
            viewRect.set(left, top, right, bottom);  
            return viewRect.contains((int) e.getRawX(), (int) e.getRawY());  
        }  
    };  
  
      
  
}  
