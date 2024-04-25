package com.khonsong.apis.khonsongapis.util;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;

public class VirtualFileInputStream extends FileInputStream {

    private InputStream stream;

    public VirtualFileInputStream(InputStream stream) {
        super(FileDescriptor.in); // This will never be used
        this.stream = stream;
    }

    public int available() throws IOException {
        throw new IllegalStateException("Unimplemented method called");
    }

    public void close() throws IOException {
        stream.close();
    }

    public boolean equals(Object obj) {
        return stream.equals(obj);
    }

    public FileChannel getChannel() {
        throw new IllegalStateException("Unimplemented method called");
    }

    public int hashCode() {
        return stream.hashCode();
    }

    public void mark(int readlimit) {
        stream.mark(readlimit);
    }

    public boolean markSupported() {
        return stream.markSupported();
    }

    public int read() throws IOException {
        return stream.read();
    }

    public int read(byte[] b, int off, int len) throws IOException {
        return stream.read(b, off, len);
    }

    public int read(byte[] b) throws IOException {
        return stream.read(b);
    }

    public void reset() throws IOException {
        stream.reset();
    }

    public long skip(long n) throws IOException {
        return stream.skip(n);
    }

    public String toString() {
        return stream.toString();
    }

}
