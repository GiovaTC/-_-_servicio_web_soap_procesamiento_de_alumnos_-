package util;

import java.sql.*;

public class DBUtil {

    private static final String URL = "jdbc:oracle:thin:@//localhost:1521/orcl";
    private static final String USER = "system";
    private static final String PASS = "Tapiero123";

    public static void guardar(String envio, String respuesta) {
        try (Connection con = DriverManager.getConnection(URL, USER, PASS)) {
            String sql = "INSERT INTO MENSAJES_HEX (MENSAJE_ENVIO, MENSAJE_RESPUESTA) VALUES (?, ?)";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, envio);
            ps.setString(2, respuesta);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
