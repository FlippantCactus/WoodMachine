package com.xemsdoom.mexdb;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.xemsdoom.mexdb.exception.EmptyListException;
import com.xemsdoom.mexdb.exception.NoSuchIndexException;
import com.xemsdoom.mexdb.file.IOManager;
import com.xemsdoom.mexdb.system.DataManager;
import com.xemsdoom.mexdb.system.Entry;

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
 * Creates a new MexDB with the given path and filename.
 */
public class MexDB {

	private IOManager io;
	private DataManager dm;

	/**
	 * Creates a new flatfile database with the given path and name.
	 * 
	 * @param path
	 *            The path of the flatfile
	 * @param filename
	 *            The name of the flatfile
	 */
	public MexDB(String path, String filename) {

		// Init IO, creates the flatfile
		this.io = new IOManager(path, filename);

		// Init DataManager
		this.dm = new DataManager(this.io);
	}

	/**
	 * Adds a new entry to the loaded content <b>without</b> updating the
	 * flatfile.
	 * 
	 * @param entry
	 *            The entry which gets added to the loaded content
	 */
	public void addEntry(Entry entry) {
		dm.addEntry(entry);
	}

	/**
	 * Removes an entry from the loaded content <b>without</b> updating the
	 * flatfile.
	 * 
	 * @param index
	 *            The index of the entry
	 * @return true if the entry was removed, false if the entry never existed.
	 */
	public boolean removeEntry(String index) {
		return dm.removeEntry(index);
	}

	/**
	 * Removes a key/keyvalue from an entry <b>without</b> updating the
	 * flatfile.
	 * 
	 * @param index
	 *            The index of the entry
	 * @param key
	 *            The key to remove
	 */
	public void removeValue(String index, String key) {
		dm.removeValue(index, key);
	}

	/**
	 * Updates a value from an entry or <b>adds</b> it if the key is nonexistent
	 * <b>without</b> updating the flatfile.
	 * 
	 * @param index
	 *            The index of the entry
	 * @param key
	 *            A key of the entry
	 * @param value
	 *            The value of the key
	 */
	public void setValue(String index, String key, Object value) {
		dm.setValue(index, key, value);
	}

	/**
	 * Updates a list from an entry or <b>adds</b> it if the key is nonexistent
	 * <b>without</b> updating the flatfile.<br>
	 * <b>Make sure that no String in the list contains the coding "%&"</b>,<br>
	 * since that is the coding for concatenating the Strings in the entry.
	 * 
	 * @param index
	 *            The index of the entry
	 * @param key
	 *            A key of the entry
	 * @param list
	 *            The list of the key
	 * @throws EmptyListException
	 *             When an empty list is passed as a parameter
	 */
	public void setList(String index, String key, List<String> list) throws EmptyListException {
		dm.setList(index, key, list);
	}

	/**
	 * Replaces the index-String of an entry with another replace-String.<br>
	 * 
	 * @param index
	 *            The index of the entry
	 * 
	 * @param replace
	 *            The String which replaces the old index-String
	 * @throws NoSuchIndexException
	 *             When the index does not exist in the database
	 */
	public void replaceIndex(String index, String replace) throws NoSuchIndexException {
		dm.replaceIndex(index, replace);
	}

	/**
	 * Gets the indices of the loaded content.
	 */
	public Set<String> getIndices() {
		return dm.getIndices();
	}

	/**
	 * Gets a String of a key from an entry.
	 * 
	 * @param index
	 *            The index of the entry
	 * @param key
	 *            The key of which we want to get the value from
	 */
	public String getString(String index, String key) {
		return dm.getString(index, key);
	}

	/**
	 * Gets an Integer of a key from an entry.
	 * 
	 * @param index
	 *            The index of the entry
	 * @param key
	 *            The key of which we want to get the value from
	 */
	public int getInt(String index, String key) {
		return dm.getInt(index, key);
	}

	/**
	 * Gets a Double of a key from an entry.
	 * 
	 * @param index
	 *            The index of the entry
	 * @param key
	 *            The key of which we want to get the value from
	 */
	public double getDouble(String index, String key) {
		return dm.getDouble(index, key);
	}

