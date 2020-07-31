package com.opentach.common.remotevehicle.provider.opentach;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;

import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

/**
 * The Class WebsocketSerializableCoder.
 */
public class WebsocketSerializableCoder implements Decoder.Binary<Object>, Encoder.Binary<Object> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.websocket.Encoder.Binary#encode(java.lang.Object)
	 */
	@Override
	public ByteBuffer encode(Object object) throws EncodeException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(object);
			return ByteBuffer.wrap(baos.toByteArray());
		} catch (Exception e) {
			throw new EncodeException(object, null, e);
		} finally {
			if (baos != null) {
				try {
					baos.close();
				} catch (IOException e) {
					// do nothing
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.websocket.Decoder.Binary#decode(java.nio.ByteBuffer)
	 */
	@Override
	public Object decode(ByteBuffer bytes) throws DecodeException {
		ByteArrayInputStream in = new ByteArrayInputStream(bytes.array());
		try {
			ObjectInputStream ois = new ObjectInputStream(in);
			return ois.readObject();
		} catch (Exception e) {
			throw new DecodeException(bytes, null, e);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					// do nothing
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.websocket.Decoder.Binary#willDecode(java.nio.ByteBuffer)
	 */
	@Override
	public boolean willDecode(ByteBuffer bytes) {
		return true;
	}

	@Override
	public void init(EndpointConfig config) {
		// not neccesary
	}

	@Override
	public void destroy() {
		// not neccesary
	}

}
