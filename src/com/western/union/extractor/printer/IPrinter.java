package com.western.union.extractor.printer;

import java.io.Closeable;

import com.western.union.extractor.data.AddressData;

public interface IPrinter extends Closeable {
	public void print(final AddressData data);
}
