/**
 * Copyright (c) 2009 - 2011 AppWork UG(haftungsbeschränkt) <e-mail@appwork.org>
 * <p/>
 * This file is part of org.appwork.shutdown
 * <p/>
 * This software is licensed under the Artistic License 2.0,
 * see the LICENSE file or http://www.opensource.org/licenses/artistic-license-2.0.php
 * for details
 */
package utils.shutdown;


/**
 * @author thomas
 *
 */
public interface ShutdownVetoListener {
    /**
     * Step 2a:<br>
     * Informs listener, that shutdown will be done for sure now. Shutdown will
     * happen immediatelly after this call
     * @param request TODO
     */
    void onShutdown(ShutdownRequest request);

    /**
     * step 2b: If one or more listeners in step 1 answered with true(veto) all
     * listeners will be informed afterwards that shutdown has been canceled
     *
     * @param vetos
     */
    void onShutdownVeto(ShutdownRequest request);

    /**
     * step 1b:<br>
     * Application requests shutdown. throws ShutdownVetoException if shutdown
     * currently not possible/wanted
     *
     * @return
     * @throws ShutdownVetoException
     */
    void onShutdownVetoRequest(ShutdownRequest request) throws ShutdownVetoException;


    /**
     * the higher the priority, the earlier the veto listener will be called
     * @return
     */
    long getShutdownVetoPriority();

}
