package ui;

import ipc.*;
import utils.Logger;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;

public class SimulatorUI extends JFrame {

    private JTextPane logPane;
    private StyledDocument doc;

    public SimulatorUI() {
        setTitle("IPC Simulator — Inter Process Communication");
        setSize(900, 620);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(0, 0));

        // Dark background
        Color bg        = new Color(18, 18, 28);
        Color panelBg   = new Color(26, 26, 40);
        Color accent    = new Color(80, 120, 255);
        Color btnBg     = new Color(36, 36, 54);
        Color btnHover  = new Color(55, 55, 80);
        Color textColor = new Color(200, 200, 220);

        getContentPane().setBackground(bg);

        // ── Title bar ──
        JPanel titleBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 12));
        titleBar.setBackground(new Color(22, 22, 35));
        titleBar.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, accent));
        JLabel title = new JLabel("⚙  IPC Simulator");
        title.setFont(new Font("Monospaced", Font.BOLD, 18));
        title.setForeground(new Color(140, 170, 255));
        JLabel sub = new JLabel("Operating Systems — Inter Process Communication");
        sub.setFont(new Font("SansSerif", Font.PLAIN, 12));
        sub.setForeground(new Color(120, 120, 150));
        titleBar.add(title);
        titleBar.add(sub);
        add(titleBar, BorderLayout.NORTH);

        // ── Left button panel ──
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBackground(panelBg);
        leftPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 2, new Color(40, 40, 60)));
        leftPanel.setPreferredSize(new Dimension(190, 0));

        JLabel menuLabel = new JLabel("  IPC MECHANISMS");
        menuLabel.setFont(new Font("Monospaced", Font.BOLD, 11));
        menuLabel.setForeground(new Color(90, 100, 140));
        menuLabel.setBorder(BorderFactory.createEmptyBorder(16, 12, 10, 0));
        leftPanel.add(menuLabel);

        String[][] buttons = {
            {"🔵  Pipes",          "PIPE"},
            {"🟢  Message Queue",  "MQ"},
            {"🟡  Shared Memory",  "SHM"},
            {"🔴  Semaphores",     "SEM"},
            {"🟠  Signals",        "SIG"},
            {"🟣  Deadlock",       "DL"},
        };

        for (String[] btn : buttons) {
            JButton b = makeButton(btn[0], btn[1], btnBg, btnHover, textColor);
            leftPanel.add(b);
            leftPanel.add(Box.createVerticalStrut(4));
        }

        leftPanel.add(Box.createVerticalGlue());

        // Clear button
        JButton clearBtn = new JButton("🗑  Clear Log");
        clearBtn.setFont(new Font("SansSerif", Font.PLAIN, 12));
        clearBtn.setBackground(new Color(60, 30, 30));
        clearBtn.setForeground(new Color(220, 120, 120));
        clearBtn.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
        clearBtn.setFocusPainted(false);
        clearBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        clearBtn.setMaximumSize(new Dimension(190, 42));
        clearBtn.addActionListener(e -> {
            try { doc.remove(0, doc.getLength()); } catch (BadLocationException ex) {}
        });
        leftPanel.add(clearBtn);
        leftPanel.add(Box.createVerticalStrut(12));

        add(leftPanel, BorderLayout.WEST);

        // ── Log panel ──
        logPane = new JTextPane();
        logPane.setEditable(false);
        logPane.setBackground(new Color(14, 14, 22));
        logPane.setFont(new Font("Monospaced", Font.PLAIN, 13));
        logPane.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
        doc = logPane.getStyledDocument();

        JScrollPane scroll = new JScrollPane(logPane);
        scroll.setBackground(bg);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setBackground(new Color(30, 30, 45));
        add(scroll, BorderLayout.CENTER);

        // ── Status bar ──
        JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 6));
        statusBar.setBackground(new Color(20, 20, 32));
        statusBar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(40, 40, 60)));
        JLabel status = new JLabel("● Ready — Select an IPC mechanism to simulate");
        status.setFont(new Font("Monospaced", Font.PLAIN, 11));
        status.setForeground(new Color(80, 200, 120));
        statusBar.add(status);
        add(statusBar, BorderLayout.SOUTH);

        // Logger callback
        Logger.setCallback((msg, color) -> SwingUtilities.invokeLater(() -> {
            try {
                SimpleAttributeSet attr = new SimpleAttributeSet();
                StyleConstants.setForeground(attr, color);
                StyleConstants.setFontFamily(attr, "Monospaced");
                StyleConstants.setFontSize(attr, 13);
                doc.insertString(doc.getLength(), msg + "\n", attr);
                logPane.setCaretPosition(doc.getLength());
                status.setText("● Running simulation...");
                status.setForeground(new Color(255, 200, 60));
            } catch (BadLocationException e) {}
        }));

        setVisible(true);

        // Welcome message
        Logger.logDivider("IPC SIMULATOR STARTED");
        Logger.log("System", "Welcome! Select any IPC mechanism from the left panel.");
        Logger.log("System", "Each simulation runs in real-time with color-coded output.");
        Logger.logDivider("");
    }

    private JButton makeButton(String label, String type, Color bg, Color hover, Color fg) {
        JButton b = new JButton(label);
        b.setFont(new Font("SansSerif", Font.PLAIN, 13));
        b.setBackground(bg);
        b.setForeground(fg);
        b.setBorder(BorderFactory.createEmptyBorder(11, 16, 11, 16));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setMaximumSize(new Dimension(190, 46));
        b.setHorizontalAlignment(SwingConstants.LEFT);
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { b.setBackground(hover); }
            public void mouseExited(MouseEvent e)  { b.setBackground(bg); }
        });
        b.addActionListener(e -> {
            new Thread(() -> {
                switch (type) {
                    case "PIPE" -> PipeSimulation.runSimulation();
                    case "MQ"   -> MessageQueue.runSimulation();
                    case "SHM"  -> SharedMemory.runSimulation();
                    case "SEM"  -> SemaphoreSimulation.runSimulation();
                    case "SIG"  -> Signals.runSimulation();
                    case "DL"   -> Deadlock.runSimulation();
                }
            }).start();
        });
        return b;
    }
}