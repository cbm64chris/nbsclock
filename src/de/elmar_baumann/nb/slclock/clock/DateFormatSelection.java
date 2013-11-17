package de.elmar_baumann.nb.slclock.clock;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.openide.util.NbBundle;

/**
 * @author Elmar Baumann
 */
public enum DateFormatSelection {

    NONE(null, "DateFormatSelection.NONE.DisplayName"),
    TIME_SHORT(DateFormat.getTimeInstance(DateFormat.SHORT), "DateFormatSelection.TIME_SHORT.DisplayName"),
    TIME_MEDIUM(DateFormat.getTimeInstance(DateFormat.MEDIUM), "DateFormatSelection.TIME_MEDIUM.DisplayName"),
    TIME_LONG(DateFormat.getTimeInstance(DateFormat.LONG), "DateFormatSelection.TIME_LONG.DisplayName"),
    DATE_SHORT(DateFormat.getDateInstance(DateFormat.SHORT), "DateFormatSelection.DATE_SHORT.DisplayName"),
    DATE_MEDIUM(DateFormat.getDateInstance(DateFormat.MEDIUM), "DateFormatSelection.DATE_MEDIUM.DisplayName"),
    DATE_LONG(DateFormat.getDateInstance(DateFormat.LONG), "DateFormatSelection.DATE_LONG.DisplayName"),
    DAY_OF_WEEK_SHORT(new SimpleDateFormat("E"), "DateFormatSelection.DAY_OF_WEEK_SHORT.DisplayName"),
    DAY_OF_WEEK_LONG(new SimpleDateFormat("EEEE"), "DateFormatSelection.DAY_OF_WEEK_LONG.DisplayName"),
    CUSTOM_PATTERN(null, "DateFormatSelection.CUSTOM_PATTERN.DisplayName")
    ;

    private final DateFormat dateFormat;
    private final String displayName;

    private DateFormatSelection(DateFormat dateFormat, String displayNameBundleKey) {
        this.dateFormat = dateFormat;
        this.displayName = NbBundle.getMessage(DateFormatSelection.class, displayNameBundleKey);
    }

    public DateFormat getDateFormat() {
        return dateFormat;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
