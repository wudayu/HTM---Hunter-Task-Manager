package com.wudayu.htm.views;

import java.util.Iterator;
import java.util.Queue;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.view.View;

import com.wudayu.htm.utils.HtmFormatter;

/**
 * 
 * @author: Wu Dayu
 * @En_Name: David Wu
 * @E-mail: wudayu@gmail.com
 * @version: 1.0
 * @Created Time: Feb 12, 2013 7:08:02 PM
 * @Description: This is David Wu's property.
 * 
 **/
public class DrawHistoryView extends View {

	public static final int COLOR_LINE = 0xFFBBFFBB;
	public static final int COLOR_POINT = 0xFFA0A0A0;

	private static Paint pointPaint;
	private static Paint linePaint;

	static {
		pointPaint = new Paint();
		pointPaint.setColor(COLOR_POINT);
		pointPaint.setAntiAlias(true);
		pointPaint.setStrokeWidth(8);
		pointPaint.setStyle(Style.STROKE);

		linePaint = new Paint();
		linePaint.setColor(COLOR_LINE);
		linePaint.setAntiAlias(true);
		linePaint.setStrokeWidth(6);
		linePaint.setStyle(Style.STROKE);
	}

	Queue<Integer> queData;
	int count;
	Integer previous;
	boolean hasLine;
	int width;
	int height;

	public DrawHistoryView(Context context) {
		super(context);

		queData = null;
	}

	public DrawHistoryView(Context context, Queue<Integer> queue, int width, int height) {
		super(context);

		this.queData = queue;
		this.width = width;
		this.height = height;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		count = 0;
		hasLine = false;
		int dividedWidth = width / (HtmFormatter.BASE_POINT_COUNT - 1);
		Iterator<Integer> iter = queData.iterator();
		while (iter.hasNext()) {
			Integer curr = iter.next();
			if (curr != null) {
				canvas.drawCircle(count * dividedWidth, height
						- (float) (height / 100.0 * curr), 3, pointPaint);
				if (hasLine) {
					canvas.drawLine((count - 1) * dividedWidth, height
							- (float) (previous * height / 100.0), count
							* dividedWidth, height
							- (float) (curr * height / 100.0), linePaint);
				}

				previous = curr;
				hasLine = true;
			} else {
				hasLine = false;
			}
			++count;
		}
		super.onDraw(canvas);
	}
}
