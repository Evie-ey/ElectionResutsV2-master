package edu.cct.ca.election;
import edu.cct.ca.election.ui.ElectionResultUI;

import javax.swing.*;

public class ElectionResult {
    public static void main(String [] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                createElectionGui();
            }
        });
    }

    private static void createElectionGui() {
        ElectionResultUI ui = new ElectionResultUI();
        JPanel root = ui.getRootPanel();
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(root);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
