/*
 * MIT License
 *
 * Copyright (c) 2018 awphi
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package ph.adamw.amazer;

import ph.adamw.amazer.gui.grid.data.DataGrid;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class FileUtils {
	public static boolean writeDataGrid(File path, String name, DataGrid grid) {
		if(!path.isDirectory()) {
			return false;
		}

		final File file = new File(path.getAbsolutePath() + "/" + name + ".maz");

		try {
			FileOutputStream fout = new FileOutputStream(file);
			ObjectOutputStream oos = new ObjectOutputStream(fout);
			oos.writeObject(grid);

			oos.close();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public static DataGrid readDataGrid(File file) {
		if(!file.exists() || !file.toString().endsWith(".maz")) {
			return null;
		}

		DataGrid grid = null;

		try {
			FileInputStream in = new FileInputStream(file);
			ObjectInputStream ois = new ObjectInputStream(in);
			grid = (DataGrid) ois.readObject();

			ois.close();
		} catch(IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}

		return grid;
	}
}
