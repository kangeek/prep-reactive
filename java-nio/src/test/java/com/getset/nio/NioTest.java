package com.getset.nio;

import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Date;

public class NioTest {

    @Test
    public void testFileChannelRead() throws IOException {
        RandomAccessFile aFile = new RandomAccessFile("pom.xml", "rw");
        FileChannel fileChannel = aFile.getChannel();

        ByteBuffer buffer = ByteBuffer.allocate(48);

        int readNum = fileChannel.read(buffer);

        while (readNum != -1) {
            System.out.println("Read " + readNum + " bytes.");
            buffer.flip();

            while (buffer.hasRemaining()) {
                System.out.print((char)buffer.get());
            }

            System.out.println();

            buffer.clear();
            readNum = fileChannel.read(buffer);
        }

        aFile.close();
    }

    @Test
    public void testFileChannelWrite() throws IOException {
        RandomAccessFile aFile = new RandomAccessFile("test.txt", "rw");
        FileChannel fileChannel = aFile.getChannel();

        ByteBuffer buffer = ByteBuffer.allocate(128);

        buffer.put(("This is a test" + new Date()).getBytes());

        buffer.flip();

        while (buffer.hasRemaining()) {
            fileChannel.write(buffer);
        }
    }
}
