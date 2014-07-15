/**
 * This file is part of GraphView.
 *
 * GraphView is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GraphView is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with GraphView.  If not, see <http://www.gnu.org/licenses/lgpl.html>.
 *
 * Copyright Jonas Gehring
 */

package com.jjoe64.graphview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.DisplayMetrics;

import com.jjoe64.graphview.GraphViewSeries.GraphViewSeriesStyle;

/**
 * Line Graph View. This draws a line chart.
 */
public class LineGraphView extends GraphView {
	private final Paint paintBackground;
	private boolean drawBackground;
    private float extraMarginsSize = 50;
    private float outerCircleSize = 9;
    private float mainCircleSize = 7;
    private float innerCircleSize = 3;

	public LineGraphView(Context context, AttributeSet attrs) {
		super(context, attrs);

		paintBackground = new Paint();
		paintBackground.setColor(Color.rgb(20, 40, 60));
		paintBackground.setStrokeWidth(4);
		paintBackground.setAlpha(128);
	}

	public LineGraphView(Context context, String title) {
		super(context, title);

		paintBackground = new Paint();
		paintBackground.setColor(Color.rgb(20, 40, 60));
		paintBackground.setStrokeWidth(4);
		paintBackground.setAlpha(128);

        final DisplayMetrics metrics = getResources().getDisplayMetrics();
        final float scaleFactor = metrics.densityDpi/160;
        extraMarginsSize *= scaleFactor;
        outerCircleSize *= scaleFactor;
        mainCircleSize *= scaleFactor;
        innerCircleSize *= scaleFactor;
	}

	@Override
	public void drawSeries(Canvas canvas, GraphViewDataInterface[] values, float graphwidth, float graphheight, float border, double minX, double minY, double diffX, double diffY, float horstart, GraphViewSeriesStyle style) {
        double lastEndY = 0.0D;
        double lastEndX = 0.0D;
        if (this.drawBackground)
        {
            float startY = graphheight + border;
            for (int i = 0; i < values.length; i++)
            {
                double valY = values[i].getY() - minY;
                double ratY = valY / diffY;
                double y = graphheight * ratY;

                double valX = values[i].getX() - minX;
                double ratX = valX / diffX;
                double x = graphwidth * ratX;

                float endX = (float)x + (horstart + 1.0F);
                float endY = (float)(border - y) + graphheight + 2.0F;
                if (i > 0)
                {
                    double numSpace = (endX - lastEndX) / 3.0D + 1.0D;
                    for (int xi = 0; xi < numSpace; xi++)
                    {
                        float spaceX = (float)(lastEndX + (endX - lastEndX) * xi / (numSpace - 1.0D));
                        float spaceY = (float)(lastEndY + (endY - lastEndY) * xi / (numSpace - 1.0D));


                        float startX = spaceX;
                        if (startX - horstart > 1.0F) {
                            canvas.drawLine(startX, startY, spaceX, spaceY, this.paintBackground);
                        }
                    }
                }
                lastEndY = endY;
                lastEndX = endX;
            }
        }
        this.paint.setStrokeWidth(style.thickness);
        this.paint.setColor(style.color);

        lastEndY = 0.0D;
        lastEndX = 0.0D;
        for (int i = 0; i < values.length; i++)
        {
            if (values.length == 1)
            {
                double valY = values[i].getY() - minY;
                double ratY = valY / diffY;
                double y = graphheight * ratY;

                double x = graphwidth / 2.0F + this.extraMarginsSize / 2.0F;
                float startX = (float)x + (horstart + 1.0F);
                float startY = (float)(border - y) + graphheight;

                this.paint.setStyle(Paint.Style.FILL);
                this.paint.setColor(-1);
                canvas.drawCircle(startX, startY, this.outerCircleSize, this.paint);
                this.paint.setColor(Color.parseColor("#4fdfbe"));
                canvas.drawCircle(startX, startY, this.mainCircleSize, this.paint);
                this.paint.setColor(-1);
                canvas.drawCircle(startX, startY, this.innerCircleSize, this.paint);
            }
            double valY = values[i].getY() - minY;
            double ratY = valY / diffY;
            double y = graphheight * ratY;

            double valX = values[i].getX() - minX;
            double ratX = valX / diffX;
            double x = graphwidth * ratX + this.extraMarginsSize / 2.0F;
            if (i > 0)
            {
                this.paint.setStyle(Paint.Style.STROKE);
                this.paint.setColor(Color.parseColor("#4fdfbe"));

                float startX = (float)lastEndX + (horstart + 1.0F);
                float startY = (float)(border - lastEndY) + graphheight;
                float endX = (float)x + (horstart + 1.0F);
                float endY = (float)(border - y) + graphheight;

                canvas.drawLine(startX, startY, endX, endY, this.paint);

                this.paint.setStyle(Paint.Style.FILL);
                this.paint.setColor(-1);
                canvas.drawCircle(startX, startY, this.outerCircleSize, this.paint);
                this.paint.setColor(Color.parseColor("#4fdfbe"));
                canvas.drawCircle(startX, startY, this.mainCircleSize, this.paint);
                this.paint.setColor(-1);
                canvas.drawCircle(startX, startY, this.innerCircleSize, this.paint);
                if (i == values.length - 1)
                {
                    this.paint.setStyle(Paint.Style.FILL);
                    this.paint.setColor(-1);
                    canvas.drawCircle(endX, endY, this.outerCircleSize, this.paint);
                    this.paint.setColor(Color.parseColor("#4fdfbe"));
                    canvas.drawCircle(endX, endY, this.mainCircleSize, this.paint);
                    this.paint.setColor(-1);
                    canvas.drawCircle(endX, endY, this.innerCircleSize, this.paint);
                }
            }
            lastEndY = y;
            lastEndX = x;
        }
	}

    private void drawDataPoint(float startX, float startY, Canvas canvas, Paint paint, int datapointColor) {
        paint.setColor(Color.WHITE);
        canvas.drawCircle(startX, startY, outerCircleSize, paint);
        paint.setColor(datapointColor);
        canvas.drawCircle(startX, startY, mainCircleSize, paint);
        paint.setColor(Color.WHITE);
        canvas.drawCircle(startX, startY, innerCircleSize, paint);
    }

	public int getBackgroundColor() {
		return paintBackground.getColor();
	}

	public boolean getDrawBackground() {
		return drawBackground;
	}

	/**
	 * sets the background color for the series.
	 * This is not the background color of the whole graph.
	 * @see #setDrawBackground(boolean)
	 */
	@Override
	public void setBackgroundColor(int color) {
		paintBackground.setColor(color);
	}

	/**
	 * @param drawBackground true for a light blue background under the graph line
	 * @see #setBackgroundColor(int)
	 */
	public void setDrawBackground(boolean drawBackground) {
		this.drawBackground = drawBackground;
	}

}
