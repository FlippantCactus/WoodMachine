package com.xemsdoom.mexdb.system;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import com.xemsdoom.mexdb.exception.EmptyListException;
import com.xemsdoom.mexdb.exception.NoSuchIndexException;
import com.xemsdoom.mexdb.file.IOManager;

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
 * Handles all data related things: entries, values, adding/removing.
 */
public class DataManager {

	// IO Manager
	private IOManager io;

	// Loaded content
	private LinkedHashMap<String, String> content;

	// Autpopush
	private boolean autopush;

	// Patterns for regex splits
	private Pattern pattern1;
	private Pattern pattern2;
	private Pattern pattern3;

	/**
	 * Handles data process.
	 */
	public DataManager(IOManager io) {

		// Init the IO
		this.io = io;

		// Faster split with pattern
		pattern1 = Pattern.compile(" ");
		pattern2 = Pattern.compile(":");
		pattern3 = Pattern.compile("%&");

		// Load the content for the first time
		this.content = io.getContent();

		// Setting autopush default to false
		autopush = false;
	}

	/**
	 * Adds a new entry to the loaded content without updating the flatfile.
	 */
	public void addEntry(Entry entry) {

		// Removing already existing entry if it exists
		if (content.containsKey(entry.getIndex()))
			content.remove(entry.getIndex());

		// Adding entry
		content.put(entry.getIndex(), entry.getArgs());

		// Autopush
		if (autopush)
			pushData();
	}

	/**
	 * Removes an entry from the loaded content
	 */
	public boolean removeEntry(String index) {

		if (content.containsKey(index.toLowerCase())) {

			content.remove(index.toLowerCase());

			// Autopush
			if (autopush)
				pushData();

			return true;
		} else
			return false;
	}

	/**
	 * Removes a key/keyvalue
	 */
	public boolean removeValue(String index, String key) {

		// Returning false if the entry does not even exist
		if (!content.containsKey(index.toLowerCase()))
			return false;

		LinkedHashMap<String, String> argsmap = getKeySet(index.toLowerCase());

		// Returning false if the key does not exist
		if (!argsmap.containsKey(key.toLowerCase()))
			return false;

		// Removing the wanted key
		argsmap.remove(key.toLowerCase());

		// Refreshing content after process
		refreshContent(index.toLowerCase(), argsmap);

		// Autopush
		if (autopush)
			pushData();

		return true;
	}

	/**
	 * Updates a value from an entry or adds it if the key is nonexistent
	 * without updating the flatfile.
	 */
	public void setValue(String index, String key, Object value) {

		// Getting a keyset
		LinkedHashMap<String, String> args = getKeySet(index.toLowerCase());

		// Adding/Updating key/keyvalue
		args.put(key.toLowerCase(), cipher(String.valueOf(value)));

		// Refreshing content after process
		refreshContent(index.toLowerCase(), args);

		// Autopush
		if (autopush)
			pushData();
	}

	/**
	 * Updates a list from an entry or adds it if the key is nonexistent without
	 * updating the flatfile.<br>
	 * <b>Make sure that no String in the list contains the coding "%&"</b>,<br>
	 * since that is the coding for concatenating the Strings in the entry.
	 * 
	 * @throws EmptyListException
	 *             When an empty list is passed as a parameter
	 */
	public void setList(String index, String key, List<String> list) throws EmptyListException {

		if (list.isEmpty())
			throw new EmptyListException("Empty list passed on method call setList()");

		// Getting a keyset
		LinkedHashMap<String, String> args = getKeySet(index.toLowerCase());

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

		// Adding/Updating key/list
		args.put(key.toLowerCase(), cipher(builder.toString()));

		// Refreshing content after process
		refreshContent(index.toLowerCase(), args);

		// Autopush
		if (autopush)
			pushData();
	}

	public void replaceIndex(String index, String replace) throws NoSuchIndexException {

		// Lowercasing te index
		String indexl = index.toLowerCase().trim();

		// Throw exception if the index doesn't exist
		if (!content.containsKey(indexl))
			throw new NoSuchIndexException("Index: \"" + indexl + "\" does not exist!");

		// Getting key/keyvalues as one String
		String keyv = content.get(indexl);

		// Replace entry
		content.remove(indexl);
		content.put(replace.toLowerCase().trim(), keyv);
	}

	/**
	 * Gets the indices of the loaded content.
	 */
	public Set<String> getIndices() {
		return content.keySet();
	}

