package de.elmar_baumann.nb.slclock.clock;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import org.openide.util.NbPreferences;

/**
 * @author Elmar Baumann
 */
public final class ClockPreferences {

    private static final int COUNT_LIMIT = 25;
    private static final String KEY_DATE_FORMAT_COUNT = "StatusLinePreferences.DateFormat.Count";
    private static final String KEY_DATE_FORMAT_SELECTION_PREFIX = "StatusLinePreferences.DateFormatSelection.";
    private static final String KEY_DATE_FORMAT_PATTERN_PREFIX = "StatusLinePreferences.DateFormatPattern.";
    private static final String KEY_DATE_FORMAT_DELIMITER_PREFIX = "StatusLinePreferences.FormatDelimiter.";
    private static final Set<ClockPreferencesListener> LISTENERS = new CopyOnWriteArraySet<>();

    static DateFormatArray restoreDateFormatArray() {
        if (!isFormatPersisted()) {
            return createDefaultDateFormatArray();
        }
        try {
            DateFormatArray array = new DateFormatArray();
            Preferences prefs = NbPreferences.forModule(ClockPreferences.class);
            int count = prefs.getInt(KEY_DATE_FORMAT_COUNT, 0);
            for (int i = 0; i < count; i++) {
                String delimiter = prefs.get(KEY_DATE_FORMAT_DELIMITER_PREFIX + i, "");
                DateFormatSelection dateFormatSelection = DateFormatSelection.valueOf(prefs.get(KEY_DATE_FORMAT_SELECTION_PREFIX + i, DateFormatSelection.NONE.name()));
                if (dateFormatSelection == DateFormatSelection.CUSTOM_PATTERN) {
                    String pattern = prefs.get(KEY_DATE_FORMAT_PATTERN_PREFIX + i, "");
                    array.addDateFormat(new SimpleDateFormat(pattern), delimiter);
                } else if (dateFormatSelection != DateFormatSelection.NONE) {
                    array.addDateFormat(dateFormatSelection.getDateFormat(), delimiter);
                }
            }
            return array;

        } catch (Throwable t) {
            Logger.getLogger(ClockPreferences.class.getName()).log(Level.SEVERE, null, t);
            return createDefaultDateFormatArray();
        }
    }

    static private boolean isFormatPersisted() {
        Preferences prefs = NbPreferences.forModule(ClockPreferences.class);
        int count = prefs.getInt(KEY_DATE_FORMAT_COUNT, 0);
        return count > 0;
    }

    static private DateFormatArray createDefaultDateFormatArray() {
        DateFormatArray array = new DateFormatArray();
        array.addDateFormat(new SimpleDateFormat("E"), "");
        array.addDateFormat(DateFormat.getDateInstance(DateFormat.SHORT), " ");
        array.addDateFormat(DateFormat.getTimeInstance(DateFormat.SHORT), " ");
        return array;
    }

    static void restoreDateFormatSettingsPanel(DateFormatSettingPanel panel, int index) {
        if (!isFormatPersisted()) {
            return;
        }
        Preferences prefs = NbPreferences.forModule(ClockPreferences.class);
        String dateFormatSelectionName = prefs.get(KEY_DATE_FORMAT_SELECTION_PREFIX + index, null);
        if (dateFormatSelectionName == null) {
            panel.setDateFormatSelection(DateFormatSelection.NONE);
            panel.setDelimiter("");
            panel.setCustomPattern("");
            return;
        }
        try {
            DateFormatSelection dateFormatSelection = DateFormatSelection.valueOf(dateFormatSelectionName);
            panel.setDateFormatSelection(dateFormatSelection);
            panel.setDelimiter(prefs.get(KEY_DATE_FORMAT_DELIMITER_PREFIX + index, ""));
            panel.setCustomPattern(prefs.get(KEY_DATE_FORMAT_PATTERN_PREFIX + index, ""));
        } catch (Throwable t) {
            Logger.getLogger(ClockPreferences.class.getName()).log(Level.SEVERE, null, t);
        }
    }

    static void persistDateFormatArray(List<DateFormatSettingPanel> panels) {
        clear();
        Preferences prefs = NbPreferences.forModule(ClockPreferences.class);
        int count = panels.size();
        prefs.putInt(KEY_DATE_FORMAT_COUNT, count);
        for (int i = 0; i < count; i++) {
            DateFormatSettingPanel panel = panels.get(i);
                prefs.put(KEY_DATE_FORMAT_SELECTION_PREFIX + i, panel.getDateFormatSelection().name());
                prefs.put(KEY_DATE_FORMAT_DELIMITER_PREFIX + i, panel.getDelimiter());
                prefs.put(KEY_DATE_FORMAT_PATTERN_PREFIX + i, panel.getCustomPattern());
        }
        notifyDateFormatChanged(restoreDateFormatArray());
    }

    static private void clear() {
        Preferences prefs = NbPreferences.forModule(ClockPreferences.class);
        prefs.remove(KEY_DATE_FORMAT_COUNT);
        for (int i = 0; i < COUNT_LIMIT; i++) {
            prefs.remove(KEY_DATE_FORMAT_DELIMITER_PREFIX + i);
            prefs.remove(KEY_DATE_FORMAT_PATTERN_PREFIX + i);
        }
    }

    static void addListener(ClockPreferencesListener listener) {
        LISTENERS.add(listener);
    }

    static void removeListener(ClockPreferencesListener listener) {
        LISTENERS.remove(listener);
    }

    private static void notifyDateFormatChanged(DateFormatArray dateFormatArray) {
        for (ClockPreferencesListener listener : LISTENERS) {
            listener.dateFormatChanged(dateFormatArray);
        }
    }

    private ClockPreferences() {
    }
}
