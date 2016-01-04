package com.xemsdoom.mexdb.file;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

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
 * Takes over the I/O of the physical file.
 */
public class IOManager {

	// The physical file
	private File flatfile;
	// The path of the file
	private String path;
	// The filename
	private String filename;
	// The lines/content of the file
	private LinkedHashMap<String, String> content = new LinkedHashMap<String, String>();
	// Faster split
	private Pattern pattern;

	/**
	 * Handles I/O of the flatfile.
	 */
	public IOManager(String path, String filename) {

		// Inits path and filename
		this.path = path.replace(" ", "_");
		this.filename = filename.concat(".mexdb");

		// Faster split with pattern
		pattern = Pattern.compile(" ");

		// Creates the flatfile if it does not exist
		createFlatFile(this.path, this.filename);

		// Loads the flatfile content
		readContent();
	}

	/**
	 * Creates the flatfile if it does not exist.
	 */
	private boolean createFlatFile(String path, String filename) {
		try {
			(new File(path)).mkdirs();
			flatfile = new File(path, filename);
			flatfile.createNewFile();
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * Loads the flatfile.
	 */
	private void readContent() {
		try {

			BufferedReader reader = new BufferedReader(new FileReader(flatfile));
			String line;

			// Loading flatfile content
			while ((line = reader.readLine()) != null) {

				String[] seg = pattern.split(line);
				StringBuilder build = new StringBuilder();

				// Adding the arguments without the index
				for (int counter = 1; counter < seg.length; counter++) {
					if (counter == 1) {
						build.append(seg[counter]);
					} else {
						build.append(" ".concat(seg[counter]));
					}
				}

				// Puttig the index and the arguments
				this.content.put(seg[0], build.toString());
			}
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Gets the content of the flatfile.
	 */
	public LinkedHashMap<String, String> getContent() {
		return this.content;
	}

	/**
	 * Writes the loaded content to the flatfile.
	 */
	public void writeContent(LinkedHashMap<String, String> newcontent) {
		this.content = newcontent;
		try {

			BufferedWriter writer = new BufferedWriter(new FileWriter(flatfile));

			for (Map.Entry<String, String> entry : this.content.entrySet()) {
				writer.write(entry.getKey().concat(" ").concat(entry.getValue()));
				writer.newLine();
			}
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Checks if the flatfile exists.
	 */
	public boolean existFlatFile() {
		return flatfile.exists();
	}

	/**
	 * Gets the directory in which the flatfile is located in.
	 */
	public String getDirectory() {
		return this.path;
	}

	/**
	 * Gets the filename.
	 */
	public String getFileName() {
		return this.filename;
	}

	/**
	 * Frees the loaded content and reloads the content of the file
	 */
	public LinkedHashMap<String, String> free(boolean reloadcontent) {
		content.clear();
		if (reloadcontent)
			readContent();
		return this.content;
	}

	/**
	 * Clears the flatfile and loaded IOcontent.
	 */
	public void clear() {
		content.clear();
		flatfile.delete();
		try {
			flatfile.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Deletes the flatfile
	 */
	public void deleteDB() {
		try {
			flatfile.delete();
		} catch (Exception ex) {
		}
	}
}
