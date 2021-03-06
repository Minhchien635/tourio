package com.tourio.controllers;

import com.tourio.dao.GroupDAO;
import com.tourio.dao.TourDAO;
import com.tourio.models.*;
import com.tourio.utils.AlertUtils;
import com.tourio.utils.DateUtils;
import com.tourio.utils.PriceFormatter;
import com.tourio.utils.StageBuilder;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.Date;
import java.util.Optional;
import java.util.ResourceBundle;

public class GroupFormController extends BaseFormController {
    @FXML
    public TableColumn<GroupCostRel, String> costTypeNameColumn, costTypeAmountColumn;
    @FXML
    public TableColumn<GroupEmployeeRel, String> employeeNameColumn, employeeJobColumn;

    @FXML
    public HBox costTypeActionButtons, customerActionButtons, employeeActionButtons;

    @FXML
    public TextField nameTextField;

    @FXML
    public ComboBox<Tour> tourComboBox;

    @FXML
    public DatePicker startDatePicker, endDatePicker;

    @FXML
    public TextArea descriptionTextArea;

    @FXML
    public TableView<GroupCostRel> costTypeTableView;

    @FXML
    public ListView<Customer> customerListView;

    @FXML
    public TableView<GroupEmployeeRel> employeeTableView;

    public GroupTableController groupTableController;

    public Group group = new Group();

    public Long group_id;

    public ObservableList<GroupCostRel> groupCostRels = FXCollections.observableArrayList();

    public ObservableList<Customer> customers = FXCollections.observableArrayList();

    public ObservableList<GroupEmployeeRel> groupEmployeeRels = FXCollections.observableArrayList();

    public ObservableList<TourPrice> tourPrices = FXCollections.observableArrayList();

    public ObservableList<Tour> tours = FXCollections.observableArrayList();

    private void initCostTypeTable() {
        // Cost type name column render
        costTypeNameColumn.setCellValueFactory(data -> {
            SimpleStringProperty property = new SimpleStringProperty();
            property.setValue(data.getValue().getCostType().getName());
            return property;
        });

        // GroupCostRel amount column render
        costTypeAmountColumn.setCellValueFactory(data -> {
            SimpleStringProperty property = new SimpleStringProperty();
            property.setValue(PriceFormatter.format(data.getValue().getAmount()));
            return property;
        });

        // Bind data
        costTypeTableView.setItems(groupCostRels);
    }

    private void initEmployeeTable() {
        // Employee name column render
        employeeNameColumn.setCellValueFactory(data -> {
            SimpleStringProperty property = new SimpleStringProperty();
            property.setValue(data.getValue().getEmployee().getName());
            return property;
        });

        // Job name column render
        employeeJobColumn.setCellValueFactory(data -> {
            SimpleStringProperty property = new SimpleStringProperty();
            property.setValue(data.getValue().getJob().getName());
            return property;
        });

        // Bind data
        employeeTableView.setItems(groupEmployeeRels);
    }

