package com.mrnadimi.cafebazzartools.exceptions;

public class BuyingProductCancelException extends CafeBazzarException{

    public BuyingProductCancelException() {
        super("The user has stopped buying this product");
    }
}
