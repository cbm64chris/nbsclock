package de.elmar_baumann.nb.slclock.clock;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Elmar Baumann
 */
final class DateFormatArray {

    private final List<DateFormatAndDelimiter> formatters = new ArrayList<>();

    void addDateFormat(DateFormat dateFormat, String delimiter) {
        addDateFormat(new DateFormatAndDelimiter(dateFormat, delimiter));
    }

    void addDateFormat(DateFormatAndDelimiter formatAndDelimiter) {
        formatters.add(formatAndDelimiter);
    }

    List<DateFormatAndDelimiter> getFormatters() {
        return Collections.unmodifiableList(formatters);
    }

    int getSize() {
        return formatters.size();
    }

    DateFormatAndDelimiter get(int index) {
        return formatters.get(index);
    }

    String format(Date date) {
        StringBuilder sb = new StringBuilder();
        for (DateFormatAndDelimiter formatter : formatters) {
            if (formatter != null && formatter.dateFormat != null && formatter.delimiter != null) {
                sb.append(formatter.delimiter);
                try {
                    sb.append(formatter.dateFormat.format(date));
                } catch (Throwable t) {
                    Logger.getLogger(DateFormatArray.class.getName()).log(Level.SEVERE, null, t);
                    sb.append("?");
                }
            }
        }
        return sb.toString();
    }
}
