package com.wudayu.htm.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.view.View;

/**
 * 
 * @author: Wu Dayu
 * @En_Name: David Wu
 * @E-mail: wudayu@gmail.com
 * @version: 1.0
 * @Created Time: Feb 12, 2013 10:56:24 AM
 * @Description: This is David Wu's property.
 * 
 **/
public class DrawNegtiveLineView extends View {

	public static final int COLOR_NEGTIVE_LINE = 0xFFBBFFBB;
	private static Paint paint;

	static {
		paint = new Paint();
		paint.setColor(COLOR_NEGTIVE_LINE);
		paint.setAntiAlias(true);
		paint.setStrokeWidth(4);
		paint.setStyle(Style.STROKE);
	}

	int x1, y1, x2, y2;

	public DrawNegtiveLineView(Context context) {
		super(context);

		x1 = 0;
		y1 = 0;
		x2 = 0;
		y2 = 0;
	}

	public DrawNegtiveLineView(Context context, int x1, int y1, int x2, int y2) {
		super(context);

		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawLine(x1, y1, x2 / 2, y2, paint);
		canvas.drawLine(x1 + x2 / 2, y1, x2, y2, paint);

		super.onDraw(canvas);
	}
}
