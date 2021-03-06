package com.tourio.controllers;

import com.tourio.dao.TourDAO;
import com.tourio.dao.TourTypeDAO;
import com.tourio.models.Tour;
import com.tourio.models.TourType;
import com.tourio.utils.AlertUtils;
import com.tourio.utils.StageBuilder;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class TourTypeTableController extends BaseTableController<TourType> {
    @FXML
    private TableView<TourType> table;

    @FXML
    private TableColumn<TourType, String> tourTypeNameColumn;

    public void onCreateClick(ActionEvent event) throws IOException {
        TourTypeFormController controller = new TourTypeFormController();
        controller.tourTypeTableController = this;

        new StageBuilder("tour_type_form", controller, "Thêm loại tour")
                .setModalOwner(event)
                .setDimensionsAuto()
                .build()
                .showAndWait();
    }

    public void onEditClick(ActionEvent event) throws IOException {
        TourType tourType = table.getSelectionModel().getSelectedItem();

        if (tourType == null) {
            AlertUtils.showWarning("Hãy chọn loại tour cần sửa");
            return;
        }

        TourTypeFormController controller = new TourTypeFormController();
        controller.tourTypeTableController = this;
        controller.tourType = tourType;

        new StageBuilder("tour_type_form", controller, "Sửa loại tour")
                .setModalOwner(event)
                .setDimensionsAuto()
                .build()
                .showAndWait();
    }

    public void onDeleteClick(ActionEvent event) {
        TourType tourType = table.getSelectionModel().getSelectedItem();

        if (tourType == null) {
            AlertUtils.showWarning("Hãy chọn loại tour cần xóa");
            return;
        }

        List<Tour> tours = TourDAO.getAll();
        long index = tours.stream().filter(p -> p.getTourType().getId().equals(tourType.getId())).count();

        if (index != 0) {
            AlertUtils.showWarning("Không thể xóa. Đã có tour chọn loại tour này");
            return;
        }

        Alert alert = AlertUtils.alert(Alert.AlertType.CONFIRMATION, "Chắc chắn xóa");
        Optional<ButtonType> option = alert.showAndWait();

        if (option.get() == ButtonType.OK) {
            TourTypeDAO.delete(tourType);
            loadData();
            return;
        }
        if (option.get() == ButtonType.CANCEL) {
            return;
        }
    }

    public void initTable() {
        // TourType name column render
        tourTypeNameColumn.setCellValueFactory(data -> {
            SimpleStringProperty property = new SimpleStringProperty();
            property.setValue(data.getValue().getName());
            return property;
        });

        // Bind table with observableList observable list
        table.setItems(observableList);
    }

    public void loadData() {
        List<TourType> tourTypes = TourTypeDAO.getAll();

        // Add tourTypes -> arrList of BaseTableController
        arrList.clear();
        arrList.addAll(tourTypes);

        // Get all observableList and set to tourType observable list
        observableList.setAll(tourTypes);
        table.refresh();
    }
}