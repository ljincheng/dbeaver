package org.jkiss.dbeaver.model.sourcecode.template;
import java.io.IOException;
import java.io.Writer;


public final class FastStringWriter extends Writer {

    private final StringBuilder builder;



    public FastStringWriter() {
        super();
        this.builder = new StringBuilder();
    }


    public FastStringWriter(final int initialSize) {
        super();
        if (initialSize < 0) {
            throw new IllegalArgumentException("Negative buffer size");
        }
        this.builder = new StringBuilder(initialSize);
    }




    @Override
    public void write(final int c) {
        this.builder.append((char) c);
    }


    @Override
    public void write(final String str) {
        this.builder.append(str);
    }


    @Override
    public void write(final String str, final int off, final int len)  {
        this.builder.append(str, off, off + len);
    }


    @Override
    public void write(final char[] cbuf) {
        this.builder.append(cbuf, 0, cbuf.length);
    }


    @Override
    public void write(final char[] cbuf, final int off, final int len) {
        if ((off < 0) || (off > cbuf.length) || (len < 0) ||
                ((off + len) > cbuf.length) || ((off + len) < 0)) {
            throw new IndexOutOfBoundsException();
        } else if (len == 0) {
            return;
        }
        this.builder.append(cbuf, off, len);
    }



    @Override
    public void flush() throws IOException {
        // Nothing to be flushed
    }


    @Override
    public void close() throws IOException {
        // Nothing to be closed
    }


    @Override
    public String toString() {
        return this.builder.toString();
    }

}
