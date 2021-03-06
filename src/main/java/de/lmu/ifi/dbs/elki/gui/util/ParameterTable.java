package de.lmu.ifi.dbs.elki.gui.util;

/*
 This file is part of ELKI:
 Environment for Developing KDD-Applications Supported by Index-Structures

 Copyright (C) 2013
 Ludwig-Maximilians-Universität München
 Lehr- und Forschungseinheit für Datenbanksysteme
 ELKI Development Team

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU Affero General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Affero General Public License for more details.

 You should have received a copy of the GNU Affero General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.BitSet;

import javax.swing.AbstractCellEditor;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.DefaultCellEditor;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;

import de.lmu.ifi.dbs.elki.logging.LoggingUtil;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameters.ClassListParameter;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameters.ClassParameter;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameters.EnumParameter;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameters.FileParameter;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameters.Flag;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameters.Parameter;

/**
 * Class showing a table of ELKI parameters.
 * 
 * @author Erich Schubert
 * 
 * @apiviz.composedOf ParametersModel
 * @apiviz.owns de.lmu.ifi.dbs.elki.gui.util.ParameterTable.ColorfulRenderer
 * @apiviz.owns de.lmu.ifi.dbs.elki.gui.util.ParameterTable.DropdownEditor
 * @apiviz.owns de.lmu.ifi.dbs.elki.gui.util.ParameterTable.FileNameEditor
 * @apiviz.owns de.lmu.ifi.dbs.elki.gui.util.ParameterTable.ClassListEditor
 * @apiviz.owns de.lmu.ifi.dbs.elki.gui.util.ParameterTable.AdjustingEditor
 */
public class ParameterTable extends JTable {
  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  /**
   * Color for parameters that are not optional and not yet specified.
   */
  static final Color COLOR_INCOMPLETE = new Color(0xFFCF9F);

  /**
   * Color for parameters with an invalid value.
   */
  static final Color COLOR_SYNTAX_ERROR = new Color(0xFFAFAF);

  /**
   * Color for optional parameters (with no default value)
   */
  static final Color COLOR_OPTIONAL = new Color(0xDFFFDF);

  /**
   * Color for parameters having a default value.
   */
  static final Color COLOR_DEFAULT_VALUE = new Color(0xDFDFDF);

  /**
   * Containing frame.
   */
  protected Frame frame;

  /**
   * The parameters we edit.
   */
  protected DynamicParameters parameters;

  /**
   * Constructor
   * 
   * @param frame Containing frame
   * @param pm Parameter Model
   * @param parameters Parameter storage
   */
  public ParameterTable(Frame frame, ParametersModel pm, DynamicParameters parameters) {
    super(pm);
    this.frame = frame;
    this.parameters = parameters;
    this.setPreferredScrollableViewportSize(new Dimension(800, 400));
    this.setFillsViewportHeight(true);
    final ColorfulRenderer colorfulRenderer = new ColorfulRenderer();
    this.setDefaultRenderer(Parameter.class, colorfulRenderer);
    this.setDefaultRenderer(String.class, colorfulRenderer);
    final AdjustingEditor editor = new AdjustingEditor();
    this.setDefaultEditor(String.class, editor);
    this.setAutoResizeMode(AUTO_RESIZE_ALL_COLUMNS);
    TableColumn col1 = this.getColumnModel().getColumn(0);
    col1.setPreferredWidth(150);
    TableColumn col2 = this.getColumnModel().getColumn(1);
    col2.setPreferredWidth(650);
  }

  /**
   * Renderer for the table that colors the entries according to their bitmask.
   * 
   * @author Erich Schubert
   */
  private class ColorfulRenderer extends DefaultTableCellRenderer {
    /**
     * Serial Version
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructor.
     */
    public ColorfulRenderer() {
      super();
    }

