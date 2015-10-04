/**
 * Copyright (c) 2009 - 2014 AppWork UG(haftungsbeschränkt) <e-mail@appwork.org>
 * <p/>
 * This file is part of org.appwork.swing.components
 * <p/>
 * This software is licensed under the Artistic License 2.0,
 * see the LICENSE file or http://www.opensource.org/licenses/artistic-license-2.0.php
 * for details
 */
package swing.components;


import storage.Storable;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Thomas
 *
 */
public class IconIdentifier implements Storable {
    private HashMap<String, Object> prps;
    private String cls;
    private String key;
    private ArrayList<IconIdentifier> rsc;

    /**
     *
     */
    public IconIdentifier(/* Storable */) {

    }

    /**
     *
     */
    public IconIdentifier(String cls) {
        this.cls = cls;
    }

    public IconIdentifier(String cls, String tld) {
        this(cls);
        this.key = tld;

    }

    public HashMap<String, Object> getPrps() {
        return prps;
    }

    public void setPrps(HashMap<String, Object> prps) {
        this.prps = prps;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getCls() {
        return cls;
    }

    public void setCls(String cls) {
        this.cls = cls;
    }

    public ArrayList<IconIdentifier> getRsc() {
        return rsc;
    }

    public void setRsc(ArrayList<IconIdentifier> rsc) {
        this.rsc = rsc;
    }

    /**
     * @param iconResource
     */
    public void add(IconIdentifier iconResource) {
        if (rsc == null) {
            rsc = new ArrayList<IconIdentifier>();
        }
        rsc.add(iconResource);
    }


    public void addProperty(String key, Object value) {
        if (prps == null) {
            prps = new HashMap<String, Object>();
        }
        prps.put(key, value);

    }
}
