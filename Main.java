import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
// import java.awt.image.BufferedImage;

class CustomPanel extends JPanel {
    private BufferedImage backgroundImage = null;
    // private BufferedImage copyImage = null;
    
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

    public BufferedImage cropImage(int left, int right, int top, int bottom) {
        int width = this.imageWidth - right - left;
        int height = this.imageHeight - bottom - top;
        BufferedImage croppedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int y = top; y <this.imageHeight - bottom; y++) {
            for (int x = left; x < this.imageWidth - right; x++) {
                int rgb = backgroundImage.getRGB(x, y);
                croppedImage.setRGB(x - left, y - top, rgb);
            }
        }
        return croppedImage;
    }

    public BufferedImage flipImage(boolean horizontal, boolean vertical) {
        BufferedImage flippedImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
        if(horizontal) {
            for (int y = 0; y < imageHeight; y++) {
                for (int x = 0; x < imageWidth; x++) {
                    int rgb = backgroundImage.getRGB(x, y);
                    flippedImage.setRGB(imageWidth - x - 1, y, rgb);
                }
            }
            return flippedImage;
        } else {
            for (int y = 0; y < imageHeight; y++) {
                for (int x = 0; x < imageWidth; x++) {
                    int rgb = backgroundImage.getRGB(x, y);
                    flippedImage.setRGB(x, imageHeight - y - 1, rgb);
                }   
            }
        }
        return flippedImage;
    }

    public BufferedImage rotateImage(int angle) {
        BufferedImage rotatedImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = rotatedImage.createGraphics();
        g2d.rotate(Math.toRadians(angle), imageWidth / 2.0, imageHeight / 2.0);
        g2d.drawImage(backgroundImage, 0, 0, null);
        g2d.dispose();
        return rotatedImage;
    }

    
