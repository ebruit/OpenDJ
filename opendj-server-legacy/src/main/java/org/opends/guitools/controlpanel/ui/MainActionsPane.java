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
 * Copyright 2008-2009 Sun Microsystems, Inc.
 * Portions Copyright 2014-2016 ForgeRock AS.
 */

package org.opends.guitools.controlpanel.ui;

import static org.opends.messages.AdminToolMessages.*;
import static com.forgerock.opendj.util.OperatingSystem.isWindows;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;

import org.forgerock.i18n.LocalizableMessage;
import org.opends.guitools.controlpanel.datamodel.Action;
import org.opends.guitools.controlpanel.datamodel.Category;
import org.opends.guitools.controlpanel.event.ConfigurationChangeEvent;
import org.opends.guitools.controlpanel.ui.components.ActionButton;
import org.opends.guitools.controlpanel.ui.components.CategoryPanel;
import org.opends.guitools.controlpanel.util.Utilities;

/**
 * The panel on the left side of the main Control Center dialog.  It contains
 * all the actions on the pane divided in categories.
 */
public class MainActionsPane extends StatusGenericPanel
{
  private static final long serialVersionUID = 7616418700758530191L;

  /** Default constructor. */
  public MainActionsPane()
  {
    super();

    setBackground(ColorAndFontConstants.greyBackground);
    GridBagConstraints gbc1 = new GridBagConstraints();
    gbc1.gridx = 0;
    gbc1.gridy = 0;
    gbc1.fill = GridBagConstraints.HORIZONTAL;
    gbc1.weightx = 1;
    ArrayList<Category> categories = createCategories();
    ButtonGroup group = new ButtonGroup();
    int maxWidth = 0;
    final Map<Action, GenericFrame> frames = new HashMap<>();
    ArrayList<ActionButton> actions = new ArrayList<>();
    for(Category category: categories)
    {
      JPanel categoryPanel = new JPanel(new GridBagLayout());
      GridBagConstraints gbc2 = new GridBagConstraints();
      gbc2.gridx = 0;
      gbc2.gridy = 0;
      gbc2.weightx = 1;
      gbc2.fill = GridBagConstraints.HORIZONTAL;
      for (Action action : category.getActions())
      {
        final ActionButton b = new ActionButton(action);
        actions.add(b);
        b.addActionListener(new ActionListener()
        {
          @Override
          public void actionPerformed(ActionEvent ev)
          {
            // Constructs the panels using reflection.
            Action action = b.getActionObject();
            GenericFrame frame = frames.get(action);
            if (frame == null)
            {
              Class<? extends StatusGenericPanel> panelClass =
                action.getAssociatedPanelClass();
              try
              {
                Constructor<? extends StatusGenericPanel> constructor =
                  panelClass.getDeclaredConstructor();
                StatusGenericPanel panel = constructor.newInstance();
                if (getInfo() != null)
                {
                  panel.setInfo(getInfo());
                }
                frame = createFrame(panel);

                frames.put(action, frame);
                Utilities.centerGoldenMean(frame,
                    Utilities.getFrame(MainActionsPane.this));
              }
              catch (Throwable t)
              {
                // Bug
                t.printStackTrace();
              }
            }
            if (!frame.isVisible())
            {
              frame.setVisible(true);
            }
            else
            {
              frame.toFront();
            }
          }
        });
        categoryPanel.add(b, gbc2);
        gbc2.gridy++;
        group.add(b);
        maxWidth = Math.max(maxWidth, b.getPreferredSize().width);
      }
      CategoryPanel p = new CategoryPanel(categoryPanel, category);
      maxWidth = Math.max(maxWidth, p.getPreferredSize().width);
      p.setExpanded(false);
      add(p, gbc1);
      gbc1.gridy++;

      if (category.getName().equals(
          INFO_CTRL_PANEL_CATEGORY_DIRECTORY_DATA.get()))
      {
        p.setExpanded(true);
      }
    }
    add(Box.createHorizontalStrut(maxWidth), gbc1);
    gbc1.gridy ++;
    gbc1.weighty = 1.0;
    add(Box.createVerticalGlue(), gbc1);
    createActionButtonListeners(actions);
  }

