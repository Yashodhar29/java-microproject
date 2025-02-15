import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Stack;

import javax.imageio.ImageIO;
import javax.swing.*;

class CustomPanel extends JPanel {
    private BufferedImage backgroundImage = null;
    private String imagePath = null;
    private Stack<BufferedImage>undoStack = new Stack<>();
    private Stack<BufferedImage>redoStack = new Stack<>();
    
    int left, right, top, bottom;
    int imageWidth, imageHeight;
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            int panelWidth = getWidth();
            int panelHeight = getHeight();

            // Get the width and height of the image
            imageWidth = backgroundImage.getWidth();
            imageHeight = backgroundImage.getHeight();

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

    public void cropImage(int left, int right, int top, int bottom) {
        int width = this.imageWidth - right - left;
        int height = this.imageHeight - bottom - top;
        BufferedImage croppedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int y = top; y <this.imageHeight - bottom; y++) {
            for (int x = left; x < this.imageWidth - right; x++) {
                int rgb = backgroundImage.getRGB(x, y);
                croppedImage.setRGB(x - left, y - top, rgb);
            }
        }
        undoStack.push(this.getBackgroundImage());
        redoStack.clear();
        this.setBackgroundImage(croppedImage);
    }

    public void flipImage(boolean horizontal, boolean vertical) {
        BufferedImage flippedImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
        if(horizontal) {
            for (int y = 0; y < imageHeight; y++) {
                for (int x = 0; x < imageWidth; x++) {
                    int rgb = backgroundImage.getRGB(x, y);
                    flippedImage.setRGB(imageWidth - x - 1, y, rgb);
                }
            }
            undoStack.push(this.getBackgroundImage());
            this.setBackgroundImage(flippedImage);
        } else {
            for (int y = 0; y < imageHeight; y++) {
                for (int x = 0; x < imageWidth; x++) {
                    int rgb = backgroundImage.getRGB(x, y);
                    flippedImage.setRGB(x, imageHeight - y - 1, rgb);
                }   
            }
        }
        undoStack.push(this.getBackgroundImage());
        redoStack.clear();
        this.setBackgroundImage(flippedImage);
    }

    public void rotateImage(int angle) {
        BufferedImage rotatedImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = rotatedImage.createGraphics();
        g2d.rotate(Math.toRadians(angle), imageWidth / 2.0, imageHeight / 2.0);
        g2d.drawImage(backgroundImage, 0, 0, null);
        g2d.dispose();
        this.setBackgroundImage(rotatedImage);
        undoStack.push(this.getBackgroundImage());
        redoStack.clear();
    }

    

public void adjustContrast(int contrast) {
    int width = backgroundImage.getWidth();
    int height = backgroundImage.getHeight();
    BufferedImage adjustedImage = new BufferedImage(width, height, backgroundImage.getType());

    // Convert contrast range from [-100, 100] to a factor (1.0 means no change)
    float factor = (float) (contrast / 100.0 + 1.0);

    for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
            int rgb = backgroundImage.getRGB(x, y);
            int a = (rgb >> 24) & 0xFF; // Extract alpha
            int r = (rgb >> 16) & 0xFF;
            int g = (rgb >> 8) & 0xFF;
            int b = rgb & 0xFF;

            // Adjust contrast: (color - 128) * factor + 128
            r = (int) ((r - 128) * factor + 128);
            g = (int) ((g - 128) * factor + 128);
            b = (int) ((b - 128) * factor + 128);

            // Clamp values between [0, 255]
            r = Math.min(Math.max(r, 0), 255);
            g = Math.min(Math.max(g, 0), 255);
            b = Math.min(Math.max(b, 0), 255);

            int newRGB = (a << 24) | (r << 16) | (g << 8) | b;
            adjustedImage.setRGB(x, y, newRGB);
        }
    }
    undoStack.push(this.getBackgroundImage());
    redoStack.clear();
    this.setBackgroundImage(adjustedImage);
}



public void adjustOpacity(int opacity) {
    int width = backgroundImage.getWidth();
    int height = backgroundImage.getHeight();
    BufferedImage adjustedImage = new BufferedImage(width, height, backgroundImage.getType());
    for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
            int rgb = backgroundImage.getRGB(x, y);
            int a = (rgb >> 24) & 0xFF; // Extract alpha
            int r = (rgb >> 16) & 0xFF;
            int g = (rgb >> 8) & 0xFF;
            int b = rgb & 0xFF;

            // Adjust opacity: alpha * opacity / 100
            a = (int) (a * opacity / 100.0);

            // Clamp values between [0, 255]
            a = Math.min(Math.max(a, 0), 255);

            int newRGB = (a << 24) | (r << 16) | (g << 8) | b;
            adjustedImage.setRGB(x, y, newRGB);
        }
    }
    undoStack.push(this.getBackgroundImage());
    redoStack.clear();
    this.setBackgroundImage(adjustedImage);
}

