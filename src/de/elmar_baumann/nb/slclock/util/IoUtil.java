package de.elmar_baumann.nb.slclock.util;

import java.io.Closeable;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.api.annotations.common.NullAllowed;

/**
 * @author Elmar Baumann
 */
public final class IoUtil {

    public static void close(@NullAllowed Closeable... closables) {
        if (closables == null) {
            return;
        }
        for (Closeable closable : closables) {
            if (closable != null) {
                try {
                    closable.close();
                } catch (Throwable throwable) {
                    Logger.getLogger(IoUtil.class.getName()).log(Level.SEVERE, null, throwable);
                }
            }
        }
    }

    private IoUtil() {
    }
}
