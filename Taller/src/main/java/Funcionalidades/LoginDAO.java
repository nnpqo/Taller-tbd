
package Funcionalidades;


import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class LoginDAO {
    
    public static boolean insertarLogin(LoginModel login) {
        Connection c = DatabaseConnection.getConnection();
        String query = "INSERT INTO public.\"Login\"(usuario,contrasenia) " +
                "values ('" + login.usuario + "','" + login.contrasenia + "')";
        System.out.println(query);
        try {
            PreparedStatement pstmt;
            pstmt = c.prepareStatement(query);
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    
    public static int getPID() {
        Connection c = DatabaseConnection.getConnection();
        String query = "select pg_backend_pid()";
        int pid = -1;
        try {
            Statement stat = c.createStatement();
            ResultSet rs = stat.executeQuery(query);
            if (rs.next()) {
                pid = rs.getInt("pg_backend_pid");
                                        
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pid;
    }

    public static LoginModel getLogin(LoginModel login) {
        Connection c = DatabaseConnection.getConnection();
        String query = "SELECT * FROM public.usuario" +
                " where nombre='" + login.usuario + "'" +
                " and contrasenia='" + login.contrasenia + "'";
        LoginModel loginResponse = null;
        try {
            System.out.println(query);
            Statement stat = c.createStatement();
            ResultSet rs = stat.executeQuery(query);
            if (rs.next()) {
                loginResponse = new LoginModel(rs.getString("nombre"),
                                        rs.getString("contrasenia"),
                                        rs.getInt("id_usuario"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return loginResponse;
    }

    public static boolean insertarLoginStoreProcedures(LoginModel login) {
        Connection c = DatabaseConnection.getConnection();
        String query = "CALL insertar_login('" + login.usuario + "','" + login.contrasenia + "')";
        System.out.println(query);
        try {
            PreparedStatement pstmt;
            pstmt = c.prepareStatement(query);
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static LoginModel getLoginStoredProcedures(LoginModel login) {
        Connection c = DatabaseConnection.getConnection();
        String query = "CALL buscar_login('" + login.usuario + "','" + login.contrasenia + "')";
        LoginModel loginResponse = null;
        System.out.println(query);
        try {
            Statement stat = c.createStatement();
            ResultSet rs = stat.executeQuery(query);
            if (rs.next()) {
                loginResponse = new LoginModel(rs.getString("usuario"),
                        rs.getString("contrasenia"),
                        rs.getInt("id_usuario"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return loginResponse;
    }
    
    public static void guardarSesion(){
        DatabaseConnection.closeConnection();
        DatabaseConnection.openConnection();
        int pid = LoginDAO.getPID();
        DatabaseConnection.setPID(pid);
        Connection c = DatabaseConnection.getConnection();
        InetAddress ip = getIp();
        
        String query = "INSERT INTO public.sesion(ip,pid) " +
                "values ('" + ip.getHostAddress() + "','" + pid + "')";
        System.out.println(query);
        try {
            PreparedStatement pstmt;
            pstmt = c.prepareStatement(query);
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            
        }        
    }
    private static InetAddress getIp(){
        InetAddress ip = null;
        String hostname;
        try {
            ip = InetAddress.getLocalHost();
            hostname = ip.getHostName();
        } catch (UnknownHostException e) { 
            e.printStackTrace();
        } finally{
            return ip;
        }
    } 
    
    public static RolModel getRol(int idUsuario) {
        Connection c = DatabaseConnection.getConnection();
        String query = "select r.nombre nombreRol,ur.activo,ur.fecha_desde,ur.fecha_hasta,u.nombre nombreUsuario " + 
                        "from rol r, usuario_rol ur, usuario u " +
                        "where ? = ur.id_usuario " +
                                "and r.id_rol = ur.id_rol";
        RolModel rolResponse = null;
        try {
            PreparedStatement pstmt = c.prepareStatement(query);
            pstmt.setInt(1, idUsuario);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                rolResponse = new RolModel(rs.getString("nombrerol"),
                                        rs.getString("nombreusuario"),
                                        rs.getBoolean("activo"),
                                        rs.getDate("fecha_desde"),
                                        rs.getDate("fecha_hasta"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rolResponse;
    }

}