package de.elmar_baumann.nb.slclock.clock;

import java.text.DateFormat;

/**
 * @author Elmar Baumann
 */
final class DateFormatAndDelimiter {

    final DateFormat dateFormat;
    final String delimiter;

    DateFormatAndDelimiter(DateFormat dateFormat, String delimiter) {
        this.dateFormat = dateFormat;
        this.delimiter = delimiter;
    }
}
