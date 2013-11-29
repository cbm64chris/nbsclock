package de.elmar_baumann.nb.slclock.alarmclock;

import java.util.ArrayList;
import java.util.Collection;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Elmar Baumann
 */
@XmlRootElement(name = "alarmevents")
@XmlAccessorType(XmlAccessType.NONE)
public final class AlarmEvents {

    private Collection<AlarmEvent> events = new ArrayList<>();

    public AlarmEvents() {
    }

    public AlarmEvents(Collection<AlarmEvent> events) {
        if (events == null) {
            throw new NullPointerException("events == null");
        }
        setEvents(new ArrayList<>(events));
    }

    @XmlElement(name = "event")
    public Collection<AlarmEvent> getEvents() {
        return events; // JAXB: modifiable
    }

    public void setEvents(Collection<AlarmEvent> events) {
        if (events == null) {
            throw new NullPointerException("events == null");
        }
        this.events = new ArrayList<>(events);
    }

    public AlarmEvents getPersistentEvents() {
        AlarmEvents evts = new AlarmEvents();
        evts.events.addAll(events);
        for (AlarmEvent event : events) {
            if (event.isTemporary()) {
                evts.events.remove(event);
            }
        }
        return evts;
    }
}
