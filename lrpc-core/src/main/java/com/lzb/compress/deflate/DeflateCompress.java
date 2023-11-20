package com.lzb.compress.deflate;


import com.lzb.compress.Compress;
import com.lzb.exception.RpcException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;


public class DeflateCompress implements Compress {


    @Override
    public byte[] compress(byte[] bytes) {
        if (bytes == null) {
            throw new NullPointerException("bytes is null");
        }
        Deflater compressor = null;
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            compressor = new Deflater(1);
            compressor.setInput(bytes);
            compressor.finish();
            final byte[] buf = new byte[2048];
            while (!compressor.finished()) {
                int count = compressor.deflate(buf);
                out.write(buf, 0, count);
            }
            return out.toByteArray();
        } catch (IOException e) {
            throw new RpcException("gzip compress failed", e);
        } finally {
            if (compressor != null) {
                compressor.end();
            }
        }
    }

    @Override
    public byte[] decompress(byte[] bytes) {
        if (bytes == null) {
            throw new NullPointerException("bytes is null");
        }
        Inflater decompressor = null;
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            decompressor = new Inflater();
            decompressor.setInput(bytes);
            final byte[] buf = new byte[2048];
            while (!decompressor.finished()) {
                int count = decompressor.inflate(buf);
                out.write(buf, 0, count);
            }
            return out.toByteArray();
        } catch (IOException | DataFormatException e) {
            throw new RpcException("gzip decompress failed", e);
        } finally {
            if (decompressor != null) {
                decompressor.end();
            }
        }
    }

    public static void main(String[] args) {
        byte[] bytes = new byte[]{1, 1, 99, 111, 109, 46, 108, 122, 98, 46, 72, 101, 108, 108, 111, 83,
                101, 114, 118, 105, 99, -27, 1, 104, 101, 108, 108, -17, 1, 2, 1, 13, 0, 1, 0, 91, 76, 106,
                97, 118, 97, 46, 108, 97, 110, 103, 46, 83, 116, 114, 105, 110, 103, -69, 1, 3, 1, 49, 49,
                -79, 1, 50, 50, -78, 1, 55, 97, 97, 51, 51, 50, 99, 55, 45, 102, 48, 102, 56, 45, 52, 48,
                97, 55, 45, 98, 97, 97, 51, 45, 101, 102, 97, 53, 98, 97, 97, 56, 99, 98, 54, -31};
        System.out.println(bytes.length);
        DeflateCompress deflateCompress = new DeflateCompress();
        byte[] compress = deflateCompress.compress(bytes);
        System.out.println(compress.length);
        byte[] decompress = deflateCompress.decompress(compress);
        System.out.println(decompress.length);
    }
}
