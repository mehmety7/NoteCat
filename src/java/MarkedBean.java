
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.faces.FacesException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Administrator
 */
@ManagedBean(name="MarkedBean")
@SessionScoped
public class MarkedBean {
    
    @ManagedProperty(value="#{LoginBean.userid}")
    private int userid;
    private int noteid;
    private boolean marked;
    
    private DataSource dataSource;
    
    public MarkedBean() {
        try {
                Context ctx = new InitialContext();
                dataSource = (DataSource) ctx.lookup("jdbc/addressbook");
            } catch (NamingException e) {
                e.printStackTrace();
            }
        }
    
    public void isaretDegistir()
    {
        if(marked)
            marked = false;
        else
            marked = true;
    }
        
    public String isaretKaydet() throws SQLException
    {
        isaretDegistir();
        if (dataSource == null) {
            throw new SQLException("Unable to obtain DataSource");
        }

        Connection connection = dataSource.getConnection();
        if (connection == null) {
            throw new SQLException("Unable to connect to DataSource");
        }

        try {
            // create a PreparedStatement to insert a new address book entry
            
                PreparedStatement ps = connection.prepareStatement("update NOTES set "
                        + " marked=? where id=? and n_id=?");

                // specify the PreparedStatement's arguments

                ps.setBoolean(1, getMarked());
                ps.setInt(2, getUserid());
                ps.setInt(3, getNoteid());


                ps.executeUpdate(); // insert the entry
                return "refresh";   
                
        } // end try
        catch (Exception e) {
           return "refresh";
        } finally {
            connection.close(); // return this connection to pool
        }
    }
    
    public String star(boolean marked)
    {
        if(marked)
            return "fas fa-star";
        else
            return "fa fa-star-o";
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public int getNoteid() {
        return noteid;
    }

    public void setNoteid(int noteid) {
        this.noteid = noteid;
    }

    public boolean getMarked() {
        return marked;
    }

    public void setMarked(boolean marked) {
        this.marked = marked;
    }
    
    
}
