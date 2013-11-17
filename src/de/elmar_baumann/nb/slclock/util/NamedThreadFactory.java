package de.elmar_baumann.nb.slclock.util;

import java.util.concurrent.ThreadFactory;

/**
 * Creates Threads with a (meaningful) name.
 *
 * @author Elmar Baumann
 */
public final class NamedThreadFactory implements ThreadFactory {

    private final String name;

    public NamedThreadFactory(String name) {
        if (name == null) {
            throw new NullPointerException("name == null");
        }
        this.name = name;
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread thread = new Thread(r);
        thread.setName(name);
        return thread;
    }
}
