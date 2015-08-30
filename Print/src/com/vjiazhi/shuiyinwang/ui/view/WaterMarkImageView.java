package com.vjiazhi.shuiyinwang.ui.view;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import com.moutian.shuiyinwang.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.FloatMath;
import android.util.TypedValue;
import android.view.MotionEvent;

public class WaterMarkImageView extends ScrollableImageView {
	private final static String TAG = WaterMarkImageView.class.getSimpleName();
	/**
	 * 图片的最大缩放比例
	 */
	public static final float MAX_SCALE = 10.0f;
	
	/**
	 * 图片的最小缩放比例
	 */
	public static final float MIN_SCALE = 0.3f;
	
	/**
	 * 控制缩放，旋转图标所在四个点得位置
	 */
	public static final int LEFT_TOP = 0;
	public static final int RIGHT_TOP = 1;
	public static final int RIGHT_BOTTOM = 2;
	public static final int LEFT_BOTTOM = 3;
	
	/**
	 * 一些默认的常量
	 */
	public static final int DEFAULT_FRAME_PADDING = 4;
	public static final int DEFAULT_FRAME_WIDTH = 1;
	public static final int DEFAULT_FRAME_COLOR = Color.RED;
	public static final float DEFAULT_SCALE = 1.0f;
	public static final float DEFAULT_DEGREE = 0;
	public static final int DEFAULT_CONTROL_LOCATION = RIGHT_TOP;
	public static final boolean DEFAULT_EDITABLE = false;
	
	
	/**
	 * 用于旋转缩放的Bitmap
	 */
	private Bitmap mWaterMarkBitmap;
	
	/**
	 * WaterMarkView的中心点坐标，相对于其父类布局而言的
	 */
	private PointF mCenterPoint = new PointF();
	
	/**
	 * View的宽度和高度，随着图片的旋转而变化(不包括控制旋转，缩放图片的宽高)
	 */
	private int mViewWidth, mViewHeight;
	
	/**
	 * 图片的旋转角度
	 */
	private float mDegree = DEFAULT_DEGREE;
	
	/**
	 * 图片的缩放比例
	 */
	private float mScale = DEFAULT_SCALE;
	
	/**
	 * 用于缩放，旋转，平移的矩阵
	 */
	private Matrix matrix = new Matrix();
	
	/**
	 * WaterMarkView距离父类布局的左间距
	 */
	private int mViewPaddingLeft;
	
	/**
	 * WaterMarkView距离父类布局的上间距
	 */
	private int mViewPaddingTop;
	
	/**
	 * 图片四个点坐标
	 */
	private PointF mLTPoint;
	private PointF mRTPoint;
	private PointF mRBPoint;
	private PointF mLBPoint;
	
	/**
	 * 用于缩放，旋转的控制点的坐标
	 */
	private PointF mControlPoint = new PointF();
	
	/**
	 * 用于缩放，旋转的图标
	 */
	private Drawable controlDrawable;
	
	/**
	 * 缩放，旋转图标的宽和高
	 */
	private int mDrawableWidth, mDrawableHeight;
	
	/**
	 * 画外围框的Path
	 */
	private Path mPath = new Path();
	
	/**
	 * 画外围框的画笔
	 */
	private Paint mPaint ;
	
	/**
	 * 初始状态
	 */
	public static final int STATUS_INIT = 0;
	
	/**
	 * 拖动状态
	 */
	public static final int STATUS_DRAG = 1;
	
	/**
	 * 旋转或者放大状态
	 */
	public static final int STATUS_ROTATE_ZOOM = 2; 
	
	/**
	 * 当前所处的状态
	 */
	private int mStatus = STATUS_INIT;
	
	/**
	 * 外边框与图片之间的间距, 单位是dip
	 */
	private int framePadding = DEFAULT_FRAME_PADDING;
	
	/**
	 * 外边框颜色
	 */
	private int frameColor = DEFAULT_FRAME_COLOR;
	
	/**
	 * 外边框线条粗细, 单位是 dip
	 */
	private int frameWidth = DEFAULT_FRAME_WIDTH;
	
	/**
	 * 是否处于可以缩放，平移，旋转状态
	 */
	private boolean isEditable = DEFAULT_EDITABLE;
	
	private DisplayMetrics metrics;
	
	
	private PointF mPreMovePointF = new PointF();
	private PointF mCurMovePointF = new PointF();
	
	/**
	 * 图片在旋转时x方向的偏移量
	 */
	private int offsetX;
	/**
	 * 图片在旋转时y方向的偏移量
	 */
	private int offsetY;
	
	/**
	 * 控制图标所在的位置（比如左上，右上，左下，右下）
	 */
	private int controlLocation = DEFAULT_CONTROL_LOCATION;
	
