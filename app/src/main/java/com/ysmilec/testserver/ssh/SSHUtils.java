package com.ysmilec.testserver.ssh;


import android.util.Log;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Logger;

public class SSHUtils {

    /**
     * 执行命令
     * @param user
     * @param host
     * @param password
     * @param command
     * @return
     * @throws JSchException
     * @throws IOException
     */
    public String exeCommand(String user, String host, String password, String command) throws JSchException, IOException {
        JSch jsch = new JSch();
        Session session = jsch.getSession(user, host, 22);
        session.setConfig("StrictHostKeyChecking", "no");
        //    java.util.Properties config = new java.util.Properties();
        //   config.put("StrictHostKeyChecking", "no");
        session.setPassword(password);
        session.connect();
        ChannelExec channelExec = (ChannelExec) session.openChannel("exec");
        InputStream in = channelExec.getInputStream();
        channelExec.setCommand(command);
        channelExec.setErrStream(System.err);
        channelExec.connect();
        byte[] tmp=new byte[1024];
        StringBuilder out = new StringBuilder();
        while(true){
            while(in.available()>0){
                int i=in.read(tmp, 0, 1024);
                if(i<0)break;
//                System.out.print(new String(tmp, 0, i));
                out.append(new String(tmp, 0,i));
                Logger.getLogger(new String(tmp, 0,i));
            }
            if(channelExec.isClosed()){
                if(in.available()>0)
                    continue;
//                System.out.println("exit-status: "+channelExec.getExitStatus());
                break;
            }
            try{
                Thread.sleep(1000);
            }catch(Exception ee){

            }
        }
        channelExec.disconnect();
        session.disconnect();
        return out.toString();
    }

    public Session getSession(String host,String user,String psw) throws JSchException {
        JSch jsch=new JSch();
        Session session=null;
        session = jsch.getSession(user, host, 22);
        java.util.Properties config = new java.util.Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);
        session.setPassword(psw);
        session.connect();
        return session;

    }

    /**
     * 得到可以执行命令的连接
     * @param session 连接session
     * @return 可以执行命令的ChannelExec
     */
    public ChannelExec getChanel(Session session){
        ChannelExec openChannel=null;
        try {
            if(null !=session){
                openChannel = (ChannelExec) session.openChannel("exec");
            }
        } catch (JSchException e) {
            e.printStackTrace();
        }
        return openChannel;
    }

    /**
     *获得执行命令的结果
     * @param openChannel
     * @param command
     * @return
     */
    public String getExcRes(ChannelExec openChannel,String command){
        InputStream in =null;
        BufferedReader reader=null;
        StringBuffer result=new StringBuffer();
        try {
            try {
                openChannel.setCommand(command);
                int exitStatus = openChannel.getExitStatus();
                Log.e("退出的错误原因：",String.valueOf(exitStatus));
                openChannel.connect();
                in = openChannel.getInputStream();
                reader = new BufferedReader(new InputStreamReader(in));
                String buf = null;
                while ((buf = reader.readLine()) != null) {
                    result.append(new String(buf.getBytes("gbk"),"UTF-8")+"<br>\r\n");
                }
            } catch (JSchException e) {
                result.append(e.getMessage());
                e.printStackTrace();
            }
        } catch (IOException e) {
            result.append(e.getMessage());
            e.printStackTrace();
        }  finally {
        }
        return result.toString();

    }

    /**
     * 关闭连接
     * @param session
     * @param openChannel
     */
    public void disConnect(Session session,ChannelExec openChannel){
        if(session!=null&&!openChannel.isClosed()){
            openChannel.disconnect();
        }
        if(session!=null&&session.isConnected()){
            session.disconnect();
        }
    }


}
