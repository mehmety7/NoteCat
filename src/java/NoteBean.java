
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import javax.faces.FacesException;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.sql.rowset.CachedRowSet;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author mehmet
 */
@ManagedBean(name="NoteBean")
@SessionScoped
public class NoteBean implements Serializable
{
    
    private static final long serialVersionUID = 124578757L;
    
    private int noteid;
    @ManagedProperty(value = "#{LoginBean.userid}")
    private int userid;
        
    private String title;
    private String content;
    private Date save_date;
    private boolean deleted;
    private boolean marked;
    
    CachedRowSet rowSet=null;
    private DataSource dataSource;
    private ResultSet result;

    public NoteBean() {
        try {
            Context ctx = new InitialContext();
            dataSource = (DataSource) ctx.lookup("jdbc/addressbook");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }
    
    public String notEkle() throws SQLException
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
            
                PreparedStatement addEntry = connection.prepareStatement("insert into app.notes "
                        + "(id, n_title, n_content) "
                        + "values (?,?,?)");

                // specify the PreparedStatement's arguments

                addEntry.setInt(1, getUserid());
                addEntry.setString(2, getTitle());
                addEntry.setString(3, getContent());


                addEntry.executeUpdate(); // insert the entry
                title = "";
                content = "";

                return "profil";
                
        } // end try
        catch (Exception e) {
           throw new FacesException(e);
           //return "not";

        } finally {
            connection.close(); // return this connection to pool
        }
    }
    
    public ResultSet notListele() throws SQLException
    {
         if ( dataSource == null )
            throw new SQLException( "Unable to obtain DataSource" );

         // obtain a connection from the connection pool
         Connection connection = dataSource.getConnection();

         // check whether connection was successful
         if ( connection == null )
            throw new SQLException( "Unable to connect to DataSource" );

         try
         {
            // create a PreparedStatement to insert a new address book entry
            PreparedStatement ps =
            connection.prepareStatement( "select notes.n_id, notes.n_title, notes.n_save, marked FROM NOTES "
                    + "where notes.id=? and deleted=false ORDER BY notes.n_save DESC" );
            
            ps.setInt(1,getUserid());
            
            rowSet = new com.sun.rowset.CachedRowSetImpl();
            rowSet.populate( ps.executeQuery() );
            return rowSet;
         } // end try
         finally
         {
            connection.close(); // return this connection to pool
         } // end finally
    }
    
    public String notGoruntule() throws SQLException
    {
        if ( dataSource == null )
            throw new SQLException( "Unable to obtain DataSource" );

         // obtain a connection from the connection pool
         Connection connection = dataSource.getConnection();

         // check whether connection was successful
         if ( connection == null )
            throw new SQLException( "Unable to connect to DataSource" );

         try
         {
         // create a PreparedStatement to insert a new address book entry
         PreparedStatement ps =
         connection.prepareStatement( "select n_title, n_content, n_save, marked FROM NOTES "
                 + "where id=? and n_id=? and deleted=?" );
         ps.setInt( 1, getUserid());
         ps.setInt( 2, getNoteid());
         ps.setBoolean(3, false);
         
         result = ps.executeQuery();
         while(result.next()){
            title = result.getString("n_title");
            content = result.getString("n_content");
            save_date = result.getDate("n_save");
            marked = result.getBoolean("marked");
        }
         return "not.xhtml";
         } // end try
         finally
         {
         connection.close(); // return this connection to pool
         } // end finally
    }
    
    public String notGuncelle() throws SQLException
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
            
                PreparedStatement ps = connection.prepareStatement("update NOTES set "
                        + "n_title=?, n_content=? , n_save=DEFAULT where id=? and n_id=?");

                // specify the PreparedStatement's arguments

                ps.setString(1, getTitle());
                ps.setString(2, getContent());
                ps.setInt(3, getUserid());
                ps.setInt(4, getNoteid());


                ps.executeUpdate(); // insert the entry

                return "profil";
                
        } // end try
        catch (Exception e) {
           //throw new FacesException(e);
           return "profil";

        } finally {
            connection.close(); // return this connection to pool
        }
    }
    
    public String notKaldır() throws SQLException
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
            
                PreparedStatement ps = connection.prepareStatement("update NOTES set "
                        + "deleted=true where id=? and n_id=?");

                // specify the PreparedStatement's arguments

                ps.setInt(1, getUserid());
                ps.setInt(2, getNoteid());


                ps.executeUpdate(); // insert the entry

                return "notlar";
                
        } // end try
        catch (Exception e) {
           //throw new FacesException(e);
           return "notlar";

        } finally {
            connection.close(); // return this connection to pool
        }
    }
    
    public ResultSet copKutusuListele() throws SQLException
    {
         if ( dataSource == null )
            throw new SQLException( "Unable to obtain DataSource" );

         // obtain a connection from the connection pool
         Connection connection = dataSource.getConnection();

         // check whether connection was successful
         if ( connection == null )
            throw new SQLException( "Unable to connect to DataSource" );

         try
         {
            // create a PreparedStatement to insert a new address book entry
            PreparedStatement ps =
            connection.prepareStatement( "select notes.n_id, notes.n_title, notes.n_save, marked FROM NOTES "
                    + "where notes.id=? and deleted=true ORDER BY notes.n_save DESC" );
            
            ps.setInt(1,getUserid());
            
            rowSet = new com.sun.rowset.CachedRowSetImpl();
            rowSet.populate( ps.executeQuery() );
            return rowSet;
         } // end try
         finally
         {
            connection.close(); // return this connection to pool
         } // end finally
    }
    
    public String notKurtar() throws SQLException
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
            
                PreparedStatement ps = connection.prepareStatement("update NOTES set "
                        + "deleted=false where id=? and n_id=?");

                // specify the PreparedStatement's arguments

                ps.setInt(1, getUserid());
                ps.setInt(2, getNoteid());


                ps.executeUpdate(); // insert the entry

                return "copkutusu";
                
        } // end try
        catch (Exception e) {
           //throw new FacesException(e);
           return "copkutusu";

        } finally {
            connection.close(); // return this connection to pool
        }
    }
    
    public String notSil() throws SQLException
    {
        if ( dataSource == null ){ 
            throw new SQLException( "Unable to obtain DataSource" );
       }

       // obtain a connection from the connection pool
       Connection connection = dataSource.getConnection();

       // check whether connection was successful
       if ( connection == null )
       throw new SQLException( "Unable to connect to DataSource" );

       try
       {
       // create a PreparedStatement to insert a new address book entry
       PreparedStatement ps =
       connection.prepareStatement( "delete from notes where n_id=?");

       // specify the PreparedStatement's arguments
       ps.setInt( 1, getNoteid());
       ps.executeUpdate(); // insert the entry
       return "copkutusu"; // go back to index.xhtml page
       } // end try
       finally
       {
       connection.close(); // return this connection to pool
       } // end finally
    }
    
    public String tumNotuSil() throws SQLException
    {
        if ( dataSource == null ){ 
            throw new SQLException( "Unable to obtain DataSource" );
       }

       // obtain a connection from the connection pool
       Connection connection = dataSource.getConnection();

       // check whether connection was successful
       if ( connection == null )
       throw new SQLException( "Unable to connect to DataSource" );

       try
       {
        PreparedStatement ps =
        connection.prepareStatement( "delete from notes where id=? and deleted=true");

        // specify the PreparedStatement's arguments
        ps.setInt( 1, getUserid());
        ps.executeUpdate(); // delete the entry
        return "copkutusu"; // go back to profil.xhtml page
       } // end try
       finally
       {
       connection.close(); // return this connection to pool
       } // end finally
    }
    
        public ResultSet onemliListele() throws SQLException
    {
         if ( dataSource == null )
            throw new SQLException( "Unable to obtain DataSource" );

         // obtain a connection from the connection pool
         Connection connection = dataSource.getConnection();

         // check whether connection was successful
         if ( connection == null )
            throw new SQLException( "Unable to connect to DataSource" );

         try
         {
            // create a PreparedStatement to insert a new address book entry
            PreparedStatement ps =
            connection.prepareStatement( "select notes.n_id, notes.n_title, notes.n_save, marked FROM NOTES "
                    + "where notes.id=? and deleted=false and marked=true ORDER BY notes.n_save DESC" );
            
            ps.setInt(1,getUserid());
            
            rowSet = new com.sun.rowset.CachedRowSetImpl();
            rowSet.populate( ps.executeQuery() );
            return rowSet;
         } // end try
         finally
         {
            connection.close(); // return this connection to pool
         } // end finally
    }
    
    public java.sql.Date sqlDate(String tarih) throws ParseException{
       SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MM-yyyy");
       java.util.Date date = sdf1.parse(tarih);
       java.sql.Date sqlDate = new java.sql.Date(date.getTime());// getTime() long tipli tarih verisi döner
       return sqlDate;
    }
    
    public String stringDate(Date sqlDate) throws ParseException{
       Date date = sqlDate;
       DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
       String stDate = df.format(date);
       return stDate;
    }
    
    public int getNoteid() {
        return noteid;
    }

    public void setNoteid(int noteid) {
        this.noteid = noteid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getSave_date() {
        return save_date;
    }

    public void setSave_date(Date save_date) {
        this.save_date = save_date;
    }
    
    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public boolean isMarked() {
        return marked;
    }

    public void setMarked(boolean marked) {
        this.marked = marked;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }
   
    
}
