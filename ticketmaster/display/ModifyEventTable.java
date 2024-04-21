package ticketmaster.display;

import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.TitledBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

public class ModifyEventTable {
  private static JTable table;
  private static TableModel model;

  public static void main(String args[]) {
    modifyEvent();
  }

  public static void modifyEvent() {
    JFrame frame = new JFrame();
    JPanel panel = new JPanel();

    // Don't allow cell 0 to be modify
    model = new PremiereTableModel();

    table = new JTable(model);

    table.setGridColor(Color.orange);
    table.setShowHorizontalLines(true); // Show horizontal lines
    table.setRowSelectionAllowed(true); // User is allow to select rows
    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // User can only select one row at a time
    table.getModel().addTableModelListener(new TableModelListener() {
      @Override
      public void tableChanged(TableModelEvent e) {
        int row = e.getFirstRow();
        int column = e.getColumn();
        String columnName = ModifyEventTable.model.getColumnName(column);
        Object data = model.getValueAt(row, column);

        System.out.println(columnName);
        System.out.println("At row: " + row + " column: " + column + ": " + data.toString());
      }
    });

    panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "ODI Rankings",
        TitledBorder.CENTER, TitledBorder.TOP));
    panel.add(new JScrollPane(table));

    // Set column 2 to have finite possibilities
    TableColumn sportColumn = table.getColumnModel().getColumn(2);
    JComboBox<String> comboBox = new JComboBox<>();
    comboBox.addItem("AUS");
    comboBox.addItem("IND");
    comboBox.addItem("NZ");
    comboBox.addItem("ENG");
    sportColumn.setCellEditor(new DefaultCellEditor(comboBox));

    frame.add(panel);
    frame.setSize(550, 400);
    frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
    frame.setVisible(true);
  }

  static class PremiereTableModel extends AbstractTableModel {
    private final String[] columnNames = { "Rank", "Player", "Country" };
    private final String[][] data = {
        { "1", "Steve", "AUS" },
        { "2", "Virat", "IND" },
        { "3", "Kane", "NZ" },
        { "4", "David", "AUS" },
        { "5", "Ben", "ENG" },
        { "6", "Eion", "ENG" },
    };

    @Override
    public boolean isCellEditable(int rowIdx, int colIdx) {
      return (colIdx != 0);
    }

    @Override
    public int getRowCount() {
      return data.length;
    }

    @Override
    public int getColumnCount() {
      return columnNames.length;
    }

    @Override
    public Object getValueAt(int rowIdx, int colIdx) {
      return data[rowIdx][colIdx];
    }
  }
}
