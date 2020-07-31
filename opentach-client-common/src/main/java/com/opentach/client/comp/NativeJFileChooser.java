package com.opentach.client.comp;

/*
 * Copyright (c) 2018, Steffen Flor All rights reserved. Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following
 * conditions are met: * Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer. * Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import java.awt.Component;
import java.awt.HeadlessException;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

/**
 * This is a drop-in replacement for Swing's file chooser. Instead of displaying Swing's file chooser, it makes use of JavaFX's file chooser. JavaFX uses the OS's native file
 * chooser. Technically, this class is a memory hog, but its use is convenient. Furthermore, if JavaFX is not available, the default file chooser will be displayed instead. Of
 * course, this class will not compile if you don't have an JDK 8 or higher that has JavaFX support. Since this class will have to call the constructor of JFileChooser, it won't
 * increase the performance of the file chooser; if anything, it might further decrease it. Please note that some methods have not been overwritten and may not have any impact on
 * the file chooser. Sometimes, the new JavaFX file chooser does not provide certain functionality. One feature that is not supported is the selection of files AND directories. If
 * trying to set this using setFileSelectionMode(), still only files will be selectable.
 *
 * @author Steffen Flor
 * @version 1.6.3
 */
public class NativeJFileChooser extends JFileChooser {

	private static final Logger	logger	= LoggerFactory.getLogger(NativeJFileChooser.class);

	public static final boolean	FX_AVAILABLE;
	private List<File>			currentFiles;
	private FileChooser			fileChooser;
	private File				currentFile;
	private DirectoryChooser	directoryChooser;

	static {
		boolean isFx;
		try {
			Class.forName("javafx.stage.FileChooser");
			isFx = true;
			// Initializes JavaFX environment
			JFXPanel jfxPanel = new JFXPanel();
			NativeJFileChooser.logger.trace("JFX initialized {}", jfxPanel);
		} catch (ClassNotFoundException exception) {
			NativeJFileChooser.logger.debug(null, exception);
			isFx = false;
		}

		FX_AVAILABLE = isFx;
	}

	public NativeJFileChooser() {
		this.initFxFileChooser(null);
	}

	public NativeJFileChooser(String currentDirectoryPath) {
		super(currentDirectoryPath);
		this.initFxFileChooser(new File(currentDirectoryPath));
	}

	public NativeJFileChooser(File currentDirectory) {
		super(currentDirectory);
		this.initFxFileChooser(currentDirectory);
	}

	public NativeJFileChooser(FileSystemView fsv) {
		super(fsv);
		this.initFxFileChooser(fsv.getDefaultDirectory());
	}

	public NativeJFileChooser(File currentDirectory, FileSystemView fsv) {
		super(currentDirectory, fsv);
		this.initFxFileChooser(currentDirectory);
	}

	public NativeJFileChooser(String currentDirectoryPath, FileSystemView fsv) {
		super(currentDirectoryPath, fsv);
		this.initFxFileChooser(new File(currentDirectoryPath));
	}

	@Override
	public int showOpenDialog(final Component parent) throws HeadlessException {
		if (!NativeJFileChooser.FX_AVAILABLE) {
			return super.showOpenDialog(parent);
		}

		final CountDownLatch latch = new CountDownLatch(1);
		Platform.runLater(() -> {

			if (parent != null) {
				parent.setEnabled(false);
			}

			if (NativeJFileChooser.this.isDirectorySelectionEnabled()) {
				NativeJFileChooser.this.currentFile = NativeJFileChooser.this.directoryChooser.showDialog(null);
			} else {
				if (NativeJFileChooser.this.isMultiSelectionEnabled()) {
					NativeJFileChooser.this.currentFiles = NativeJFileChooser.this.fileChooser.showOpenMultipleDialog(null);
				} else {
					NativeJFileChooser.this.currentFile = NativeJFileChooser.this.fileChooser.showOpenDialog(null);
				}
			}
			latch.countDown();
		});
		try {
			latch.await();
		} catch (InterruptedException ex) {
			throw new RuntimeException(ex);
		} finally {
			if (parent != null) {
				parent.setEnabled(true);
			}
		}

		if (this.isMultiSelectionEnabled()) {
			if (this.currentFiles != null) {
				return JFileChooser.APPROVE_OPTION;
			}
			return JFileChooser.CANCEL_OPTION;
		}
		if (this.currentFile != null) {
			return JFileChooser.APPROVE_OPTION;
		}
		return JFileChooser.CANCEL_OPTION;

	}

	@Override
	public int showSaveDialog(final Component parent) throws HeadlessException {
		if (!NativeJFileChooser.FX_AVAILABLE) {
			return super.showSaveDialog(parent);
		}

		final CountDownLatch latch = new CountDownLatch(1);

		Platform.runLater(() -> {

			if (parent != null) {
				parent.setEnabled(false);
			}

			if (NativeJFileChooser.this.isDirectorySelectionEnabled()) {
				NativeJFileChooser.this.currentFile = NativeJFileChooser.this.directoryChooser.showDialog(null);
			} else {
				NativeJFileChooser.this.currentFile = NativeJFileChooser.this.fileChooser.showSaveDialog(null);
			}
			latch.countDown();
		});
		try {
			latch.await();
		} catch (InterruptedException ex) {
			throw new RuntimeException(ex);
		} finally {
			if (parent != null) {
				parent.setEnabled(true);
			}
		}

		if (this.currentFile != null) {
			return JFileChooser.APPROVE_OPTION;
		}
		return JFileChooser.CANCEL_OPTION;
	}

