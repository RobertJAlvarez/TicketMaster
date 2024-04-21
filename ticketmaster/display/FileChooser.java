package ticketmaster.display;

import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;

/**
 * This class provide a method (chooseFile) which make a pop up window at the current directory and it lets you select
 * a .csv file and the absolute path is return if one is selected. Otherwise, a null string is returned.
 *
 * @author Robert J Alvarez
 * @date November 8th, 2022
 */
public class FileChooser {
  private static JFileChooser fc = null;

  private FileChooser() {}

  public static String chooseFile(String reason) {
    String file = null;

    if (fc == null) {
      fc = new JFileChooser(FileSystemView.getFileSystemView().getParentDirectory(new File(".")));
      fc.setAcceptAllFileFilterUsed(false);

      FileNameExtensionFilter restriction = new FileNameExtensionFilter("Only .csv files", "csv");
      fc.addChoosableFileFilter(restriction);
    }

    //Write pop up header
    fc.setDialogTitle("Select a .csv file to " + reason);

    int r = fc.showOpenDialog(null);

    if (r == JFileChooser.APPROVE_OPTION) {
      file = fc.getSelectedFile().getAbsolutePath();
    }

    return file;
  }
}
