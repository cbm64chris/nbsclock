package de.elmar_baumann.nb.slclock.alarmclock;

import de.elmar_baumann.nb.slclock.util.IoUtil;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.prefs.Preferences;
import javax.xml.bind.JAXB;
import org.openide.util.NbPreferences;

/**
 * @author Elmar Baumann
 */
public final class AlarmEventsRepository {

    private static final String KEY_EVENTS = "AlarmEventsRepository.Events";

    public void save(AlarmEvents events) {
        if (events == null) {
            throw new NullPointerException("events == null");
        }
        StringWriter sw = new StringWriter();
        try {
            JAXB.marshal(events, sw);
        } finally {
            IoUtil.close(sw);
        }
        Preferences prefs = NbPreferences.forModule(AlarmEventsRepository.class);
        prefs.put(KEY_EVENTS, sw.toString());
    }

    public AlarmEvents load() {
        Preferences prefs = NbPreferences.forModule(AlarmEventsRepository.class);
        String events = prefs.get(KEY_EVENTS, null);
        return events == null || events.trim().isEmpty()
                ? new AlarmEvents()
                : JAXB.unmarshal(new StringReader(events), AlarmEvents.class);
    }
}
