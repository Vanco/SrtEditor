package io.vanstudio.srt;

import javafx.animation.AnimationTimer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
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

import static io.vanstudio.srt.Languages.getCode;

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
    public ProgressBar bar;
    public TextArea log;
    public Button btnProject;
    public ChoiceBox<String> cbxLanguage;
    public ChoiceBox<String> cbxProvider;
    //    public MenuButton mbTranslate;
    public Button mbTranslate;
    public Button btnSyncTime;
    public Button btnCancel;

    @FXML
    private SrtTableController leftSrtController;
    @FXML
    private SrtTableController rightSrtController;
    @FXML
    private ResourceBundle resources;

    @FXML
    private void initialize() {

        cbxLanguage.setItems(FXCollections.observableArrayList(Languages.defaultLanguages()));
        cbxLanguage.setValue("Chinese Simplified");

        cbxProvider.setItems(FXCollections.observableArrayList("Azure", "LLM"));
        cbxProvider.setValue("Azure");
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
            btnCopyToRight.setDisable(newValue == null);
            btnRemove.setDisable(btnCopyToLeft.isDisable() && btnCopyToRight.isDisable());
        });


        rightSrtController.srtTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            btnCopyToLeft.setDisable(newValue == null);

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
                to.paste(from.getSelectedCopy(selectedItem.getId(), CopyConfig.Inset.BEFORE));