  @Override
  public Component getPreferredFocusComponent()
  {
    return null;
  }

  /**
   * Creates the frame to be displayed using the provided panel.
   * @param panel the panel that will be contained in the frame.
   * @return the frame to be displayed using the provided panel.
   */
  protected GenericFrame createFrame(StatusGenericPanel panel)
  {
    return new GenericFrame(panel);
  }

  /**
   * Creates the categories contained by this panel.
   * @return the categories contained by this panel.
   */
  protected ArrayList<Category> createCategories()
  {
    ArrayList<Category> categories = new ArrayList<>();
    LocalizableMessage[][] labels;
    if (isWindows())
    {
      labels = new LocalizableMessage[][] {
          {
            INFO_CTRL_PANEL_CATEGORY_DIRECTORY_DATA.get(),
            INFO_CTRL_PANEL_ACTION_MANAGE_ENTRIES.get(),
            INFO_CTRL_PANEL_ACTION_NEW_BASEDN.get(),
            INFO_CTRL_PANEL_ACTION_IMPORT_LDIF.get(),
            INFO_CTRL_PANEL_ACTION_EXPORT_LDIF.get(),
            INFO_CTRL_PANEL_ACTION_BACKUP.get(),
            INFO_CTRL_PANEL_ACTION_RESTORE.get()
          },
          {
            INFO_CTRL_PANEL_CATEGORY_SCHEMA.get(),
            INFO_CTRL_PANEL_ACTION_MANAGE_SCHEMA.get()
          },
          {
            INFO_CTRL_PANEL_CATEGORY_INDEXES.get(),
            INFO_CTRL_PANEL_ACTION_MANAGE_INDEXES.get(),
            INFO_CTRL_PANEL_ACTION_VERIFY_INDEXES.get(),
            INFO_CTRL_PANEL_ACTION_REBUILD_INDEXES.get()
          },
          {
            INFO_CTRL_PANEL_CATEGORY_MONITORING.get(),
            INFO_CTRL_PANEL_BROWSE_GENERAL_MONITORING.get(),
            INFO_CTRL_PANEL_CONNECTION_HANDLER_MONITORING.get(),
            INFO_CTRL_PANEL_MANAGE_TASKS.get()
          },
          {
            INFO_CTRL_PANEL_CATEGORY_RUNTIME_OPTIONS.get(),
            INFO_CTRL_PANEL_ACTION_JAVA_SETTINGS.get(),
            INFO_CTRL_PANEL_ACTION_WINDOWS_SERVICE.get()
          }
      };
    }
    else
    {
      labels = new LocalizableMessage[][] {
          {
            INFO_CTRL_PANEL_CATEGORY_DIRECTORY_DATA.get(),
            INFO_CTRL_PANEL_ACTION_MANAGE_ENTRIES.get(),
            INFO_CTRL_PANEL_ACTION_NEW_BASEDN.get(),
            INFO_CTRL_PANEL_ACTION_IMPORT_LDIF.get(),
            INFO_CTRL_PANEL_ACTION_EXPORT_LDIF.get(),
            INFO_CTRL_PANEL_ACTION_BACKUP.get(),
            INFO_CTRL_PANEL_ACTION_RESTORE.get()
          },
          {
            INFO_CTRL_PANEL_CATEGORY_SCHEMA.get(),
            INFO_CTRL_PANEL_ACTION_MANAGE_SCHEMA.get()
          },
          {
            INFO_CTRL_PANEL_CATEGORY_INDEXES.get(),
            INFO_CTRL_PANEL_ACTION_MANAGE_INDEXES.get(),
            INFO_CTRL_PANEL_ACTION_VERIFY_INDEXES.get(),
            INFO_CTRL_PANEL_ACTION_REBUILD_INDEXES.get()
          },
          {
            INFO_CTRL_PANEL_CATEGORY_MONITORING.get(),
            INFO_CTRL_PANEL_BROWSE_GENERAL_MONITORING.get(),
            INFO_CTRL_PANEL_CONNECTION_HANDLER_MONITORING.get(),
            INFO_CTRL_PANEL_MANAGE_TASKS.get()
          },
          {
            INFO_CTRL_PANEL_CATEGORY_RUNTIME_OPTIONS.get(),
            INFO_CTRL_PANEL_ACTION_JAVA_SETTINGS.get()
          }
      };
    }
    ArrayList<Class<? extends StatusGenericPanel>> classes = new ArrayList<>();
    classes.add(BrowseEntriesPanel.class);
    classes.add(NewBaseDNPanel.class);
    classes.add(ImportLDIFPanel.class);
    classes.add(ExportLDIFPanel.class);
    classes.add(BackupPanel.class);
    classes.add(RestorePanel.class);
    classes.add(BrowseSchemaPanel.class);
    classes.add(BrowseIndexPanel.class);
    classes.add(VerifyIndexPanel.class);
    classes.add(RebuildIndexPanel.class);
    classes.add(BrowseGeneralMonitoringPanel.class);
    classes.add(ConnectionHandlerMonitoringPanel.class);
    classes.add(ManageTasksPanel.class);
    classes.add(JavaPropertiesPanel.class);
    if (isWindows())
    {
      classes.add(WindowsServicePanel.class);
    }
    int classIndex = 0;
    for (LocalizableMessage[] label : labels)
    {
      Category category = new Category();
      category.setName(label[0]);
      for (int j = 1; j < label.length; j++)
      {
        Action action = new Action();
        action.setName(label[j]);
        action.setAssociatedPanel(classes.get(classIndex));
        classIndex ++;

        category.getActions().add(action);
      }
      categories.add(category);
    }
    return categories;
  }