    @Override
    public void setValue(Object value) {
      if (value instanceof String) {
        setText((String) value);
        setToolTipText(null);
        return;
      }
      if (value instanceof DynamicParameters.Node) {
        Parameter<?> o = ((DynamicParameters.Node) value).param;
        // Simulate a tree using indentation - there is no JTreeTable AFAICT
        StringBuilder buf = new StringBuilder();
        for (int i = 1; i < ((DynamicParameters.Node) value).depth; i++) {
          buf.append(' ');
        }
        buf.append(o.getOptionID().getName());
        setText(buf.toString());
        setToolTipText(o.getOptionID().getDescription());
        return;
      }
      setText("");
      setToolTipText(null);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
      Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
      if (!hasFocus) {
        if (row < parameters.size()) {
          BitSet flags = parameters.getNode(row).flags;
          // TODO: don't hardcode black - maybe mix the other colors, too?
          c.setForeground(Color.BLACK);
          if ((flags.get(DynamicParameters.BIT_INVALID))) {
            c.setBackground(COLOR_SYNTAX_ERROR);
          } else if ((flags.get(DynamicParameters.BIT_SYNTAX_ERROR))) {
            c.setBackground(COLOR_SYNTAX_ERROR);
          } else if ((flags.get(DynamicParameters.BIT_INCOMPLETE))) {
            c.setBackground(COLOR_INCOMPLETE);
          } else if ((flags.get(DynamicParameters.BIT_DEFAULT_VALUE))) {
            c.setBackground(COLOR_DEFAULT_VALUE);
          } else if ((flags.get(DynamicParameters.BIT_OPTIONAL))) {
            c.setBackground(COLOR_OPTIONAL);
          } else {
            c.setBackground(null);
          }
        }
      }
      return c;
    }
  }

  /**
   * Editor using a Dropdown box to offer known values to choose from.
   * 
   * @author Erich Schubert
   */
  private class DropdownEditor extends DefaultCellEditor {
    /**
     * Serial Version
     */
    private static final long serialVersionUID = 1L;

    /**
     * We need a panel to ensure focusing.
     */
    final JPanel panel;

    /**
     * Combo box to use
     */
    private final JComboBox<String> comboBox;

    /**
     * Constructor.
     * 
     * @param comboBox Combo box we're going to use
     */
    public DropdownEditor(JComboBox<String> comboBox) {
      super(comboBox);
      this.comboBox = comboBox;
      panel = new DispatchingPanel((JComponent) comboBox.getEditor().getEditorComponent());
      panel.setLayout(new BorderLayout());
      panel.add(comboBox, BorderLayout.CENTER);
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
      // remove old contents
      comboBox.removeAllItems();
      // Put the current value in first.
      Object val = table.getValueAt(row, column);
      if (val != null && val instanceof String) {
        String sval = (String) val;
        if (sval.equals(DynamicParameters.STRING_OPTIONAL)) {
          sval = "";
        }
        if (sval.startsWith(DynamicParameters.STRING_USE_DEFAULT)) {
          sval = "";
        }
        if (sval != "") {
          comboBox.addItem(sval);
          comboBox.setSelectedIndex(0);
        }
      }
      if (row < parameters.size()) {
        Parameter<?> option = parameters.getNode(row).param;
        // We can do dropdown choices for class parameters
        if (option instanceof ClassParameter<?>) {
          ClassParameter<?> cp = (ClassParameter<?>) option;
          // For parameters with a default value, offer using the default
          // For optional parameters, offer not specifying them.
          if (cp.hasDefaultValue()) {
            comboBox.addItem(DynamicParameters.STRING_USE_DEFAULT + cp.getDefaultValueAsString());
          } else if (cp.isOptional()) {
            comboBox.addItem(DynamicParameters.STRING_OPTIONAL);
          }
          // Offer the shorthand version of class names.
          for (Class<?> impl : cp.getKnownImplementations()) {
            comboBox.addItem(ClassParameter.canonicalClassName(impl, cp.getRestrictionClass()));
          }
        }
        // and for Flag parameters.
        else if (option instanceof Flag) {
          if (!Flag.SET.equals(val)) {
            comboBox.addItem(Flag.SET);
          }
          if (!Flag.NOT_SET.equals(val)) {
            comboBox.addItem(Flag.NOT_SET);
          }
        }
        // and for Enum parameters.
        else if (option instanceof EnumParameter<?>) {
          EnumParameter<?> ep = (EnumParameter<?>) option;
          for (String s : ep.getPossibleValues()) {
            if (ep.hasDefaultValue() && ep.getDefaultValueAsString().equals(s)) {
              if (!(DynamicParameters.STRING_USE_DEFAULT + ep.getDefaultValueAsString()).equals(val)) {
                comboBox.addItem(DynamicParameters.STRING_USE_DEFAULT + s);
              }
            } else if (!s.equals(val)) {
              comboBox.addItem(s);
            }
          }
        }
        // No completion for others
      }
      return panel;
    }
  }