public void adjustSaturation(int saturation) {
    int width = backgroundImage.getWidth();
    int height = backgroundImage.getHeight();
    BufferedImage adjustedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

    // Convert input range (-100 to 100) to a usable factor
    float saturationFactor = (saturation / 100.0f) + 1.0f; // -100 → 0.0, 0 → 1.0, +100 → 2.0

    for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
            int rgb = backgroundImage.getRGB(x, y);
            int alpha = (rgb >> 24) & 0xFF; // Extract alpha

            // Extract RGB values
            int red = (rgb >> 16) & 0xFF;
            int green = (rgb >> 8) & 0xFF;
            int blue = rgb & 0xFF;

            // Convert RGB to HSB
            float[] hsb = Color.RGBtoHSB(red, green, blue, null);

            // Adjust the saturation and clamp it between [0,1]
            hsb[1] *= saturationFactor;
            hsb[1] = Math.min(1.0f, Math.max(0.0f, hsb[1]));

            // Convert back to RGB
            int newRGB = Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]);

            // Preserve the original alpha channel
            int finalRGB = (alpha << 24) | (newRGB & 0xFFFFFF);
            adjustedImage.setRGB(x, y, finalRGB);
        }
    }
    undoStack.push(this.getBackgroundImage());
    redoStack.clear();
    this.setBackgroundImage(adjustedImage);
}

public void invertImage() {
    // Create a new BufferedImage to store the inverted image
    BufferedImage invertedImage = new BufferedImage(backgroundImage.getWidth(), backgroundImage.getHeight(), backgroundImage.getType());

    // Iterate through each pixel of the image
    for (int x = 0; x < backgroundImage.getWidth(); x++) {
        for (int y = 0; y < backgroundImage.getHeight(); y++) {
            // Get the RGB value of the current pixel
            int rgb = backgroundImage.getRGB(x, y);

            // Extract the color components (red, green, blue)
            Color color = new Color(rgb);
            int red = color.getRed();
            int green = color.getGreen();
            int blue = color.getBlue();

            // Invert the colors by subtracting each component from 255
            int invertedRed = 255 - red;
            int invertedGreen = 255 - green;
            int invertedBlue = 255 - blue;

            // Set the new inverted color back to the pixel
            Color invertedColor = new Color(invertedRed, invertedGreen, invertedBlue);
            invertedImage.setRGB(x, y, invertedColor.getRGB());
        }
    }

    // Return the inverted image
    undoStack.push(this.getBackgroundImage());
    redoStack.clear();
    this.setBackgroundImage(invertedImage);
}

public void setImagePath(String path) {
    System.out.println("obtained path: " + path);
    this.imagePath = path;
}

public void setBackgroundImage(BufferedImage image) {
    if (image != null) {
        // Store a deep copy of the original image
        this.backgroundImage = image;
    }
    repaint();
}

public BufferedImage getBackgroundImage() {
    return backgroundImage;
}

public void reset() {
    File imageFile = new File(imagePath);
    
    try {
        // Read the image using ImageIO.read()
        BufferedImage image = ImageIO.read(imageFile);
        if (image != null) {
            System.out.println("Image loaded successfully.");
            this.setBackgroundImage(image);
        } else {
            System.out.println("Error: Image could not be read (returned null).");
        }
    } catch (IOException ex) {
        ex.printStackTrace();
        System.out.println("Error reading the image file.");
    }
}

public void undo() {
    if (!undoStack.isEmpty()) {
        redoStack.push(this.getBackgroundImage()); // Save current state to redo stack
        BufferedImage previousImage = undoStack.pop(); // Restore last image from undo stack
        this.setBackgroundImage(previousImage);
    } else {
        JOptionPane.showMessageDialog(null, "No actions to undo!");
    }
}

public void redo() {
    if (!redoStack.isEmpty()) {
        undoStack.push(this.getBackgroundImage()); // Save current state to undo stack
        BufferedImage nextImage = redoStack.pop(); // Restore last image from redo stack
        this.setBackgroundImage(nextImage);
    } else {
        JOptionPane.showMessageDialog(null, "No actions to redo!");
    }
}

