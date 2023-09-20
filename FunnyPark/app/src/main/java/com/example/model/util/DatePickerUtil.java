package com.example.model.util;

import android.app.DatePickerDialog;
import android.content.Context;
import android.widget.DatePicker;
import android.widget.TextView;

import java.util.Calendar;

/**
 * Created by zouqi on 2017/7/12.
 * 日期选择弹框工具类
 */

public class DatePickerUtil {


    private static int year = Calendar.getInstance().get(Calendar.YEAR);// 当前年份
    private static int month = Calendar.getInstance().get(Calendar.MONTH);// 当前月份
    private static int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);// 当前天

    // 日期对话框的主题，不同主题显示的外观不一样
    public static final int THEME_DEVICE_DEFAULT_DARK = DatePickerDialog.THEME_DEVICE_DEFAULT_DARK;
    public static final int THEME_HOLO_DARK = DatePickerDialog.THEME_HOLO_DARK;
    public static final int THEME_DEVICE_DEFAULT_LIGHT = DatePickerDialog.THEME_DEVICE_DEFAULT_LIGHT;
    public static final int THEME_HOLO_LIGHT = DatePickerDialog.THEME_HOLO_LIGHT;
    public static final int THEME_TRADITIONAL = DatePickerDialog.THEME_TRADITIONAL;


    /**
     * 弹出日期框选择日期
     * @param context 上下文对象
     * @param tv_date 显示日期的textview
     * @param theme 主题
     */
    public static void pickDate(Context context, final TextView tv_date, int theme) {

        DatePickerDialog dialog = new DatePickerDialog(context, theme, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int y, int m, int d) {
                // 例：2017-01-01
                String s = y+"-"+format(m+1)+"-"+format(d);

                // 设置日期
                tv_date.setText(s);

                // 为了使再次点击选择日期时，弹出的日期对话框中的选中日期是textview控件上显示的日期
                // 细节处理，优化用户体验
                year = y;
                month = m;
                day = d;
            }
        }, year, month, day);
        dialog.show();

    }

    /**
     * 弹出日期框选择日期
     * @param context 上下文对象
     * @param tv_date 显示日期的textview
     */
    public static void pickDate(Context context, final TextView tv_date) {

        DatePickerDialog dialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int y, int m, int d) {
                // 例：2017-01-01
                String s = y+"-"+format(m+1)+"-"+format(d);

                // 设置日期
                tv_date.setText(s);

                // 为了使再次点击选择日期时，弹出的日期对话框中的选中日期是textview控件上显示的日期
                // 细节处理，优化用户体验
                year = y;
                month = m;
                day = d;
            }
        }, year, month, day);
        dialog.show();

    }

    /**
     * 格式化日期
     * 1-9 -> 01-09
     * @param i
     * @return
     */
    private static String format(int i) {
        return (String.valueOf(i).length() == 1)?("0"+i): String.valueOf(i);
    }
}
