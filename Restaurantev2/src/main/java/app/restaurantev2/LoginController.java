package app.restaurantev2;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import app.restaurantev2.MainApp.ConexionBaseDatos;
import app.restaurantev2.MainApp.Cambiodepantallas;


public class LoginController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    @FXML
    private CheckBox rememberMe;

    private final ConexionBaseDatos db = new ConexionBaseDatos();

    @FXML
    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();

        if (email.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Por favor, completa todos los campos.");
            errorLabel.setVisible(true);
            return;
        }

        // Verifica en base de datos
        String rol = db.obtenerRolSiCredencialesValidas(email, password);

        if (rol != null) {
            errorLabel.setVisible(false); // Oculta el error

            System.out.println("Inicio de sesión exitoso. Rol: " + rol);

            if (rol.equalsIgnoreCase("ADMIN")) {
                // Aquí rediriges a la vista de administrador
                System.out.println("Redirigiendo a vista de ADMIN...");
                Cambiodepantallas cambioAdmin = new Cambiodepantallas();
                cambioAdmin.AdminMenu(); // Llama al método para abrir la ventana de administrador

            } else if (rol.equalsIgnoreCase("EMPLEADO")) {
                // Rediriges a la vista de empleado
                System.out.println("Redirigiendo a vista de EMPLEADO...");
            } else {
                System.out.println("Rol desconocido.");
            }

            // Si rememberMe está seleccionado, aquí puedes guardar datos en archivo o preferencias

        } else {
            // Usuario no válido o inactivo
            errorLabel.setText("Correo o contraseña inválidos o usuario inactivo.");
            errorLabel.setVisible(true);
        }
    }

}
