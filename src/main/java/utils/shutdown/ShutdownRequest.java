/**
 * Copyright (c) 2009 - 2013 AppWork UG(haftungsbeschränkt) <e-mail@appwork.org>
 * <p/>
 * This file is part of org.appwork.shutdown
 * <p/>
 * This software is licensed under the Artistic License 2.0,
 * see the LICENSE file or http://www.opensource.org/licenses/artistic-license-2.0.php
 * for details
 */
package utils.shutdown;

import java.util.List;

/**
 * @author daniel
 *
 */
public interface ShutdownRequest {

    public boolean askForVeto(ShutdownVetoListener listener);

    public void addVeto(ShutdownVetoException e);

    /**
     * @return
     */
    public boolean isSilent();

    /**
     * @return
     */
    public List<ShutdownVetoException> getVetos();

    /**
     * @return
     */
    public boolean hasVetos();

    /**
     *
     */
    public void onShutdownVeto();

    /**
     *
     */
    public void onShutdown();
}
