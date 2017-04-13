package com.utils;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Properties;


/**
 * Created by cnccpc on 2016/11/15.
 */
public class SSHMysql2 {


    /**
     * @param sshId，sshPasswd，sshHost，localHostIp
     */
    public String LocalHostIP = getLocalHostIP();

    public void getConfig() {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("SSHMysql.properties");
        Properties p = new Properties();
        try {
            p.load(inputStream);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        System.out.println("Get->SSHMysql.properties:"
                + p.getProperty("sshId")
                + "/" + p.getProperty("sshPasswd")
                + "/" + p.getProperty("sshHost")
                + "/" + p.getProperty("localHostIp"));

    }

    public static void main(String[] args) {
        new SSHMysql2().go();
    }

    public void go() {

        try {

            InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("SSHMysql.properties");
            Properties p = new Properties();
            try {
                p.load(inputStream);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            String sshId = p.getProperty("sshId");
            String sshHost = p.getProperty("sshHost");
            String sshPasswd = p.getProperty("sshPasswd");
            String localHostIp = p.getProperty("localHostIp");

            JSch jSch = new JSch();
            Session session = jSch.getSession(sshId, sshHost, 22);
            session.setPassword(sshPasswd);
            session.setConfig("StrictHostKeyChecking", "no");

            System.out.println("local ip:" + LocalHostIP);
            if (LocalHostIP.equals(sshHost)) {
                System.out.println("you have in SSH!");
            } else {
                if (isLoclePortUsing(5555)) {
                    System.out.println("SSH has started!");
                } else {

                    session.connect();
                    System.out.println("Connectting to SSH");
                    System.out.println(session.getServerVersion());


                    int assinged_port = session.setPortForwardingL(localHostIp, 5555, sshHost, 3306);

                    System.out.println("Proxy is created:" + localHostIp + ":" + assinged_port);
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     */
    public static String getLocalHostIP() {
        String ip;
        try {
            InetAddress addr = InetAddress.getLocalHost();
            ip = addr.getHostAddress();
        } catch (Exception ex) {
            ip = "";
        }

        return ip;
    }

    /***
     *  true:already in using  false:not using
     * @param port
     */
    public static boolean isLoclePortUsing(int port) {
        boolean flag = true;
        try {
            flag = isPortUsing("127.0.0.1", port);
        } catch (Exception e) {
        }
        return flag;
    }

    /***
     *  true:already in using  false:not using
     * @param host
     * @param port
     * @throws UnknownHostException
     */
    public static boolean isPortUsing(String host, int port) throws UnknownHostException {
        boolean flag = false;
        InetAddress theAddress = InetAddress.getByName(host);
        try {
            Socket socket = new Socket(theAddress, port);
            flag = true;
        } catch (IOException e) {

        }
        return flag;
    }


}