    private void initCustomerList() {
        // Render
        customerListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Customer item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                    return;
                }
                setText(item.getName());
            }
        });

        // Bind data
        customerListView.setItems(customers);
    }

    private void initTourComboBox() {
        // Render
        Callback<ListView<Tour>, ListCell<Tour>> factory = (lv) ->
                new ListCell<>() {
                    @Override
                    protected void updateItem(Tour item, boolean empty) {
                        super.updateItem(item, empty);
                        setText(empty ? "" : item.getName());
                    }
                };
        tourComboBox.setCellFactory(factory);
        tourComboBox.setButtonCell(factory.call(null));

        // Load data
        tours.setAll(TourDAO.getAll());

        // Bind data
        tourComboBox.setItems(tours);
    }

    public void onAddCostTypeClick(ActionEvent event) throws IOException {
        // Init controller
        GroupCostTypeFormController controller = new GroupCostTypeFormController();
        controller.groupFormController = this;

        // Show modal
        new StageBuilder("group_cost_type_form", controller, "Th??m lo???i ph??")
                .setModalOwner(event)
                .setDimensionsAuto()
                .build()
                .showAndWait();//costTypeComboBox
    }

    public void onDeleteCostTypeClick() {
        int index = costTypeTableView.getSelectionModel().getSelectedIndex();

        if (index == -1) {
            AlertUtils.showWarning("H??y ch???n lo???i ph?? mu???n x??a");
            return;
        }

        groupCostRels.remove(index);
    }

    public void onAddCustomerClick(ActionEvent event) throws IOException {
        // Init controller
        GroupCustomerFormController controller = new GroupCustomerFormController();
        controller.groupFormController = this;

        // Show modal
        new StageBuilder("group_customer_form", controller, "Th??m kh??ch du l???ch")
                .setModalOwner(event)
                .setDimensionsAuto()
                .build()
                .showAndWait();
    }

    public void onDeleteCustomerClick() {
        int index = customerListView.getSelectionModel().getSelectedIndex();

        if (index == -1) {
            AlertUtils.showWarning("H??y ch???n kh??ch du l???ch mu???n x??a");
            return;
        }

        customers.remove(index);
    }

    public void onAddEmployeeClick(ActionEvent event) throws IOException {
        // Init controller
        GroupEmployeeFormController controller = new GroupEmployeeFormController();
        controller.groupFormController = this;

        // Show modal
        new StageBuilder("group_employee_form", controller, "Th??m nh??n vi??n")
                .setModalOwner(event)
                .setDimensionsAuto()
                .build()
                .showAndWait();
    }

    public void onDeleteEmployeeClick() {
        int index = employeeTableView.getSelectionModel().getSelectedIndex();

        if (index == -1) {
            AlertUtils.showWarning("H??y ch???n nh??n vi??n mu???n x??a");
            return;
        }

        groupEmployeeRels.remove(index);
    }

    @Override
    public void initReadOnly() {
        nameTextField.setDisable(true);
        tourComboBox.setDisable(true);
        startDatePicker.setDisable(true);
        endDatePicker.setDisable(true);
        descriptionTextArea.setDisable(true);
        costTypeActionButtons.getChildren().clear();
        customerActionButtons.getChildren().clear();
        employeeActionButtons.getChildren().clear();
        saveButton.setManaged(false);
    }

    @Override
    public void initFormValues() {
        group = GroupDAO.get(group_id);

        nameTextField.setText(group.getName());
        tourComboBox.setValue(group.getTour());
        startDatePicker.setValue(DateUtils.parseLocalDate(group.getDateStart()));
        endDatePicker.setValue(DateUtils.parseLocalDate(group.getDateEnd()));
        descriptionTextArea.setText(group.getDescription());
        groupCostRels.setAll(group.getGroupCostRels());
        customers.setAll(group.getCustomers());
        groupEmployeeRels.setAll(group.getGroupEmployeeRels());
    }

    @Override
    public void onSaveClick(ActionEvent e) {
        String name = nameTextField.getText();
        if (name == null || name.trim().isEmpty()) {
            AlertUtils.showWarning("H??y nh???p t??n ??o??n");
            return;
        }

        Tour tour = tourComboBox.getValue();
        if (tour == null) {
            AlertUtils.showWarning("H??y ch???n tour");
            return;
        }

        LocalDate startLocalDate = startDatePicker.getValue();
        if (startLocalDate == null) {
            AlertUtils.showWarning("H??y ch???n ng??y ??i");
            return;
        }

        LocalDate endLocalDate = endDatePicker.getValue();
        if (endLocalDate == null) {
            AlertUtils.showWarning("H??y ch???n ng??y k???t th??c chuy???n ??i");
            return;
        }

        if (startLocalDate.isAfter(endLocalDate)) {
            AlertUtils.showWarning("Ng??y ??i ph???i tr?????c ng??y k???t th??c");
            return;
        }

        Date startDate = DateUtils.parseDate(startLocalDate);
        Date endDate = DateUtils.parseDate(endLocalDate);
        Optional<TourPrice> tourPrice = tour.getTourPrices()
                .stream()
                .filter(p -> startDate.after(p.getDateStart()) && endDate.before(p.getDateEnd()))
                .findFirst();

        if (tourPrice.isEmpty()) {
            AlertUtils.showWarning("Kh??ng t???n t???i gi?? tour v???i kho???ng th???i gian ???? ch???n");
            return;
        }

        String description = descriptionTextArea.getText();
        if (description == null || description.trim().isEmpty()) {
            AlertUtils.showWarning("H??y nh???p th??ng tin ??o??n");
            return;
        }

        if (groupEmployeeRels.isEmpty()) {
            AlertUtils.showWarning("H??y th??m ??t nh???t 1 nh??n vi??n");
            return;
        }

        group.setName(nameTextField.getText());
        group.setTour(tour);
        group.setTourPrice(tourPrice.get().getAmount());
        group.setCreatedAt(new Date());
        group.setDateStart(startDate);
        group.setDateEnd(endDate);
        group.setDescription(description);

        if (group.getGroupCostRels() == null) {
            group.setGroupCostRels(groupCostRels);
        } else {
            group.getGroupCostRels().clear();
            group.getGroupCostRels().addAll(groupCostRels);
        }

        if (group.getCustomers() == null) {
            group.setCustomers(customers);
        } else {
            group.getCustomers().clear();
            group.getCustomers().addAll(customers);
        }

        if (group.getGroupEmployeeRels() == null) {
            group.setGroupEmployeeRels(groupEmployeeRels);
        } else {
            group.getGroupEmployeeRels().clear();
            group.getGroupEmployeeRels().addAll(groupEmployeeRels);
        }

        GroupDAO.save(group);

        groupTableController.loadData();
        closeWindow(e);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initTourComboBox();
        initCostTypeTable();
        initCustomerList();
        initEmployeeTable();

        if (group_id != null) {
            initFormValues();
        }

        if (read_only) {
            initReadOnly();
        }
    }
}