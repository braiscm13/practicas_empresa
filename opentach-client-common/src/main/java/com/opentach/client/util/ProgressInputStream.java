package com.opentach.client.util;

import java.io.IOException;
import java.io.InputStream;

/**
 * The Class PercentageInputStream.
 */
public class ProgressInputStream extends InputStream {

	/** The current length. */
	protected long						currentLength	= 0;

	/** The total length. */
	protected long						totalLength;

	/** The wrap. */
	protected InputStream				wrap;

	protected IProgressListener	progressListener;

	/**
	 * Instantiates a new percentage input stream.
	 *
	 * @param is
	 *            the is
	 * @param transferable
	 *            the transferable
	 * @param totalLength
	 *            the total length
	 */
	public ProgressInputStream(InputStream is, long totalLength, IProgressListener progressListener) {
		super();
		this.currentLength = 0;
		this.wrap = is;
		this.totalLength = totalLength;
		this.progressListener = progressListener;
	}

	/*
	 * (non-Javadoc)
	 * @see java.io.InputStream#read()
	 */
	@Override
	public int read() throws IOException {
		int read = this.wrap.read();
		if ((read >= 0) && (this.totalLength > 0)) {
			this.currentLength++;
			this.progressListener.setProgress(this.currentLength / this.totalLength);
		}
		return read;
	}

	/*
	 * (non-Javadoc)
	 * @see java.io.InputStream#read(byte[], int, int)
	 */
	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		int read = this.wrap.read(b, off, len);
		if (read > 0) {
			this.currentLength += read;
			this.progressListener.setProgress((double) this.currentLength / this.totalLength);
		}
		return read;
	}

	/*
	 * (non-Javadoc)
	 * @see java.io.InputStream#read(byte[], int, int)
	 */
	@Override
	public int read(byte[] b) throws IOException {
		int read = this.wrap.read(b);
		if (read > 0) {
			this.currentLength += read;
			this.progressListener.setProgress((double) this.currentLength / this.totalLength);
		}
		return read;
	}

	@Override
	public long skip(long n) throws IOException {
		long skip = super.skip(n);
		if (skip > 0) {
			this.currentLength += skip;
			this.progressListener.setProgress((double) this.currentLength / this.totalLength);
		}
		return skip;
	}

	public static interface IProgressListener {
		void setProgress(double perone);
	}
}