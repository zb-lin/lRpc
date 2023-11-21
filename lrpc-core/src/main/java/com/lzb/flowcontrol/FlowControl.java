package com.lzb.flowcontrol;

import com.lzb.serviceloader.SPI;

/**
 * 限流
 */
@SPI
public interface FlowControl {

    void doFlowControlWithWait(String key);

    boolean doFlowControlWithFailFast(String key);

}
