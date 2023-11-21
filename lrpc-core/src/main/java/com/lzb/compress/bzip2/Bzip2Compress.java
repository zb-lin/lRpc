package com.lzb.compress.bzip2;


import com.lzb.compress.Compress;
import com.lzb.exception.RpcException;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.DataFormatException;


public class Bzip2Compress implements Compress {


    @Override
    public byte[] compress(byte[] bytes) {
        if (bytes == null) {
            throw new NullPointerException("bytes is null");
        }
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             BZip2CompressorOutputStream bZip2CompressorOutputStream = new BZip2CompressorOutputStream(out)) {
            bZip2CompressorOutputStream.write(bytes);
            bZip2CompressorOutputStream.close();
            return out.toByteArray();
        } catch (IOException e) {
            throw new RpcException("gzip compress failed", e);
        }
    }

    @Override
    public byte[] decompress(byte[] bytes) {
        if (bytes == null) {
            throw new NullPointerException("bytes is null");
        }
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             ByteArrayInputStream in = new ByteArrayInputStream(bytes);
             BZip2CompressorInputStream bZip2CompressorInputStream = new BZip2CompressorInputStream(in)) {
            byte[] buffer = new byte[2048];
            int n;
            while ((n = bZip2CompressorInputStream.read(buffer)) >= 0) {
                out.write(buffer, 0, n);
            }
            return out.toByteArray();
        } catch (IOException e) {
            throw new RpcException("gzip decompress failed", e);
        }
    }
}
