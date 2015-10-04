/**
 * Copyright (c) 2009 - 2010 AppWork UG(haftungsbeschränkt) <e-mail@appwork.org>
 * <p/>
 * This file is part of org.appwork.net.ssl
 * <p/>
 * This software is licensed under the Artistic License 2.0,
 * see the LICENSE file or http://www.opensource.org/licenses/artistic-license-2.0.php
 * for details
 */
package utils.net.ssl;

import javax.net.ssl.ManagerFactoryParameters;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactorySpi;
import javax.net.ssl.X509TrustManager;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.X509Certificate;

public final class CryptServiceProvider extends TrustManagerFactorySpi {
    public CryptServiceProvider() {
    }

    public static String getAlgorithm() {
        return "XTrust509";
    }

    @Override
    protected TrustManager[] engineGetTrustManagers() {
        return new TrustManager[]{new X509TrustManager() {
            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }

            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }

            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        }};
    }

    @Override
    protected void engineInit(KeyStore keystore) throws KeyStoreException {
    }

    @Override
    protected void engineInit(ManagerFactoryParameters mgrparams) throws InvalidAlgorithmParameterException {
        throw new InvalidAlgorithmParameterException("ManagerFactoryParameters are not implemented in " + TrustProvider.getProviderName());
    }
}