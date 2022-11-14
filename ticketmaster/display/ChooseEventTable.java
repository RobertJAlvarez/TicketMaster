package ticketmaster.display;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.TitledBorder;

public class ChooseEventTable implements ActionListener {
  private static JTable table;

  public static void main(String[] args) {
    JFrame frame = new JFrame();
    JPanel panel = new JPanel();
    String[][] rec = {
       { "1", "Steve", "AUS" },
       { "2", "Virat", "IND" },
       { "3", "Kane", "NZ" },
       { "4", "David", "AUS" },
       { "5", "Ben", "ENG" },
       { "6", "Eion", "ENG" },
    };
    String[] header = { "Rank", "Player", "Country" };
    table = new JTable(rec, header);
    JScrollPane sp = new JScrollPane(table);

    table.setGridColor(Color.orange);
    table.setShowHorizontalLines(true);       //Show horizontal lines
    table.setRowSelectionAllowed(true);       //User is allow to select rows
    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);   //User can only select one row at a time

    panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "ODI Rankings", TitledBorder.CENTER, TitledBorder.TOP));
    panel.add(new JScrollPane(table));

    frame.add(sp);
    frame.add(panel);
    frame.setSize(550, 400);
    frame.setVisible(true);
 }

  @Override
  public void actionPerformed(ActionEvent e) {
    int eventID = Integer.parseInt(table.getModel().getValueAt(table.getSelectedRow(), 0).toString());

    System.out.println(eventID);
  }
}
