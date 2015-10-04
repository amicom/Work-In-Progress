/**
 * 
 */
package events;

import java.util.EventListener;

/**
 * @author $Author: unknown$
 * 
 */
@Deprecated
public interface BasicListener<E> extends EventListener {

    /**
     * @param event
     */
    void onEvent(BasicEvent<E> event);

}
