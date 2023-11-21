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
}
