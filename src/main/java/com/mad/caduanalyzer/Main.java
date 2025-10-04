package com.mad.caduanalyzer;

import com.mad.caduanalyzer.controller.MainController;
import com.mad.caduanalyzer.view.MainFrame;

import javax.swing.SwingUtilities;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            MainFrame view = new MainFrame();
            new MainController(view, new ArrayList<>());
            view.setVisible(true);
        });
    }
}
