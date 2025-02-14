import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.imageio.*;
import java.io.*;

public class PhotoEditor extends JFrame {
    private BufferedImage image;
    private JLabel imageLabel;

    public PhotoEditor() {
        setTitle("Java Swing Photo Editor");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1000, 700);
        setMinimumSize(new Dimension(400, 300));
        setupUI();
    }

    private void setupUI() {
        imageLabel = new JLabel();
        add(new JScrollPane(imageLabel), BorderLayout.CENTER);

        // Toolbar
        JToolBar toolbar = new JToolBar();
        JButton openBtn = new JButton("Open");
        JButton grayscaleBtn = new JButton("Grayscale");

        openBtn.addActionListener(e -> openImage());
        grayscaleBtn.addActionListener(e -> applyGrayscale());

        toolbar.add(openBtn);
        toolbar.add(grayscaleBtn);
        add(toolbar, BorderLayout.NORTH);
    }

    private void openImage() {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                image = ImageIO.read(fileChooser.getSelectedFile());
                imageLabel.setIcon(new ImageIcon(image));
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Failed to open image.");
            }
        }
    }

    private void applyGrayscale() {
        if (image != null) {
            for (int y = 0; y < image.getHeight(); y++) {
                for (int x = 0; x < image.getWidth(); x++) {
                    int rgb = image.getRGB(x, y);
                    int r = (rgb >> 16) & 0xFF;
                    int g = (rgb >> 8) & 0xFF;
                    int b = rgb & 0xFF;
                    int gray = (r + g + b) / 3;
                    int newRGB = (gray << 16) | (gray << 8) | gray;
                    image.setRGB(x, y, newRGB);
                }
            }
            imageLabel.setIcon(new ImageIcon(image));
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PhotoEditor().setVisible(true));
    }
}
