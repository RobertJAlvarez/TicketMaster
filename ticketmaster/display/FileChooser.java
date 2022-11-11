package ticketmaster.display;

import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;

/**
 * 
 * @author Robert J Alvarez
 * @date September 18th, 2022
 */
public class FileChooser {
  static JLabel label;

  private FileChooser() {}

  public static String chooseFile(String reason) {
System.out.println("111");
    String file = null;

    JFileChooser fileChooser = new JFileChooser(FileSystemView.getFileSystemView().getParentDirectory(new File(".")));
    fileChooser.setAcceptAllFileFilterUsed(false);
    fileChooser.setDialogTitle("Select a .csv file to " + reason);

    FileNameExtensionFilter restriction = new FileNameExtensionFilter("Only .csv files", "csv");
    fileChooser.addChoosableFileFilter(restriction);

    int r = fileChooser.showOpenDialog(null);

    if (r == JFileChooser.APPROVE_OPTION) {
      file = fileChooser.getSelectedFile().getAbsolutePath();
    }

    return file;
  }
}