	/**
	 * Gets the string of a key from an entry.
	 */
	public String getString(String index, String key) {
		String mkey = key.toLowerCase().trim();
		return encode(getSharpKeySet(index.toLowerCase(), mkey).get(mkey));
	}

	/**
	 * Gets the arraylist<String> of a key from an entry.
	 */
	public ArrayList<String> getArrayList(String index, String key) {

		String mkey = key.toLowerCase().trim();

		// Getting raw string
		String s = encode(getSharpKeySet(index.toLowerCase(), mkey).get(mkey));

		// Creating new list to return
		ArrayList<String> list = new ArrayList<String>();

		// Splitting raw string
		String[] items = pattern3.split(s);

		// Fill list with items
		for (String st : items)
			list.add(st);

		return list;
	}

	/**
	 * Gets the linkedlist<String> of a key from an entry.
	 */
	public LinkedList<String> getLinkedList(String index, String key) {

		String mkey = key.toLowerCase().trim();

		// Getting raw string
		String s = encode(getSharpKeySet(index.toLowerCase(), mkey).get(mkey));

		// Creating new list to return
		LinkedList<String> list = new LinkedList<String>();

		// Splitting raw string
		String[] items = pattern3.split(s);

		// Fill list with items
		for (String st : items)
			list.add(st);

		return list;
	}

	/**
	 * Gets an int of a key from an entry.
	 */
	public int getInt(String index, String key) {
		String mkey = key.toLowerCase().trim();
		return Integer.valueOf(getSharpKeySet(index.toLowerCase(), mkey).get(mkey));
	}

	/**
	 * Gets a double of a key from an entry.
	 */
	public double getDouble(String index, String key) {
		String mkey = key.toLowerCase().trim();
		return Double.valueOf(getSharpKeySet(index.toLowerCase(), mkey).get(mkey));
	}

	/**
	 * Gets a long of a key from an entry.
	 */
	public long getLong(String index, String key) {
		String mkey = key.toLowerCase().trim();
		return Long.valueOf(getSharpKeySet(index.toLowerCase(), mkey).get(mkey));
	}

	/**
	 * Gets a float of a key from an entry.
	 */
	public float getFloat(String index, String key) {
		String mkey = key.toLowerCase().trim();
		return Float.valueOf(getSharpKeySet(index.toLowerCase(), mkey).get(mkey));
	}

	/**
	 * Gets a boolean of a key from an entry.
	 */
	public boolean getBoolean(String index, String key) {
		String mkey = key.toLowerCase().trim();
		return Boolean.valueOf(getSharpKeySet(index.toLowerCase(), mkey).get(mkey));
	}

	/**
	 * Gets the values of an entry
	 */
	public ArrayList<String> getValues(String index) {

		LinkedHashMap<String, String> keyset = getKeySet(index.toLowerCase());
		ArrayList<String> values = new ArrayList<String>();

		for (Map.Entry<String, String> entry : keyset.entrySet())
			values.add(encode(entry.getValue()));

		return values;
	}

	/**
	 * Gets the keys of an entry
	 */
	public ArrayList<String> getKeys(String index) {

		LinkedHashMap<String, String> keyset = getKeySet(index.toLowerCase());
		ArrayList<String> values = new ArrayList<String>();

		for (Map.Entry<String, String> entry : keyset.entrySet()) {
			values.add(entry.getKey().trim());
		}
		return values;
	}

	/**
	 * Made for the value getters, as soon as a key was found on the
	 * args-string, the linkedhashmap is returned.
	 */
	private LinkedHashMap<String, String> getSharpKeySet(String index, String key) {

		// Getting the arguments
		String args = content.get(index);

		LinkedHashMap<String, String> argsmap = new LinkedHashMap<String, String>();

		// Return if we do not have anything to work with
		if (args == null || args.isEmpty())
			return argsmap;

		// Splitting the singel keys/keyvalues
		String[] seg = pattern1.split(args);

		// Returning if the key/keyvalues do not exist
		if (seg.length == 0)
			return argsmap;

		// Adding keys/keyvalues to map
		for (int counter = 0; counter < seg.length; counter++) {

			String[] shorterseg = pattern2.split(seg[counter]);

			argsmap.put(shorterseg[0], shorterseg.length == 2 ? shorterseg[1] : null);

			if (shorterseg[0].equals(key))
				break;
		}

		return argsmap;
	}