	//added by fengyi just for test
//	private Bitmap testBitmap;
	
	private RectF waterMarkRect;

	
	public WaterMarkImageView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public WaterMarkImageView(Context context) {
		this(context, null);
	}

	public WaterMarkImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs);
		obtainStyledAttributes(attrs);
		init();
	}
	
	/**
	 * 获取自定义属性
	 * @param attrs
	 */
	private void obtainStyledAttributes(AttributeSet attrs){
		metrics = getContext().getResources().getDisplayMetrics();
		framePadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_FRAME_PADDING, metrics);
		frameWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_FRAME_WIDTH, metrics);
		
		TypedArray mTypedArray = getContext().obtainStyledAttributes(attrs,
				R.styleable.WaterMarkView);
		
		Drawable srcDrawble = mTypedArray.getDrawable(R.styleable.WaterMarkView_src);
		if(srcDrawble instanceof BitmapDrawable){
			BitmapDrawable bd = (BitmapDrawable) srcDrawble;
			this.mWaterMarkBitmap = bd.getBitmap();
		}
		
		framePadding = mTypedArray.getDimensionPixelSize(R.styleable.WaterMarkView_framePadding, framePadding);
		frameWidth = mTypedArray.getDimensionPixelSize(R.styleable.WaterMarkView_frameWidth, frameWidth);
		frameColor = mTypedArray.getColor(R.styleable.WaterMarkView_frameColor, DEFAULT_FRAME_COLOR);
		mScale = mTypedArray.getFloat(R.styleable.WaterMarkView_scale, DEFAULT_SCALE);
		mDegree = mTypedArray.getFloat(R.styleable.WaterMarkView_degree, DEFAULT_DEGREE);
		controlDrawable = mTypedArray.getDrawable(R.styleable.WaterMarkView_controlDrawable);
		controlLocation = mTypedArray.getInt(R.styleable.WaterMarkView_controlLocation, DEFAULT_CONTROL_LOCATION);
		isEditable = mTypedArray.getBoolean(R.styleable.WaterMarkView_editable, DEFAULT_EDITABLE);
		
		mTypedArray.recycle();
		
	}
	
	
	private void init(){
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setColor(frameColor);
		mPaint.setStrokeWidth(frameWidth);
		mPaint.setStyle(Style.STROKE);
		
		if(controlDrawable == null){
			controlDrawable = getContext().getResources().getDrawable(R.drawable.icon_rotate);
		}
		
//		testBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
		
		mDrawableWidth = controlDrawable.getIntrinsicWidth();
		mDrawableHeight = controlDrawable.getIntrinsicHeight();
		
		transformDraw();
	}
	
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		
		int w = MeasureSpec.getSize(widthMeasureSpec);
		int h = MeasureSpec.getSize(heightMeasureSpec);
		mCenterPoint.set(w/2, h/2);
		
		calculateRectCanvas();
//		System.out.println("w:" + w + ",h:" + h);
		
//		setMeasuredDimension(w, h);
		//获取SingleTouchView所在父布局的中心点
