package app.restaurantev2;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;

public class AdminController {

    @FXML public TextField txtNumeroMesa;
    @FXML public TextField txtCapacidad;
    @FXML public TextField txtArea;
    @FXML public TableView<Mesa> tablaMesas;
    @FXML public TableColumn<Mesa, Integer> colNumero;
    @FXML public TableColumn<Mesa, Integer> colCapacidad;
    @FXML public TableColumn<Mesa, String> colArea;
    @FXML public Button btnAgregarMesa;
    @FXML public Button btnEditar;
    @FXML public Button btnEliminar;
    @FXML public Button btnActualizar;
    @FXML public Label lblTitulo;

    public MainApp.ConexionBaseDatos db;
    public ObservableList<Mesa> listaMesas;

    @FXML
    public void initialize() {
        db = new MainApp.ConexionBaseDatos();
        listaMesas = FXCollections.observableArrayList();

        colNumero.setCellValueFactory(new PropertyValueFactory<>("numeroMesa"));
        colCapacidad.setCellValueFactory(new PropertyValueFactory<>("capacidad"));
        colArea.setCellValueFactory(new PropertyValueFactory<>("area"));

        tablaMesas.setItems(listaMesas);

        cargarMesas();

        btnAgregarMesa.setOnAction(e -> agregarMesa());
        btnEditar.setOnAction(e -> editarMesa());
        btnEliminar.setOnAction(e -> eliminarMesa());
        btnActualizar.setOnAction(e -> cargarMesas());

        tablaMesas.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }

    public void cargarMesas() {
        listaMesas.clear();

        String sql = "SELECT * FROM MESAS ORDER BY NUMERO_MESA";

        try (Connection conn = db.obtenerConexion();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Mesa mesa = new Mesa(
                        rs.getInt("ID_MESA"),
                        rs.getInt("NUMERO_MESA"),
                        rs.getInt("CAPACIDAD"),
                        rs.getString("UBICACION"),
                        rs.getString("ESTADO")
                );
                listaMesas.add(mesa);
            }
            tablaMesas.setItems(listaMesas);
        } catch (Exception e) {
            mostrarAlerta("Error", "Error al cargar las mesas: " + e.getMessage());
        }
    }

    public void agregarMesa() {
        try {
            int numeroMesa = Integer.parseInt(txtNumeroMesa.getText().trim());
            int capacidad = Integer.parseInt(txtCapacidad.getText().trim());
            String area = txtArea.getText().trim();

            if (area.isEmpty()) {
                mostrarAlerta("Error", "El campo 'Area' no puede estar vacío.");
                return;
            }

            String sql = "INSERT INTO MESAS (NUMERO_MESA, CAPACIDAD, UBICACION, ESTADO) VALUES (?, ?, ?, 'DISPONIBLE')";

            try (Connection conn = db.obtenerConexion();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setInt(1, numeroMesa);
                pstmt.setInt(2, capacidad);
                pstmt.setString(3, area);

                pstmt.executeUpdate();
                limpiarCampos();
                cargarMesas();
                mostrarAlerta("Éxito", "Mesa agregada correctamente");
            }
        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "Los campos número de mesa y capacidad deben ser números");
        } catch (Exception e) {
            mostrarAlerta("Error", "Error al agregar la mesa: " + e.getMessage());
        }
    }

    public void editarMesa() {
        Mesa mesaSeleccionada = tablaMesas.getSelectionModel().getSelectedItem();
        if (mesaSeleccionada == null) {
            mostrarAlerta("Error", "Por favor, seleccione una mesa para editar");
            return;
        }

        try {
            int numeroMesa = Integer.parseInt(txtNumeroMesa.getText().trim());
            int capacidad = Integer.parseInt(txtCapacidad.getText().trim());
            String area = txtArea.getText().trim();

            String sql = "UPDATE MESAS SET NUMERO_MESA = ?, CAPACIDAD = ?, UBICACION = ? WHERE ID_MESA = ?";

            try (Connection conn = db.obtenerConexion();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setInt(1, numeroMesa);
                pstmt.setInt(2, capacidad);
                pstmt.setString(3, area);
                pstmt.setInt(4, mesaSeleccionada.getIdMesa());

                pstmt.executeUpdate();
                limpiarCampos();
                cargarMesas();
                mostrarAlerta("Éxito", "Mesa actualizada correctamente");
            }
        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "Los campos número de mesa y capacidad deben ser números");
        } catch (Exception e) {
            mostrarAlerta("Error", "Error al actualizar la mesa: " + e.getMessage());
        }
    }

    public void eliminarMesa() {
        Mesa mesaSeleccionada = tablaMesas.getSelectionModel().getSelectedItem();
        if (mesaSeleccionada == null) {
            mostrarAlerta("Error", "Por favor, seleccione una mesa para eliminar");
            return;
        }

        try {
            String sql = "DELETE FROM MESAS WHERE ID_MESA = ?";

            try (Connection conn = db.obtenerConexion();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setInt(1, mesaSeleccionada.getIdMesa());
                pstmt.executeUpdate();
                cargarMesas();
                mostrarAlerta("Éxito", "Mesa eliminada correctamente");
            }
        } catch (Exception e) {
            mostrarAlerta("Error", "Error al eliminar la mesa: " + e.getMessage());
        }
    }

    public void limpiarCampos() {
        txtNumeroMesa.clear();
        txtCapacidad.clear();
        txtArea.clear();
    }

    public void mostrarAlerta(String titulo, String contenido) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(contenido);
        alert.showAndWait();
    }

    // Clase Mesa pública y estática para que JavaFX pueda acceder correctamente
    public static class Mesa {
        private int idMesa;
        private int numeroMesa;
        private int capacidad;
        private String area;
        private String estado;

        public Mesa(int idMesa, int numeroMesa, int capacidad, String area, String estado) {
            this.idMesa = idMesa;
            this.numeroMesa = numeroMesa;
            this.capacidad = capacidad;
            this.area = area;
            this.estado = estado;
        }

        public int getIdMesa() { return idMesa; }
        public void setIdMesa(int idMesa) { this.idMesa = idMesa; }

        public int getNumeroMesa() { return numeroMesa; }
        public void setNumeroMesa(int numeroMesa) { this.numeroMesa = numeroMesa; }

        public int getCapacidad() { return capacidad; }
        public void setCapacidad(int capacidad) { this.capacidad = capacidad; }

        public String getArea() { return area; }
        public void setArea(String area) { this.area = area; }

        public String getEstado() { return estado; }
        public void setEstado(String estado) { this.estado = estado; }

        @Override
        public String toString() {
            return "Mesa{" +
                    "idMesa=" + idMesa +
                    ", numeroMesa=" + numeroMesa +
                    ", capacidad=" + capacidad +
                    ", area='" + area + '\'' +
                    ", estado='" + estado + '\'' +
                    '}';
        }
    }
}