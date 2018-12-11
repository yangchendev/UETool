package me.ele.appboard;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.PixelFormat;
import android.os.Build;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static me.ele.appboard.TransparentActivity.Type.TYPE_UNKNOWN;

public class UETMenu extends LinearLayout {

    private View vMenu;
    private ViewGroup vSubMenuContainer;
    private ViewGroup vMainMenu;
    private ViewGroup mLayoutUed;
    private ViewGroup mUedMenuContainer;
    private ViewGroup mSubMenuCatch;
    private ViewGroup mSubMenuGrid;
    private ViewGroup mSubMenuPosition;
    private ValueAnimator animator;
    private Interpolator defaultInterpolator = new AccelerateDecelerateInterpolator();
    private List<UETSubMenu.SubMenu> subMenus = new ArrayList<>();

    private WindowManager windowManager;
    private WindowManager.LayoutParams params = new WindowManager.LayoutParams();
    private int touchSlop;
    private int y;

    public enum Appboard {
        UED,
        PERFORM,
        DEVTOOL,
        APPDATA
    }
    public UETMenu(final Context context, int y) {
        super(context);
        inflate(context, R.layout.uet_menu_layout, this);
        setGravity(Gravity.CENTER_VERTICAL);

        this.y = y;
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        vMenu = findViewById(R.id.menu);
        vSubMenuContainer = findViewById(R.id.sub_menu_container);
        vMainMenu = findViewById(R.id.include_appboard_main_menu);
        mLayoutUed = findViewById(R.id.appboard_ued);
        mUedMenuContainer = findViewById(R.id.include_appboard_ued_menu);
        mSubMenuCatch = mUedMenuContainer.findViewById(R.id.ued_catch);
        mSubMenuGrid = mUedMenuContainer.findViewById(R.id.ued_grid);
        mSubMenuPosition = mUedMenuContainer.findViewById(R.id.ued_position);
        mLayoutUed.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,"ued",Toast.LENGTH_LONG).show();
                //todo 切换成ued的布局
                if(vMainMenu.getVisibility() == VISIBLE){
                    vMainMenu.setVisibility(GONE);
                    mUedMenuContainer.setVisibility(VISIBLE);
                }
            }
        });
        mSubMenuCatch.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                open(TransparentActivity.Type.TYPE_EDIT_ATTR);
            }
        });
        mSubMenuGrid.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                open(TransparentActivity.Type.TYPE_SHOW_GRIDDING);
            }
        });
        mSubMenuPosition.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                open(TransparentActivity.Type.TYPE_RELATIVE_POSITION);
            }
        });
        Resources resources = context.getResources();

