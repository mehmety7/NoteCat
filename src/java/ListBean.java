
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
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
@ManagedBean(name="ListBean")
@SessionScoped
public class ListBean implements Serializable
{
    
    private static final long serialVersionUID = 124418757L;
    
    private int listid;
    private int elmid;
    @ManagedProperty(value = "#{LoginBean.userid}")
    private int userid;
        
    private String title;
    private Date save_date;
    private boolean deleted;
    private boolean list_finished;
    
    List<String> elements = new ArrayList<>();
    List<Integer> lists_id = new ArrayList<>();
    private String newElm; 
    private boolean elm_finished;
    
    CachedRowSet rowSet=null;
    private DataSource dataSource;
    private ResultSet result;

    public ListBean() {
        try {
            Context ctx = new InitialContext();
            dataSource = (DataSource) ctx.lookup("jdbc/addressbook");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }
    
    public void elemanEkle()
    {
        elements.add(newElm);
        newElm = null;
    }
    
    public void elemanKaydet(int listid) throws SQLException
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
            for(int i = 0 ; i < elements.size() ; i++){
                
                PreparedStatement addEntry = connection.prepareStatement("insert into app.elements "
                        + "(l_id, content) "
                        + "values (?,?)");

                // specify the PreparedStatement's arguments

                addEntry.setInt(1, getListid());
                addEntry.setString(2, getElements().get(i));


                addEntry.executeUpdate(); // insert the entry
              
                
            }
            elements.clear();
                
        } // end try
        catch (Exception e) {
           throw new FacesException(e);
           //return "not";

        } finally {
            connection.close(); // return this connection to pool
        }
    }
    
    public int listIdBul() throws SQLException
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
            
            PreparedStatement ps =
            connection.prepareStatement( "select l_id FROM lists "
                    + "where id=? and deleted=false "
                    + "ORDER BY l_id DESC fetch first 1 rows only");

                // specify the PreparedStatement's arguments

                ps.setInt(1, getUserid());
                result = ps.executeQuery();
                if(result != null){
                    while(result.next()){
                        listid = result.getInt("l_id");
                    }
                }
                
                return listid;
                
        } // end try
        catch (Exception e) {
           throw new FacesException(e);
           //return "not";

        } finally {
            connection.close(); // return this connection to pool
        }
    }
    
    public String listeEkle() throws SQLException
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
            
                PreparedStatement addEntry = connection.prepareStatement("insert into app.lists "
                        + "(id, l_title) "
                        + "values (?,?)");

                // specify the PreparedStatement's arguments

                addEntry.setInt(1, getUserid());
                addEntry.setString(2, getTitle());


                addEntry.executeUpdate(); // insert the entry
                title = "";
                listid = listIdBul();
                elemanKaydet(listid);
                
                return "profil";
                
        } // end try
        catch (Exception e) {
           throw new FacesException(e);
           //return "not";

        } finally {
            connection.close(); // return this connection to pool
        }
    }
    
    public String listeGuncelle() throws SQLException
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
            
                PreparedStatement ps = connection.prepareStatement("update app.lists "
                        + "set l_title=?, l_save=DEFAULT where l_id=? ");

                // specify the PreparedStatement's arguments

                ps.setString(1, getTitle());
                ps.setInt(2, getListid());
                


                ps.executeUpdate(); // insert the entry
                title = "";
                elemanKaydet(getListid());
                return "profil";
                
        } // end try
        catch (Exception e) {
           throw new FacesException(e);
           //return "not";

        } finally {
            connection.close(); // return this connection to pool
        }
    }
    
    public ResultSet listeListele() throws SQLException
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
            connection.prepareStatement( "select * FROM lists "
                    + "where id=? and deleted=false ORDER BY l_save DESC" );
            
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
    
    public String listeGoruntule() throws SQLException
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
            connection.prepareStatement( "select l_save,l_title FROM lists "
                    + "where l_id=?" );
            
            ps.setInt(1,getListid());
            
            result = ps.executeQuery();
            while(result.next()){
               title = result.getString("l_title");
               save_date = result.getDate("l_save");
            }
            elemanGetir();
            return "liste";
         } // end try
         finally
         {
            connection.close(); // return this connection to pool
         } // end finally
    }
        
    public ResultSet elemanGetir() throws SQLException
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
            connection.prepareStatement( "select * FROM elements "
                    + "where l_id=? ORDER BY elm_id ASC" );
            
            ps.setInt(1,getListid());
            
            result = ps.executeQuery();
            rowSet = new com.sun.rowset.CachedRowSetImpl();
            rowSet.populate( ps.executeQuery() );
            return rowSet;
         }
         finally
         {
            connection.close(); // return this connection to pool
         } // end finally
    }
     
    public String elemanSil() throws SQLException
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
                
                PreparedStatement ps = connection.prepareStatement("delete from elements "
                        + "where elm_id=? ");

                // specify the PreparedStatement's arguments

                ps.setInt(1, getElmid());


                ps.executeUpdate(); // delete the entry
                return"liste";
                               
        } // end try
        catch (Exception e) {
           throw new FacesException(e);
           //return "not";

        } finally {
            connection.close(); // return this connection to pool
        }
    }
        
    public String elemanSayisi(int lid) throws SQLException
    {
        int elmNumber = 0;
        if (dataSource == null) {
            throw new SQLException("Unable to obtain DataSource");
        }

        Connection connection = dataSource.getConnection();
        if (connection == null) {
            throw new SQLException("Unable to connect to DataSource");
        }

        try {
            // create a PreparedStatement to insert a new address book entry
            
            PreparedStatement ps =
            connection.prepareStatement( "select count(elm_id) AS count FROM elements "
                    + "where l_id=? ");

                // specify the PreparedStatement's arguments

                ps.setInt(1, lid);
                result = ps.executeQuery();
                if(result != null){
                    while(result.next()){
                         elmNumber = result.getInt("count");
                    }
                }
                
                return String.valueOf(elmNumber);
                
        } // end try
        catch (Exception e) {
           throw new FacesException(e);
           //return "not";

        } finally {
            connection.close(); // return this connection to pool
        }
    }
    
    public String listeKaldır() throws SQLException
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
            
                PreparedStatement ps = connection.prepareStatement("update app.lists "
                        + "set deleted=true where l_id=? ");

                // specify the PreparedStatement's arguments

                ps.setInt(1, getListid());
                


                ps.executeUpdate(); // insert the entry
                                
                return "refresh";
                
        } // end try
        catch (Exception e) {
           throw new FacesException(e);
           //return "not";

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
            connection.prepareStatement( "select * FROM lists "
                    + "where id=? and deleted=true ORDER BY l_save DESC" );
            
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
    
        public String listeKurtar() throws SQLException
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
            
                PreparedStatement ps = connection.prepareStatement("update app.lists "
                        + "set deleted=false where l_id=? ");

                // specify the PreparedStatement's arguments

                ps.setInt(1, getListid());
                


                ps.executeUpdate(); // insert the entry
                                
                return "refresh";
                
        } // end try
        catch (Exception e) {
           throw new FacesException(e);
           //return "not";

        } finally {
            connection.close(); // return this connection to pool
        }
    }
    
    public String listeSil() throws SQLException
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
                
                PreparedStatement ps = connection.prepareStatement("delete from app.elements "
                        + "where l_id=? ");

                // specify the PreparedStatement's arguments

                ps.setInt(1, getListid());


                ps.executeUpdate(); // delete the record

                
                ps = connection.prepareStatement("delete from app.lists "
                        + "where id=? ");

                // specify the PreparedStatement's arguments

                ps.setInt(1, getListid());
                
                ps.executeUpdate(); // insert the entry
                
                return "refresh";
                               
        } // end try
        catch (Exception e) {
           throw new FacesException(e);
           //return "not";

        } finally {
            connection.close(); // return this connection to pool
        }
    }
        
    public String tumListeleriSil() throws SQLException
    {
        ekleTumListeid();
        if (dataSource == null) {
            throw new SQLException("Unable to obtain DataSource");
        }

        Connection connection = dataSource.getConnection();
        if (connection == null) {
            throw new SQLException("Unable to connect to DataSource");
        }
        
        try {
            // create a PreparedStatement to insert a new address book entry
            PreparedStatement ps;
            for(int i = 0; i < lists_id.size(); i++){    
            
                ps = connection.prepareStatement("delete from app.elements where l_id = ?");
                
                ps.setInt(1, lists_id.get(i));                                   
                // specify the PreparedStatement's arguments

                ps.executeUpdate(); // delete the record
            }

                
                ps = connection.prepareStatement("delete from app.lists where id=? and deleted=true");
                
                ps.setInt(1,getUserid()); // specify the PreparedStatement's arguments
                
                ps.executeUpdate(); // insert the entry
                
                return "copkutusu";
                               
        } // end try
        catch (Exception e) {
           throw new FacesException(e);
           //return "not";

        } finally {
            connection.close(); // return this connection to pool
        }
    }   
    
    public void ekleTumListeid() throws SQLException
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
            
            PreparedStatement ps =
            connection.prepareStatement( "select l_id FROM lists "
                    + "where id=? and deleted=true ");

                // specify the PreparedStatement's arguments

                ps.setInt(1, getUserid());
                
                result = ps.executeQuery();
                if(result != null){
                    while(result.next()){
                        lists_id.add(result.getInt("l_id"));
                    }
                }
                
                
        } // end try
        catch (Exception e) {
           throw new FacesException(e);
           //return "not";

        } finally {
            connection.close(); // return this connection to pool
        }
    }
    
    
        
        
    public String stringDate(Date sqlDate) throws ParseException{
       Date date = sqlDate;
       DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
       String stDate = df.format(date);
       return stDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public int getListid() {
        return listid;
    }

    public void setListid(int listid) {
        this.listid = listid;
    }

    public int getElmid() {
        return elmid;
    }

    public void setElmid(int elmid) {
        this.elmid = elmid;
    }

    public boolean isElm_finished() {
        return elm_finished;
    }

    public void setElm_finished(boolean elm_finished) {
        this.elm_finished = elm_finished;
    }

    public boolean isList_finished() {
        return list_finished;
    }

    public void setList_finished(boolean list_finished) {
        this.list_finished = list_finished;
    }

    public List<String> getElements() {
        return elements;
    }

    public void setElements(List<String> elements) {
        this.elements = elements;
    }

    public String getNewElm() {
        return newElm;
    }

    public void setNewElm(String newElm) {
        this.newElm = newElm;
    }
   
    
    
}
