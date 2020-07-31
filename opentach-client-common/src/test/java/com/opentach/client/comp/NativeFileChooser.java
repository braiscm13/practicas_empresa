package com.opentach.client.comp;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.filechooser.FileNameExtensionFilter;

public class NativeFileChooser {

	public static void main(String[] args) {
		EventQueue.invokeLater(() -> {
			JFrame frame = new JFrame("Test");
			JButton button = new JButton("Click me!");
			JFileChooser fileChooser = new NativeJFileChooser();
			fileChooser.setFileFilter(new FileNameExtensionFilter("JPG", "*.jpg"));
			button.addActionListener((ActionEvent e) -> {
				fileChooser.showDialog(frame, "Open");
			});
			frame.add(button);
			frame.setLocationRelativeTo(null);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.pack();
			frame.setVisible(true);
		});
	}

}