	/**
	 * Gets an ArrayList of a key from an entry.
	 * 
	 * @param index
	 *            The index of the entry
	 * @param key
	 *            The key of which we want to get the arraylist from
	 */
	public ArrayList<String> getArrayList(String index, String key) {
		return dm.getArrayList(index, key);
	}

	/**
	 * Gets a LinkedList of a key from an entry.
	 * 
	 * @param index
	 *            The index of the entry
	 * @param key
	 *            The key of which we want to get the linkedlist from
	 */
	public LinkedList<String> getLinkedList(String index, String key) {
		return dm.getLinkedList(index, key);
	}

	/**
	 * Gets a Long of a key from an entry.
	 * 
	 * @param index
	 *            The index of the entry
	 * @param key
	 *            The key of which we want to get the value from
	 */
	public long getLong(String index, String key) {
		return dm.getLong(index, key);
	}

	/**
	 * Gets a Float of a key from an entry.
	 * 
	 * @param index
	 *            The index of the entry
	 * @param key
	 *            The key of which we want to get the value from
	 */
	public float getFloat(String index, String key) {
		return dm.getFloat(index, key);
	}

	/**
	 * Gets a Boolean of a key from an entry.
	 * 
	 * @param index
	 *            The index of the entry
	 * @param key
	 *            The key of which we want to get the value from
	 */
	public boolean getBoolean(String index, String key) {
		return dm.getBoolean(index, key);
	}

	/**
	 * Gets all keyvalues of an entry.
	 * 
	 * @param index
	 *            The index of the entry
	 */
	public ArrayList<String> getValues(String index) {
		return dm.getValues(index);
	}

	/**
	 * Gets all the keys of an entry.
	 * 
	 * @param index
	 *            The index of the entry
	 */
	public ArrayList<String> getKeys(String index) {
		return dm.getKeys(index);
	}

	/**
	 * Gets the keys and keyvalues of an entry.
	 * 
	 * @param index
	 *            The index of the entry
	 */
	public LinkedHashMap<String, String> getKeySet(String index) {
		return dm.getKeySet(index);
	}

	/**
	 * Checks if a key exists in an entry.
	 * 
	 * @param index
	 *            The index of the entry
	 * @param key
	 *            The key to check
	 * @return true if it does, false when not
	 */
	public boolean hasKey(String index, String key) {
		return dm.hasKey(index, key);
	}

	/**
	 * Checks if an index exists.
	 * 
	 * @param index
	 *            The index of the entry
	 * @return true if it does, false when not
	 */
	public boolean hasIndex(String index) {
		return dm.hasIndex(index);
	}

	/**
	 * Writes the loaded content to the flatfile.
	 */
	public void push() {
		dm.pushData();
	}

	/**
	 * Checks if the flatfile exists.
	 * 
	 * @return true if the flatfile exists, false when not
	 */
	public boolean existFlatFile() {
		return io.existFlatFile();
	}

	/**
	 * Gets the directory in which the flatfile is located in.
	 */
	public String getDirectory() {
		return io.getDirectory();
	}

	/**
	 * Gets the filename.
	 */
	public String getFileName() {
		return io.getFileName();
	}

	/**
	 * Clears the loaded content.
	 * 
	 * @param reloadcontent
	 *            If true, then the content of the flatfile is reloaded.
	 */
	public void free(boolean reloadcontent) {
		dm.free(reloadcontent);
	}

	/**
	 * Clears the flatfile and loaded content.
	 */
	public void clear() {
		dm.clear();
	}

	/**
	 * Deletes the flatfile and clears the loaded content.<br>
	 * <b>Note:</b> Write or read actions after this method call, could lead to
	 * errors in the MexDB system.<br>
	 * <b>Do not use the MexDB object anymore after this method call.</b>
	 */
	public void destroyMexDB() {
		dm.clear();
		io.deleteDB();
	}

	/**
	 * If set to true, <b>every</b> change to the loaded content gets auto
	 * written to the flatfile.<br>
	 * You should turn off autopush on iterations which have writting processes
	 * in it as <br>
	 * would not be very efficient. Autopush is turned off by default.
	 */
	public void autopush(boolean auto) {
		dm.autopush(auto);
	}
}
