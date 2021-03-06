/*
 * The contents of this file are subject to the terms of the Common Development and
 * Distribution License (the License). You may not use this file except in compliance with the
 * License.
 *
 * You can obtain a copy of the License at legal/CDDLv1.0.txt. See the License for the
 * specific language governing permission and limitations under the License.
 *
 * When distributing Covered Software, include this CDDL Header Notice in each file and include
 * the License file at legal/CDDLv1.0.txt. If applicable, add the following below the CDDL
 * Header, with the fields enclosed by brackets [] replaced by your own identifying
 * information: "Portions Copyright [year] [name of copyright owner]".
 *
 * Copyright 2008 Sun Microsystems, Inc.
 */

package org.opends.guitools.controlpanel.event;

import org.opends.guitools.controlpanel.datamodel.AbstractIndexDescriptor;

/**
 * The event that describes that an index has been selected.  This is used
 * in the dialog 'Manage Index' to notify that a new index is selected on the
 * tree.
 *
 */
public class IndexSelectionEvent
{
  private Object source;
  private AbstractIndexDescriptor index;

  /**
   * Constructor of the event.
   * @param source the source of the event.
   * @param index the index that has been selected.
   */
  public IndexSelectionEvent(Object source, AbstractIndexDescriptor index)
  {
    this.source = source;
    this.index = index;
  }

  /**
   * Returns the index that has been selected.
   * @return the index that has been selected.
   */
  public AbstractIndexDescriptor getIndex()
  {
    return index;
  }

  /**
   * Returns the source of the event.
   * @return the source of the event.
   */
  public Object getSource()
  {
    return source;
  }
}