//		ViewGroup mViewGroup = (ViewGroup) getParent();
//		if(null != mViewGroup){
//			int parentWidth = mViewGroup.getWidth();
//			int parentHeight = mViewGroup.getHeight();
//			mCenterPoint.set(parentWidth/2, parentHeight/2);
//		}
		
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
	
	
	/**
	 * 调整View的大小，位置
	 */
	private void adjustLayout(){
		int actualWidth = mDrawableWidth;
		int actualHeight = mDrawableHeight;
		
		int newPaddingLeft = (int) (mCenterPoint.x - actualWidth /2);
		int newPaddingTop = (int) (mCenterPoint.y - actualHeight/2);
		
		if(mViewPaddingLeft != newPaddingLeft || mViewPaddingTop != newPaddingTop){
			mViewPaddingLeft = newPaddingLeft;
			mViewPaddingTop = newPaddingTop;
//			Log.e(TAG,"mViewPaddingLeft:" + mViewPaddingLeft + ",mViewPaddingTop:"+mViewPaddingTop);
			matrix.postTranslate(mViewPaddingLeft,mViewPaddingTop);
			invalidate();
			
//			layout(newPaddingLeft, newPaddingTop, newPaddingLeft + actualWidth, newPaddingTop + actualHeight);
		}
		
	}
	
	
	/**
	 * 设置旋转图
	 * @param bitmap
	 */
	public void setWaterMarkBitamp(Bitmap bitmap){
		this.mWaterMarkBitmap = bitmap;
//		calculateRectCanvas();
		transformDraw();
	}
	
	
	public Bitmap getWaterMarkBitmap()
	{
		return this.mWaterMarkBitmap;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		
		if(mWaterMarkBitmap == null) return;
		if(rectCanvas!=null){
			canvas.clipRect(rectCanvas);
		}
		canvas.drawBitmap(mWaterMarkBitmap, matrix, null);
		
		//just for test 
		//added by fengyi
//		canvas.drawBitmap(testBitmap, mCenterPoint.x, mCenterPoint.y, null);
//		Log.d("zzd", "center.x:" + getWidth()/2 + ", center.y:" + getHeight()/2);
//		Log.d("zzd","mCenterPoint.x:" + mCenterPoint.x + ", mCenterPoint.y:" + mCenterPoint.y);
//		float[] values = new float[9];
//		matrix.getValues(values);
//		Log.d("zzd", "transX:" + values[2] + ",transY:" + values[5]);
		
		//处于可编辑状态才画边框和控制图标
		if(isEditable){
			mPath.reset();
			mPath.moveTo(mLTPoint.x, mLTPoint.y);
			mPath.lineTo(mRTPoint.x, mRTPoint.y);
			mPath.lineTo(mRBPoint.x, mRBPoint.y);
			mPath.lineTo(mLBPoint.x, mLBPoint.y);
			mPath.lineTo(mLTPoint.x, mLTPoint.y);
			mPath.lineTo(mRTPoint.x, mRTPoint.y);
			canvas.drawPath(mPath, mPaint);
			//画旋转, 缩放图标
			
			controlDrawable.setBounds((int)mControlPoint.x - mDrawableWidth / 2,
					(int)mControlPoint.y - mDrawableHeight / 2, (int)mControlPoint.x + mDrawableWidth
							/ 2, (int)mControlPoint.y + mDrawableHeight / 2);
			controlDrawable.draw(canvas);
		}
	}
	
	
	
	/**
	 * 设置Matrix, 强制刷新
	 */
	private void transformDraw(){
		if(mWaterMarkBitmap == null) return;
		int bitmapWidth = (int)(mWaterMarkBitmap.getWidth() * mScale);
		int bitmapHeight = (int)(mWaterMarkBitmap.getHeight()* mScale);
//		computeRect(-framePadding + (int)mCenterPoint.x, -framePadding + (int)mCenterPoint.y, bitmapWidth + framePadding + (int)mCenterPoint.x, bitmapHeight + framePadding + (int)mCenterPoint.y, mDegree);
		computeRect((int)mCenterPoint.x - bitmapWidth/2 -framePadding, (int)mCenterPoint.y - bitmapHeight/2 -framePadding,  (int)mCenterPoint.x + bitmapWidth/2 + framePadding, (int)mCenterPoint.y + bitmapHeight/2 + framePadding, mDegree);
		
		//设置缩放比例
		matrix.setScale(mScale, mScale);
		//绕着图片中心进行旋转
		matrix.postRotate(mDegree % 360, bitmapWidth/2, bitmapHeight/2);
		//设置画该图片的起始点
		matrix.postTranslate(mCenterPoint.x - (offsetX + bitmapWidth/2),mCenterPoint.y -( offsetY + bitmapHeight/2));
		
		invalidate();
	}
	
	
	public boolean onTouchEvent(MotionEvent event) {
//		if(!isEditable){
////			switch (event.getAction() ) {
////			case MotionEvent.ACTION_DOWN:
////				setEditable(true);
////				return true;
////			}
//			return super.onTouchEvent(event);
//		}
		switch (event.getAction() ) {
		case MotionEvent.ACTION_DOWN:
//			Log.e("zzd", "event.getX():" + event.getX() + ", event.getY():" + event.getY());
			mPreMovePointF.set(event.getX() , event.getY() );
			mStatus = JudgeStatus(event.getX(), event.getY());
			
			if (mLTPoint != null && mLBPoint != null && mRTPoint != null && mRBPoint != null) {
				if (calculatePoint(mLTPoint, mLBPoint, mRTPoint, mRBPoint, new PointF(event.getX(), event.getY()))) {
					if (!isEditable) {
						setEditable(true);
					}
				} else {
					//如果点击的时候是STATUS_ROTATE_ZOOM状态，就不设置isEditable为false
					if (mStatus != STATUS_ROTATE_ZOOM) {
						setEditable(false);
						super.onTouchEvent(event);
					}
				}
				
//				Log.e("zzd", "isEditable:" + isEditable);
			}

			break;
		case MotionEvent.ACTION_UP:
			mStatus = STATUS_INIT;
			break;
		case MotionEvent.ACTION_MOVE:
			
			if(!isEditable){
				return super.onTouchEvent(event) ;
			}
			mCurMovePointF.set(event.getX() , event.getY() );
			
			
			if (mStatus == STATUS_ROTATE_ZOOM) {
				float scale = 1f;
				
				int halfBitmapWidth = mWaterMarkBitmap.getWidth() / 2;
				int halfBitmapHeight = mWaterMarkBitmap.getHeight() /2 ;
				
				//图片某个点到图片中心的距离
				float bitmapToCenterDistance = FloatMath.sqrt(halfBitmapWidth * halfBitmapWidth + halfBitmapHeight * halfBitmapHeight);
				
				//移动的点到图片中心的距离
				float moveToCenterDistance = distance4PointF(mCenterPoint, mCurMovePointF);
				
				//计算缩放比例
				scale = moveToCenterDistance / bitmapToCenterDistance;
				
				
				//缩放比例的界限判断
				if (scale <= MIN_SCALE) {
					scale = MIN_SCALE;
				} else if (scale >= MAX_SCALE) {
					scale = MAX_SCALE;
				}
				
				
				// 角度
				double a = distance4PointF(mCenterPoint, mPreMovePointF);
				double b = distance4PointF(mPreMovePointF, mCurMovePointF);
				double c = distance4PointF(mCenterPoint, mCurMovePointF);
				
				double cosb = (a * a + c * c - b * b) / (2 * a * c);
				
				if (cosb >= 1) {
					cosb = 1f;
				}
				
				double radian = Math.acos(cosb);
				float newDegree = (float) radianToDegree(radian);
				
				//center -> proMove的向量， 我们使用PointF来实现
				PointF centerToProMove = new PointF((mPreMovePointF.x - mCenterPoint.x), (mPreMovePointF.y - mCenterPoint.y));
				
				//center -> curMove 的向量  
				PointF centerToCurMove = new PointF((mCurMovePointF.x - mCenterPoint.x), (mCurMovePointF.y - mCenterPoint.y));
				
				//向量叉乘结果, 如果结果为负数， 表示为逆时针， 结果为正数表示顺时针
				float result = centerToProMove.x * centerToCurMove.y - centerToProMove.y * centerToCurMove.x;

				if (result < 0) {
					newDegree = -newDegree;
				} 
				
				mDegree = mDegree + newDegree;
				mScale = scale;
//				Log.e(TAG,"mScale:" + mScale);
				transformDraw();
			}
			else if (mStatus == STATUS_DRAG) {
				// 修改中心点
				mCenterPoint.x += mCurMovePointF.x - mPreMovePointF.x;
				mCenterPoint.y += mCurMovePointF.y - mPreMovePointF.y;
				
//				adjustLayout();
				transformDraw();
			}
			
			mPreMovePointF.set(mCurMovePointF);
			break;
		}
		return true;
	}
	
	
	
	/**
	 * 获取四个点和View的大小
	 * @param left
	 * @param top
	 * @param right
	 * @param bottom
	 * @param degree
	 */
	private void computeRect(int left, int top, int right, int bottom, float degree){
		PointF lt = new PointF(left, top);
		PointF rt = new PointF(right, top);
		PointF rb = new PointF(right, bottom);
		PointF lb = new PointF(left, bottom);
		PointF cp = new PointF((left + right) / 2, (top + bottom) / 2);
		mLTPoint = obtainRoationPoint(cp, lt, degree);
		mRTPoint = obtainRoationPoint(cp, rt, degree);
		mRBPoint = obtainRoationPoint(cp, rb, degree);
		mLBPoint = obtainRoationPoint(cp, lb, degree);
		
		//计算X坐标最大的值和最小的值
		int maxCoordinateX = (int)getMaxValue(mLTPoint.x, mRTPoint.x, mRBPoint.x, mLBPoint.x);
		int minCoordinateX = (int)getMinValue(mLTPoint.x, mRTPoint.x, mRBPoint.x, mLBPoint.x);;
		
		mViewWidth = maxCoordinateX - minCoordinateX ;
		
		
		//计算Y坐标最大的值和最小的值
		int maxCoordinateY = (int)getMaxValue(mLTPoint.y, mRTPoint.y, mRBPoint.y, mLBPoint.y);
		int minCoordinateY = (int)getMinValue(mLTPoint.y, mRTPoint.y, mRBPoint.y, mLBPoint.y);

		mViewHeight = maxCoordinateY - minCoordinateY ;
		
//		Log.e("zzd","mViewWidth:"+ mViewWidth + ", mViewHeight:"+ mViewHeight);
//		Log.e(TAG,"mLTPoint.x:"+ mLTPoint.x + ", mLTPoint.y:"+ mLTPoint.y);
		//View中心点的坐标
		Point viewCenterPoint = new Point((maxCoordinateX + minCoordinateX) / 2, (maxCoordinateY + minCoordinateY) / 2);
		
		offsetX = 0;//mViewWidth / 2 - viewCenterPoint.x;
		offsetY = 0;//mViewHeight / 2 - viewCenterPoint.y;
		
//		Log.e(TAG,"offsetX:"+ offsetX + ", offsetY:"+ offsetY);
		
		int halfDrawableWidth = mDrawableWidth / 2;
		int halfDrawableHeight = mDrawableHeight /2;
		
		//将Bitmap的四个点的X的坐标移动offsetX + halfDrawableWidth
//		mLTPoint.x += (offsetX + halfDrawableWidth);
//		mRTPoint.x += (offsetX + halfDrawableWidth);
//		mRBPoint.x += (offsetX + halfDrawableWidth);
//		mLBPoint.x += (offsetX + halfDrawableWidth);
//
//		//将Bitmap的四个点的Y坐标移动offsetY + halfDrawableHeight
//		mLTPoint.y += (offsetY + halfDrawableHeight);
//		mRTPoint.y += (offsetY + halfDrawableHeight);
//		mRBPoint.y += (offsetY + halfDrawableHeight);
//		mLBPoint.y += (offsetY + halfDrawableHeight);
		
		mControlPoint = LocationToPoint(controlLocation);
	}
	
	
	/**
	 * 根据位置判断控制图标处于那个点
	 * @return
	 */
	private PointF LocationToPoint(int location){
		switch(location){
		case LEFT_TOP:
			return mLTPoint;
		case RIGHT_TOP:
			return mRTPoint;
		case RIGHT_BOTTOM:
			return mRBPoint;
		case LEFT_BOTTOM:
			return mLBPoint;
		}
		return mLTPoint;
	}
	
	
	/**
	 * 获取变长参数最大的值
	 * @param array
	 * @return
	 */
	public float getMaxValue(Float...array){
		List<Float> list = Arrays.asList(array);
		Collections.sort(list);
		return list.get(list.size() -1);
	}
	
	
	/**
	 * 获取变长参数最大的值
	 * @param array
	 * @return
	 */
	public float getMinValue(Float...array){
		List<Float> list = Arrays.asList(array);
		Collections.sort(list);
		return list.get(0);
	}
	
	
	
	/**
	 * 获取旋转某个角度之后的点
	 * @param viewCenter
	 * @param source
	 * @param degree
	 * @return
	 */
	public static PointF obtainRoationPoint(PointF center, PointF source, float degree) {
		//两者之间的距离
		PointF disPoint = new PointF();
		disPoint.x = source.x - center.x;
		disPoint.y = source.y - center.y;
		
		//没旋转之前的弧度
		double originRadian = 0;

		//没旋转之前的角度
		double originDegree = 0;
		
		//旋转之后的角度
		double resultDegree = 0;
		
		//旋转之后的弧度
		double resultRadian = 0;
		
		//经过旋转之后点的坐标
		PointF resultPoint = new PointF();
		
		double distance = Math.sqrt(disPoint.x * disPoint.x + disPoint.y * disPoint.y);
		if (disPoint.x == 0 && disPoint.y == 0) {
			return center;
			// 第一象限
		} else if (disPoint.x >= 0 && disPoint.y >= 0) {
			// 计算与x正方向的夹角
			originRadian = Math.asin(disPoint.y / distance);
			
			// 第二象限
		} else if (disPoint.x < 0 && disPoint.y >= 0) {
			// 计算与x正方向的夹角
			originRadian = Math.asin(Math.abs(disPoint.x) / distance);
			originRadian = originRadian + Math.PI / 2;
			
			// 第三象限
		} else if (disPoint.x < 0 && disPoint.y < 0) {
			// 计算与x正方向的夹角
			originRadian = Math.asin(Math.abs(disPoint.y) / distance);
			originRadian = originRadian + Math.PI;
		} else if (disPoint.x >= 0 && disPoint.y < 0) {
			// 计算与x正方向的夹角
			originRadian = Math.asin(disPoint.x / distance);
			originRadian = originRadian + Math.PI * 3 / 2;
		}
		
		// 弧度换算成角度
		originDegree = radianToDegree(originRadian);
		resultDegree = originDegree + degree;
		
		// 角度转弧度
		resultRadian = degreeToRadian(resultDegree);
		
		resultPoint.x = (int) Math.round(distance * Math.cos(resultRadian));
		resultPoint.y = (int) Math.round(distance * Math.sin(resultRadian));
		resultPoint.x += center.x;
		resultPoint.y += center.y;

		return resultPoint;
	}

	/**
	 * 弧度换算成角度
	 * 
	 * @return
	 */
	public static double radianToDegree(double radian) {
		return radian * 180 / Math.PI;
	}

	
	/**
	 * 角度换算成弧度
	 * @param degree
	 * @return
	 */
	public static double degreeToRadian(double degree) {
		return degree * Math.PI / 180;
	}
	
	/**
	 * 根据点击的位置判断是否点中控制旋转，缩放的图片， 初略的计算
	 * @param x
	 * @param y
	 * @return
	 */
	private int JudgeStatus(float x, float y){
		PointF touchPoint = new PointF(x, y);
		PointF controlPointF = mControlPoint;//new PointF(mControlPoint.x, mControlPoint.y);
		
		//点击的点到控制旋转，缩放点的距离
		float distanceToControl = distance4PointF(touchPoint, controlPointF);
		
		//如果两者之间的距离小于 控制图标的宽度，高度的最小值，则认为点中了控制图标
		if(distanceToControl < Math.min(mDrawableWidth/2, mDrawableHeight/2)){
			return STATUS_ROTATE_ZOOM;
		}
		
		return STATUS_DRAG;
		
	}
	
	
	public float getImageDegree() {
		return mDegree;
	}

	/**
	 * 设置图片旋转角度
	 * @param degree
	 */
	public void setImageDegree(float degree) {
		if(this.mDegree != degree){
			this.mDegree = degree;
			transformDraw();
		}
	}

	public float getImageScale() {
		return mScale;
	}

	/**
	 * 设置图片缩放比例
	 * @param scale
	 */
	public void setImageScale(float scale) {
		if(this.mScale != scale){
			this.mScale = scale;
			transformDraw();
		};
	}
	

	public Drawable getControlDrawable() {
		return controlDrawable;
	}

	/**
	 * 设置控制图标
	 * @param drawable
	 */
	public void setControlDrawable(Drawable drawable) {
		this.controlDrawable = drawable;
		mDrawableWidth = drawable.getIntrinsicWidth();
		mDrawableHeight = drawable.getIntrinsicHeight();
		transformDraw();
	}

	public int getFramePadding() {
		return framePadding;
	}

	public void setFramePadding(int framePadding) {
		if(this.framePadding == framePadding)
			return;
		this.framePadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, framePadding, metrics);
		transformDraw();
	}

	public int getFrameColor() {
		return frameColor;
	}

	public void setFrameColor(int frameColor) {
		if(this.frameColor == frameColor)
			return;
		this.frameColor = frameColor;
		mPaint.setColor(frameColor);
		invalidate();
	}

	public int getFrameWidth() {
		return frameWidth;
	}

	public void setFrameWidth(int frameWidth) {
		if(this.frameWidth == frameWidth) 
			return;
		this.frameWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, frameWidth, metrics);
		mPaint.setStrokeWidth(frameWidth);
		invalidate();
	}
	
	/**
	 * 设置控制图标的位置, 设置的值只能选择LEFT_TOP ，RIGHT_TOP， RIGHT_BOTTOM，LEFT_BOTTOM
	 * @param controlLocation
	 */
	public void setControlLocation(int location) {
		if(this.controlLocation == location)
			return;
		this.controlLocation = location;
		transformDraw();
	}

	public int getControlLocation() {
		return controlLocation;
	}
	
	

	public PointF getCenterPoint() {
		return mCenterPoint;
	}

	/**
	 * 设置图片中心点位置，相对于父布局而言
	 * @param mCenterPoint
	 */
	public void setCenterPoint(PointF mCenterPoint) {
		this.mCenterPoint = mCenterPoint;
		adjustLayout();
	}
	

	public boolean isEditable() {
		return isEditable;
	}

	/**
	 * 设置是否处于可缩放，平移，旋转状态
	 * @param isEditable
	 */
	public void setEditable(boolean isEditable) {
		this.isEditable = isEditable;
		invalidate();
	}

	/**
	 * 两个点之间的距离
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @return
	 */
	private float distance4PointF(PointF pf1, PointF pf2) {
		float disX = pf2.x - pf1.x;
		float disY = pf2.y - pf1.y;
		return FloatMath.sqrt(disX * disX + disY * disY);
	}
	
	
	@SuppressWarnings("serial")
	public static class NotSupportedException extends RuntimeException{
		private static final long serialVersionUID = 1674773263868453754L;

		public NotSupportedException() {
			super();
		}

		public NotSupportedException(String detailMessage) {
			super(detailMessage);
		}
		
	}

	private Rect mImageViewDrawingRect;
	private Matrix mImageViewMatrix;
	private RectF rectCanvas;
	
	private void calculateRectCanvas(){
		if (getDrawable() != null) {
			mImageViewDrawingRect = getDrawable().getBounds();
			float[] values = new float[9];
			mImageViewMatrix = getImageMatrix();
			mImageViewMatrix.getValues(values);
			float xScale = values[0];
			float yScale = values[4];
			float widthf = xScale * mImageViewDrawingRect.width();
			float heightf = yScale * mImageViewDrawingRect.height();
			float xTrans = values[2];
			float yTrans = values[5];
			rectCanvas = new RectF();
			rectCanvas.left = xTrans;
			rectCanvas.right = xTrans + widthf;
			rectCanvas.top = yTrans;
			rectCanvas.bottom = yTrans + heightf;
		}
		
//		Log.d(TAG,"calculateRectCanvas() method is invoked");
	}
	
	/**
	 * 获得水印图片对应大图所需要的Matrix
	 * added by fengyi
	 * 
	 * @return Matrix
	 */
	public Matrix getWaterMarkMatrix(float bitmapScaleRate){
		Matrix tempMatrix = getImageMatrix();
		Matrix finalMatrix = new Matrix();
		
		float[] tempvalues = new float[9];
		
		tempMatrix.getValues(tempvalues);
		
		finalMatrix.set(matrix);
		
//		if(tempvalues[Matrix.MTRANS_X]>0){
			finalMatrix.postTranslate(-tempvalues[Matrix.MTRANS_X], 0);
//		}
//		if(tempvalues[Matrix.MTRANS_Y]>0){
			finalMatrix.postTranslate(0, -tempvalues[Matrix.MTRANS_Y]);
//		}
		if(tempvalues[Matrix.MSCALE_X]<1){
			finalMatrix.postScale(1/tempvalues[Matrix.MSCALE_X], 1);
		}else{
			finalMatrix.postScale(1/tempvalues[Matrix.MSCALE_X], 1);
		}
		
		if(tempvalues[Matrix.MSCALE_Y]<1){
			finalMatrix.postScale(1, 1/tempvalues[Matrix.MSCALE_Y]);
		}else{
			finalMatrix.postScale(1, 1/tempvalues[Matrix.MSCALE_Y]);
		}
		
		finalMatrix.postScale(bitmapScaleRate, bitmapScaleRate);
		
		return finalMatrix;
	}
	
	/**
	 * 以左上角起始点为基准点
	 * 
	 * 取得水印相对位置对象WaterMarkPosition
	 * 
	 * @return
	 */
	public WaterMarkPosition getWaterMarkPosition(float bitmapScaleRate, float width , float height){
		Matrix watermarkmatrix = getWaterMarkMatrix(bitmapScaleRate);
		WaterMarkPosition mWaterMarkPosition = new WaterMarkPosition(watermarkmatrix);
		
		float[] values = new float[9];
		watermarkmatrix.getValues(values);
		
//		float scaleX = values[Matrix.MSCALE_X];
//		float scaleY = values[Matrix.MSCALE_Y];
//		
//		float waterMarkWidth = mViewWidth/scaleX;
//		float waterMarkHeight = mViewHeight/scaleY;
//		
//		mWaterMarkPosition.setWaterMarkWidth(waterMarkWidth * bitmapScaleRate);
//		mWaterMarkPosition.setWaterMarkHeight(waterMarkHeight * bitmapScaleRate);
		
//		float rateX = (values[Matrix.MTRANS_X] - rectCanvas.left)/rectCanvas.width();
//		float rateY = (values[Matrix.MTRANS_Y] - rectCanvas.top)/rectCanvas.height();
		float rateX = values[Matrix.MTRANS_X]/width;
		float rateY = values[Matrix.MTRANS_Y]/height;
		
		mWaterMarkPosition.setRelativeRateX(rateX);
		mWaterMarkPosition.setRelativeRateY(rateY);
		
		return mWaterMarkPosition;
	}
	
	/**
	 * 以中心点为基准点
	 * 
	 * 取得水印相对位置对象WaterMarkPosition
	 * 
	 * @return
	 */
