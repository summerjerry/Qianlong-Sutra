package com.buddhism.util;

import android.widget.ScrollView;

/**
 * Created by summerxiaqing on 15/11/21.
 * <p/>
 * This class provide a group of function for scroll view
 */
final public class StringUtils {
    public static final int INVALID_HIHGLIGHT = -1;

    // We do not need to new ScrollViewUtils.
    private StringUtils () {
    }

    /**
     * @param content
     *     The source string, can not be null
     * @param query
     *     The query, can not be null
     * @param startPos
     *     The start position
     * @param direction
     *     Find from begin or end
     *
     * @return position
     */
    public static int findNextQuery (
        final String content, final String query, final int startPos,
        final boolean direction) {

        int nextPos;

        if (Utils.isStringEmpty(content) || Utils.isStringEmpty(query) ||
            startPos > content.length() - 1) {
            return INVALID_HIHGLIGHT;
        }

        if (direction) {
            nextPos = content.indexOf(query, startPos);
        } else {
            nextPos = content.lastIndexOf(query, startPos);
        }

        // Did not find query in content.
        if (nextPos == -1) {
            return INVALID_HIHGLIGHT;
        }

        return nextPos;
    }
}
