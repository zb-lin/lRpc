package com.lzb.compress.lzo;


import com.lzb.compress.Compress;
import com.lzb.exception.RpcException;
import org.anarres.lzo.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;


public class LZOCompress implements Compress {


    @Override
    public byte[] compress(byte[] bytes) {
        if (bytes == null) {
            throw new NullPointerException("bytes is null");
        }
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            LzoCompressor compressor = LzoLibrary.getInstance().newCompressor(
                    LzoAlgorithm.LZO1X, null);
            LzoOutputStream lzoOutputStream = new LzoOutputStream(out, compressor);
            lzoOutputStream.write(bytes);
            lzoOutputStream.close();
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
             ByteArrayInputStream in = new ByteArrayInputStream(bytes)) {
            LzoDecompressor decompressor = LzoLibrary.getInstance()
                    .newDecompressor(LzoAlgorithm.LZO1X, null);
            LzoInputStream lzoInputStream = new LzoInputStream(in, decompressor);
            int count;
            byte[] buffer = new byte[2048];
            while ((count = lzoInputStream.read(buffer)) != -1) {
                out.write(buffer, 0, count);
            }
            return out.toByteArray();
        } catch (IOException e) {
            throw new RpcException("gzip decompress failed", e);
        }
    }
}
