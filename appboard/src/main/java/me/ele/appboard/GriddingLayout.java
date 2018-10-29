package me.ele.appboard;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import me.ele.appboard.base.DimenUtil;

import static me.ele.appboard.base.DimenUtil.dip2px;
import static me.ele.appboard.base.DimenUtil.getScreenHeight;
import static me.ele.appboard.base.DimenUtil.getScreenWidth;

public class GriddingLayout extends View {

    public static final int LINE_INTERVAL = dip2px(5);
    public static final int SCALE_MARK = dip2px(50);
    private final int screenWidth = getScreenWidth();
    private final int screenHeight = getScreenHeight();

    private Paint paint = new Paint() {
        {
            setAntiAlias(true);
            setColor(0x30000000);
            setStrokeWidth(1);
        }
    };

    private Paint textPain = new Paint(){
        {
            setAntiAlias(true);
            setColor(Color.BLACK);
            setTextSize(DimenUtil.sp2px(10));
        }

    };
    private Activity bindActivity = UETool.getInstance().getTargetActivity();

    public GriddingLayout(Context context) {
        super(context);
    }

    public GriddingLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public GriddingLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int startX = 0;
        while (startX < screenWidth) {
            canvas.drawLine(startX, 0, startX, screenHeight, paint);
            startX = startX + LINE_INTERVAL;
        }
        int startY = 0;
        while (startY < screenHeight) {
            canvas.drawLine(0, startY, screenWidth, startY, paint);
            startY = startY + LINE_INTERVAL;
        }

        //画刻度
        drawScale(canvas);
    }

    private void drawScale(Canvas canvas) {
        int startX = 0;
        int countX = 0;
        int textWidth = 0;
        String textString = "";
        while (startX < screenWidth) {
            paint.setColor(Color.BLACK);
            if(countX % 10 == 0 ){
                textString = countX * 5 + "";
                Rect rectX = new Rect();
                textPain.getTextBounds(textString,0,textString.length(),rectX);
                textWidth = rectX.right - rectX.left;
                paint.setStrokeWidth(6);
                canvas.drawLine(startX, 0, startX, dip2px(20), paint);
                canvas.drawText(textString,startX-textWidth/2,dip2px(28),textPain);
            }else{
                paint.setStrokeWidth(3);
                canvas.drawLine(startX, 0, startX, dip2px(10), paint);
            }
            startX = startX + LINE_INTERVAL;
            countX++;
        }
        int startY = 0;
        int countY = 0;
        while (startY < screenHeight) {
            if(countY % 10 == 0 ){
                paint.setStrokeWidth(6);
                canvas.drawLine(0, startY, dip2px(20), startY, paint);
                canvas.drawText(countY*5+ "",dip2px(26),startY,textPain);
            }else{
                paint.setStrokeWidth(3);
                canvas.drawLine(0, startY, dip2px(10), startY, paint);
            }
            startY = startY + LINE_INTERVAL;
            countY++;
        }

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        bindActivity.dispatchTouchEvent(event);
        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        bindActivity = null;
    }
}
