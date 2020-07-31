import java.util.stream.Collectors;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

public class PieChartSample extends Application {

	@Override
	public void start(Stage stage) {
		Pane root = new Pane();
		Scene scene = new Scene(root);
		stage.setTitle("Imported Fruits");
		stage.setWidth(500);
		stage.setHeight(500);

		ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(new PieChart.Data("Grapefruit", 13), new PieChart.Data("Oranges", 25), new PieChart.Data(
				"Plums", 10), new PieChart.Data("Pears", 22), new PieChart.Data("Apples", 30));
		final PieChart chart = new PieChart(pieChartData);
		chart.setTitle("Imported Fruits");
		chart.setLabelsVisible(false);

		final Label caption = new Label("");
		caption.setVisible(false);
		caption.getStyleClass().addAll("chart-line-symbol", "chart-series-line");
		caption.setStyle("-fx-font-size: 12; -fx-font-weight: bold;");
		caption.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

		DoubleBinding total = Bindings.createDoubleBinding(() -> pieChartData.stream().collect(Collectors.summingDouble(PieChart.Data::getPieValue)), pieChartData);
		for (final PieChart.Data data : chart.getData()) {
			data.getNode().addEventHandler(MouseEvent.MOUSE_MOVED, e -> {
				caption.setTranslateX(e.getSceneX() + 10);
				caption.setTranslateY(e.getSceneY() + 10);
				String text = String.format("%.1f%%", (100 * data.getPieValue()) / total.get());
				caption.setText(text);
				caption.setVisible(true);
			});
			data.getNode().addEventHandler(MouseEvent.MOUSE_EXITED, e -> {
				System.out.println(e);
				caption.setVisible(false);
			});
		}

		root.getChildren().addAll(chart, caption);
		stage.setScene(scene);
		stage.show();
	}

	public static void main(String[] args) {
		Application.launch(args);
	}
}