//        subMenus.add(new UETSubMenu.SubMenu(resources.getString(R.string.uet_catch_view), R.drawable.uet_edit_attr, new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                open(TransparentActivity.Type.TYPE_EDIT_ATTR);
//            }
//        }));
//        subMenus.add(new UETSubMenu.SubMenu(resources.getString(R.string.uet_relative_location), R.drawable.uet_relative_position,
//                new OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        open(TransparentActivity.Type.TYPE_RELATIVE_POSITION);
//                    }
//                }));
//        subMenus.add(new UETSubMenu.SubMenu(resources.getString(R.string.uet_grid), R.drawable.uet_show_gridding,
//                new OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        open(TransparentActivity.Type.TYPE_SHOW_GRIDDING);
//                    }
//                }));
//        for (UETSubMenu.SubMenu subMenu : subMenus) {
//            UETSubMenu uetSubMenu = new UETSubMenu(getContext());
//            uetSubMenu.update(subMenu);
//            vSubMenuContainer.addView(uetSubMenu);
//        }

        vMenu.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //startAnim();
                if(vMainMenu.getVisibility() == VISIBLE){
                    vMainMenu.setVisibility(GONE);
                }else if(mUedMenuContainer.getVisibility() == VISIBLE){
                    mUedMenuContainer.setVisibility(GONE);
                    vMainMenu.setVisibility(VISIBLE);
                }else{
                    vMainMenu.setVisibility(VISIBLE);
                }
            }
        });

        vMenu.setOnTouchListener(new View.OnTouchListener() {
            private float downX, downY;
            private float lastY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        downX = event.getRawX();
                        downY = event.getRawY();
                        lastY = downY;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        params.y += event.getRawY() - lastY;
                        params.y = Math.max(0, params.y);
                        windowManager.updateViewLayout(UETMenu.this, params);
                        lastY = event.getRawY();
                        break;
                    case MotionEvent.ACTION_UP:
                        if (Math.abs(event.getRawX() - downX) < touchSlop && Math.abs(event.getRawY() - downY) < touchSlop) {
                            try {
                                Field field = View.class.getDeclaredField("mListenerInfo");
                                field.setAccessible(true);
                                Object object = field.get(vMenu);
                                field = object.getClass().getDeclaredField("mOnClickListener");
                                field.setAccessible(true);
                                object = field.get(object);
                                if (object != null && object instanceof View.OnClickListener) {
                                    ((View.OnClickListener) object).onClick(vMenu);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        break;
                }
                return true;
            }
        });
    }

    private void startAnim() {
        ensureAnim();
        final boolean isOpen = vSubMenuContainer.getTranslationX() <= -vSubMenuContainer.getWidth();
        animator.setInterpolator(isOpen ? defaultInterpolator : new ReverseInterpolator(defaultInterpolator));
        animator.removeAllListeners();
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                vSubMenuContainer.setVisibility(VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (!isOpen) {
                    vSubMenuContainer.setVisibility(GONE);
                }
            }
        });
        animator.start();
    }

    private void ensureAnim() {
        if (animator == null) {
            animator = ValueAnimator.ofInt(-vSubMenuContainer.getWidth(), 0);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    vSubMenuContainer.setTranslationX((int) animation.getAnimatedValue());
                }
            });
            animator.setDuration(400);
        }
    }

    private void open() {
        open(TYPE_UNKNOWN);
    }

    private void open(@TransparentActivity.Type int type) {
        Activity currentTopActivity = Util.getCurrentActivity();
        if (currentTopActivity == null) {
            return;
        } else if (currentTopActivity.getClass() == TransparentActivity.class) {
            currentTopActivity.finish();
            return;
        }
        Intent intent = new Intent(currentTopActivity, TransparentActivity.class);
        intent.putExtra(TransparentActivity.EXTRA_TYPE, type);
        currentTopActivity.startActivity(intent);
        currentTopActivity.overridePendingTransition(0, 0);
        UETool.getInstance().setTargetActivity(currentTopActivity);
    }

    public void show() {
        try {
            windowManager.addView(this, getWindowLayoutParams());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int dismiss() {
        try {
            windowManager.removeView(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return params.y;
    }

    private WindowManager.LayoutParams getWindowLayoutParams() {
        params.width = FrameLayout.LayoutParams.WRAP_CONTENT;
        params.height = FrameLayout.LayoutParams.WRAP_CONTENT;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        } else {
            params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        }
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        params.format = PixelFormat.TRANSLUCENT;
        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.x = 10;
        params.y = y;
        return params;
    }

    private static class ReverseInterpolator implements TimeInterpolator {

        private TimeInterpolator mWrappedInterpolator;

        ReverseInterpolator(TimeInterpolator interpolator) {
            mWrappedInterpolator = interpolator;
        }

        @Override
        public float getInterpolation(float input) {
            return mWrappedInterpolator.getInterpolation(Math.abs(input - 1f));
        }
    }

    private void updateSubMenus(Appboard type,Context context){
        if(subMenus == null){
            return;
        }
        subMenus.clear();
        vSubMenuContainer.removeAllViews();
        Resources resources = context.getResources();
        switch (type){
            case UED:
                subMenus.add(new UETSubMenu.SubMenu(resources.getString(R.string.uet_catch_view), R.drawable.uet_edit_attr, new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        open(TransparentActivity.Type.TYPE_EDIT_ATTR);
                    }
                }));
                subMenus.add(new UETSubMenu.SubMenu(resources.getString(R.string.uet_relative_location), R.drawable.uet_relative_position,
                        new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                open(TransparentActivity.Type.TYPE_RELATIVE_POSITION);
                            }
                        }));
                subMenus.add(new UETSubMenu.SubMenu(resources.getString(R.string.uet_grid), R.drawable.uet_show_gridding,
                        new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                open(TransparentActivity.Type.TYPE_SHOW_GRIDDING);
                            }
                        }));
                for (UETSubMenu.SubMenu subMenu : subMenus) {
                    UETSubMenu uetSubMenu = new UETSubMenu(getContext());
                    uetSubMenu.update(subMenu);
                    vSubMenuContainer.addView(uetSubMenu);
                }
                break;
            case PERFORM:
                break;
            case APPDATA:
                break;
            case DEVTOOL:
                break;
            default:
                break;
        }
    }
}
