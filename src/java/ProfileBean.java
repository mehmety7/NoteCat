
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.event.ValueChangeEvent;
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
@ManagedBean(name="ProfileBean")
@SessionScoped
public class ProfileBean implements Serializable {
    
        @ManagedProperty(value="#{LoginBean.profilback}")
        private String profilback;
        
        @ManagedProperty(value="#{LoginBean.userid}")
        private int userid;
    	
        private static final long serialVersionUID = 123L;

	private static Map<String,String> pages;

	static{
		pages = new LinkedHashMap<String,String>();
		pages.put("Pastel", "7.PNG"); //label, value
                pages.put("Işık", "5.PNG"); //label, value
                pages.put("Geometrik", "3.PNG"); //label, value
                pages.put("Eskiz Kağıdı", "4.PNG"); //label, value
                pages.put("Kıvrım", "6.PNG"); //label, value
                pages.put("Mat", "1.PNG"); //label, value
                pages.put("Kübik", "8.PNG"); //label, value
                pages.put("Işıltı", "9.PNG"); //label, value
                pages.put("Sulu Boya", "10.PNG"); //label, value

	}

        public ProfileBean() {
        try {
                Context ctx = new InitialContext();
                dataSource = (DataSource) ctx.lookup("jdbc/addressbook");
            } catch (NamingException e) {
                e.printStackTrace();
            }
        }

        
	public void profilBackChanged(ValueChangeEvent e) throws SQLException{
		//assign new value to localeCode
		profilback = e.getNewValue().toString();
                savePage();
	}
        
    private DataSource dataSource;
    
    public void savePage() throws SQLException
    {
        if (dataSource == null) {
            throw new SQLException("Unable to obtain DataSource");
        }

        Connection connection = dataSource.getConnection();
        if (connection == null) {
            throw new SQLException("Unable to connect to DataSource");
        }

        try {
            // create a PreparedStatement to insert a new address book entry
            
                PreparedStatement ps = connection.prepareStatement("update users set "
                        + "back_profile=? where id=?");

                // specify the PreparedStatement's arguments

                ps.setString(1, getProfilback());
                ps.setInt(2, getUserid());
                
                ps.executeUpdate(); // insert the entry

                
        } // end try
        catch (Exception e) {
           //throw new FacesException(e);
            
        } finally {
            connection.close(); // return this connection to pool
        }
    }
    
    public String getProfilback() {
        return profilback;
    }

    public void setProfilback(String profilback) {
        this.profilback = profilback;
    }
    
    	public Map<String,String> getPageInMap() {
		return this.pages;
	}

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }
}
