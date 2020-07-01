package com.ysmilec.testserver.ssh;


import android.util.Log;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.ysmilec.testserver.interfaces.Callback;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class ServerManager {
    private String user;
    private String password;
    private String host;
    private Session session;
    private Callback cb;
    public ServerManager(String host, String user, String password) {
        this.user = user;
        this.password = password;
        this.host = host;
    }

    public void connect(final Callback[] callbacks) throws JSchException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSch jsch = new JSch();
                    session = jsch.getSession(user, host, 22);
                    java.util.Properties config = new java.util.Properties();
                    config.put("StrictHostKeyChecking", "no");
                    session.setConfig(config);
                    session.setPassword(password);
                    session.connect();
                    for (Callback cbi : callbacks) {
                        cbi.onConnect(ServerManager.this);
                    }
                } catch (JSchException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public boolean isConnect() {
        return session != null; //
    }

    public String getCpuUsage() {
        //
        String result = exec("cat /proc/cpuinfo");
        // 用命令查询，并且解析好
        return result;
    }

    public String getDiskUsage() {
        //
        String result = exec("df -h");
        // 用命令查询，并且解析好
        return result;
    }

    public String getProcessUsage() {
        //
        String result = exec("ps -aux");
        // 用命令查询，并且解析好
        return result;
    }

    public String getInternetUsage() {
        //
        String result = exec("ifconfig");
        // 用命令查询，并且解析好
        return result;
    }


    private String exec(String command) {
        if (!isConnect()) {
            return null;
        }
        InputStream in =null;
        BufferedReader reader=null;
        StringBuffer result=new StringBuffer();
        try {
                ChannelExec openChannel = (ChannelExec) session.openChannel("exec");
                openChannel.setCommand(command);
                int exitStatus = openChannel.getExitStatus();
                Log.e("退出的错误原因：",String.valueOf(exitStatus));
                openChannel.connect();
                in = openChannel.getInputStream();
                reader = new BufferedReader(new InputStreamReader(in));
                String buf = null;
                while ((buf = reader.readLine()) != null) {
                    result.append(new String(buf.getBytes("gbk"),"UTF-8")+"\n");
                }
            }catch (IOException e) {
            result.append(e.getMessage());
            e.printStackTrace();
            }catch (JSchException e) {
            e.printStackTrace();
            } finally {
        }
        return result.toString();
    }
}
