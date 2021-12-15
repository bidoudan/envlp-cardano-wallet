package com.univocity.cardano.wallet.common;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import okhttp3.*;
import org.apache.commons.codec.binary.*;
import org.apache.commons.io.*;
import org.slf4j.*;

import java.io.*;
import java.net.*;
import java.nio.charset.*;
import java.time.*;
import java.time.format.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;

public class Utils {

	private static final Logger log = LoggerFactory.getLogger(Utils.class);

	private static final ObjectWriter OBJECT_PRINTER = new ObjectMapper().writerWithDefaultPrettyPrinter();
	private static File tempDir;




	/**
	 * Ensures a given argument is not null.
	 *
	 * @param o         the object to validate
	 * @param fieldName the description of the field
	 */
	public static final void notNull(Object o, String fieldName) {
		if (o == null) {
			throw new IllegalArgumentException(fieldName + " cannot be null");
		}
	}

	public static final void notBlank(CharSequence o, String fieldName) {
		notNull(o, fieldName);
		if (o.toString().trim().isEmpty()) {
			throw new IllegalArgumentException(fieldName + " cannot be blank");
		}
	}

	/**
	 * Throws an IllegalArgumentException if the given array is null or empty.
	 *
	 * @param argDescription the description of the elements
	 * @param args           the elements to be validated.
	 * @param <T>            Type of arguments to be validated
	 */
	public static <T> void notEmpty(String argDescription, T... args) {
		if (args == null) {
			throw new IllegalArgumentException(argDescription + " must not be null");
		}
		if (args.length == 0) {
			throw new IllegalArgumentException(argDescription + " must not be empty");
		}
	}

	/**
	 * Ensures a given {@link CharSequence} argument is not null/empty/blank
	 *
	 * @param o         a character sequence
	 * @param fieldName the description of the field
	 */




}
