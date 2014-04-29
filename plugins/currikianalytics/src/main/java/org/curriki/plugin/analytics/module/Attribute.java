package org.curriki.plugin.analytics.module;

import java.util.regex.Pattern;

/**
 * Simple class to represent an attribute being processed in the analytics evolution and persisted
 * the database.
 */
public class Attribute<T> {

    public Attribute(String name, T value) {

    }

    T value;
    String name;

    public T get() {
        Pattern.compile("([0-9]*)\n").matcher("blabl");
        return value;
    }

    public String getName() { return name; }


    /*
    - have the plugin which creates modules
      - request the needed attribute-names from modules
      - provide these attributes every time a session is created
        - when there's one in DB, deserialize
        - when there's none in DB, call the module for initialization
        - ensure a somewhat regular save (sometimes after the request is finished? after a given time which may be interrupted but not always)
      - also store the requests with user-names, jsession-id, IP, decomposed parameters

     */
}
