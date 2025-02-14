import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;

class Panel1 extends JPanel {
    private BufferedImage backgroundImage = null;
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            int panelWidth = getWidth();
            int panelHeight = getHeight();
    
            // Get the width and height of the image
            int imageWidth = backgroundImage.getWidth();
            int imageHeight = backgroundImage.getHeight();
    
            // Calculate the scaling factor to fit the image within the panel
            double scaleX = (double) panelWidth / imageWidth;
            double scaleY = (double) panelHeight / imageHeight;
            double scale = Math.min(scaleX, scaleY); // Choose the smaller scaling factor to maintain aspect ratio
    
            // Calculate the new width and height of the image after scaling
            int newImageWidth = (int) (imageWidth * scale);
            int newImageHeight = (int) (imageHeight * scale);
    
            // Calculate the x and y position to center the image in the panel
            int x = (panelWidth - newImageWidth) / 2; // Horizontal center
            int y = (panelHeight - newImageHeight) / 2; // Vertical center
    
            // Draw the scaled image at the calculated position
            g.drawImage(backgroundImage, x, y, newImageWidth, newImageHeight, this);
        }
    }

    // Method to set the background image
    public void setBackgroundImage(BufferedImage image) {
        this.backgroundImage = image;
        repaint();  // Repaint the panel to apply the new image
    }
}
public class Main {
    public static void main(String[] args) {
        final JFrame frame = new JFrame("Photo Editor");

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1273, 638);
        frame.setMinimumSize(new Dimension(1273, 630));
        // frame.setLayout(null);
        frame.setIconImage(new ImageIcon("./E (3).png").getImage());
        
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem fileItem1 = new JMenuItem("OPEN");    
        JMenuItem fileItem2 = new JMenuItem("NEW");    
        JMenuItem fileItem3 = new JMenuItem("SAVE (ctrl+s)");    
        JMenuItem fileItem4 = new JMenuItem("SAVE AS (ctrl+shift+s)");    
        JMenuItem fileItem5 = new JMenuItem("EXIT (alt+f4)");    

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new FlowLayout(FlowLayout.CENTER));  // Center content
        mainPanel.setBackground(Color.CYAN);

        Panel1 panel1 = new Panel1();

        panel1.setLayout(new FlowLayout());
        panel1.setBackground(Color.RED);
        panel1.setPreferredSize(new Dimension(900, 450));
        panel1.setMaximumSize(new Dimension(900, 450));
        
        // Adding padding around panel1 using EmptyBorder
        panel1.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40)); // Padding (top, left, bottom, right)

        
        mainPanel.add(panel1);
        frame.add(mainPanel);
        
        fileMenu.add(fileItem1);
        fileMenu.add(fileItem2);
        fileMenu.add(fileItem3);
        fileMenu.add(fileItem4);
        fileMenu.add(fileItem5);



        fileItem1.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Open Image");

            // Open the file chooser and check if the user selected a file
            int result = fileChooser.showOpenDialog(frame);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                System.out.println("Selected file: " + selectedFile.getAbsolutePath());

                // Read the selected file (example for image loading)
                try {
                    BufferedImage image = ImageIO.read(selectedFile);
                    if (image != null) {

                        panel1.setBackgroundImage(image);
                        System.out.println("Image successfully loaded.");
                    } else {
                        JOptionPane.showMessageDialog(frame, "Error loading image.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(frame, "Error reading the file.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });


        fileItem5.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                System.exit(0);
            }
        });


        JMenu editMenu = new JMenu("Edit");
        JMenuItem editItem1 = new JMenuItem("UNDO (ctrl+z)");    
        JMenuItem editItem2 = new JMenuItem("REDO (ctrl+y)");    
        JMenuItem editItem3 = new JMenuItem("CUT (ctrl+x)");    
        JMenuItem editItem4 = new JMenuItem("COPY (ctrl+c)");    
        JMenuItem editItem5 = new JMenuItem("PASTE (ctrl+v)");    

        editMenu.add(editItem1);
        editMenu.add(editItem2);
        editMenu.add(editItem3);
        editMenu.add(editItem4);
        editMenu.add(editItem5);

        
        JMenu infoMenu = new JMenu("Info");
        JMenuItem infoItem1 = new JMenuItem("About");
        infoMenu.add(infoItem1);
        
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(infoMenu);


        frame.setJMenuBar(menuBar);
        frame.setVisible(true);
    }
}