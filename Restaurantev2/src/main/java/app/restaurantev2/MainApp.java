package app.restaurantev2;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.*;


public class MainApp extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("Login.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1200, 800);
        stage.setTitle("Login - Restaurante");
        stage.setScene(scene);
        stage.show();

    }

    public static class ConexionBaseDatos {
        // Constantes para la configuración de la base de datos
        private static final String UBICACION_WALLET = "C:\\Users\\Diara\\IdeaProjects\\Restaurantev2\\src\\wallet";
        private static final String JDBC_URL = "jdbc:oracle:thin:@opbok408mwktam0m_high";
        private static final String USUARIO = "ADMIN";
        private static final String PASSWORD = "Maxoto2005$$$$$";

        private Connection conexion;

        public ConexionBaseDatos() {
            // Configura la ubicación del wallet al crear la instancia
            System.setProperty("oracle.net.tns_admin", UBICACION_WALLET);
        }

        public Connection obtenerConexion() throws Exception {
            if (conexion == null || conexion.isClosed()) {
                try {
                    Class.forName("oracle.jdbc.OracleDriver");
                    conexion = DriverManager.getConnection(JDBC_URL, USUARIO, PASSWORD);
                    System.out.println("Conexión establecida correctamente.");
                } catch (Exception e) {
                    System.out.println("Error al establecer la conexión.");
                    throw e;
                }
            }
            return conexion;
        }

        public void cerrarConexion() {
            try {
                if (conexion != null && !conexion.isClosed()) {
                    conexion.close();
                    System.out.println("Conexión cerrada correctamente.");
                }
            } catch (Exception e) {
                System.out.println("Error al cerrar la conexión.");
                e.printStackTrace();
            }
        }

        // Método de ejemplo para consultar usuarios
        public void consultarUsuarios() {
            try (Statement stmt = obtenerConexion().createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM USUARIOS")) {

                while (rs.next()) {
                    System.out.println("ID: " + rs.getString(1));
                    System.out.println("Nombre: " + rs.getString(2));
                    System.out.println("Correo: " + rs.getString(3));
                    System.out.println("Contraseña: " + rs.getString(4));
                    System.out.println("Rol: " + rs.getString(5));
                    System.out.println("-------------------");
                }
            } catch (Exception e) {
                System.out.println("Error al consultar usuarios.");
                e.printStackTrace();
            }
        }

        public String obtenerRolSiCredencialesValidas(String email, String password) {
            String sql = "SELECT ROL FROM USUARIOS WHERE CORREO = ? AND CONTRASENA = ? AND ESTADO = 'ACTIVO'";
            try (PreparedStatement stmt = obtenerConexion().prepareStatement(sql)) {
                stmt.setString(1, email);
                stmt.setString(2, password);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getString("ROL"); // Devuelve el rol si coincide y está activo
                    }
                }
            } catch (Exception e) {
                System.out.println("Error al validar credenciales.");
                e.printStackTrace();
            }
            return null; // No encontró usuario válido o hubo error
        }

    }

    //abrir la ventanta Admin.fxml
public static class Cambiodepantallas{

    public  void AdminMenu() {


        try {

            FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("/app/restaurantev2/Admin.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 1600, 900);
            Stage stage = new Stage();
            stage.setTitle("Admin - Restaurante");
            stage.setScene(scene);
            stage.show();

            Stage currentStage = (Stage) fxmlLoader.getNamespace().get("currentStage");
            if (currentStage != null) {
                currentStage.close();
            }



        } catch (IOException e) {
            System.out.println("Error al abrir la ventana de administrador: " + e.getMessage());
            e.printStackTrace();

        }

    }









    public static void main(String[] args) {

        try {
            if (new ConexionBaseDatos().obtenerConexion() != null) {
                System.out.println("Conexión a la base de datos configurada correctamente.");
                // Aquí puedes realizar operaciones adicionales con la base de datos si es necesario
                launch();
            } else {
                System.out.println("No se pudo configurar la conexión a la base de datos.");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }
}
}
