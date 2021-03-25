
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
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
@ManagedBean(name="RemBean")
@SessionScoped
public class RemBean implements Serializable
{
    
    private static final long serialVersionUID = 124575557L;
    
    private int remid;
    @ManagedProperty(value = "#{LoginBean.userid}")
    private int userid;
        
    private String title;
    private String content;
    private Date save_date;
    private String goal_date;
    private boolean deleted;
    
    private long difference;
    private String alert = "Yaklaşan hatırlatmanız bulunmamaktadır.";
    private Date goal;
    
    CachedRowSet rowSet=null;
    private DataSource dataSource;
    private ResultSet result;

    public RemBean() {
        try {
            Context ctx = new InitialContext();
            dataSource = (DataSource) ctx.lookup("jdbc/addressbook");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }
    
    public String remEkle() throws SQLException
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
            
                PreparedStatement addEntry = connection.prepareStatement("insert into app.remainders "
                        + "(id, r_title, r_content, r_goal) "
                        + "values (?,?,?,?)");

                // specify the PreparedStatement's arguments

                addEntry.setInt(1, getUserid());
                addEntry.setString(2, getTitle());
                addEntry.setString(3, getContent());
                addEntry.setDate(4, sqlDate(getGoal_date()));


                addEntry.executeUpdate(); // insert the entry
                title="";
                content="";

                return "profil";
                
        } // end try
        catch (Exception e) {
           throw new FacesException(e);
           //return "not";

        } finally {
            connection.close(); // return this connection to pool
        }
    }
    
    public ResultSet remListele() throws SQLException
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
            connection.prepareStatement( "select * FROM remainders "
                    + "where id=? and deleted=false ORDER BY r_goal DESC" );
            
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
    
    public String remGoruntule() throws SQLException
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
         connection.prepareStatement( "select r_id,r_title,r_content,r_save,r_goal,deleted FROM remainders "
                 + "where id=? and r_id=? and deleted=?" );
         ps.setInt( 1, getUserid());
         ps.setInt( 2, getRemid());
         ps.setBoolean(3, false);
         
         result = ps.executeQuery();
         while(result.next()){
            remid = result.getInt("r_id");
            title = result.getString("r_title");
            content = result.getString("r_content");
            save_date = result.getDate("r_save");
            goal_date = result.getString("r_goal");
            goal = result.getDate("r_goal");
        }
         return "hatırlatıcı.xhtml";
         }catch (Exception e) {
             return "hatırlatıcılar";
        } // end try
         finally
         {
         connection.close(); // return this connection to pool
         } // end finally
    }
    
    public String remGuncelle() throws SQLException
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
            
                PreparedStatement ps = connection.prepareStatement("update remainders set "
                        + "r_title=?, r_content=? , r_save=DEFAULT, r_goal=? where id=? and r_id=?");

                // specify the PreparedStatement's arguments

                ps.setString(1, getTitle());
                ps.setString(2, getContent());
                ps.setDate(3, sqlDate(getGoal_date()));
                ps.setInt(3, getUserid());
                ps.setInt(4, getRemid());


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
    
    public String remKaldır() throws SQLException
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
            
                PreparedStatement ps = connection.prepareStatement("update remainders set "
                        + "deleted=true where id=? and r_id=?");

                // specify the PreparedStatement's arguments

                ps.setInt(1, getUserid());
                ps.setInt(2, getRemid());


                ps.executeUpdate(); // insert the entry

                return "hatırlatıcılar";
                
        } // end try
        catch (Exception e) {
           //throw new FacesException(e);
           return "hatırlatıcılar";

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
            connection.prepareStatement( "select * FROM remainders "
                    + "where id=? and deleted=true ORDER BY r_goal DESC" );
            
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
    
    public String remKurtar() throws SQLException
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
            
                PreparedStatement ps = connection.prepareStatement("update remainders set "
                        + "deleted=false where id=? and r_id=?");

                // specify the PreparedStatement's arguments

                ps.setInt(1, getUserid());
                ps.setInt(2, getRemid());


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
    
    public String remSil() throws SQLException
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
       connection.prepareStatement( "delete from remainders where r_id=?");

       // specify the PreparedStatement's arguments
       ps.setInt( 1, getRemid());
       ps.executeUpdate(); // insert the entry
       return "copkutusu"; // go back to index.xhtml page
       } // end try
       finally
       {
       connection.close(); // return this connection to pool
       } // end finally
    }
    
    public String tumRemiSil() throws SQLException
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
        connection.prepareStatement( "delete from remainders where id=? and deleted=true");

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
    
    public String kalanGunSayisi(String goal, String save)
    {
        String[] goals, saves;
        int goalDays, saveDays, diffDays;
        String diff;
        goals = goal.split("/");
        saves = save.split("/");
        goalDays = Integer.parseInt(goals[0]) + Integer.parseInt(goals[1])*30 + Integer.parseInt(goals[2])*365;
        saveDays = Integer.parseInt(saves[0]) + Integer.parseInt(saves[1])*30 + Integer.parseInt(saves[2])*365;
        diffDays = goalDays - saveDays;
        diff = String.valueOf(diffDays);
        return diff;
        
    }
    
    public String alertRem() throws SQLException
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
            connection.prepareStatement( "select * FROM remainders "
                    + "where id=? and deleted=false "
                    + "ORDER BY r_goal ASC fetch first 1 rows only");
            
            ps.setInt(1,getUserid());
            result = ps.executeQuery();
                while(result.next()){
                    title = result.getString("r_title");
                    goal = result.getDate("r_goal");
                    save_date = result.getDate("r_save");
                    alert = "Başlık : " + title + "\n" + "Kalan gün sayısı : ";
                    alert = alert + kalanGunSayisi(stringDate(goal),now()); 
                }
               
            
            return alert;
         } // end try
        catch (SQLException | ParseException e) {
            return alert;
        }       
         finally
         {
            connection.close(); // return this connection to pool
         } // end finally
         
    }
    
    public static final String DATE_FORMAT_NOW = "dd/MM/yyyy";

    public static String now() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
        return sdf.format(cal.getTime());
    }
    
    public java.sql.Date sqlDate(String tarih) throws ParseException{
       SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
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
    
    public int getRemid() {
        return remid;
    }

    public void setRemid(int remid) {
        this.remid = remid;
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

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public String getGoal_date() {
        return goal_date;
    }

    public void setGoal_date(String goal_date) {
        this.goal_date = goal_date;
    }

    public long getDifference() {
        return difference;
    }

    public void setDifference(long difference) {
        this.difference = difference;
    }

    public String getAlert() {
        return alert;
    }

    public void setAlert(String alert) {
        this.alert = alert;
    }

    public Date getGoal() {
        return goal;
    }

    public void setGoal(Date goal) {
        this.goal = goal;
    }
   
    
    
}
