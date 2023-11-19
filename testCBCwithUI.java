import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

public class testCBCwithUI extends JFrame {

    private JButton encryptButton;
    private JButton decryptButton;

    private CBCmode cbcEncryptor = new CBCmode();
    private CBCmode cbcDecryptor = new CBCmode();

    public testCBCwithUI() {
        initUI();
    }

    private void initUI() {
        setTitle("Image Encryption and Decryption");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        getContentPane().add(panel);

        encryptButton = new JButton("Encrypt Image");
        decryptButton = new JButton("Decrypt Image");

        panel.add(encryptButton);
        panel.add(decryptButton);

        encryptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performEncryption();
            }
        });

        decryptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performDecryption();
            }
        });
    }

    private void performEncryption() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try {
                cbcEncryptor.addKey(new int[]{10, 12, 13, 14});

                // Read the image file and perform encryption
                performFileOperation(selectedFile, cbcEncryptor, "_encrypted");

                JOptionPane.showMessageDialog(this, "Encryption successful!");
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error during encryption: " + ex.getMessage());
            }
        }
    }

    private void performDecryption() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try {
                cbcDecryptor.addKey(new int[]{10, 12, 13, 14});

                // Read the encrypted image file and perform decryption
                performFileOperation(selectedFile, cbcDecryptor, "_decrypted");

                JOptionPane.showMessageDialog(this, "Decryption successful!");
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error during decryption: " + ex.getMessage());
            }
        }
    }

    private void performFileOperation(File inputFile, CBCmode encryptor, String suffix) throws IOException {
        int[] img = new int[2];
        boolean check = true;

        try (FileInputStream imgIn = new FileInputStream(inputFile);
             FileOutputStream imgOut = new FileOutputStream(getOutputFileName(inputFile, suffix));
             DataInputStream dataIn = new DataInputStream(imgIn);
             DataOutputStream dataOut = new DataOutputStream(imgOut)) {

            for (int i = 0; i < 10; i++) {
                if (dataIn.available() > 0) {
                    img[0] = dataIn.readInt();
                    img[1] = dataIn.readInt();
                    dataOut.writeInt(img[0]);
                    dataOut.writeInt(img[1]);
                }
            }

            while (dataIn.available() > 0) {
                try {
                    img[0] = dataIn.readInt();
                    check = true;
                    img[1] = dataIn.readInt();
                    int[] result = encryptor.encrypt(img, img); 
                    dataOut.writeInt(result[0]);
                    dataOut.writeInt(result[1]);
                    check = false;
                } catch (EOFException e) {
                    if (!check) {
                        dataOut.writeInt(img[0]);
                        dataOut.writeInt(img[1]);
                    } else {
                        dataOut.writeInt(img[0]);
                    }
                }
            }
        }
    }

    private String getOutputFileName(File inputFile, String suffix) {
        String inputFileName = inputFile.getName();
        String outputFileName = inputFileName.replaceFirst("[.][^.]+$", suffix + ".bmp");
        return inputFile.getParent() + File.separator + outputFileName;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                testCBCwithUI ex = new testCBCwithUI();
                ex.setVisible(true);
            }
        });
    }
}
