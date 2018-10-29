package me.ele.appboard;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import me.ele.appboard.base.DimenUtil;

import static android.view.Gravity.BOTTOM;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static me.ele.appboard.TransparentActivity.Type.TYPE_EDIT_ATTR;
import static me.ele.appboard.TransparentActivity.Type.TYPE_RELATIVE_POSITION;
import static me.ele.appboard.TransparentActivity.Type.TYPE_SHOW_GRIDDING;
import static me.ele.appboard.TransparentActivity.Type.TYPE_PERFORM;
import static me.ele.appboard.TransparentActivity.Type.TYPE_UNKNOWN;

public class TransparentActivity extends AppCompatActivity {

    public static final String EXTRA_TYPE = "extra_type";

    private ViewGroup vContainer;
    private int type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            finish();
            return;
        }
        //设置状态栏颜色
        Util.setStatusBarColor(getWindow(), Color.TRANSPARENT);
        //设置全屏
        Util.enableFullscreen(getWindow());
        setContentView(R.layout.uet_activity_transparent);

        vContainer = findViewById(R.id.container);
        //右下角显示当前activity
        final BoardTextView board = new BoardTextView(this);

        type = getIntent().getIntExtra(EXTRA_TYPE, TYPE_UNKNOWN);

        //根据type打开对应的功能
        switch (type) {
            case TYPE_EDIT_ATTR:
                //打开属性编辑面板
                EditAttrLayout editAttrLayout = new EditAttrLayout(this);
                editAttrLayout.setOnDragListener(new EditAttrLayout.OnDragListener() {
                    @Override
                    public void showOffset(String offsetContent) {
                        board.updateInfo(offsetContent);
                    }
                });
                vContainer.addView(editAttrLayout);
                break;
            case TYPE_RELATIVE_POSITION:
                //打开相对位置面板
                vContainer.addView(new RelativePositionLayout(this));
                break;
            case TYPE_SHOW_GRIDDING:
                //打开网格面板
                vContainer.addView(new GriddingLayout(this));
                board.updateInfo("LINE_INTERVAL: " + DimenUtil.px2dip(GriddingLayout.LINE_INTERVAL, true));
                break;
            default:
                Toast.makeText(this, getString(R.string.uet_coming_soon), Toast.LENGTH_SHORT).show();
                finish();
                break;
        }

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        params.gravity = BOTTOM;
        vContainer.addView(board, params);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UETool.getInstance().release();
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }

    @IntDef({
            TYPE_UNKNOWN,
            TYPE_EDIT_ATTR,
            TYPE_SHOW_GRIDDING,
            TYPE_RELATIVE_POSITION,
            TYPE_PERFORM
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface Type {
        int TYPE_UNKNOWN = -1;
        int TYPE_EDIT_ATTR = 1;
        int TYPE_SHOW_GRIDDING = 2;
        int TYPE_RELATIVE_POSITION = 3;
        int TYPE_PERFORM = 4;
    }
}
