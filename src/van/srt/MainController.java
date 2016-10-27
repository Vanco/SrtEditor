package van.srt;

import javafx.animation.AnimationTimer;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.*;

/**
 * &copy; fanhuagang@gmail.com
 * Created by van on 23/10/2016.
 */
public class MainController {
    public VBox leftSrt;
    public Button btnCopyToLeft;
    public Button btnCopyToRight;
    public Button btnRemove;
    public VBox rightSrt;
    public Button btnMerge;

    @FXML
    private SrtTableController leftSrtController;
    @FXML
    private SrtTableController rightSrtController;
    @FXML
    private ResourceBundle resources;

    @FXML
    private void initialize() {
        // An AnimationTimer, whose handle(...) method will be invoked once
        // on each frame pulse (i.e. each rendering of the scene graph)
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                ScrollBar scrollBarLeft = (ScrollBar) leftSrtController.srtTable.lookup(".scroll-bar:vertical");
                ScrollBar scrollBarRight = (ScrollBar) rightSrtController.srtTable.lookup(".scroll-bar:vertical");
                if (scrollBarLeft != null && scrollBarRight != null) {
                    scrollBarLeft.valueProperty().bindBidirectional(scrollBarRight.valueProperty());
                    stop();
                }
            }
        };
        timer.start();

        leftSrtController.srtTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                btnCopyToRight.setDisable(false);
            } else {
                btnCopyToRight.setDisable(true);
            }
            btnRemove.setDisable(btnCopyToLeft.isDisable() && btnCopyToRight.isDisable());
        });


        rightSrtController.srtTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                btnCopyToLeft.setDisable(false);
            } else {
                btnCopyToLeft.setDisable(true);
            }

            btnRemove.setDisable(btnCopyToLeft.isDisable() && btnCopyToRight.isDisable());
        });
    }

    public void copyToLeft() {
        copy(rightSrtController, leftSrtController, resources.getString("dialog.copy.target.left"));

    }

    public void copyToRight() {
        copy(leftSrtController, rightSrtController, resources.getString("dialog.copy.target.right"));
    }

    private void copy(SrtTableController from, SrtTableController to, String target) {

        SrtRecord selectedItem = from.getSelectedItem();
        List<String> choices = new ArrayList<>();
        String before = MessageFormat.format(resources.getString("choicebox.item.before.0"), selectedItem.getId());
        choices.add(before);
        choices.add(MessageFormat.format(resources.getString("choicebox.item.after.0"), selectedItem.getId()));

        ChoiceDialog<String> dialog = new ChoiceDialog<>(before, choices);
        dialog.setTitle(resources.getString("dialog.copy.confirm.copy.position"));
        dialog.setHeaderText(MessageFormat.format(resources.getString("dialog.copy.the.selected.row.0.to.the.1"), selectedItem.getId(), target));
        dialog.setContentText(resources.getString("dialog.copy.insert.it"));

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(c -> {
            if (c.equals(before)) {
                to.addItem(selectedItem, selectedItem.getId() - 0.3f);
            } else {
                to.addItem(selectedItem, selectedItem.getId() + 0.3f);
            }
        });
    }

    public void about() {
        Alert alert = new Alert(Alert.AlertType.NONE);
//        alert.getDialogPane().setBackground(new Background(new BackgroundImage(new Image("/icons/srte.png"),null,null,null,null)));
        alert.setGraphic(new ImageView(new Image("/icons/srte-76x76.png")));
        alert.setHeaderText(resources.getString("app.name") + " " + Version.version());
        alert.setContentText("© 2016 VanStudio");
//        alert.getDialogPane().setPrefSize(650, 440);
        alert.getButtonTypes().addAll(ButtonType.CLOSE);
        alert.show();
    }

    public void remove() {
        SrtRecord leftSelectedItem = leftSrtController.getSelectedItem();
        SrtRecord rightSelectedItem = rightSrtController.getSelectedItem();

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(resources.getString("dialog.remove.confirmation"));
        alert.setHeaderText(resources.getString("dialog.remove.selected.row"));
        alert.setContentText(resources.getString("dialog.remove.choose.your.deletion"));

        alert.getButtonTypes().clear();
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        alert.getDialogPane().setPrefSize(320, 240);

        ButtonType buttonTypeLeftOnly = new ButtonType(resources.getString("dialog.remove.target.left"));
        ButtonType buttonTypeRightOnly = new ButtonType(resources.getString("dialog.remove.target.right"));
        ButtonType buttonTypeBoth = new ButtonType(resources.getString("dialog.remove.target.both"));
        ButtonType buttonTypeCancel = new ButtonType(resources.getString("dialog.button.cancel"), ButtonBar.ButtonData.CANCEL_CLOSE);


        if (leftSelectedItem != null) {
            alert.getButtonTypes().add(buttonTypeLeftOnly);
        }

        if (rightSelectedItem != null) {
            alert.getButtonTypes().add(buttonTypeRightOnly);
        }

        if (leftSelectedItem != null && rightSelectedItem != null) {
            alert.getButtonTypes().add(buttonTypeBoth);
        }

        alert.getButtonTypes().add(buttonTypeCancel);

        Optional<ButtonType> result = alert.showAndWait();

        if (result.get() == buttonTypeLeftOnly) {
            leftSrtController.removeItem(leftSelectedItem);
        } else if (result.get() == buttonTypeRightOnly) {
            rightSrtController.removeItem(rightSelectedItem);
        } else if (result.get() == buttonTypeBoth) {
            leftSrtController.removeItem(leftSelectedItem);
            rightSrtController.removeItem(rightSelectedItem);
        }

    }

    public void clear() {
        leftSrtController.clear();
        rightSrtController.clear();
    }

    public void merge() {
        // validate
        if (leftSrtController.srtTable.getItems().isEmpty() || rightSrtController.srtTable.getItems().isEmpty()
                || leftSrtController.srtTable.getItems().size() != rightSrtController.srtTable.getItems().size()) {
            new Alert(Alert.AlertType.INFORMATION, resources.getString("dialog.merge.info.both.size.do.not.match.each.other")).show();
            return;
        }

        // open merge config choice dialog
        Dialog<MergeConfig> dialog = createMergeConfigDialog();

        Optional<MergeConfig> result = dialog.showAndWait();

        result.ifPresent(mergeConfig -> {

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle(resources.getString("dialog.filechooder.save.srt.file"));
            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("SRT", "*.srt"));
            File file = fileChooser.showSaveDialog(btnMerge.getScene().getWindow());

            if (file != null) {
                try {
                    Path savePath = file.toPath();
                    Charset charset = mergeConfig.getCharset();
                    BufferedWriter bufferedWriter = Files.newBufferedWriter(savePath, charset);
                    SrtWriter writer = new SrtWriter(bufferedWriter);

                    ObservableList<SrtRecord> left = leftSrtController.srtTable.getItems();
                    ObservableList<SrtRecord> right = rightSrtController.srtTable.getItems();

                    for (int i = 0; i < left.size(); i++) {
                        SrtRecord lsr = left.get(i);
                        SrtRecord rsr = right.get(i);

                        SrtRecord srtRecord = merge(i + 1, lsr, rsr, mergeConfig);
                        writer.write(srtRecord);
                    }
                    writer.flush();
                    writer.close();
                    bufferedWriter.close();
                } catch (IOException e) {
                    System.err.format("IOException: %s%n", e);
                }
            }

        });
    }

    private Dialog<MergeConfig> createMergeConfigDialog() {
        Dialog<MergeConfig> dialog = new Dialog<MergeConfig>();
        dialog.setTitle(resources.getString("dialog.merge.srt.file"));
        dialog.setHeaderText(resources.getString("dialog.merge.the.two.srt.file.into.one"));

        dialog.setGraphic(new ImageView(getClass().getResource("/icons/merge-76x76.png").toString()));

        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.NEXT, ButtonType.CANCEL);

        Label contentText = new Label(resources.getString("dialog.merge.choose.the.content.you.want.to.include.in.the.final.file"));

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 20, 20, 20));

        grid.add(new Label(resources.getString("dialog.merge.target.left")), 0, 0);
        grid.add(new Label(resources.getString("dialog.merge.target.right")), 1, 0);

        ToggleGroup timeGroup = new ToggleGroup();
        RadioButton timeLeft = new RadioButton(resources.getString("table.col.time"));
        RadioButton timeRight = new RadioButton(resources.getString("table.col.time"));

        timeLeft.setToggleGroup(timeGroup);
        timeRight.setToggleGroup(timeGroup);

        timeLeft.setUserData("Left");
        timeRight.setUserData("Right");

        timeLeft.setSelected(true);

        grid.add(timeLeft, 0, 1);
        grid.add(timeRight, 1, 1);

        ToggleGroup subGroup = new ToggleGroup();
        RadioButton subLeft = new RadioButton(resources.getString("table.col.subtitle"));
        RadioButton subRight = new RadioButton(resources.getString("table.col.subtitle"));
        RadioButton subBoth = new RadioButton(resources.getString("dialog.merge.combine.subtitle"));

        subLeft.setToggleGroup(subGroup);
        subRight.setToggleGroup(subGroup);
        subBoth.setToggleGroup(subGroup);

        subLeft.setUserData("Left");
        subRight.setUserData("Right");
        subBoth.setUserData("Both");

        subRight.setSelected(true);

        grid.add(subLeft, 0, 2);
        grid.add(subRight, 1, 2);
        grid.add(subBoth, 2, 2);

        ToggleGroup charsetGroup = new ToggleGroup();
        RadioButton leftCharset = new RadioButton(leftSrtController.charset.getValue().name());
        RadioButton rightCharset = new RadioButton(rightSrtController.charset.getValue().name());
        RadioButton otherCharset = new RadioButton("Other");

        leftCharset.setToggleGroup(charsetGroup);
        rightCharset.setToggleGroup(charsetGroup);
        otherCharset.setToggleGroup(charsetGroup);

        leftCharset.setUserData(leftSrtController.charset.getValue());
        rightCharset.setUserData(rightSrtController.charset.getValue());
        otherCharset.setUserData(StandardCharsets.UTF_8);

        leftCharset.setSelected(true);

        grid.add(leftCharset, 0, 3);
        grid.add(rightCharset, 1, 3);
        grid.add(otherCharset, 2, 3);

        ChoiceBox<Charset> other = new ChoiceBox<>();

        SortedMap<String, Charset> cs = Charset.availableCharsets();

        for (Map.Entry<String, Charset> e : cs.entrySet()) {
            if (e.getValue().isRegistered())
                other.getItems().add(e.getValue());
        }
        other.setValue(StandardCharsets.UTF_8);

        other.setDisable(true);

        grid.add(other, 3, 3);

        charsetGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            other.setDisable(!((RadioButton) newValue).getText().equals("Other"));
        });


        other.onActionProperty().setValue(event -> {
            otherCharset.setUserData(other.getValue());
        });

        VBox vBox = new VBox(contentText, grid);

        GridPane.setHgrow(vBox, Priority.ALWAYS);

        dialog.getDialogPane().setContent(vBox);
        dialog.getDialogPane().setPrefSize(520, 340);

        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.NEXT) {
                return new MergeConfig(
                        timeGroup.getSelectedToggle().getUserData().toString(),
                        subGroup.getSelectedToggle().getUserData().toString(),
                        (Charset) charsetGroup.getSelectedToggle().getUserData());
            }
            return null;
        });
        return dialog;
    }

    private SrtRecord merge(int id, SrtRecord lsr, SrtRecord rsr, MergeConfig mergeConfig) {

        return new SrtRecord(
                id,
                "Left".equals(mergeConfig.getTime()) ? lsr.getTime() : rsr.getTime(),
                "Both".equals(mergeConfig.getSub()) ? lsr.getSub() + rsr.getSub() : "Left".equals(mergeConfig.getSub()) ? lsr.getSub() : rsr.getSub()
        );
    }

}