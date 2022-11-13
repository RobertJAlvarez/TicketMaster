package ticketmaster.display;

import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;

/**
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
