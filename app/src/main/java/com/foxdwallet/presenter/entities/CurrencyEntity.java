package com.foxdwallet.presenter.entities;

import java.io.Serializable;


/**
 * RavenWallet
 * <p/>
 * Created by Mihail Gutan <mihail@breadwallet.com> on 8/18/15.
 * Copyright (c) 2016 breadwallet LLC
 * <p/>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p/>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

public class CurrencyEntity implements Serializable {

    //Change this after modifying the class
    private static final long serialVersionUID = 7526472295622776147L;

    public static final String TAG = CurrencyEntity.class.getName();
    public String code;//this currency code (USD, EUR)
    public String name;//this currency name (Dollar)
    public float rate;
    public String iso;//this wallet's iso (foxd, BTC)

    public CurrencyEntity(String code, String name, float rate, String iso) {
        this.code = code;
        this.name = name;
        this.rate = rate;
        this.iso = iso;
    }

    public CurrencyEntity() {
    }

    @Override
    public String toString() {
        return "code: " + code + " name: " + name + " rate: " + rate;
    }
}
