package com.mrnadimi.cafebazzartools.util.communication;


import com.mrnadimi.cafebazzartools.util.IabResult;

public interface BillingSupportCommunication {
    void onBillingSupportResult(int response);
    void remoteExceptionHappened(IabResult result);
}
