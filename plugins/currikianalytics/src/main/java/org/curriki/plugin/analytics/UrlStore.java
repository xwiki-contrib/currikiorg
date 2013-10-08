package org.curriki.plugin.analytics;

import java.util.LinkedList;

/**
 * This class is a wrapper around a list which is used in the CurrikiAnalyitcsSession to store the url history
 * of the current user. The wrapper is needed to limit the size of the information stored in the session of each user.
 */
public class UrlStore extends LinkedList<String> {
    private final static int MAX_SIZE = 50;

    @Override
    public void addLast(String s) {
        if(this.size()+1>MAX_SIZE)this.removeFirst();
        super.addLast(s);
    }

    @Override
    public void addFirst(String s) {
        if(this.size()+1>MAX_SIZE)this.removeLast();
        super.addFirst(s);
    }

    @Override
    public boolean add(String s) {
        if(this.size()+1>MAX_SIZE)this.removeFirst();
        return super.add(s);
    }
}