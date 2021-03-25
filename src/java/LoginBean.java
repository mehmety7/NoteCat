
import com.sun.rowset.CachedRowSetImpl;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;


import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.faces.FacesException;



@ManagedBean(name = "LoginBean")
@SessionScoped
public class LoginBean implements Serializable {
    
    private static final long serialVersionUID = 124571477L;
    
    private int userid;
        
    private String name;
    private String mail;
    private String password;
    private String repeat;
    private String background;
    private String profilback;
        
    private boolean loggedIn;
    
    ResultSet result = null;

    DataSource dataSource;
    private CachedRowSetImpl rowSet;
    public LoginBean() {
        try {
            Context ctx = new InitialContext();
            dataSource = (DataSource) ctx.lookup("jdbc/addressbook");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    public String doLogin() throws SQLException  {
  
    if ( dataSource == null )
        throw new SQLException( "Unable to obtain DataSource" );

    // obtain a connection from the connection pool
    Connection connection = dataSource.getConnection();

    // check whether connection was successful
    if ( connection == null )
    throw new SQLException( "Unable to connect to DataSource" );

    try
    {
        String sql = "Select * from USERS where MAIL=? AND PASSWORD=?";
        PreparedStatement   ps = connection.prepareStatement(sql);
        ps.setString(1, getMail());
        ps.setString(2, saltedHash(getPassword(), getMail()));

        result = ps.executeQuery();


        while(result.next()){
            userid = result.getInt("ID");
            name = result.getString("NAME");
            background = result.getString("BACKGROUND");
            profilback = result.getString("BACK_PROFILE");
            loggedIn = true;
        }
        
        if(!loggedIn){
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN,
                    "Kullanıcı mail veya şifre geçerli değil","");
                    FacesContext.getCurrentInstance().addMessage(null, msg);
            return "girisyap";
        }
     return "profil";

    } // end try
    catch(Exception e){
        throw new FacesException(e);
    }

    finally
    {
        connection.close(); // return this connection to pool
    }
    
    }

    public String doLogout() {
        // Set the paremeter indicating that user is logged in to false
        loggedIn = false;
        password = "";
        mail="";

        // Set logout message
        FacesMessage msg = new FacesMessage("Logout success!", "INFO MSG");
        msg.setSeverity(FacesMessage.SEVERITY_INFO);
        FacesContext.getCurrentInstance().addMessage(null, msg);

        return "index.xhtml";
    }
    
    public void userDataLoad() throws SQLException
    {
        int note = 0,list = 0,rem = 0;
        if ( dataSource == null )
        throw new SQLException( "Unable to obtain DataSource" );

    // obtain a connection from the connection pool
        Connection connection = dataSource.getConnection();

    // check whether connection was successful
        if ( connection == null )
        throw new SQLException( "Unable to connect to DataSource" );

        try
        {
        String sqlNote = "Select count(*) as note from notes where id=?";
        String sqlList = "Select count(*) as list from lists where id=?";
        String sqlRem = "Select count(*) as rem from remainders where id=?";
        String sqlData = "update user_data set notes_number=?,"
                + "lists_number=?,remainders_number=? where id=?";
        PreparedStatement ps = connection.prepareStatement(sqlNote);
        ps.setInt(1, getUserid());
        result = ps.executeQuery();
        if(result.next()){
        while(result.next()){
            note = result.getInt("note");
        }
        }
        
        ps = connection.prepareStatement(sqlList);
        ps.setInt(1, getUserid());
        result = ps.executeQuery();
        if(result.next()){
        while(result.next()){
            list = result.getInt("list");
        }
        }
        
        ps = connection.prepareStatement(sqlRem);
        ps.setInt(1, getUserid());
        result = ps.executeQuery();
        if(result.next()){
        while(result.next()){
            rem = result.getInt("rem");
        }
        }
       
        ps = connection.prepareStatement(sqlData);
        ps.setInt(1, note);
        ps.setInt(2, list);
        ps.setInt(3, rem);
        ps.setInt(4, getUserid());
        ps.executeUpdate();
        
        }
        finally
        {
            connection.close();
        }
        
    }
    
    public ResultSet userDataTable() throws SQLException
    {
        userDataLoad();
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
            connection.prepareStatement( "select * FROM user_data "
                    + "where id=?" );
            
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
    
    public String Update() throws SQLException 
    {
        if(getPassword() != null && getRepeat() != null){ 
            if(!getPassword().equals(getRepeat())){
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN,
                       "Şifreler uyuşmamaktadır","");
                FacesContext.getCurrentInstance().addMessage(null, msg);
                return "ayarlar";
            }
        }
        repeat = "";
        
        if (dataSource == null) {
            throw new SQLException("Unable to obtain DataSource");
        }

        Connection connection = dataSource.getConnection();
        if (connection == null) {
            throw new SQLException("Unable to connect to DataSource");
        }

        try {
            // create a PreparedStatement to insert a new address book entry
            
                String sql = "update users set password=? , name=? where id=?";
                PreparedStatement ps = connection.prepareStatement(sql);
                ps.setInt(3, getUserid());
                ps.setString(1, saltedHash(getPassword(),getMail()));
                ps.setString(2, getName());


                ps.executeUpdate(); // insert the entry
                password="";
                mail="";

                return "profil";
                
        } // end try
        catch (Exception e) {
           throw new FacesException(e);

        } finally {
            connection.close(); // return this connection to pool
        }
    }
    
        public String saltedHash(String pw, String mail) {

        String salted = pw + mail;
        String hashedPw = null;
        try {
            // Create MessageDigest instance for MD5
            MessageDigest md = MessageDigest.getInstance("MD5");
            //Add password bytes to digest
            md.update(salted.getBytes());
            //Get the hash's bytes
            byte[] bytes = md.digest();
            //This bytes[] has bytes in decimal format;
            //Convert it to hexadecimal format
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            //Get complete hashed password in hex format
            hashedPw = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return hashedPw;

    }
       
    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }
    
    public String getRepeat() {
        return repeat;
    }

    public void setRepeat(String repeat) {
        this.repeat = repeat;
    }

    public String getProfilback() {
        return profilback;
    }

    public void setProfilback(String profilback) {
        this.profilback = profilback;
    }
    
    

}
