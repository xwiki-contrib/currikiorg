package org.curriki.cloud;

import org.xwiki.component.annotation.ComponentRole;

@ComponentRole
public interface CdnWatcher {
    public void invalidateAttachment(String fullName, String attachmentName);
}