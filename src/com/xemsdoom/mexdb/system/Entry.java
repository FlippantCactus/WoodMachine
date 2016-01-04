package com.xemsdoom.mexdb.system;

import java.util.List;

import com.xemsdoom.mexdb.exception.EmptyIndexException;
import com.xemsdoom.mexdb.exception.EmptyListException;

/**
 * Copyright (C) 2012-2013 Moser Luca <moser.luca@gmail.com>
 * 
 * This file is part of MexDB.
 * 
 * MexDB is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * MexDB is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * Foobar. If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * Entry for the flatfile, consisting of an index, keys/keyvalues
 */
public class Entry {

	// The index of the entry
	private String index;

	// Check if the first value is passed
	private boolean first = true;

	// Entry builder
	private StringBuilder valuebuilder = new StringBuilder();

	/**
	 * Entry, consisting of an index, keys/keyvalues.<br>
	 * The index should be usually a String object. The index is lowercased and
	 * trimmed.
	 * 
	 * @throws EmptyIndexException
	 *             If the index passed is empty/insufficient
	 */
	public Entry(Object index) throws EmptyIndexException {

		String in = index.toString();

		if (in.isEmpty())
			throw new EmptyIndexException("Insufficient index passed");

		// Init the index of this entry
		this.index = in.trim().toLowerCase();
	}

	/**
	 * Adds the key and keyvalue to the entry. The key gets trimmed and
	 * lowercased.
	 */
	public void addValue(Object key, Object keyvalue) {

		String keya = key.toString();
		String valuea = keyvalue == null ? "" : keyvalue.toString();

		// Adds the key and keyvalue to the stringbuilder
		// while removing spaces in the key
		if (first) {
			valuebuilder.append(keya.trim().toLowerCase().concat(":").concat(cipher(valuea)));
			first = false;
		} else
			valuebuilder.append(" ".concat(keya.trim().toLowerCase()).concat(":").concat(cipher(valuea)));
	}

	/**
	 * Adds the key and list to the entry.<br>
	 * The key gets trimmed. <b>Make sure that no String in the list contains
	 * the coding "%&"</b>,<br>
	 * since that is the coding for concatenating the Strings in the entry.
	 * <p/>
	 * A list is stored like this:<br>
	 * index key:value1%&value2%&value3%& etc.
	 * 
	 * @throws EmptyListException
	 *             When an empty list is passed as a parameter
	 */
	public void addList(Object key, List<String> list) throws EmptyListException {

		if (list.isEmpty())
			throw new EmptyListException("Empty list passed on method call addList()");

		String keya = key.toString();

		// Builder
		StringBuilder builder = new StringBuilder();

		// List size, so we know we are at the end
		int size = list.size();

		// Counter
		int c = 0;

		// Concatenating all list items
		for (String s : list) {

			if (c + 1 == size)
				// Concat last item without code
				builder.append(s);
			else
				builder.append(s.concat("%&"));
			c++;
		}

		// Adds the key and list to the stringbuilder
		// while removing spaces in the key
		if (first) {
			valuebuilder.append(keya.trim().toLowerCase().concat(":").concat(cipher(builder.toString())));
			first = false;
		} else
			valuebuilder.append(" ".concat(keya.trim().toLowerCase()).concat(":").concat(cipher(builder.toString())));
	}

	/**
	 * Gets the index of this entry
	 */
	public String getIndex() {
		return this.index;
	}

	/**
	 * Gets the arguments of this entry
	 */
	public String getArgs() {
		return this.valuebuilder.toString();
	}

	/**
	 * Replaces ':' with '%_' and ' ' with '%.'.<br>
	 * This is method is faster than using the String build in replace(); method
	 */
	private String cipher(String s) {

		if (s.length() == 0)
			return s;

		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < s.length(); i++) {
			if (s.charAt(i) == ':')
				builder.append("%_");
			else if (s.charAt(i) == ' ')
				builder.append("%.");
			else
				builder.append(s.charAt(i));
		}
		return builder.toString();
	}
}