  /**
   * Editor for selecting input and output file and folders names
   * 
   * @author Erich Schubert
   */
  private class FileNameEditor extends AbstractCellEditor implements TableCellEditor, ActionListener {
    /**
     * Serial version number
     */
    private static final long serialVersionUID = 1L;

    /**
     * We need a panel to put our components on.
     */
    final JPanel panel;

    /**
     * Text field to store the name
     */
    final JTextField textfield = new JTextField();

    /**
     * The button to open the file selector
     */
    final JButton button = new JButton("...");

    /**
     * File selector mode.
     */
    int mode = FileDialog.LOAD;

    /**
     * Constructor.
     */
    public FileNameEditor() {
      button.addActionListener(this);
      panel = new DispatchingPanel(textfield);
      panel.setLayout(new BorderLayout());
      panel.add(textfield, BorderLayout.CENTER);
      panel.add(button, BorderLayout.EAST);
    }

    /**
     * Button callback to show the file selector
     */
    @Override
    public void actionPerformed(ActionEvent e) {
      final FileDialog fc = new FileDialog(frame);
      fc.setDirectory((new File(".")).getAbsolutePath());
      fc.setMode(mode);
      final String curr = textfield.getText();
      if (curr != null && curr.length() > 0) {
        fc.setFile(curr);
      }
      fc.setVisible(true);
      String filename = fc.getFile();
      if (filename != null) {
        textfield.setText(new File(fc.getDirectory(), filename).getPath());
      }
      fc.dispose();
      textfield.requestFocus();

      // Swing file chooser. Currently much worse on Linux/GTK.
      // final JFileChooser fc = new JFileChooser(new File("."));
      // final String curr = textfield.getText();
      // if (curr != null && curr.length() > 0) {
      // fc.setSelectedFile(new File(curr));
      // }
      // int returnVal = fc.showOpenDialog(button);
      //
      // if(returnVal == JFileChooser.APPROVE_OPTION) {
      // textfield.setText(fc.getSelectedFile().getPath());
      // }
      // else {
      // // Do nothing on cancel.
      // }
      fireEditingStopped();
    }

    /**
     * Delegate getCellEditorValue to the text field.
     */
    @Override
    public Object getCellEditorValue() {
      return textfield.getText();
    }