//                to.addItem(selectedItem, selectedItem.getId() - 0.3f);
            } else {
                to.paste(from.getSelectedCopy(selectedItem.getId(), CopyConfig.Inset.AFTER));
//                to.addItem(selectedItem, selectedItem.getId() + 0.3f);
            }
        });
    }

    public void about() {
        Alert alert = new Alert(Alert.AlertType.NONE);
        alert.setGraphic(new ImageView(ImageUtil.createImage(this, "/icons/srte-76x76.png")));
        alert.setHeaderText(resources.getString("app.name") + " " + Version.version());
        final int year = Calendar.getInstance().get(Calendar.YEAR);
        final String contentText = "© " + year + " VanStudio";
        alert.setContentText(contentText);
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

        result.ifPresent(buttonType -> {
            if (buttonType == buttonTypeLeftOnly) {
                leftSrtController.removeItem(leftSelectedItem);
            } else if (buttonType == buttonTypeRightOnly) {
                rightSrtController.removeItem(rightSelectedItem);
            } else if (buttonType == buttonTypeBoth) {
                leftSrtController.removeItem(leftSelectedItem);
                rightSrtController.removeItem(rightSelectedItem);
            }
        });

    }

    /**
     * Clear selection
     */
    public void clear() {
        leftSrtController.clear();
        rightSrtController.clear();
    }

    public void project() {
        leftSrtController.clearAll();
        rightSrtController.clearAll();
        log.clear();
    }

    public void merge() {
        // validate
        if (leftSrtController.srtTable.getItems().isEmpty() || rightSrtController.srtTable.getItems().isEmpty()
                || leftSrtController.srtTable.getItems().size() != rightSrtController.srtTable.getItems().size()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText(resources.getString("dialog.merge.header.could.not.merge"));
            alert.setContentText(resources.getString("dialog.merge.info.both.size.do.not.match.each.other"));
            alert.show();
            return;
        }

        // open merge config choice dialog
        Dialog<MergeConfig> dialog = createMergeConfigDialog();

        Optional<MergeConfig> result = dialog.showAndWait();

        result.ifPresent(mergeConfig -> {

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle(resources.getString("dialog.filechooser.save.srt.file"));
            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("SRT", "*.srt"));
            File file = fileChooser.showSaveDialog(btnMerge.getScene().getWindow());

            if (file != null) {
                Path savePath = file.toPath();
                Charset charset = mergeConfig.getCharset();
                try (BufferedWriter bufferedWriter = Files.newBufferedWriter(savePath, charset);
                     SrtWriter writer = new SrtWriter(bufferedWriter)) {

                    ObservableList<SrtRecord> left = leftSrtController.srtTable.getItems();
                    ObservableList<SrtRecord> right = rightSrtController.srtTable.getItems();

                    for (int i = 0; i < left.size(); i++) {
                        SrtRecord lsr = left.get(i);
                        SrtRecord rsr = right.get(i);

                        SrtRecord srtRecord = merge(i + 1, lsr, rsr, mergeConfig);
                        writer.write(srtRecord);
                    }
                    writer.flush();
                } catch (IOException e) {
                    System.err.format("IOException: %s%n", e);
                    log.appendText(e.getMessage() + "\n");
                }
            }

        });
    }


    public void translate(ActionEvent event) {

        // validate
        if (leftSrtController.srtTable.getItems().isEmpty() && rightSrtController.srtTable.getItems().isEmpty()
                || !leftSrtController.srtTable.getItems().isEmpty() && !rightSrtController.srtTable.getItems().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText(resources.getString("dialog.translate.header.could.not.translate"));
            alert.setContentText(resources.getString("dialog.translate.info.could.not.translate"));
            alert.show();
            return;
        }

        mbTranslate.setDisable(true);

//        MenuItem source = (MenuItem) event.getSource();

        try (Translator g = TranslatorFactory.getInstance(getSelectedProvider())) {

            String toLang = getCode(cbxLanguage.getValue());
            if (leftSrtController.srtTable.getItems().isEmpty()) {
                translate(rightSrtController, leftSrtController, toLang, g);
            } else {
                translate(leftSrtController, rightSrtController, toLang, g);
            }

        } catch (IOException e) {
            log.appendText(e.getMessage() + "\n");
//            e.printStackTrace();
        }

        mbTranslate.setDisable(false);
    }

    private void translate(SrtTableController from, SrtTableController to, String toLang, Translator g) throws IOException {
        final int max = from.srtTable.getItems().size();
        Task<Integer> task = new Task<>() {
            //            @Override
//            protected Integer call() throws Exception {
//                int idx = 0;
//                if (g.isMultiTranslateSupported()) {
//                    g.connect();
//                    int batch = 800;
//                    for (int i = 0, exCount = 0; i * batch < max; ) {
//                        if (exCount == 5) {
//                            exCount = 0;
//                            g.close();
//                            g.connect();
//                        }
//                        int fromIdx = i * batch;
//                        int toIdx = Math.min(i * batch + batch, max);
//                        List<SrtRecord> srtRecords = from.srtTable.getItems().subList(fromIdx, toIdx);
//
//                        List<String> list = srtRecords.stream().map(it -> TextUtil.normalize(it.getSub())).toList();
//
//                        List<String> translatedText = list;
//                        try {
//                            translatedText = g.translateText(list, "auto", toLang);
//                        } catch (Exception e) {
//                            log.appendText(e.getMessage() + "\n");
//                            // reconnect and try again.
//                            Thread.sleep(10000);
//
//                            exCount = 0;
//                            g.close();
//                            g.connect();
//                            continue;
//                        }
//
//                        for (int j = 0; j < srtRecords.size(); j++) {
//                            SrtRecord srtRecord = srtRecords.get(j);
//                            String text = translatedText.get(j);
//                            to.addItem(new SrtRecord(srtRecord.getId(), srtRecord.getTime(), TextUtil.autoLine(text)), srtRecord.getId());
//                        }
//
//                        idx = toIdx;
//
//                        updateProgress(idx, max);
//                        Thread.sleep(100);
//                        i ++;
//                        exCount ++;
//                    }
//                } else {
//                    for (SrtRecord srtRecord : from.srtTable.getItems()) {
//                        if (isCancelled()) break;
//
//                        if (idx % 100 == 0) {
//                            g.close();
//                            g.connect();
//                        }
//
//                        String toSub;
//                        try {
//                            toSub = g.translateText(TextUtil.normalize(srtRecord.getSub()), "auto", toLang);
//                        } catch (Exception e) {
//                            log.appendText(e.getMessage() + "\n");
//                            toSub = srtRecord.getSub();
//                        }
//                        to.addItem(new SrtRecord(srtRecord.getId(), srtRecord.getTime(), TextUtil.autoLine(toSub)), srtRecord.getId());
//
//                        updateProgress(++idx, max);
//
//                    }
//                }
//
//
//                return idx;
//            }
            @Override
            protected Integer call() throws Exception {
                int idx = 0;
                if (g.isMultiTranslateSupported()) {
                    g.connect();

                    // 使用较大的批次大小，但增加延迟
                    int batch = 200; // 使用较大的批次，但通过延迟控制频率
                    int baseDelay = 2000; // 基础延迟2秒
                    int maxRetries = 3; // 最大重试次数
                    int interBatchDelay = 1000; // 批次间延迟5秒

                    // 字符数限制
                    final int MAX_CHARS_PER_BATCH = 50000; // 每批次最多50000字符

                    int requestCountInMinute = 0;
                    long minuteStartTime = System.currentTimeMillis();

                    // 修复：使用单独的索引变量，不依赖i*batch计算
                    int currentIndex = 0;
                    int batchNumber = 0;

                    while (currentIndex < max) {
                        if (isCancelled()) break;
                        batchNumber++;

                        // 检查每分钟请求限制
                        long currentTime = System.currentTimeMillis();
                        if (currentTime - minuteStartTime >= 60000) {
                            requestCountInMinute = 0;
                            minuteStartTime = currentTime;
                        }

                        if (requestCountInMinute >= 10) { // 保守限制，每分钟最多8个批次
                            long waitTime = 60000 - (currentTime - minuteStartTime) + 2000; // 额外加2秒缓冲
                            appendLog("达到每分钟请求限制，等待 " + (waitTime/1000) + " 秒...\n");
                            Thread.sleep(waitTime);
                            requestCountInMinute = 0;
                            minuteStartTime = System.currentTimeMillis();
                        }

                        // 动态调整批次大小基于字符数
                        int actualBatch = 0;
                        int currentBatchChars = 0;
                        for (int k = currentIndex; k < max && actualBatch < batch; k++) {
                            String text = TextUtil.normalize(from.srtTable.getItems().get(k).getSub());
                            if (currentBatchChars + text.length() > MAX_CHARS_PER_BATCH) {
                                break;
                            }
                            currentBatchChars += text.length();
                            actualBatch++;
                        }

                        if (actualBatch == 0) actualBatch = 1; // 确保至少处理一条

                        int fromIdx = currentIndex;
                        int toIdx = currentIndex + actualBatch;
                        List<SrtRecord> srtRecords = from.srtTable.getItems().subList(fromIdx, toIdx);

                        List<String> list = srtRecords.stream()
                                .map(it -> TextUtil.normalize(it.getSub()))
                                .toList();

                        List<String> translatedText = null;
                        boolean success = false;
                        int retryCount = 0;

                        // 重试逻辑
                        while (retryCount < maxRetries && !success) {
                            try {
                                translatedText = g.translateText(list, "auto", toLang);
                                success = true;
                                requestCountInMinute++; // 成功请求计数

                            } catch (Exception e) {
                                retryCount++;
                                appendLog("异常类型: " + e.getClass().getSimpleName() + "\n");
                                appendLog("异常信息: " + e.getMessage() + "\n");

                                if (e.getMessage() != null && e.getMessage().contains("429")) {
                                    // 对于429错误，采用更激进的等待策略
                                    int waitTime;
                                    if (retryCount == 1) {
                                        waitTime = 10000; // 第一次遇到429等待10秒
                                    } else if (retryCount == 2) {
                                        waitTime = 30000; // 第二次等待30秒
                                    } else {
                                        waitTime = 60000; // 第三次等待60秒
                                    }

                                    appendLog("遇到请求限制错误(429)，第 " + retryCount + " 次重试，等待 " + (waitTime/1000) + " 秒\n");
                                    Thread.sleep(waitTime);

                                    // 重新连接
                                    g.close();
                                    Thread.sleep(2000);
                                    g.connect();

                                } else {
                                    // 其他错误使用标准指数退避
                                    int waitTime = baseDelay * (1 << retryCount);
                                    appendLog("翻译批次 " + batchNumber + " 失败，第 " + retryCount + " 次重试，等待 " + waitTime + " 毫秒\n");
                                    Thread.sleep(waitTime);
                                }

                                if (retryCount >= maxRetries) {
                                    appendLog("重试 " + maxRetries + " 次后仍然失败，跳过当前批次\n");
                                    // 使用原文作为后备方案
                                    translatedText = list;
                                    break;
                                }
                            }
                        }

                        // 处理翻译结果 - 确保每条记录都被处理
                        List<SrtRecord> batchToAdd = new ArrayList<>();
                        for (int j = 0; j < srtRecords.size(); j++) {
                            SrtRecord srtRecord = srtRecords.get(j);
                            String text = translatedText != null ? translatedText.get(j) : srtRecord.getSub();
                            batchToAdd.add(new SrtRecord(srtRecord.getId(), srtRecord.getTime(), TextUtil.autoLine(text)));
                        }
                        final List<SrtRecord> finalBatch = batchToAdd;
                        javafx.application.Platform.runLater(() -> {
                            for (SrtRecord record : finalBatch) {
                                to.addMasterData(record);
                            }
                            to.refreshTable();
                        });

                        // 修复：正确更新当前索引
                        currentIndex += actualBatch;
                        idx = currentIndex;
                        updateProgress(idx, max);

                        // 批次间延迟
                        if (currentIndex < max) {
                            // 根据已处理记录数动态增加延迟
                            int dynamicDelay = (idx % 1000 == 0) ? 10000: interBatchDelay;

                            appendLog("批次 " + batchNumber + " 完成 (" + actualBatch + "条)，已处理 " + idx + "/" + max + "，等待 " + (dynamicDelay/1000) + " 秒\n");
                            Thread.sleep(dynamicDelay);
                        }
                    }
                } else {
                    // 单条翻译的优化
                    int delayBetweenRequests = 2000; // 每条请求间隔2秒
                    int requestCount = 0;
                    int consecutive429Errors = 0;
                    long minuteStartTime = System.currentTimeMillis();
                    int requestsThisMinute = 0;

                    for (SrtRecord srtRecord : from.srtTable.getItems()) {
                        if (isCancelled()) break;

                        // 检查每分钟请求限制
                        long currentTime = System.currentTimeMillis();
                        if (currentTime - minuteStartTime >= 60000) {
                            requestsThisMinute = 0;
                            minuteStartTime = currentTime;
                        }

                        if (requestsThisMinute >= 25) { // 每分钟最多25条单条请求
                            long waitTime = 60000 - (currentTime - minuteStartTime) + 1000;
                            appendLog("达到每分钟单条请求限制，等待 " + (waitTime/1000) + " 秒\n");
                            Thread.sleep(waitTime);
                            requestsThisMinute = 0;
                            minuteStartTime = System.currentTimeMillis();
                        }

                        if (requestCount % 20 == 0) {
                            g.close();
                            Thread.sleep(2000);
                            g.connect();
                        }

                        String toSub;
                        try {
                            toSub = g.translateText(TextUtil.normalize(srtRecord.getSub()), "auto", toLang);
                            consecutive429Errors = 0;
                            requestsThisMinute++;
                        } catch (Exception e) {
                            if (e.getMessage() != null && e.getMessage().contains("429")) {
                                consecutive429Errors++;
                                int waitTime = Math.min(consecutive429Errors * 15000, 120000); // 最多等待2分钟
                                final int c429 = consecutive429Errors;
                                appendLog("遇到限制错误，等待 " + (waitTime/1000) + " 秒 (连续第 " + c429 + " 次)\n");
                                Thread.sleep(waitTime);
                                g.close();
                                Thread.sleep(3000);
                                g.connect();
                            }
                            final String errMsg = e.getMessage();
                            appendLog("翻译单条记录失败: " + (errMsg != null ? errMsg : e.getClass().getSimpleName()) + "，使用原文\n");
                            toSub = srtRecord.getSub();
                        }

                        final String finalToSub = toSub;
                        final SrtRecord finalSrtRecord = srtRecord;
                        javafx.application.Platform.runLater(() -> {
                            to.addMasterData(new SrtRecord(finalSrtRecord.getId(), finalSrtRecord.getTime(), TextUtil.autoLine(finalToSub)));
                        });
                        updateProgress(++idx, max);
                        requestCount++;

                        // 单条请求间延迟
                        if (idx < max) {
                            Thread.sleep(delayBetweenRequests);
                        }
                    }
                }

                final int totalProcessed = idx;
                appendLog("翻译完成！总计处理 " + totalProcessed + " 条记录\n");
                return idx;
            }
        };

        btnCancel.setOnAction(event -> {
            task.cancel();
            bar.setDisable(true);
            bar.setVisible(false);
            btnCancel.setDisable(true);
            btnCancel.setVisible(false);
        });

        bar.progressProperty().unbind();
        bar.progressProperty().bind(task.progressProperty());
        bar.setDisable(false);
        bar.setVisible(true);
        btnCancel.setDisable(false);
        btnCancel.setVisible(true);

        task.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == max || newValue == -1) {
                bar.setDisable(true);
                bar.setVisible(false);
                btnCancel.setDisable(true);
                btnCancel.setVisible(false);
            }
        });

        task.exceptionProperty().addListener((obs, oldExc, newExc) -> {
            if (newExc != null) {
                log.appendText("翻译错误: " + newExc.getMessage() + "\n");
                newExc.printStackTrace();
            }
        });

        // start translate
        new Thread(task).start();

    }

    private Dialog<MergeConfig> createMergeConfigDialog() {
        Dialog<MergeConfig> dialog = new Dialog<>();
        dialog.setTitle(resources.getString("dialog.merge.srt.file"));
        dialog.setHeaderText(resources.getString("dialog.merge.the.two.srt.file.into.one"));

        dialog.setGraphic(new ImageView(ImageUtil.createImage(this, "/icons/merge-76x76.png")));

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

        charsetGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> other.setDisable(!((RadioButton) newValue).getText().equals("Other")));


        other.onActionProperty().setValue(event -> otherCharset.setUserData(other.getValue()));

        VBox vBox = new VBox(contentText, grid);

        GridPane.setHgrow(vBox, Priority.ALWAYS);

        dialog.getDialogPane().setContent(vBox);
        dialog.getDialogPane().setPrefSize(560, 340);

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
                "Both".equals(mergeConfig.getSub()) ? lsr.getSub().replace('\n', ' ') + "\n" + rsr.getSub().replace('\n', ' ') : "Left".equals(mergeConfig.getSub()) ? lsr.getSub() : rsr.getSub()
        );
    }

    public void syncTime() {
        if (!(leftSrtController.srtTable.getItems().isEmpty() || rightSrtController.srtTable.getItems().isEmpty())) {
            long shiftTime = getShiftTime();
            if (shiftTime > 0) {
                leftSrtController.timeShift.setText("-" + shiftTime);
                rightSrtController.timeShift.setText("+" + shiftTime);
            } else {
                leftSrtController.timeShift.setText("+" + (-shiftTime));
                rightSrtController.timeShift.setText("-" + (-shiftTime));
            }
        }
    }

    private long getShiftTime() {
        SrtRecord srtRecordLeft = Objects.requireNonNullElse(leftSrtController.getSelectedItem(), leftSrtController.srtTable.getItems().getFirst());
        SrtRecord srtRecordRight = Objects.requireNonNullElse(rightSrtController.getSelectedItem(), rightSrtController.srtTable.getItems().getFirst());

        SrtTime srtTimeLeft = new SrtTime(srtRecordLeft.getTime());
        SrtTime srtTimeRight = new SrtTime(srtRecordRight.getTime());

        return srtTimeLeft.getStart() - srtTimeRight.getStart();
    }

    public void showLog(ActionEvent event) {
        ToggleButton source = (ToggleButton) event.getSource();
        log.setVisible(source.isSelected());
        log.setManaged(source.isSelected());
    }

    private String getSelectedProvider() {
        String selected = cbxProvider.getValue();
        if ("LLM (Local)".equals(selected)) {
            return "llm";
        }
        return "azure";
    }

    private void appendLog(String msg) {
        javafx.application.Platform.runLater(() -> log.appendText(msg));
    }

    public void magic() {
        if (!leftSrtController.srtTable.getItems().isEmpty()) {
            leftSrtController.magic();
        }
        if (!rightSrtController.srtTable.getItems().isEmpty()) {
            rightSrtController.magic();
        }
    }

    public void mergeLeft(ActionEvent actionEvent) {
        // validate
        if (leftSrtController.srtTable.getItems().isEmpty() || rightSrtController.srtTable.getItems().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText(resources.getString("dialog.merge.header.could.not.merge"));
            alert.setContentText(resources.getString("dialog.merge.info.can.not.merge.empty"));
            alert.show();
            return;
        }

        // merge right to left
        ObservableList<SrtRecord> right = rightSrtController.srtTable.getItems();
        for (SrtRecord srtRecord : right) {
            leftSrtController.insertItem(srtRecord);
        }
    }
}
