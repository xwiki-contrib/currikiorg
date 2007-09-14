package org.curriki.gwt.client;

import java.util.EventObject;
/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 *
 * @author jeremi
 */

public class BeforeCurrentAssetChangeEvent  extends EventObject {
    private boolean cancel = false;

    /**
     * Constructs a prototypical Event.
     *
     * @param source The object on which the Event initially occurred.
     * @throws IllegalArgumentException if source is null.
     */
    public BeforeCurrentAssetChangeEvent(Object source) {
        super(source);
    }


  /**
   * Gets whether this change is canceled or not
   *
   * @return <code>true</code> if the change will be cancelled
   */
  public boolean isCancelled() {
    return cancel;
  }

  /**
   * Sets whether the change will be cancelled.
   *
   * @param cancel <code>true</code> to cancel the change
   */
  public void setCancelled(boolean cancel) {
    this.cancel = cancel;
  }
}
