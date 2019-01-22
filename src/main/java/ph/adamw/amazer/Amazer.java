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

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.Getter;
import ph.adamw.amazer.gui.MainGuiController;
import ph.adamw.amazer.gui.SplashGuiController;
import ph.adamw.amazer.gui.grid.data.DataGrid;
import ph.adamw.amazer.mazer.MazerEvolution;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URL;

public class Amazer extends Application {
	@Getter
	private static Scene scene;

	@Getter
	private static Stage stage;

	@Getter
	private static MazerEvolution evolution;

	private static MainGuiController gui;

	public static void main(String[] args) {
		Application.launch(args);
	}

	@Override
	public void start(Stage stage) throws IOException {
		final Parent root;
		final FXMLLoader loader = new FXMLLoader();
		root = loader.load(MainGuiController.class.getResource("main.fxml").openStream());
		gui = loader.getController();

		scene = new Scene(root);

		Amazer.stage = stage;
		stage.setTitle("a_mazer");
		stage.setScene(scene);
		stage.show();
	}

	public static void openSplash(DataGrid grid) {
		final Parent splash;
		final FXMLLoader loader = new FXMLLoader();

		try {
			splash = loader.load(SplashGuiController.class.getResource("splash.fxml").openStream());
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		// Quick cast as it's only used once
		((SplashGuiController) loader.getController()).setGrid(grid);

		final Stage splashStage = new Stage();
		splashStage.setTitle("a_mazer - New Evolution");
		splashStage.initModality(Modality.WINDOW_MODAL);
		splashStage.initOwner(scene.getWindow());
		splashStage.setScene(new Scene(splash));
		splashStage.setResizable(false);
		splashStage.show();
	}

	public static void loadEvolution(MazerEvolution evo) {
		gui.setGridEditable(false);
		gui.loadGrid(evo.getDataGrid());

		evolution = evo;

		// Simple logic switch, if it's a newly created evolution we run the next (1st) gen on load, otherwise we just load the current state in
		// i.e. if it's from disk
		if(evo.getGeneration() != null) {
			gui.occupyGenerationList(evo.getGeneration().getSortedCopyOfMembers());
		} else {
			gui.onNextGenPressed(null);
		}
	}
}
