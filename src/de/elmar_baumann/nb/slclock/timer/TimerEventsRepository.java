package de.elmar_baumann.nb.slclock.timer;

import de.elmar_baumann.nb.slclock.util.IoUtil;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.prefs.Preferences;
import javax.xml.bind.JAXB;
import org.openide.util.NbPreferences;

/**
 * @author Elmar Baumann
 */
public final class TimerEventsRepository {

    private static final String KEY_EVENTS = "TimerEventsRepository.Events";

    public void save(TimerEvents events) {
        if (events == null) {
            throw new NullPointerException("events == null");
        }
        StringWriter sw = new StringWriter();
        events.removeNonPersistentEvents();
        try {
            JAXB.marshal(events, sw);
        } finally {
            IoUtil.close(sw);
        }
        Preferences prefs = NbPreferences.forModule(TimerEventsRepository.class);
        prefs.put(KEY_EVENTS, sw.toString());
    }

    public TimerEvents load() {
        Preferences prefs = NbPreferences.forModule(TimerEventsRepository.class);
        String events = prefs.get(KEY_EVENTS, null);
        return events == null || events.trim().isEmpty()
                ? new TimerEvents()
                : JAXB.unmarshal(new StringReader(events), TimerEvents.class);
    }
}
