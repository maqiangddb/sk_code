package com.android.Samkoonhmi.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Security;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;

import javax.activation.CommandMap;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.activation.MailcapCommandMap;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import android.os.Bundle;
import android.text.TextUtils;

import com.android.Samkoonhmi.skenum.DATE_FORMAT;
import com.android.Samkoonhmi.skwindow.EmailOperDialog;

public class GMailSender extends javax.mail.Authenticator{


	private String mailhost = "smtp.163.com"; //服务器    
    private String user;      //from
    private String password;     
    private Session session; 
    private String recipients;
    private ArrayList<String> filePath; //附件内容
    private String content; //邮件内容
 
    static {    
        Security.addProvider(new JSSEProvider());    
    } 
 
    public GMailSender(Bundle bundle, String contString, ArrayList<String> list) {
    	
        this.user = bundle.getString(EmailOperDialog.SET_FROM);    
        this.password = bundle.getString(EmailOperDialog.SET_PASSWORD);      
        this.mailhost = bundle.getString(EmailOperDialog.SET_SERVER); 
        this.recipients = bundle.getString(EmailOperDialog.SET_TO); 
        
        this.content = contString;
        this.filePath = list;
 
        Properties props = new Properties();    
        props.setProperty("mail.transport.protocol", "smtp");    
        props.setProperty("mail.host", mailhost);    
        props.put("mail.smtp.auth", "true");     
        
        session = Session.getInstance(props, this);    
    }    
 
    protected PasswordAuthentication getPasswordAuthentication() {    
        return new PasswordAuthentication(user, password);    
    }    
 
    public synchronized int sendMail( ) throws Exception {   
    	 int state = 1;
        try{ 
        	MimeMessage message = new MimeMessage(session);  
            message.setSender(new InternetAddress(user));    

            Date date = new Date(System.currentTimeMillis());//邮件标题
			String subject = DateStringUtil.convertDate(DATE_FORMAT.YYYYMMDD_SLASH, date);
            message.setSubject(subject); 
            message.setFrom(new InternetAddress(user));  
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(user));  
            
            
            MimeMultipart mp = new MimeMultipart();
            if (!TextUtils.isEmpty(content)) {//邮件内容
          	  MimeBodyPart contentBody = new MimeBodyPart();  
                MimeMultipart contentMulti = new MimeMultipart("related");  
                MimeBodyPart textBody = new MimeBodyPart();  
                textBody.setContent(content, "text/html;charset=UTF-8"); 
                contentMulti.addBodyPart(textBody);  
                contentBody.setContent(contentMulti); 
                mp.addBodyPart(contentBody);
           	}
            if (filePath.size() > 0) {//邮件附件
            	
                for(String file : filePath){
              	  MimeBodyPart attachPartPicture = createAttachment(file); 
                    mp.addBodyPart(attachPartPicture);
                }
				
			}
            
            message.setContent(mp); 
            message.saveChanges();  
            
            if (recipients.indexOf(',') > 0)    
            {
            	message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipients));   
            }
            else {
            	 message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipients));  
    		}
                 
            MailcapCommandMap mc = (MailcapCommandMap) CommandMap.getDefaultCommandMap();
            mc.addMailcap("text/html;; x-java-content-handler=com.sun.mail.handlers.text_html");
            mc.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml");
            mc.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain");
            mc.addMailcap("multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed");
            mc.addMailcap("message/rfc822;; x-java-content-handler=com.sun.mail.handlers.message_rfc822");
            CommandMap.setDefaultCommandMap(mc);
            Transport.send(message); 
           
        }catch(Exception e){ 
        	e.printStackTrace();
        	state = 2;// 发送失败返回2
        } 
        return state;
    }    
    
    private static MimeBodyPart createAttachment(String filename) throws Exception { 
        // TODO Auto-generated method stub 
        MimeBodyPart attachPart = new MimeBodyPart(); 
        FileDataSource fds = new FileDataSource(filename); 
        attachPart.setDataHandler(new DataHandler(fds));
        //attachPart.setFileName(fds.getName()); 
        attachPart.setFileName(MimeUtility.encodeText(fds.getName()));
        return attachPart; 
        
    } 
    
    public class ByteArrayDataSource implements DataSource {    
        private byte[] data;    
        private String type;    
 
        public ByteArrayDataSource(byte[] data, String type) {    
            super();    
            this.data = data;    
            this.type = type;    
        }    
 
        public ByteArrayDataSource(byte[] data) {    
            super();    
            this.data = data;    
        }    
 
        public void setType(String type) {    
            this.type = type;    
        }    
 
        public String getContentType() {    
            if (type == null)    
                return "application/octet-stream";    
            else   
                return type;    
        }    
 
        public InputStream getInputStream() throws IOException {    
            return new ByteArrayInputStream(data);    
        }    
 
        public String getName() {    
            return "ByteArrayDataSource";    
        }    
 
        public OutputStream getOutputStream() throws IOException {    
            throw new IOException("Not Supported");    
        }    
    }    
	
}
