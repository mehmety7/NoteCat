
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
import java.util.ArrayList;
import java.util.List;
import javax.faces.FacesException;



@ManagedBean(name = "UserBean")
@SessionScoped
public class UserBean implements Serializable {
    
    private static final long serialVersionUID = 124578777L;
    
    
    private int userid;
    
    private String name;
    private String mail;
    private String password;
    private String repeat; // repeat of password
    List<String> questions = new ArrayList<>(); 
    private String question;
    private String answer;
        
    ResultSet result = null;

    DataSource dataSource;
    public UserBean() {
        try {
            Context ctx = new InitialContext();
            dataSource = (DataSource) ctx.lookup("jdbc/addressbook");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    public String signUp() throws SQLException 
    {
        if(password != null && repeat != null){ 
            if(!password.equals(repeat)){
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN,
                       "Şifreler uyuşmamaktadır","");
                FacesContext.getCurrentInstance().addMessage(null, msg);
                return "kaydol";
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

        password = saltedHash(password, mail);

        try {
            // create a PreparedStatement to insert a new address book entry
            
                PreparedStatement addEntry = connection.prepareStatement("INSERT INTO USERS "
                        + "(NAME, MAIL, PASSWORD) VALUES (?, ?, ?)");

                // specify the PreparedStatement's arguments

                addEntry.setString(1, getName());
                addEntry.setString(2, getMail());
                addEntry.setString(3, getPassword());


                addEntry.executeUpdate(); // insert the entry
                
                password = "";
                mail = "";
                
        } // end try
        catch (Exception e) {
            System.out.println("sign");
           throw new FacesException(e);
           //return "kaydol";

        } finally {
            connection.close(); // return this connection to pool
        }
        
        userDataEkle();
        userQueEkle();
        return "index";
    }
    
    public void userDataEkle() throws SQLException
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
            
                PreparedStatement addEntry = connection.prepareStatement("INSERT INTO USER_DATA "
                        + "(ID) VALUES (?)");

                // specify the PreparedStatement's arguments

                addEntry.setInt(1, findId());

                addEntry.executeUpdate(); // insert the entry
               
                                
        } // end try
        catch (Exception e) {
           System.out.println("data");
           throw new FacesException(e);

        } finally {
            connection.close(); // return this connection to pool
        }
    }
    
    public void userQueEkle() throws SQLException
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
            
                PreparedStatement addEntry = connection.prepareStatement("INSERT INTO SECQUESTION "
                        + "(ID, QUESTION, ANSWER) VALUES (?, ?, ?)");

                // specify the PreparedStatement's arguments

                addEntry.setInt(1, findId());
                addEntry.setString(2, getQuestion());
                addEntry.setString(3, getAnswer());

                addEntry.executeUpdate(); // insert the entry
                question="";
                answer="";
               
                                
        } // end try
        catch (Exception e) {
           System.out.println("que");
           throw new FacesException(e);

        } finally {
            connection.close(); // return this connection to pool
        }
    }
    
    public int findId() throws SQLException
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
            connection.prepareStatement( "select id FROM users "
                    + "ORDER BY id DESC fetch first 1 rows only");

                // specify the PreparedStatement's arguments

                result = ps.executeQuery();
                if(result != null){
                    while(result.next()){
                        userid = result.getInt("id");
                    }
                }
                
                return userid;
                
        } // end try
        catch (Exception e) {
           return 0;

        } finally {
            connection.close(); // return this connection to pool
        }
    }
    
    public String qAnsControl() throws SQLException
    {   
        boolean control = false;
        
        if ( dataSource == null )
        throw new SQLException( "Unable to obtain DataSource" );

        // obtain a connection from the connection pool
        Connection connection = dataSource.getConnection();

        // check whether connection was successful
        if ( connection == null )
        throw new SQLException( "Unable to connect to DataSource" );
        
        try
        {
            String sql = "Select * from SECQUESTION where id=? and question=? and answer=?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, getId());
            ps.setString(2, getQuestion());
            ps.setString(3, getAnswer());

            result = ps.executeQuery();

            while(result.next()){
                control=true;
            }
            if(!control){
                return "guvenliksorusu";
            }
            question = "";
            answer = "";
            return "sifreyenile";
            
        }catch(Exception e){
            return "guvenliksorusu";            
        }
        finally
        {
            connection.close();
        }

    }
    
        public int getId() throws SQLException
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
            connection.prepareStatement( "select id FROM users where mail='"+getMail()+"'"
                    + "ORDER BY id DESC fetch first 1 rows only");

                // specify the PreparedStatement's arguments

                result = ps.executeQuery();
                if(result != null){
                    while(result.next()){
                        userid = result.getInt("id");
                    }
                }
                
                return userid;
                
        } // end try
        catch (Exception e) {
           return 0;

        } finally {
            connection.close(); // return this connection to pool
        }
    }
    
    public String sifreDegistir() throws SQLException
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
            String sql = "update users set password=? where id=?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(2, getId());
            ps.setString(1, saltedHash(getPassword(),getMail()));

            ps.executeUpdate();
        
            return "index";
            
        }catch(Exception e){
            throw new FacesException(e);
            //return "sifreyenile";            
        }
        finally
        {
            connection.close();
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
    
    public String getRepeat() {
        return repeat;
    }

    public void setRepeat(String repeat) {
        this.repeat = repeat;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public List<String> getQuestions() {
        return questions;
    }

    public void setQuestions(List<String> questions) {
        this.questions = questions;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }
    
}


/*
    public void dataAndQueDelete() throws SQLException
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
                
                PreparedStatement ps = connection.prepareStatement("delete from app.user_data"
                        + "where id=?");

                // specify the PreparedStatement's arguments
                ps.setInt(1, getUserid());

                ps.executeUpdate(); // delete the record

                
                ps = connection.prepareStatement("delete from app.secquestion"
                        + "where id=?");

                // specify the PreparedStatement's arguments
                ps.setInt(1, getUserid());
                
                ps.executeUpdate(); // insert the entry
                                               
        } // end try
        catch (Exception e) {
           throw new FacesException(e);
        } finally {
            connection.close(); // return this connection to pool
        }   
    }
    
   public String userDelete() throws SQLException 
    {
        
        setAllDeleted();
        listBean.tumListeleriSil();
        noteBean.tumNotuSil();
        remBean.tumRemiSil();
        dataAndQueDelete();
        
        if (dataSource == null) {
            throw new SQLException("Unable to obtain DataSource");
        }

        Connection connection = dataSource.getConnection();
        if (connection == null) {
            throw new SQLException("Unable to connect to DataSource");
        }
        
        try {
            // create a PreparedStatement to insert a new address book entry
                
                PreparedStatement ps = connection.prepareStatement("delete from app.elements");

                // specify the PreparedStatement's arguments

                ps.executeUpdate(); // delete the record

                
                ps = connection.prepareStatement("delete from app.lists");

                // specify the PreparedStatement's arguments
                
                ps.executeUpdate(); // insert the entry
                
                return "index";
                               
        } // end try
        catch (Exception e) {
           throw new FacesException(e);
           //return "not";

        } finally {
            connection.close(); // return this connection to pool
        }   
    }
    
    public void setAllDeleted() throws SQLException 
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
            String note = "update notes set deleted=true where id=?";
            String rem = "update remainders set deleted=true where id=?";
            String list = "update lists set deleted=true where id=?";

            PreparedStatement ps = connection.prepareStatement(note);
            ps.setInt(1, 0);

            ps.executeUpdate();
            
            
             ps = connection.prepareStatement(rem);
            ps.setInt(1, getUserid());

            ps.executeUpdate();
            
            
            
             ps = connection.prepareStatement(list);
            ps.setInt(1, getUserid());

            ps.executeUpdate();
        
            
        }catch(Exception e){
            throw new FacesException("Sorun");
        }
        finally
        {
            connection.close();
        }
    }*/