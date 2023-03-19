package com.cheezycode.notesample.ui.note;

import android.graphics.Typeface;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;

public class textApi {

    //Apply Bold Span
    public static void applyBoldSpan(Editable editable) {
        StyleSpan boldSpan = new StyleSpan(Typeface.BOLD);
        int start = editable.getSpanStart(boldSpan);
        int end = editable.getSpanEnd(boldSpan);
        if (start >= 0 && end >= 0) {
            editable.removeSpan(boldSpan);
            if (start != end) {
                editable.setSpan(boldSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        } else {
            int selectionStart = Selection.getSelectionStart(editable);
            int selectionEnd = Selection.getSelectionEnd(editable);
            if (selectionStart != selectionEnd) {
                editable.setSpan(boldSpan, selectionStart, selectionEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
    }

    //Apply Italic Span
    public static void applyItalicSpan(Editable editable) {
        StyleSpan italicSpan = new StyleSpan(Typeface.ITALIC);
        int start = editable.getSpanStart(italicSpan);
        int end = editable.getSpanEnd(italicSpan);
        if (start >= 0 && end >= 0) {
            editable.removeSpan(italicSpan);
            if (start != end) {
                editable.setSpan(italicSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        } else {
            int selectionStart = Selection.getSelectionStart(editable);
            int selectionEnd = Selection.getSelectionEnd(editable);
            if (selectionStart != selectionEnd) {
                editable.setSpan(italicSpan, selectionStart, selectionEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
    }

    //Apply underline span
    public static void applyUnderlineSpan(Editable editable) {
        UnderlineSpan underlineSpan = new UnderlineSpan();
        int start = editable.getSpanStart(underlineSpan);
        int end = editable.getSpanEnd(underlineSpan);
        if (start >= 0 && end >= 0) {
            editable.removeSpan(underlineSpan);
            if (start != end) {
                editable.setSpan(underlineSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        } else {
            int selectionStart = Selection.getSelectionStart(editable);
            int selectionEnd = Selection.getSelectionEnd(editable);
            if (selectionStart != selectionEnd) {
                editable.setSpan(underlineSpan, selectionStart, selectionEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
    }

    // Apply Strikethrough Span
    public static void applyStrikethroughSpan(Editable editable) {
        StrikethroughSpan strikethroughSpan = new StrikethroughSpan();
        int start = editable.getSpanStart(strikethroughSpan);
        int end = editable.getSpanEnd(strikethroughSpan);
        if (start >= 0 && end >= 0) {
            editable.removeSpan(strikethroughSpan);
            if (start != end) {
                editable.setSpan(strikethroughSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        } else {
            int selectionStart = Selection.getSelectionStart(editable);
            int selectionEnd = Selection.getSelectionEnd(editable);
            if (selectionStart != selectionEnd) {
                editable.setSpan(strikethroughSpan, selectionStart, selectionEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
    }



}
