/**
 * Copyright (c) 2009 - 2011 AppWork UG(haftungsbeschränkt) <e-mail@appwork.org>
 * <p/>
 * This file is part of org.appwork.txtresource
 * <p/>
 * This software is licensed under the Artistic License 2.0,
 * see the LICENSE file or http://www.opensource.org/licenses/artistic-license-2.0.php
 * for details
 */
package utils.txtresource;


/**
 * Youc an define controller methods in here. all Translation interfaces have
 * this methods. implement these methods in
 * org.appwork.txtresource.TranslationHandler.invoke(Object, Method, Object[])
 * all methods here have to start with _ to improove lookup speed
 *
 * @author thomas
 *
 */
public interface TranslateInterface {

    TranslationHandler _getHandler();

    /**
     * @return
     */


}
