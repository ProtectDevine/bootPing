package com.boot.ping.controller;

import com.boot.ping.dto.TaskDto;
import com.boot.ping.enums.AlertCodes;
import com.boot.ping.enums.MenuCodes;
import com.boot.ping.service.TaskService;
import com.boot.ping.utils.LocaleUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class TaskController {

    @FXML
    private TableView<TaskDto> taskTable;
    @FXML
    private TableColumn<TaskDto, String> nameColumn;
    @FXML
    private TableColumn<TaskDto, Integer> pidColumn;
    @FXML
    private TableColumn<TaskDto, String> sessionNameColumn;
    @FXML
    private TableColumn<TaskDto, Integer> sessionNumberColumn;
    @FXML
    private TableColumn<TaskDto, Long> memoryUsageColumn;
    @FXML
    private TableColumn<TaskDto, Boolean> selectedColumn;

    private final TaskService taskService = new TaskService();

    @FXML
    public void initialize() {
        // 테이블 컬럼 설정
        this.nameColumn.setText(MenuCodes.menuDisplay(MenuCodes.TASK_NAME).getMenu());
        this.pidColumn.setText(MenuCodes.menuDisplay(MenuCodes.TASK_PID).getMenu());
        this.sessionNameColumn.setText(MenuCodes.menuDisplay(MenuCodes.TASK_SESSION_NAME).getMenu());
        this.sessionNumberColumn.setText(MenuCodes.menuDisplay(MenuCodes.TASK_SESSION_NUMBER).getMenu());
        this.memoryUsageColumn.setText(MenuCodes.menuDisplay(MenuCodes.TASK_MEMORY_USAGE).getMenu());
        this.selectedColumn.setText(MenuCodes.menuDisplay(MenuCodes.TASK_SELECT).getMenu());

        this.nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        this.pidColumn.setCellValueFactory(new PropertyValueFactory<>("pid"));
        this.sessionNameColumn.setCellValueFactory(new PropertyValueFactory<>("sessionName"));
        this.sessionNumberColumn.setCellValueFactory(new PropertyValueFactory<>("sessionNumber"));
        this.memoryUsageColumn.setCellValueFactory(new PropertyValueFactory<>("memoryUsage"));

        this.selectedColumn.setCellValueFactory(cellData -> cellData.getValue().getSelected());
        this.selectedColumn.setCellFactory(CheckBoxTableCell.forTableColumn(selectedColumn));
        this.selectedColumn.setEditable(true);

        this.taskTable.setEditable(true);

        try {
            List<TaskDto> tasks = this.taskService.getTasks();
            this.taskTable.setItems(FXCollections.observableArrayList(tasks));
        } catch (IOException e) {
            System.err.println("초기 데이터 로드 실패: " + e.getMessage());
            AlertCodes.alertDisplay(AlertCodes.TASKS_GET_FAIL);
        }
    }

    @FXML
    public void terminateAllTasksAction() {
        this.taskService.terminateAllTasks(this.taskTable.getItems());
    }

    @FXML
    public void terminateSelectedTasksAction() throws IOException {

        LocaleUtil localeUtil = new LocaleUtil();

        List<TaskDto> selectedTasks = this.taskTable.getItems().stream()
                .filter(TaskDto::isSelected)
                .collect(Collectors.toList());

        // TODO 하드코딩
        if (selectedTasks.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle(
                    AlertCodes.getAlertCodes(AlertCodes.TASKS_TERMINATION_WARNING, localeUtil.getUserLocale()).getTitle()
            );
            alert.setContentText(
                    AlertCodes.getAlertCodes(AlertCodes.TASKS_TERMINATION_WARNING, localeUtil.getUserLocale()).getDescription()
            );
            alert.showAndWait();
            return;
        }

        // TODO 하드코딩
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle(
                AlertCodes.getAlertCodes(AlertCodes.TASKS_TERMINATION_CONFIRM, localeUtil.getUserLocale()).getTitle()
        );
        confirmation.setHeaderText(
                AlertCodes.getAlertCodes(AlertCodes.TASKS_TERMINATION_CONFIRM, localeUtil.getUserLocale()).getDescription()
        );

        if (confirmation.showAndWait().filter(ButtonType.OK::equals).isPresent()) {
            this.taskService.terminateTasks(selectedTasks);
            ObservableList<TaskDto> observableList = this.taskService.refreshTable(this.taskService.getTasks());
            this.taskTable.setItems(observableList);
        }
    }


}