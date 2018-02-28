package com.sung.extensibletextview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by sung on 2018/2/28.
 */

public class Utils {
    public static final String TAG = "ExtensibleTextview Utils";
    private static final String textColor = "#ff0099cc";

    /**
     * 转换动态文字内容（emoji，话题）
     *
     * @param content 内容
     * @param context 上下文
     * @param scale   缩放
     */
    public static SpannableStringBuilder getDynamicString(String content, final Context context, boolean scale) {
        SpannableStringBuilder stringBuilder = new SpannableStringBuilder();
        stringBuilder.append(content);

        InputStream open = null;
        try {
            //emoji表情的查找替换
            String regex = "(\\[([^\\[\\]]*?)*\\])";
            Matcher matcher = Pattern.compile(regex).matcher(stringBuilder);

            while (matcher.find()) {
                String emoji = matcher.group();
                String regEx = "[0-9]+";
                Pattern p = Pattern.compile(regEx);
                Matcher m = p.matcher(emoji);
                if (m.find()) {
                    String group = m.group();
                    int emojiNumber = Integer.parseInt(group.trim());
                    open = context.getAssets().open(String.format("emoji/%d.png", emojiNumber));
                    Bitmap bitmap = BitmapFactory.decodeStream(open);
                    if (scale) {
                        Matrix matrix = new Matrix();
                        int width = bitmap.getWidth();
                        int height = bitmap.getHeight();
                        matrix.postScale(0.6f, 0.6f);
                        bitmap = Bitmap.createBitmap(bitmap, 0, 0,
                                width, height, matrix, true);
                    }
                    ImageSpan span = new ImageSpan(context, bitmap, ImageSpan.ALIGN_BOTTOM);
                    stringBuilder.setSpan(span, matcher.start(), matcher.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }

            //话题的查找替换
            String topicRegex = "[(\\\"|#)][^(#/ \\s*/\\n*)]+[(\\\"|#)]";
            final Matcher topicMatcher = Pattern.compile(topicRegex).matcher(stringBuilder);
            while (topicMatcher.find()) {
                final String topic = topicMatcher.group();
                int start = topicMatcher.start();
                int end = topicMatcher.end();
                stringBuilder.setSpan(new ForegroundColorSpan(Color.parseColor(textColor)), start, end, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                stringBuilder.setSpan(new ClickableSpan() {
                    @Override
                    public void updateDrawState(TextPaint ds) {
                        super.updateDrawState(ds);
                        ds.setColor(Color.parseColor(textColor));
                        ds.setUnderlineText(false);
                    }

                    @Override
                    public void onClick(View widget) {
                        Toast.makeText(context, topic, Toast.LENGTH_SHORT).show();
                    }
                }, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (open != null) {
                try {
                    open.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return stringBuilder;
    }

}