  /**
   * This is required because in some desktops we might encounter a case
   * where several actions are highlighted.
   * @param actions the actions
   */
  private void createActionButtonListeners(
      final Collection<ActionButton> actions)
  {
    ActionListener actionListener = new ActionListener()
    {
      @Override
      public void actionPerformed(ActionEvent ev)
      {
        for (ActionButton button : actions)
        {
          if (ev.getSource() == button)
          {
            button.actionPerformed(ev);
            break;
          }
        }
      }
    };

    MouseAdapter mouseListener = new MouseAdapter()
    {
      @Override
      public void mousePressed(MouseEvent ev)
      {
        for (ActionButton button : actions)
        {
          if (ev.getSource() == button)
          {
            button.mousePressed(ev);
            break;
          }
        }
      }

      @Override
      public void mouseReleased(MouseEvent ev)
      {
        for (ActionButton button : actions)
        {
          if (ev.getSource() == button)
          {
            button.mouseReleased(ev);
            break;
          }
        }
      }

      @Override
      public void mouseExited(MouseEvent ev)
      {
        for (ActionButton button : actions)
        {
          if (ev.getSource() == button)
          {
            button.mouseExited(ev);
            break;
          }
        }
      }

      @Override
      public void mouseEntered(MouseEvent ev)
      {
        for (ActionButton button : actions)
        {
          if (ev.getSource() == button)
          {
            button.mouseEntered(ev);
          }
          else
          {
            button.mouseExited(ev);
          }
        }
      }
    };

    for (ActionButton button : actions)
    {
      button.addActionListener(actionListener);
      button.addMouseListener(mouseListener);
    }
  }

  @Override
  public LocalizableMessage getTitle()
  {
    return null;
  }

  @Override
  public void configurationChanged(ConfigurationChangeEvent ev)
  {
  }

  @Override
  public void okClicked()
  {
  }
}