//	public WaterMarkPosition getWaterMarkPosition(float bitmapScaleRate){
//		Matrix watermarkmatrix = getWaterMarkMatrix(bitmapScaleRate);
//		WaterMarkPosition mWaterMarkPosition = new WaterMarkPosition(watermarkmatrix);
//		
//		float[] values = new float[9];
//		Matrix tempMatrix = getImageMatrix();
//		tempMatrix.getValues(values);
//		
//		float scaleX = values[Matrix.MSCALE_X];
//		float scaleY = values[Matrix.MSCALE_Y];
//		
//		float waterMarkWidth = mViewWidth/scaleX;
//		float waterMarkHeight = mViewHeight/scaleY;
//		
//		float rateX = (mCenterPoint.x - rectCanvas.left)/rectCanvas.width();
//		float rateY = (mCenterPoint.y - rectCanvas.top)/rectCanvas.height();
//		
//		mWaterMarkPosition.setWaterMarkWidth(waterMarkWidth * bitmapScaleRate);
//		mWaterMarkPosition.setWaterMarkHeight(waterMarkHeight * bitmapScaleRate);
//		mWaterMarkPosition.setRelativeRateX(rateX);
//		mWaterMarkPosition.setRelativeRateY(rateY);
//		
//		return mWaterMarkPosition;
//	}
	
	/**
	 * 计算点是不是在矩形内
	 * 
	 * @param p1
	 * @param p2
	 * @param p3
	 * @param p4
	 * @param p5
	 * @return
	 */
	private boolean calculatePoint(PointF p1, PointF p2, PointF p3, PointF p4, PointF p5){
		double len51 = Math.pow(p5.x - p1.x, 2) + Math.pow(p5.y - p1.y, 2);
		double len52 = Math.pow(p5.x - p2.x, 2) + Math.pow(p5.y - p2.y, 2);
		double len53 = Math.pow(p5.x - p3.x, 2) + Math.pow(p5.y - p3.y, 2);
		double len54 = Math.pow(p5.x - p4.x, 2) + Math.pow(p5.y - p4.y, 2);
		
		double len12 = Math.pow(p2.x - p1.x, 2) + Math.pow(p2.y - p1.y, 2);
		double len23 = Math.pow(p2.x - p3.x, 2) + Math.pow(p2.y - p3.y, 2);
		double len34 = Math.pow(p4.x - p3.x, 2) + Math.pow(p4.y - p3.y, 2);
		double len41 = Math.pow(p4.x - p1.x, 2) + Math.pow(p4.y - p1.y, 2);
		
		double r1 = Math.acos((len51 + len52 - len12)
				/ (2 * Math.pow(len51, 0.5) * Math.pow(len52, 0.5)));
		double r2 = Math.acos((len52 + len53 - len23)
				/ (2 * Math.pow(len52, 0.5) * Math.pow(len53, 0.5)));
		double r3 = Math.acos((len53 + len54 - len34)
				/ (2 * Math.pow(len53, 0.5) * Math.pow(len54, 0.5)));
		double r4 = Math.acos((len54 + len51 - len41)
				/ (2 * Math.pow(len54, 0.5) * Math.pow(len51, 0.5)));
		double sum = r1 + r2 + r3 + r4;
//		Log.e(TAG, "sum:" + sum);
		
		return (2 * Math.PI < sum);
	}
	
	/**
	 * 水印相对位置对象
	 * 
	 * @author fengyi
	 *
	 */
	public class WaterMarkPosition{
		//水印对应的Matrix
		Matrix matrix;
		//相对位置X的比例
		float relativeRateX;
		//相对位置Y的比例
		float relativeRateY;
		
		//水印的宽度
		float waterMarkWidth;
		//水印的高度
		float waterMarkHeight;
		
		
		public WaterMarkPosition(Matrix matrix){
			this.matrix = matrix;
		}
		
		public Matrix getMatrix() {
			return matrix;
		}
		public void setMatrix(Matrix matrix) {
			this.matrix = matrix;
		}

		public float getRelativeRateX() {
			return relativeRateX;
		}

		public void setRelativeRateX(float relativeRateX) {
			this.relativeRateX = relativeRateX;
		}

		public float getRelativeRateY() {
			return relativeRateY;
		}

		public void setRelativeRateY(float relativeRateY) {
			this.relativeRateY = relativeRateY;
		}

		public float getWaterMarkWidth() {
			return waterMarkWidth;
		}

		public void setWaterMarkWidth(float waterMarkWidth) {
			this.waterMarkWidth = waterMarkWidth;
		}

		public float getWaterMarkHeight() {
			return waterMarkHeight;
		}

		public void setWaterMarkHeight(float waterMarkHeight) {
			this.waterMarkHeight = waterMarkHeight;
		}
		
	}
	
	
//	//第一象限 右上
//	private final static int QUADRANT_ONE = 1;
//	//第二象限 左上
//	private final static int QUADRANT_TWO = 2;
//	//第三象限 左下
//	private final static int QUADRANT_THREE = 3;
//	//第四象限 右下
//	private final static int QUADRANT_FOUR = 4;
//	
//	private int calculatePositionOfCenter(PointF center, PointF source){
//		// 两者之间的距离
//		PointF disPoint = new PointF();
//		disPoint.x = source.x - center.x;
//		disPoint.y = source.y - center.y;
//
//		// 第一象限
//		if (disPoint.x >= 0 && disPoint.y >= 0) {
//			return QUADRANT_ONE;
//		// 第二象限
//		} else if (disPoint.x < 0 && disPoint.y > 0) {
//			return QUADRANT_TWO;
//		// 第三象限
//		} else if (disPoint.x <= 0 && disPoint.y <= 0) {
//			return QUADRANT_THREE;
//		// 第四象限
//		} else if (disPoint.x > 0 && disPoint.y < 0) {
//			return QUADRANT_FOUR;
//		}
//
//		return QUADRANT_ONE;
//	}
}