public void about() {
    String message = "Java Swing Photo Enhancer (MICROPROJECT)\nCreated By:\n1. Yashodhar Chavan(23210230262)\n2. Yuvraj Gandhmal(enrollment_no)";
    JOptionPane.showMessageDialog(null, message);

}


}

public class Main {
    public static void main(String[] args) {
        final JFrame frame = new JFrame("Photo Editor");

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1280, 730);
        frame.setMinimumSize(new Dimension(1280, 730));
        // frame.setLayout(null);
        frame.setIconImage(new ImageIcon("./E (3).png").getImage());

        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem fileItem1 = new JMenuItem("OPEN");
        JMenuItem fileItem2 = new JMenuItem("SAVE AS (ctrl+shift+s)");
        JMenuItem fileItem3 = new JMenuItem("EXIT (alt+f4)");

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));
        mainPanel.setBackground(new Color(146, 147, 148));

        CustomPanel canvas = new CustomPanel();
        canvas.setLayout(new FlowLayout());
        canvas.setBackground(Color.WHITE);
        canvas.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        canvas.setPreferredSize(new Dimension(900, 550));
        canvas.setMaximumSize(new Dimension(900, 550));

        JPanel leftSpacePanel = new JPanel();
        leftSpacePanel.setBackground(new Color(146, 147, 148));
        leftSpacePanel.setPreferredSize(new Dimension(30, 800));
        leftSpacePanel.setMaximumSize(new Dimension(30, 800));

        CustomPanel options = new CustomPanel();
        options.setLayout(new BoxLayout(options, BoxLayout.Y_AXIS));
        options.setBackground(Color.CYAN);
        options.setPreferredSize(new Dimension(300, 800));
        options.setMaximumSize(new Dimension(300, 800));
        options.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // NOTE: 
        // 1. Cropping of image done yusss yess I am happy thank I really thank.
        // 2. Ohh my GOD, the rotate has also completed. I am so happy. The sky is limit. Thank you
        // 3. I am stunned to see that flip is also completed. Thank you so much.
        // 4. Thank GOD reset also works. ChatGPT is very good I just know at high level what is happening
        // 5. I really really thank universe. the saturation part is done.
        // 6. contrast part is also done
        // 7. invert also works very well
        // 8. opacity works also well. What else we need?? I am totally happy now. Yes I did not code it but I at least understood it in high level
        // I am so happy now. The universe made me happy. I really love engineers at chatGPT and universe. Thank you so much the 90% work is done. The software is ready. I did not thing I can do it. But it happend. 


        JButton[] listOfButtons = new JButton[8];

        listOfButtons[0] = new JButton("CROP");
        listOfButtons[1] = new JButton("ROTATE");
        listOfButtons[2] = new JButton("FLIP");
        listOfButtons[3] = new JButton("OPACITY");
        listOfButtons[4] = new JButton("CONTRAST");
        listOfButtons[5] = new JButton("SATURATION");
        listOfButtons[6] = new JButton("RESET");
        listOfButtons[7] = new JButton("INVERT");

        // NOTE: Setting the buttons in options

        for (JButton button : listOfButtons) {
            options.add(button);
            options.add(Box.createVerticalStrut(5));
        }

        CustomPanel adjustments = new CustomPanel();
        adjustments.setLayout(new BoxLayout(adjustments, BoxLayout.Y_AXIS));
        adjustments.setBackground(new Color(146, 147, 148));
        adjustments.setPreferredSize(new Dimension(300, 300));
        adjustments.setMaximumSize(new Dimension(300, 300));
        adjustments.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel adjustmentsLabel = new JLabel("Adjustments: ");
        adjustmentsLabel.setFont(new Font("Arial", Font.BOLD, 20));
        adjustmentsLabel.setForeground(Color.WHITE);
        adjustments.add(adjustmentsLabel);

        for (JButton button : listOfButtons) {
            button.addActionListener(e -> {

                adjustments.removeAll(); // Clear previous adjustments
                adjustments.add(adjustmentsLabel); // Re-add the label after clearing

                String action = button.getText();

                // Update canvas mode based on button text
                switch (action) {
                    case "CROP":
                        // adjustments.add(new JLabel("CROP MODE ENABLED"));
                        adjustments.add(new JLabel("Left: "));
                        adjustments.add(new JTextField(""));
                        adjustments.add(new JLabel("right: "));
                        adjustments.add(new JTextField(""));
                        adjustments.add(new JLabel("top: "));
                        adjustments.add(new JTextField(""));
                        adjustments.add(new JLabel("bottom: "));
                        adjustments.add(new JTextField(""));

                        JButton cropSubmitButton = new JButton("submit");
                        adjustments.add(cropSubmitButton);
                        cropSubmitButton.addActionListener(e1 -> {
                            // NOTE: was working on crop feature
                            int left = Integer.parseInt(((JTextField) adjustments.getComponent(2)).getText());
                            int right = Integer.parseInt(((JTextField) adjustments.getComponent(4)).getText());
                            int top = Integer.parseInt(((JTextField) adjustments.getComponent(6)).getText());
                            int bottom = Integer.parseInt(((JTextField) adjustments.getComponent(8)).getText());
                            canvas.cropImage(left, right, top, bottom);

                            adjustments.removeAll();
                            adjustments.add(adjustmentsLabel);
                            adjustments.revalidate();
                            adjustments.repaint();
                        });

                        break;
                        case "ROTATE":

                        adjustments.add(new JLabel("Angle: "));
                        JTextField angleField = new JTextField("");
                        angleField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
                        adjustments.add(angleField);


                        JButton rotateSubmitButton = new JButton("Submit");
                        adjustments.add(rotateSubmitButton);



                        rotateSubmitButton.addActionListener(e1 -> {
                            int angle = Integer.parseInt(((JTextField) adjustments.getComponent(2)).getText());
                            canvas.rotateImage(angle);
                            adjustments.removeAll();
                            adjustments.add(adjustmentsLabel);
                            adjustments.revalidate();
                            adjustments.repaint();
                        });


                        break;
                    case "FLIP":
                        JButton horizontalFlipButton = new JButton("HORIZONTAL");
                        adjustments.add(horizontalFlipButton);
                        
                        JButton verticalFlipButton = new JButton("VERTICAL");
                        adjustments.add(verticalFlipButton);

                        horizontalFlipButton.addActionListener(e1 -> {
                            canvas.flipImage(true, false);
                            adjustments.removeAll();
                            adjustments.add(adjustmentsLabel);
                            adjustments.revalidate();
                            adjustments.repaint();
                        });

                        verticalFlipButton.addActionListener(e1 -> {
                            canvas.flipImage(false, true);
                            adjustments.removeAll();
                            adjustments.add(adjustmentsLabel);
                            adjustments.revalidate();
                            adjustments.repaint();
                        });



                        break;
                    case "OPACITY":
                        // it is not working
                        adjustments.add(new JLabel("Opacity: (0 to 100)"));
                        JTextField opacityInputField = new JTextField("");
                        opacityInputField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
                        adjustments.add(opacityInputField);


                        JButton opacitySubmitButton = new JButton("Submit");
                        adjustments.add(opacitySubmitButton);


                        opacitySubmitButton.addActionListener(a -> {
                            int value = Integer.parseInt(opacityInputField.getText());

                            canvas.adjustOpacity(value);

                        });
                        break;
                    case "CONTRAST":
                        adjustments.add(new JLabel("Contrast: (-100 to 100)"));
                        JTextField contrastInputField = new JTextField("");
                        contrastInputField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
                        adjustments.add(contrastInputField);


                        JButton contrastSubmitButton = new JButton("Submit");
                        adjustments.add(contrastSubmitButton);

                        contrastSubmitButton.addActionListener(a -> {
                            int value = Integer.parseInt(contrastInputField.getText());
                            canvas.adjustContrast(value);
                        });

                        break;
                    
                    case "SATURATION":
                        System.out.println("SATURATION");
                        adjustments.add(new JLabel("Saturation: (0 to 100)"));
                        JTextField saturationInputField = new JTextField("");
                        saturationInputField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
                        adjustments.add(saturationInputField);

                        JButton saturationSubmitButton = new JButton("submit");
                        adjustments.add(saturationSubmitButton);

                        saturationSubmitButton.addActionListener(ac -> {
                            int value = Integer.parseInt(saturationInputField.getText());
                            canvas.adjustSaturation(value);
                        });
                        break;
                    case "RESET":
                        canvas.reset();
                        break;
                    case "INVERT":
                        JButton invertButton = new JButton("INVERT");
                        adjustments.add(invertButton);

                        invertButton.addActionListener(e1 -> {
                            canvas.invertImage();
                        });
                        break;
                }

                // Revalidate and repaint to reflect changes
                adjustments.revalidate();
                adjustments.repaint();
            });
        }

        mainPanel.add(leftSpacePanel);
        mainPanel.add(canvas);
        mainPanel.add(Box.createHorizontalGlue());
        mainPanel.add(options);
        frame.add(mainPanel);

        options.add(Box.createVerticalGlue());
        options.add(adjustments);

        fileMenu.add(fileItem1);
        fileMenu.add(fileItem2);
        fileMenu.add(fileItem3);

        fileItem1.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Open Image");

            // Open the file chooser and check if the user selected a file
            int result = fileChooser.showOpenDialog(frame);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();

                try {
                    BufferedImage image = ImageIO.read(selectedFile);
                    if (image != null) {
                        canvas.setImagePath(selectedFile.getAbsolutePath());
                        canvas.setBackgroundImage(image);
                    } else {
                        JOptionPane.showMessageDialog(frame, "Error loading image.", "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(frame, "Error reading the file.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        canvas.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                canvas.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                canvas.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            }
        });

        

       fileItem2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Save Image");

                fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("PNG Images", "png"));
                
                int userSelection = fileChooser.showSaveDialog(null);
                
                if (userSelection == JFileChooser.APPROVE_OPTION) {
                    File fileToSave = fileChooser.getSelectedFile();
                    
                    if (!fileToSave.getName().endsWith(".png")) {
                        fileToSave = new File(fileToSave.getAbsolutePath() + ".png");
                    }
                    
                    try {
                        BufferedImage imageToSave = canvas.getBackgroundImage();  // Replace with your image
                        
                        ImageIO.write(imageToSave, "PNG", fileToSave);
                        JOptionPane.showMessageDialog(null, "Image saved successfully!");
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(null, "Error saving image: " + ex.getMessage());
                    }
                }
            }
        });

        InputMap inputMap = canvas.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = canvas.getActionMap();

        // Associate Ctrl + Shift + S with the "saveImage" action
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK), "saveImage");
        actionMap.put("saveImage", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Save Image");

                fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("PNG Images", "png"));
                
                int userSelection = fileChooser.showSaveDialog(null);
                
                if (userSelection == JFileChooser.APPROVE_OPTION) {
                    File fileToSave = fileChooser.getSelectedFile();
                    
                    if (!fileToSave.getName().endsWith(".png")) {
                        fileToSave = new File(fileToSave.getAbsolutePath() + ".png");
                    }
                    
                    try {
                        BufferedImage imageToSave = canvas.getBackgroundImage();  // Replace with your image
                        
                        ImageIO.write(imageToSave, "PNG", fileToSave);
                        JOptionPane.showMessageDialog(null, "Image saved successfully!");
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(null, "Error saving image: " + ex.getMessage());
                    }
                }
            }   
        });

        fileItem3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                System.exit(0);
            }
        });

        JMenu editMenu = new JMenu("Edit");
        JMenuItem editItem1 = new JMenuItem("UNDO (ctrl+z)");
        JMenuItem editItem2 = new JMenuItem("REDO (ctrl+y)");

        editMenu.add(editItem1);
        editMenu.add(editItem2);

        editItem1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                canvas.undo();
            }
        });

        // Add Keyboard Shortcut (Ctrl + Z) for Undo
        InputMap inputMapForUndo = canvas.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMapForUndo = canvas.getActionMap();

        // Bind Ctrl + Z to "undo"
        inputMapForUndo.put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK), "undo");

        // Define the Undo action
        actionMapForUndo.put("undo", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                canvas.undo(); // Call the undo method
            }
        });


        editItem2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                canvas.redo();
            }
        });

        InputMap inputMapForRedo = canvas.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMapForRedo = canvas.getActionMap();

        // Bind Ctrl + Z to "undo"
        inputMapForRedo.put(KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_DOWN_MASK), "redo");

        // Define the Undo action
        actionMapForRedo.put("redo", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                canvas.redo(); // Call the undo method
            }
        });


        JMenu infoMenu = new JMenu("Info");
        JMenuItem infoItem1 = new JMenuItem("About");
        infoMenu.add(infoItem1);

        infoItem1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                canvas.about();
            }
        });

        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(infoMenu);

        frame.setJMenuBar(menuBar);
        frame.setVisible(true);
    }
}