    /**
     * Apply the Editor for a selected option.
     */
    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
      if (row < parameters.size()) {
        Parameter<?> option = parameters.getNode(row).param;
        if (option instanceof FileParameter) {
          FileParameter fp = (FileParameter) option;
          File f = null;
          mode = FileParameter.FileType.INPUT_FILE.equals(fp.getFileType()) ? FileDialog.LOAD : FileDialog.SAVE;
          if (fp.isDefined()) {
            f = fp.getValue();
          }
          if (f != null) {
            String fn = f.getPath();
            textfield.setText(fn);
          } else {
            textfield.setText("");
          }
        }
      }
      textfield.requestFocus();
      return panel;
    }
  }

  /**
   * Editor for selecting input and output file and folders names
   * 
   * @author Erich Schubert
   */
  private class ClassListEditor extends AbstractCellEditor implements TableCellEditor, ActionListener {
    /**
     * Serial version number
     */
    private static final long serialVersionUID = 1L;

    /**
     * We need a panel to put our components on.
     */
    final JPanel panel;

    /**
     * Text field to store the name
     */
    final JTextField textfield = new JTextField();

    /**
     * The button to open the file selector
     */
    final JButton button = new JButton("+");

    /**
     * The combobox we are abusing to produce the popup
     */
    final JComboBox<String> combo = new JComboBox<>();

    /**
     * The popup menu.
     */
    final SuperPopup popup;

    /**
     * Constructor.
     */
    public ClassListEditor() {
      button.addActionListener(this);
      // So the first item doesn't get automatically selected
      combo.setEditable(true);
      combo.addActionListener(this);
      popup = new SuperPopup(combo);

      panel = new DispatchingPanel(textfield);

      panel.setLayout(new BorderLayout());
      panel.add(textfield, BorderLayout.CENTER);
      panel.add(button, BorderLayout.EAST);
    }

    /**
     * Callback to show the popup menu
     */
    @Override
    public void actionPerformed(ActionEvent e) {
      if (e.getSource() == button) {
        popup.show(panel);
      } else if (e.getSource() == combo) {
        String newClass = (String) combo.getSelectedItem();
        if (newClass != null && newClass.length() > 0) {
          String val = textfield.getText();
          if (val.equals(DynamicParameters.STRING_OPTIONAL)) {
            val = "";
          }
          if (val.startsWith(DynamicParameters.STRING_USE_DEFAULT)) {
            val = "";
          }
          if (val.length() > 0) {
            val = val + ClassListParameter.LIST_SEP + newClass;
          } else {
            val = newClass;
          }
          textfield.setText(val);
          popup.hide();
        }
        fireEditingStopped();
      } else {
        LoggingUtil.warning("Unrecognized action event in ClassListEditor: " + e);
      }
    }

    /**
     * Modified popup
     * 
     * @author Erich Schubert
     * 
     * @apiviz.exclude
     */
    class SuperPopup extends BasicComboPopup {
      /**
       * Serial version
       */
      private static final long serialVersionUID = 1L;

      /**
       * Constructor.
       * 
       * @param combo Combo box used for data storage.
       */
      public SuperPopup(JComboBox<String> combo) {
        super(combo);
      }

      /**
       * Show the menu on a particular panel.
       * 
       * This code is mostly copied from
       * {@link BasicComboPopup#getPopupLocation}
       * 
       * @param parent Parent element to show at.
       */
      public void show(JPanel parent) {
        Dimension popupSize = parent.getSize();
        Insets insets = getInsets();

        // reduce the width of the scrollpane by the insets so that the popup
        // is the same width as the combo box.
        popupSize.setSize(popupSize.width - (insets.right + insets.left), getPopupHeightForRowCount(comboBox.getMaximumRowCount()));
        Rectangle popupBounds = computePopupBounds(0, comboBox.getBounds().height, popupSize.width, popupSize.height);
        Dimension scrollSize = popupBounds.getSize();

        scroller.setMaximumSize(scrollSize);
        scroller.setPreferredSize(scrollSize);
        scroller.setMinimumSize(scrollSize);

        list.revalidate();

        super.show(parent, 0, parent.getBounds().height);
      }
    }

    /**
     * Delegate getCellEditorValue to the text field.
     */
    @Override
    public Object getCellEditorValue() {
      return textfield.getText();
    }

    /**
     * Apply the Editor for a selected option.
     */
    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
      combo.removeAllItems();
      if (row < parameters.size()) {
        Parameter<?> option = parameters.getNode(row).param;
        // We can do dropdown choices for class parameters
        if (option instanceof ClassListParameter<?>) {
          ClassListParameter<?> cp = (ClassListParameter<?>) option;
          // Offer the shorthand version of class names.
          String prefix = cp.getRestrictionClass().getPackage().getName() + ".";
          for (Class<?> impl : cp.getKnownImplementations()) {
            String name = impl.getName();
            if (name.startsWith(prefix)) {
              name = name.substring(prefix.length());
            }
            combo.addItem(name);
          }
        }
        if (option.isDefined()) {
          if (option.tookDefaultValue()) {
            textfield.setText(DynamicParameters.STRING_USE_DEFAULT + option.getDefaultValueAsString());
          } else {
            textfield.setText(option.getValueAsString());
          }
        } else {
          textfield.setText("");
        }
      }
      return panel;
    }
  }

  /**
   * This Editor will adjust to the type of the Option: Sometimes just a plain
   * text editor, sometimes a ComboBox to offer known choices, and sometime a
   * file selector dialog.
   * 
   * TODO: class list parameters etc.
   * 
   * @author Erich Schubert
   * 
   */
  private class AdjustingEditor extends AbstractCellEditor implements TableCellEditor {
    /**
     * Serial version
     */
    private static final long serialVersionUID = 1L;

    /**
     * The dropdown editor
     */
    private final DropdownEditor dropdownEditor;

    /**
     * The plain text cell editor
     */
    private final DefaultCellEditor plaintextEditor;

    /**
     * The class list editor
     */
    private final ClassListEditor classListEditor;

    /**
     * The file selector editor
     */
    private final FileNameEditor fileNameEditor;

    /**
     * We need to remember which editor we delegated to, so we know whom to ask
     * for the value entered.
     */
    private TableCellEditor activeEditor;

    /**
     * Constructor.
     */
    public AdjustingEditor() {
      final JComboBox<String> combobox = new JComboBox<>();
      combobox.setEditable(true);
      this.dropdownEditor = new DropdownEditor(combobox);
      this.plaintextEditor = new DefaultCellEditor(new JTextField());
      this.classListEditor = new ClassListEditor();
      this.fileNameEditor = new FileNameEditor();
    }

    @Override
    public Object getCellEditorValue() {
      if (activeEditor == null) {
        return null;
      }
      return activeEditor.getCellEditorValue();
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
      if (value instanceof String) {
        String s = (String) value;
        if (s.startsWith(DynamicParameters.STRING_USE_DEFAULT)) {
          value = s.substring(DynamicParameters.STRING_USE_DEFAULT.length());
        }
      }
      if (row < parameters.size()) {
        Parameter<?> option = parameters.getNode(row).param;
        if (option instanceof Flag) {
          activeEditor = dropdownEditor;
          return dropdownEditor.getTableCellEditorComponent(table, value, isSelected, row, column);
        }
        if (option instanceof ClassListParameter<?>) {
          activeEditor = classListEditor;
          return classListEditor.getTableCellEditorComponent(table, value, isSelected, row, column);
        }
        if (option instanceof ClassParameter<?>) {
          activeEditor = dropdownEditor;
          return dropdownEditor.getTableCellEditorComponent(table, value, isSelected, row, column);
        }
        if (option instanceof FileParameter) {
          activeEditor = fileNameEditor;
          return fileNameEditor.getTableCellEditorComponent(table, value, isSelected, row, column);
        }
        if (option instanceof EnumParameter<?>) {
          activeEditor = dropdownEditor;
          return dropdownEditor.getTableCellEditorComponent(table, value, isSelected, row, column);
        }
      }
      activeEditor = plaintextEditor;
      return plaintextEditor.getTableCellEditorComponent(table, value, isSelected, row, column);
    }
  }

  /**
   * This is a panel that will dispatch keystrokes to a particular component.
   * 
   * This makes the tabular GUI much more user friendly.
   * 
   * @author Erich Schubert
   * 
   * @apiviz.exclude
   */
  private class DispatchingPanel extends JPanel {
    /**
     * Serial version.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Component to dispatch to.
     */
    protected JComponent component;

    /**
     * Constructor.
     * 
     * @param component Component to dispatch to.
     */
    public DispatchingPanel(JComponent component) {
      super();
      this.component = component;
      setRequestFocusEnabled(true);
    }

    @Override
    public void addNotify() {
      super.addNotify();
      component.requestFocus();
    }

    @Override
    protected boolean processKeyBinding(KeyStroke ks, KeyEvent e, int condition, boolean pressed) {
      InputMap map = component.getInputMap(condition);
      ActionMap am = component.getActionMap();

      if (map != null && am != null && isEnabled()) {
        Object binding = map.get(ks);
        Action action = (binding == null) ? null : am.get(binding);
        if (action != null) {
          return SwingUtilities.notifyAction(action, ks, e, component, e.getModifiers());
        }
      }
      return false;
    }
  };
}
