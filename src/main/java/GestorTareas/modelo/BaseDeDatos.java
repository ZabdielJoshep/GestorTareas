/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package GestorTareas.modelo;

/**
 * @author jesuz
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class BaseDeDatos {
    private Connection conexion;

    public BaseDeDatos() {
    }

    public void conectar() throws SQLException {
        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("No se encontró el driver H2", e);
        }

        String url = "jdbc:h2:./data/GestorTareas;MODE=MySQL;AUTO_SERVER=TRUE";

        this.conexion = DriverManager.getConnection(url, "sa", "");

        crearTablas();
    }

    private void crearTablas() throws SQLException {
        String sqlUsuario = """
            CREATE TABLE IF NOT EXISTS usuario (
                idUsuario INT AUTO_INCREMENT PRIMARY KEY,
                nombre VARCHAR(100) NOT NULL,
                correoElectronico VARCHAR(150) NOT NULL UNIQUE,
                contraseñaHash VARCHAR(255) NOT NULL
            );
        """;

        String sqlTarea = """
            CREATE TABLE IF NOT EXISTS tarea (
                idTarea INT AUTO_INCREMENT PRIMARY KEY,
                idUsuario INT NOT NULL,
                tituloTarea VARCHAR(255) NOT NULL,
                descripcionTarea VARCHAR(500) NOT NULL,
                fechaVencimiento DATETIME NOT NULL,
                asignatura VARCHAR(100) NOT NULL,
                estado VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE',
                prioridad VARCHAR(50) NOT NULL,
                FOREIGN KEY (idUsuario) REFERENCES usuario(idUsuario),
                CHECK (estado IN ('PENDIENTE','COMPLETADA'))
            );
        """;

        conexion.createStatement().execute(sqlUsuario);
        conexion.createStatement().execute(sqlTarea);
    }

    public void cerrarConexion() {
        try {
            if (conexion != null) {
                conexion.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Usuario obtenerUsuarioPorCorreo(String email) throws SQLException {
        String sql = "SELECT idUsuario, nombre, correoElectronico, contraseñaHash FROM usuario WHERE correoElectronico = ?";
        PreparedStatement stmt = conexion.prepareStatement(sql);
        stmt.setString(1, email);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            Usuario user = new Usuario(
                    rs.getInt("idUsuario"),
                    rs.getString("nombre"),
                    rs.getString("correoElectronico"),
                    rs.getString("contraseñaHash")
            );
            rs.close();
            stmt.close();
            return user;
        }
        rs.close();
        stmt.close();
        return null;
    }

    public void guardarUsuario(Usuario user) throws SQLException {
        String sql = "INSERT INTO usuario (nombre, correoElectronico, contraseñaHash) VALUES (?, ?, ?)";
        PreparedStatement stmt = conexion.prepareStatement(sql);
        stmt.setString(1, user.getNombre());
        stmt.setString(2, user.getCorreoElectronico());
        stmt.setString(3, user.getContraseña());
        stmt.executeUpdate();
        stmt.close();
    }

    public void editarUsuario(Usuario user) throws SQLException {
        String sql = "UPDATE usuario SET nombre=?, correoElectronico=?, contraseñaHash=? WHERE idUsuario=?";
        PreparedStatement stmt = conexion.prepareStatement(sql);
        stmt.setString(1, user.getNombre());
        stmt.setString(2, user.getCorreoElectronico());
        stmt.setString(3, user.getContraseña());
        stmt.setInt(4, user.getIdUsuario());
        stmt.executeUpdate();
        stmt.close();
    }

    public void guardarTarea(Tarea tarea, int idUsuario) throws SQLException {
        String sql = "INSERT INTO tarea (idUsuario, tituloTarea, descripcionTarea, fechaVencimiento, asignatura, estado, prioridad) VALUES (?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement stmt = conexion.prepareStatement(sql);
        stmt.setInt(1, idUsuario);
        stmt.setString(2, tarea.getTituloTarea());
        stmt.setString(3, tarea.getDescripcionTarea());
        stmt.setString(4, tarea.getFechaDeVencimiento().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        stmt.setString(5, tarea.getAsignatura());
        stmt.setString(6, tarea.getEstado().toString().toUpperCase());
        stmt.setString(7, tarea.getPrioridad().toString().toUpperCase());
        stmt.executeUpdate();
        stmt.close();
    }

    public void editarTarea(Tarea tarea) throws SQLException {
        String sql = "UPDATE tarea SET tituloTarea=?, descripcionTarea=?, fechaVencimiento=?, asignatura=?, estado=?, prioridad=? WHERE idTarea=?";
        PreparedStatement stmt = conexion.prepareStatement(sql);
        stmt.setString(1, tarea.getTituloTarea());
        stmt.setString(2, tarea.getDescripcionTarea());
        stmt.setString(3, tarea.getFechaDeVencimiento().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        stmt.setString(4, tarea.getAsignatura());
        stmt.setString(5, tarea.getEstado().toString().toUpperCase());
        stmt.setString(6, tarea.getPrioridad().toString().toUpperCase());
        stmt.setInt(7, tarea.getIdTarea());
        stmt.executeUpdate();
        stmt.close();
    }

    public void eliminarTarea(int idTarea) throws SQLException {
        String sql = "DELETE FROM tarea WHERE idTarea=?";
        PreparedStatement stmt = conexion.prepareStatement(sql);
        stmt.setInt(1, idTarea);
        stmt.executeUpdate();
        stmt.close();
    }

    public List<Tarea> obtenerTareasPorUsuario(int idUsuario) throws SQLException {
        List<Tarea> tareas = new ArrayList<>();
        String sql = "SELECT idTarea, tituloTarea, descripcionTarea, fechaVencimiento, asignatura, estado, prioridad FROM tarea WHERE idUsuario=?";
        PreparedStatement stmt = conexion.prepareStatement(sql);
        stmt.setInt(1, idUsuario);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            int id = rs.getInt("idTarea");
            String titulo = rs.getString("tituloTarea");
            String descripcion = rs.getString("descripcionTarea");
            LocalDateTime fecha = LocalDateTime.parse(rs.getString("fechaVencimiento"), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            String asignatura = rs.getString("asignatura");
            Tarea.Estado estado = parseEstado(rs.getString("estado"));
            Tarea.Prioridad prioridad = parsePrioridad(rs.getString("prioridad"));
            
            Tarea tarea = new Tarea(id, titulo, descripcion, fecha, asignatura, estado, prioridad);
            tareas.add(tarea);
        }
        rs.close();
        stmt.close();
        return tareas;
    }
    private Tarea.Estado parseEstado(String estado) {
        if (estado == null) return Tarea.Estado.Pendiente;
        return switch (estado.toUpperCase().trim()) {
            case "PENDIENTE" -> Tarea.Estado.Pendiente;
            case "COMPLETADA" -> Tarea.Estado.Completada;
            default -> Tarea.Estado.Pendiente;
        };
    }
    private Tarea.Prioridad parsePrioridad(String prioridad) {
        if (prioridad == null) return Tarea.Prioridad.Normal;
        return switch (prioridad.toUpperCase().trim()) {
            case "IMPORTANTE" -> Tarea.Prioridad.Importante;
            case "NORMAL" -> Tarea.Prioridad.Normal;
            default -> Tarea.Prioridad.Normal;
        };
    }
}