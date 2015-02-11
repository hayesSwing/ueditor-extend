package net.viservice.editor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class StandardMultipartFile implements MultipartFile {

	public static final int BUFFER_SIZE = 4096;

	private final String fieldName;// 参数名
	private final InputStream inputStream;
	private final String filename;
	private final long size;

	public StandardMultipartFile(String fieldName, InputStream inputStream, String filename, long size) {
		this.fieldName = fieldName;
		this.inputStream = inputStream;
		this.filename = filename;
		this.size = size;
	}

	@Override
	public String getName() {
		return this.fieldName;
	}

	@Override
	public String getOriginalFilename() {
		if (filename == null) {
			// Should never happen.
			return "";
		}
		// check for Unix-style path
		int pos = filename.lastIndexOf("/");
		if (pos == -1) {
			// check for Windows-style path
			pos = filename.lastIndexOf("\\");
		}
		if (pos != -1) {
			// any sort of path separator found
			return filename.substring(pos + 1);
		} else {
			// plain name
			return filename;
		}
	}

	@Override
	public boolean isEmpty() {
		return (this.size == 0);
	}

	@Override
	public long getSize() {
		return this.size;
	}

	@Override
	public byte[] getBytes() throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream(BUFFER_SIZE);
		InputStream in = this.inputStream;
		try {
			// int byteCount = 0;
			byte[] buffer = new byte[BUFFER_SIZE];
			int bytesRead = -1;
			while ((bytesRead = in.read(buffer)) != -1) {
				out.write(buffer, 0, bytesRead);
				// byteCount += bytesRead;
			}
			out.flush();
		} finally {
			try {
				in.close();
			} catch (IOException ex) {
			}
			try {
				out.close();
			} catch (IOException ex) {
			}
		}
		return out.toByteArray();
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return (this.inputStream != null ? this.inputStream : new ByteArrayInputStream(new byte[0]));
	}

}
