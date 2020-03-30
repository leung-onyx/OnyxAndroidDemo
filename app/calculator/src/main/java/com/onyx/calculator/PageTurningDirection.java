package com.onyx.calculator;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import androidx.annotation.IntDef;

/**
 * Created by Joy on 14-2-21.
 */
public class PageTurningDirection {
    public static final int NONE = -1;
    public static final int PREV = 0;
    public static final int NEXT = 1;
    // ... type definitions
    // Describes when the annotation will be discarded
    @Retention(RetentionPolicy.SOURCE)
    // Enumerate valid values for this interface
    @IntDef({NONE, PREV,NEXT})
    // Create an interface for validating int types
    public @interface PageTurningDirectionDef {
    }
}
