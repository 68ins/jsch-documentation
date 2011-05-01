/* -*-mode:java; c-basic-offset:2; indent-tabs-mode:nil -*- */
import com.jcraft.jsch.*;
import java.awt.*;
import javax.swing.*;
import java.io.*;

/**
 * This example demonstrates execution of a remote command.
 *<p>
 *  You will be asked username, hostname, passwd and command.
 *  If everything works fine, given command will be invoked 
 *  on the remote side and outputs will be printed out.
 *</p>
 */
public class Exec{
  public static void main(String[] arg)
    throws Exception
  {

    JSch jsch=new JSch();  

    String host=null;
    if(arg.length>0){
      host=arg[0];
    }
    else{
      host=JOptionPane.showInputDialog("Enter username@hostname",
                                       System.getProperty("user.name")+
                                       "@localhost"); 
    }
    String user=host.substring(0, host.indexOf('@'));
    host=host.substring(host.indexOf('@')+1);

    Session session=jsch.getSession(user, host, 22);
      
    // username and password will be given via UserInfo interface.
    UserInfo ui=new SwingDialogUserInfo();
    session.setUserInfo(ui);
    session.connect();

    String command=JOptionPane.showInputDialog("Enter command", 
                                               "set|grep SSH");

    Channel channel=session.openChannel("exec");
    ((ChannelExec)channel).setCommand(command);


    // no input
    channel.setInputStream(null);

    ((ChannelExec)channel).setErrStream(System.err);

    InputStream in=channel.getInputStream();

    channel.connect();

    // loop to read the output until the command finished.
    byte[] tmp=new byte[1024];
    while(true){
      while(in.available()>0){
        int i=in.read(tmp, 0, 1024);
        if(i<0)break;
        System.out.print(new String(tmp, 0, i));
      }
      if(channel.isClosed()){
        System.out.println("exit-status: "+channel.getExitStatus());
        break;
      }
      try{Thread.sleep(1000);}catch(Exception ee){}
    }
    channel.disconnect();
    session.disconnect();
  }

}