	/**
	 * Gets the keys/keyvalues of an entry
	 */
	public LinkedHashMap<String, String> getKeySet(String index) {

		// Getting the arguments
		String args = content.get(index.toLowerCase().trim());

		LinkedHashMap<String, String> argsmap = new LinkedHashMap<String, String>();

		// Return if we do not have anything to work with
		if (args == null || args.isEmpty())
			return argsmap;

		// Splitting the singel keys/keyvalues
		String[] seg = pattern1.split(args);

		// Returning if the key/keyvalues do not exist
		if (seg.length == 0)
			return argsmap;

		// Adding keys/keyvalues to map
		for (int counter = 0; counter < seg.length; counter++) {

			String[] shorterseg = pattern2.split(seg[counter]);

			argsmap.put(shorterseg[0], shorterseg.length == 2 ? shorterseg[1] : null);

		}

		return argsmap;
	}

	/**
	 * Checks if a key exists in an entry
	 */
	public boolean hasKey(String index, String key) {
		if (getKeySet(index.toLowerCase().trim()).containsKey(key.toLowerCase().trim()))
			return true;
		else
			return false;
	}

	/**
	 * Checks if the passed index exists
	 */
	public boolean hasIndex(String index) {
		if (content.containsKey(index.toLowerCase().trim()))
			return true;
		else
			return false;
	}

	/**
	 * Adds the loaded data to the IOManager
	 */
	public void pushData() {
		io.writeContent(this.content);
	}

	/**
	 * Frees the loaded content and reloads the content of the file
	 */
	public void free(boolean reloadcontent) {
		content = io.free(reloadcontent);
	}

	/**
	 * Resets basically everything, flatfile, loadedcontet, IOcontent
	 */
	public void clear() {
		this.content.clear();
		io.clear();
	}

	/**
	 * Enables/Disables autopush
	 */
	public void autopush(boolean auto) {
		this.autopush = auto;
	}

	/**
	 * Refreshes the content with the new content
	 */
	private void refreshContent(String index, LinkedHashMap<String, String> keyset) {

		// Refreshing entry
		StringBuilder build = new StringBuilder();
		boolean first = false;

		for (Map.Entry<String, String> entry : keyset.entrySet()) {
			if (!first) {
				build.append(entry.getKey().concat(":").concat(entry.getValue()));
				first = true;
			} else
				build.append(" ".concat(entry.getKey()).concat(":").concat(entry.getValue()));
		}

		// Adding refreshed entry to the loaded content
		content.put(index, build.toString());
	}

	/**
	 * Replaces ':' with '%_' and ' ' with '%.'.<br>
	 * This is method is faster than using the String build-in replace();
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

	/**
	 * Replaces %_ with : and %. with a singel space
	 */
	private String encode(String s) {

		// Return if value length is less then 2 or null
		if (s == null || s.length() < 2)
			return s;

		// Pointer
		int counter = 0;

		// Index
		int index = s.indexOf("%_", counter);

		// Appending pieces later to this
		StringBuilder builder = new StringBuilder();

		// Return if %_ is not in String
		if (index != -1) {

			// Replacing %_ with :
			do {
				// Append substring from counter to index -1
				builder.append(s.substring(counter, index));
				// Append our replace
				builder.append(":");
				// Counter + 2 as our replace is one char
				counter = index + 2;
			} while ((index = s.indexOf("%_", counter)) != -1);

			// Checking if we got everything
			if (counter < s.length())
				builder.append(s.substring(counter, s.length()));

		}

		// Starting stuff again but replace %.
		String s2 = builder.toString();

		if (s2.isEmpty())
			s2 = s;

		// Pointer2
		int counter2 = 0;

		// Index2
		int index2 = s2.indexOf("%.", counter2);

		// Return if %. is not in String
		if (index2 == -1)
			return s2;

		// Appending pices later to this
		StringBuilder builder2 = new StringBuilder();

		// Replacing %. with singel space
		do {
			// Append substring from counter to index -1
			builder2.append(s2.substring(counter2, index2));
			// Append our replace
			builder2.append(" ");
			// Counter + 2 as our replace is one char
			counter2 = index2 + 2;
		} while ((index2 = s2.indexOf("%.", counter2)) != -1);

		// Checking if we got everything
		if (counter2 < s2.length())
			builder2.append(s2.substring(counter2, s2.length()));

		// Return our modified String
		return builder2.toString();
	}
}