	@Override
	public int showDialog(Component parent, String approveButtonText) {
		if (!NativeJFileChooser.FX_AVAILABLE) {
			return super.showDialog(parent, approveButtonText);
		}
		return this.showOpenDialog(parent);
	}

	@Override
	public File[] getSelectedFiles() {
		if (!NativeJFileChooser.FX_AVAILABLE) {
			return super.getSelectedFiles();
		}
		if (this.currentFiles == null) {
			return null;
		}
		return this.currentFiles.toArray(new File[this.currentFiles.size()]);
	}

	@Override
	public File getSelectedFile() {
		if (!NativeJFileChooser.FX_AVAILABLE) {
			return super.getSelectedFile();
		}
		return this.currentFile;
	}

	@Override
	public void setSelectedFiles(File[] selectedFiles) {
		if (!NativeJFileChooser.FX_AVAILABLE) {
			super.setSelectedFiles(selectedFiles);
			return;
		}
		if ((selectedFiles == null) || (selectedFiles.length == 0)) {
			this.currentFiles = null;
		} else {
			this.setSelectedFile(selectedFiles[0]);
			this.currentFiles = new ArrayList<>(Arrays.asList(selectedFiles));
		}
	}

	@Override
	public void setSelectedFile(File file) {
		if (!NativeJFileChooser.FX_AVAILABLE) {
			super.setSelectedFile(file);
			return;
		}
		this.currentFile = file;
		if (file != null) {
			if (file.isDirectory()) {
				this.fileChooser.setInitialDirectory(file.getAbsoluteFile());

				if (this.directoryChooser != null) {
					this.directoryChooser.setInitialDirectory(file.getAbsoluteFile());
				}
			} else if (file.isFile() || !file.exists()) {
				this.fileChooser.setInitialDirectory(file.getParentFile());
				this.fileChooser.setInitialFileName(file.getName());

				if (this.directoryChooser != null) {
					this.directoryChooser.setInitialDirectory(file.getParentFile());
				}
			}

		}
	}

	@Override
	public void setFileSelectionMode(int mode) {
		super.setFileSelectionMode(mode);
		if (!NativeJFileChooser.FX_AVAILABLE) {
			return;
		}
		if (mode == JFileChooser.DIRECTORIES_ONLY) {
			if (this.directoryChooser == null) {
				this.directoryChooser = new DirectoryChooser();
			}
			// Set file again, so directory chooser will be affected by it
			this.setSelectedFile(this.currentFile);
			this.setDialogTitle(this.getDialogTitle());
		}
	}

	@Override
	public void setDialogTitle(String dialogTitle) {
		if (!NativeJFileChooser.FX_AVAILABLE) {
			super.setDialogTitle(dialogTitle);
			return;
		}
		this.fileChooser.setTitle(dialogTitle);
		if (this.directoryChooser != null) {
			this.directoryChooser.setTitle(dialogTitle);
		}
	}

	@Override
	public String getDialogTitle() {
		if (!NativeJFileChooser.FX_AVAILABLE) {
			return super.getDialogTitle();
		}
		return this.fileChooser.getTitle();
	}

	@Override
	public void changeToParentDirectory() {
		if (!NativeJFileChooser.FX_AVAILABLE) {
			super.changeToParentDirectory();
			return;
		}
		File parentDir = this.fileChooser.getInitialDirectory().getParentFile();
		if (parentDir.isDirectory()) {
			this.fileChooser.setInitialDirectory(parentDir);
			if (this.directoryChooser != null) {
				this.directoryChooser.setInitialDirectory(parentDir);
			}
		}
	}

	@Override
	public void addChoosableFileFilter(FileFilter filter) {
		super.addChoosableFileFilter(filter);
		if (!NativeJFileChooser.FX_AVAILABLE || (filter == null)) {
			return;
		}
		if (filter.getClass().equals(FileNameExtensionFilter.class)) {
			FileNameExtensionFilter f = (FileNameExtensionFilter) filter;

			List<String> ext = new ArrayList<>();
			for (String extension : f.getExtensions()) {
				ext.add(extension.replaceAll("^\\*?\\.?(.*)$", "*.$1"));
			}
			this.fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(f.getDescription(), ext));
		}
	}

	@Override
	public void setAcceptAllFileFilterUsed(boolean bool) {
		boolean differs = this.isAcceptAllFileFilterUsed() ^ bool;
		super.setAcceptAllFileFilterUsed(bool);
		if (!NativeJFileChooser.FX_AVAILABLE) {
			return;
		}
		if (!differs) {
			return;
		}
		if (bool) {
			this.fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("All files", "*.*"));
		} else {
			for (Iterator<FileChooser.ExtensionFilter> it = this.fileChooser.getExtensionFilters().iterator(); it.hasNext();) {
				FileChooser.ExtensionFilter filter = it.next();
				if ((filter.getExtensions().size() == 1) && filter.getExtensions().contains("*.*")) {
					it.remove();
				}
			}
		}

	}

	private void initFxFileChooser(File currentFile) {
		if (NativeJFileChooser.FX_AVAILABLE) {
			this.fileChooser = new FileChooser();
			this.currentFile = currentFile;
			this.setSelectedFile(currentFile);
		}
	}

}