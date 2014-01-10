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
 * @Created Time: Feb 12, 2013 2:05:11 PM
 * @Description: This is David Wu's property.
 *
 **/
public class DrawTextView extends View {
	public static final int COLOR_TEXT = 0xFFBBFFBB;
	private static Paint paint;

	static {
		paint = new Paint();
		paint.setColor(COLOR_TEXT);
		paint.setAntiAlias(true);
		paint.setStrokeWidth(2);
		paint.setStyle(Style.STROKE);
		paint.setTextSize(30);
	}

	int x, y;
	String text;

	public DrawTextView(Context context) {
		super(context);

		x = 0;
		y = 0;
		text = null;
	}

	public DrawTextView(Context context, int x, int y, String text) {
		super(context);

		this.x = x;
		this.y = y;
		this.text = text;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawText(text, x, y, paint);

		super.onDraw(canvas);
	}
}
