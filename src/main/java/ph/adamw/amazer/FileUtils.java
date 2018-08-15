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

import javafx.stage.FileChooser;
import lombok.Getter;
import ph.adamw.amazer.gui.grid.data.DataGrid;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URISyntaxException;

public class FileUtils {
	@Getter
	private static FileChooser mazeChooser = new FileChooser();

	@Getter
	private static FileChooser evolutionChooser = new FileChooser();

	static {
		FileChooser.ExtensionFilter mazeExtFilter = new FileChooser.ExtensionFilter("MAZE files (*.maz)", "*.maz");
		mazeChooser.getExtensionFilters().add(mazeExtFilter);

		FileChooser.ExtensionFilter evoExtFilter = new FileChooser.ExtensionFilter("MAZER EVOLUTION files (*.evo)", "*.evo");
		evolutionChooser.getExtensionFilters().add(evoExtFilter);

		try {
			final File dir = new File(new File(FileUtils.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile().getPath());

			mazeChooser.setInitialDirectory(dir);
			evolutionChooser.setInitialDirectory(dir);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

	public static boolean writeObjectToFile(File file, DataGrid obj) {
		if(obj == null) {
			return false;
		}

		try {
			FileOutputStream fout = new FileOutputStream(file);
			ObjectOutputStream oos = new ObjectOutputStream(fout);
			oos.writeObject(obj);

			oos.close();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	@SuppressWarnings("unchecked")
	public static <T> T readObjectFromFile(File file) {
		if(!file.exists()) {
			return null;
		}

		T obj = null;

		try {
			FileInputStream in = new FileInputStream(file);
			ObjectInputStream ois = new ObjectInputStream(in);

			// Unsafe cast usage to avoid creating wrapper methods for every class I want to read/write to
			// - this shouldn't be an issue regardless
			obj = (T) ois.readObject();

			ois.close();
		} catch(Exception e) {
			e.printStackTrace();
		}

		return obj;
	}
}
