
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
@ManagedBean(name="PageBean")
@SessionScoped
public class PageBean implements Serializable {
    
        @ManagedProperty(value="#{LoginBean.background}")
        private String background;
        
        @ManagedProperty(value="#{LoginBean.userid}")
        private int userid;
    	
        private static final long serialVersionUID = 1L;

	private static Map<String,String> pages;

	static{
		pages = new LinkedHashMap<String,String>();
		pages.put("Sandal", "sandal.jpg"); //label, value
		pages.put("Okur Yazar Kedi", "cat.jpg");
		pages.put("Kitaplar", "books.jpg");
		pages.put("Odak", "table.jpg");
                pages.put("Plankton", "ocean.jpg");
                pages.put("Sade Kahve", "cafe.jpg");

	}

        public PageBean() {
        try {
                Context ctx = new InitialContext();
                dataSource = (DataSource) ctx.lookup("jdbc/addressbook");
            } catch (NamingException e) {
                e.printStackTrace();
            }
        }

        
	public void pageBackChanged(ValueChangeEvent e) throws SQLException{
		//assign new value to localeCode
		background = e.getNewValue().toString();
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
                        + "background=? where id=?");

                // specify the PreparedStatement's arguments

                ps.setString(1, getBackground());
                ps.setInt(2, getUserid());
                
                ps.executeUpdate(); // insert the entry

                
        } // end try
        catch (Exception e) {
           //throw new FacesException(e);
            
        } finally {
            connection.close(); // return this connection to pool
        }
    }
    
    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
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
