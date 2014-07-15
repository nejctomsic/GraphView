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
        final float scaleFactor = metrics.densityDpi / 160;
        extraMarginsSize *= scaleFactor;
        outerCircleSize *= scaleFactor;
        mainCircleSize *= scaleFactor;
        innerCircleSize *= scaleFactor;
    }

    @Override
    public void drawSeries(Canvas canvas, GraphViewDataInterface[] values, float graphwidth, float graphheight, float border, double minX, double minY, double diffX, double diffY, float horstart, GraphViewSeriesStyle style) {
        // draw background
        double lastEndY = 0;
        double lastEndX = 0;

        // draw data
        paint.setStrokeWidth(style.thickness);

        Path bgPath = null;
        if (drawBackground) {
            bgPath = new Path();
        }

        lastEndY = 0;
        lastEndX = 0;
        float firstX = 0;
        for (int i = 0; i < values.length; i++) {
            double valY = values[i].getY() - minY;
            double ratY = valY / diffY;
            double y = graphheight * ratY;

            double valX = values[i].getX() - minX;
            double ratX = valX / diffX;
            double x = graphwidth * ratX + (extraMarginsSize/2);

            if (i > 0) {
                float startX = (float) lastEndX + (horstart + 1);
                float startY = (float) (border - lastEndY) + graphheight;
                float endX = (float) x + (horstart + 1);
                float endY = (float) (border - y) + graphheight;

                // draw data point
                if (style.drawDataPoints) {
                    //fix: last value was not drawn. Draw here now the end values
                    drawDataPoint(endX, endY, canvas, paint, style.dataPointColor);
                }

                paint.setColor(style.color);
                canvas.drawLine(startX, startY, endX, endY, paint);
                if (bgPath != null) {
                    if (i == 1) {
                        firstX = startX;
                        bgPath.moveTo(startX, startY);
                    }
                    bgPath.lineTo(endX, endY);
                }
            } else if (style.drawDataPoints) {
                //fix: last value not drawn as datapoint. Draw first point here, and then on every step the end values (above)
                float first_X;
                if (values.length == 1) {
                    first_X = graphwidth / 2 + horstart;
                } else {
                    first_X = (float) x + (horstart + 1);
                }
                float first_Y = (float) (border - y) + graphheight;
                paint.setColor(style.dataPointColor);
                drawDataPoint(first_X, first_Y, canvas, paint, style.dataPointColor);
            }
            lastEndY = y;
            lastEndX = x;
        }

        if (bgPath != null) {
            // end / close path
            bgPath.lineTo((float) lastEndX, graphheight + border);
            bgPath.lineTo(firstX, graphheight + border);
            bgPath.close();
            canvas.drawPath(bgPath, paintBackground);
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
     *
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
