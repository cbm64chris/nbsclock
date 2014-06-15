package de.elmar_baumann.nb.slclock.timer;

import java.util.ArrayList;
import java.util.Collection;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Elmar Baumann
 */
@XmlRootElement(name = "timerevents")
@XmlAccessorType(XmlAccessType.NONE)
public final class TimerEvents {

    @XmlElement(name = "event")
    private Collection<TimerEvent> events = new ArrayList<>();

    public TimerEvents() {
    }

    public TimerEvents(Collection<TimerEvent> events) {
        if (events == null) {
            throw new NullPointerException("events == null");
        }
        setEvents(new ArrayList<>(events));
    }

    public void removeNonPersistentEvents() {
        for (TimerEvent event : new ArrayList<>(events)) {
            if (!event.isPersistent()) {
                events.remove(event);
            }
        }
    }

    public Collection<TimerEvent> getEvents() {
        return events; // JAXB: modifiable
    }

    public void setEvents(Collection<TimerEvent> events) {
        if (events == null) {
            throw new NullPointerException("events == null");
        }
        this.events = new ArrayList<>(events);
    }
}
