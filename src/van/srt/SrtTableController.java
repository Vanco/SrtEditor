package van.srt;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.MalformedInputException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.SortedMap;

public class SrtTableController {
    public TableView<SrtRecord> srtTable;
    public Label filename;
    public Button btnOpenSrt;
    public ChoiceBox<Charset> charset;
    public Button btnSaveSrt;

    private ObservableList<SrtRecord> masterData = FXCollections.observableArrayList();

    private Path path;

    @FXML
    private ResourceBundle resources;

    @FXML
    private void initialize() {
        SortedMap<String, Charset> cs = Charset.availableCharsets();

        for (Map.Entry<String, Charset> e : cs.entrySet()) {
            if (e.getValue().isRegistered())
                charset.getItems().add(e.getValue());
        }

        srtTable.setEditable(true);
        SortedList<SrtRecord> sortedData = new SortedList<SrtRecord>(masterData);

        sortedData.setComparator((o1, o2) -> Float.compare(o1.getId(), o2.getId()));

        srtTable.setItems(sortedData);

    }

    public void openSrtFile() {
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(resources.getString("dialog.filechooser.choose.subtitle.file.srt"));
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("SRT", "*.srt"));
        File file = fileChooser.showOpenDialog(btnOpenSrt.getScene().getWindow());
        if (file != null ) {
            path = file.toPath();
            filename.setText(file.getAbsolutePath());
            charset.setValue(detect());
            loadFile(charset.getValue());
        }
    }

    private Charset detect() {
        BufferedReader in = null;
        ArrayList<Charset> charsets = new ArrayList<>();
        charsets.add(charset.getValue()); // User choose first
        charsets.add(Charset.defaultCharset()); // system default seconds
        charsets.addAll(charset.getItems()); // than try to guess

        for (Charset c : charsets) {
            try {
                in = Files.newBufferedReader(path, c);

                in.readLine();

                return c;

            } catch (IOException e) {
                //System.out.println(e.getMessage());
            } finally {
                if (in != null) try {
                    in.close();
                } catch (IOException e) {
                    //ignore
                }
            }
        }

        return StandardCharsets.UTF_8;
    }

    private void loadFile(Charset detect) {
        try {
            masterData.clear();
            BufferedReader in = Files.newBufferedReader(path, detect);
            SrtReader reader = new SrtReader(in);
            while (reader.ready()) {
                SrtRecord srtRecord = reader.readRecord();
                masterData.add(srtRecord);
            }
            reader.close();
            in.close();
        } catch (IOException e) {
            System.err.format("IOException: %s%n", e);
            if (e instanceof MalformedInputException) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle(resources.getString("dialog.error.title"));
                alert.setHeaderText(resources.getString("dialog.error.wrong.charset"));
                alert.setContentText(resources.getString("dialog.error.ooops.please.try.another.encode"));
                alert.showAndWait();
            }
        }
    }

    public void charsetChanged() {
        if (path != null) {
            loadFile(charset.getValue());
        }
    }

    public void saveSrtFile() {
        if (srtTable.getItems().isEmpty()) return;

        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle(resources.getString("dialog.filechooder.save.srt.file"));
            fileChooser.setInitialFileName(defaultFileName());
            fileChooser.setInitialDirectory(path.getParent().toFile());
            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("SRT", "*.srt"));
            File file = fileChooser.showSaveDialog(btnSaveSrt.getScene().getWindow());
            if (file != null ) {
                Path savePath = file.toPath();
                BufferedWriter bufferedWriter = Files.newBufferedWriter(savePath, charset.getValue());
                SrtWriter writer = new SrtWriter(bufferedWriter);
                for (SrtRecord srtRecord : srtTable.getItems()) {
                    writer.write(srtRecord);
                }
                writer.flush();
                writer.close();
                bufferedWriter.close();
            }
        } catch (IOException e) {
            System.err.format("IOException: %s%n", e);
        }
    }

    private String defaultFileName() {
        String s = path.getFileName().toString();
        s = s.substring(0, s.indexOf(".srt")) + "_v.srt";
        return s;
    }

    public SrtRecord getSelectedItem() {
        return srtTable.getSelectionModel().getSelectedItem();
    }

    public void addItem(SrtRecord srtRecord, float newId) {
        masterData.add(new SrtRecord(newId, srtRecord.getTime(), srtRecord.getSub()));
        adjustId();
    }

    public void removeItem(SrtRecord srtRecord) {
        masterData.removeAll(srtRecord);
        adjustId();
    }

    private void adjustId() {
        ObservableList<SrtRecord> sorted = srtTable.getItems();
        for (int i = 0; i < sorted.size(); i++) {
            sorted.get(i).setId(i + 1);
        }
    }

    public void clear() {
        srtTable.getSelectionModel().clearSelection();
    }
}
