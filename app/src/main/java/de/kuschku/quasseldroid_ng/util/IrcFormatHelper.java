package de.kuschku.quasseldroid_ng.util;


import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Parcel;
import android.provider.Browser;
import android.text.ParcelableSpan;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.URLSpan;
import android.util.Log;
import android.view.View;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.kuschku.quasseldroid_ng.R;

public class IrcFormatHelper {
    private static final String scheme = "(?:(?:mailto:|(?:[+.-]?\\w)+://)|www(?=\\.\\S+\\.))";
    private static final String authority = "(?:(?:[,.;@:]?[-\\w]+)+\\.?|\\[[0-9a-f:.]+\\])(?::\\d+)?";
    private static final String urlChars = "(?:[,.;:]*[\\w~@/?&=+$()!%#*-])";
    private static final String urlEnd = "(?:>|[,.;:\"]*\\s|\\b|$)";
    private static final Pattern urlPattern = Pattern.compile(String.format("\\b(%s%s(?:/%s*)?)%s", scheme, authority, urlChars, urlEnd), Pattern.CASE_INSENSITIVE);
    private static final Pattern channelPattern = Pattern.compile("((?:#|![A-Z0-9]{5})[^,:\\s]+(?::[^,:\\s]+)?)\\b", Pattern.CASE_INSENSITIVE);

    private final ThemeUtil.Colors colors;

    public IrcFormatHelper(ThemeUtil.Colors colors) {
        this.colors = colors;
    }

    public CharSequence formatUserNick(String nick) {
        int colorIndex = IrcUserUtils.getSenderColor(nick);
        int color = colors.senderColors[colorIndex];

        SpannableString str = new SpannableString(nick);
        str.setSpan(new ForegroundColorSpan(color), 0, nick.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        str.setSpan(new StyleSpan(Typeface.BOLD), 0, nick.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        return str;
    }

    public CharSequence formatIrcMessage(String message) {
        List<FutureClickableSpan> spans = new LinkedList<>();

        SpannableString str = new SpannableString(message);
        Matcher urlMatcher = urlPattern.matcher(str);
        while (urlMatcher.find()) {
            spans.add(new FutureClickableSpan(new CustomURLSpan(urlMatcher.toString()), urlMatcher.start(), urlMatcher.end()));
        }
        for (FutureClickableSpan span : spans) {
            str.setSpan(span.span, span.start, span.end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        }
        return str;
    }

    private static class FutureClickableSpan {
        public final ClickableSpan span;
        public final int start;
        public final int end;

        public FutureClickableSpan(ClickableSpan span, int start, int end) {
            this.span = span;
            this.start = start;
            this.end = end;
        }
    }

    private class CustomURLSpan extends ClickableSpan implements ParcelableSpan {
        private final String mURL;

        public CustomURLSpan(String url) {
            mURL = url;
        }

        public CustomURLSpan(Parcel src) {
            mURL = src.readString();
        }

        public int getSpanTypeId() {
            return R.id.custom_url_span;
        }

        public int describeContents() {
            return 0;
        }

        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(mURL);
        }

        public String getURL() {
            return mURL;
        }

        @Override
        public void onClick(View widget) {
            Log.e("TEST", "THIS IS A TEST");

            Uri uri = Uri.parse(getURL());
            Context context = widget.getContext();
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.putExtra(Browser.EXTRA_APPLICATION_ID, context.getPackageName());
            try {
                context.startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Log.w("URLSpan", "Actvity was not found for intent, " + intent.toString());
            }
        }
    }
}