public BufferedImage adjustBrightness(int brightness) {
    int width = backgroundImage.getWidth();
    int height = backgroundImage.getHeight();
    
    // Create an output image that supports transparency.
    BufferedImage adjustedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    
    // Get the Graphics2D context.
    Graphics2D g2d = adjustedImage.createGraphics();
    
    // Draw the original image.
    g2d.drawImage(backgroundImage, 0, 0, null);
    
    // Only apply an overlay if brightness is non-zero.
    if (brightness != 0) {
        // Calculate the overlay opacity.
        // brightness is expected to be in the range -100 to 100.
        // For darkening, brightness < 0, we overlay black.
        // For brightening, brightness > 0, we overlay white.
        float alpha = Math.min(Math.abs(brightness) / 100.0f, 1.0f);
        Color overlayColor = (brightness > 0) ? Color.WHITE : Color.BLACK;
        
        // Set the composite with the calculated alpha.
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        g2d.setColor(overlayColor);
        g2d.fillRect(0, 0, width, height);
    }
    
    // Dispose the graphics context.
    g2d.dispose();
    
    return adjustedImage;
}
    // private BufferedImage deepCopy(BufferedImage image) {
    //     BufferedImage copy = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
    //     Graphics2D g2d = copy.createGraphics();
    //     g2d.drawImage(image, 0, 0, null);
    //     g2d.dispose();
    //     return copy;
    // }

    public void setBackgroundImage(BufferedImage image) {
        this.backgroundImage = image;
        // this.copyImage = deepCopy(image);
        repaint(); 
    }

    public void reset(boolean reset) {
        if (reset) {
            System.out.println("reaching here");
            // this.backgroundImage = deepCopy(copyImage);
            // repaint();
        }
    }

    public void setCropMode(boolean cropMode) {
        if (cropMode) {
            setBackground(Color.RED);
        } else {
            setBackground(Color.WHITE);
        }
    }

    public void setFlipMode(boolean flipMode) {
        if (flipMode) {
            setBackground(Color.BLUE);
        } else {
            setBackground(Color.WHITE);
        }
    }

    public void setRotateMode(boolean rotateMode) {
        if (rotateMode) {
            setBackground(Color.GREEN);
        } else {
            setBackground(Color.WHITE);
        }
    }

    public void setBrightnessMode(boolean brightnessMode) {
        if (brightnessMode) {
            setBackground(Color.YELLOW);
        } else {
            setBackground(Color.WHITE);
        }
    }

    public void setContrastMode(boolean contrastMode) {
        if (contrastMode) {
            setBackground(Color.PINK);
        } else {
            setBackground(Color.WHITE);
        }
    }

    public void setHueMode(boolean hueMode) {
        if (hueMode) {
            setBackground(Color.CYAN);
        } else {
            setBackground(Color.WHITE);
        }
    }

    public void setSaturationMode(boolean saturationMode) {
        if (saturationMode) {
            setBackground(Color.MAGENTA);
        } else {
            setBackground(Color.WHITE);
        }
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
        JMenuItem fileItem2 = new JMenuItem("SAVE (ctrl+s)");
        JMenuItem fileItem3 = new JMenuItem("SAVE AS (ctrl+shift+s)");
        JMenuItem fileItem4 = new JMenuItem("EXIT (alt+f4)");

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

        JButton[] listOfButtons = new JButton[8];

        listOfButtons[0] = new JButton("CROP");
        listOfButtons[1] = new JButton("ROTATE");
        listOfButtons[2] = new JButton("FLIP");
        listOfButtons[3] = new JButton("BRIGHTNESS");
        listOfButtons[4] = new JButton("CONTRAST");
        listOfButtons[5] = new JButton("HUE");
        listOfButtons[6] = new JButton("SATURATION");
        listOfButtons[7] = new JButton("RESET");

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
                        canvas.setCropMode(true);
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
                            BufferedImage croppedImage = canvas.cropImage(left, right, top, bottom);
                            canvas.setBackgroundImage(croppedImage);

                            adjustments.removeAll();
                            adjustments.add(adjustmentsLabel);
                            adjustments.revalidate();
                            adjustments.repaint();
                        });
                        break;
                        case "ROTATE":

                        canvas.setRotateMode(true);
                        adjustments.add(new JLabel("Angle: "));
                        JTextField angleField = new JTextField("");
                        angleField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
                        adjustments.add(angleField);


                        JButton rotateSubmitButton = new JButton("Submit");
                        adjustments.add(rotateSubmitButton);



                        rotateSubmitButton.addActionListener(e1 -> {
                            int angle = Integer.parseInt(((JTextField) adjustments.getComponent(2)).getText());
                            BufferedImage rotatedImage = canvas.rotateImage(angle);
                            canvas.setBackgroundImage(rotatedImage);
                            adjustments.removeAll();
                            adjustments.add(adjustmentsLabel);
                            adjustments.revalidate();
                            adjustments.repaint();
                        });


                        break;
                    case "FLIP":
                        canvas.setFlipMode(true);
                        JButton horizontalFlipButton = new JButton("HORIZONTAL");
                        adjustments.add(horizontalFlipButton);
                        
                        JButton verticalFlipButton = new JButton("VERTICAL");
                        adjustments.add(verticalFlipButton);

                        horizontalFlipButton.addActionListener(e1 -> {
                            BufferedImage flippedImage = canvas.flipImage(true, false);
                            canvas.setBackgroundImage(flippedImage);
                            adjustments.removeAll();
                            adjustments.add(adjustmentsLabel);
                            adjustments.revalidate();
                            adjustments.repaint();
                        });

                        verticalFlipButton.addActionListener(e1 -> {
                            BufferedImage flippedImage = canvas.flipImage(false, true);
                            canvas.setBackgroundImage(flippedImage);
                            adjustments.removeAll();
                            adjustments.add(adjustmentsLabel);
                            adjustments.revalidate();
                            adjustments.repaint();
                        });



                        break;
                    case "BRIGHTNESS":
                        canvas.setBrightnessMode(true);
                        adjustments.add(new JLabel("Brightness: "));
                        JTextField brightnessInputField = new JTextField("");
                        JButton brightnessSubmitButton = new JButton("submit");


                        brightnessSubmitButton.addActionListener(a -> {
                            int value = Integer.parseInt(brightnessInputField.getText());

                            BufferedImage brightenedImage = canvas.adjustBrightness(value);
                            canvas.setBackgroundImage(brightenedImage);

                        });
                        adjustments.add(brightnessInputField);
                        adjustments.add(brightnessSubmitButton );
                        break;
                    case "CONTRAST":
                        canvas.setContrastMode(true);
                        break;
                    case "HUE":
                        canvas.setHueMode(true);
                        break;
                    case "SATURATION":
                        canvas.setSaturationMode(true);
                        break;
                    case "RESET":
                        canvas.reset(true);
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
        fileMenu.add(fileItem4);

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

                        canvas.setBackgroundImage(image);
                        System.out.println("Image successfully loaded.");
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

        fileItem4.addActionListener(new ActionListener() {
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