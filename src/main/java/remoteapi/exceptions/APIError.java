/**
 * Copyright (c) 2009 - 2013 AppWork UG(haftungsbeschränkt) <e-mail@appwork.org>
 * 
 * This file is part of org.appwork.remoteapi.upload
 * 
 * This software is licensed under the Artistic License 2.0,
 * see the LICENSE file or http://www.opensource.org/licenses/artistic-license-2.0.php
 * for details
 */
package remoteapi.exceptions;


import http.HTTPConstants;

/**
 * @author Thomas
 *
 */
public interface APIError {

   
  

    String name();

    HTTPConstants.ResponseCode getCode();

}
