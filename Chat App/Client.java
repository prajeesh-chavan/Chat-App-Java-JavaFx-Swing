import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;


public class Client extends JFrame {

    private Socket socket;
    private BufferedReader br; // for reading
    private PrintWriter out; // for writing

    // Components Swing

    private JLabel heading = new JLabel("Client Area");
    private JTextArea messageAreaS = new JTextArea();
    private JTextArea messageAreaC = new JTextArea();
    private JTextField messageInput = new JTextField();
    private Font font = new Font("Roboto", Font.PLAIN, 20);

    public Client() {
        createGUI();
        System.out.println("Sending request to server");
        try {
            while (true) {
                try {
                    socket = new Socket("127.0.0.1", 7777); // ip adress of server and port
                    System.out.println("connection done.");
                    break; // Break the loop if connection is successful
                } catch (ConnectException e) {
                    
                }
            }

            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream());

            handleEvents();
            startReading();

        } catch (Exception e) {
            
        }
    }

    private void createGUI() {
        // GUI Code
        this.setTitle("Client Messager");
        this.setSize(700, 500);
        this.setLocationRelativeTo(null); // center
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        heading.setFont(font);
        heading.setIcon(new ImageIcon("pc.png")); // logo

        Color trans = new Color(0, 0, 0, 0);

        messageAreaS.setBackground(trans);
        messageAreaC.setBackground(trans);

        messageAreaS.setFont(font);
        messageAreaC.setFont(font);
        messageInput.setFont(font);
        heading.setHorizontalTextPosition(SwingConstants.CENTER);
        heading.setVerticalTextPosition(SwingConstants.BOTTOM);
        heading.setHorizontalAlignment(SwingConstants.CENTER);
        heading.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        messageInput.setHorizontalAlignment(SwingConstants.CENTER);

        messageAreaS.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 0));
        messageAreaC.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 15));

        messageAreaS.setEditable(false);
        messageAreaC.setEditable(false);

        this.setLayout(new BorderLayout());

        this.add(heading, BorderLayout.NORTH);
        this.add(messageAreaS, BorderLayout.WEST);
        this.add(messageAreaC, BorderLayout.EAST);
        this.add(messageInput, BorderLayout.SOUTH);

        this.setVisible(true);
    }

    private void handleEvents() {
        messageInput.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String contentToSend = messageInput.getText();
                messageAreaC.append("Client : " + contentToSend + "\n");
                messageAreaS.append("\n");
                out.println(contentToSend);
                out.flush();
                messageInput.setText("");
                messageInput.requestFocus();
            }
        });
    }

    public void startReading() {
        Runnable r1 = () -> {
            System.out.println("Reader started...");

            try {
                String msg;
                while ((msg = br.readLine()) != null) {
                    messageAreaS.append("Server : " + msg + "\n");
                    messageAreaC.append("\n");
                }
            } catch (IOException e) {
                
                System.out.println("Connection Closed");
            } finally {
                try {
                    if (socket != null) {
                        socket.close();
                    }
                    if (br != null) {
                        br.close();
                    }
                    if (out != null) {
                        out.close();
                    }
                } catch (IOException ex) {
                    
                }
            }
        };

        new Thread(r1).start();
    }

    public static void main(String[] args) {
        new Client();
        
    }
}
