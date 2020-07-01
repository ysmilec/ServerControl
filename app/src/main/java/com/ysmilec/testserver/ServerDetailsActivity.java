package com.ysmilec.testserver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.ysmilec.testserver.fragment.CPUFragment;
import com.ysmilec.testserver.fragment.DiskFragment;
import com.ysmilec.testserver.fragment.InternetFragment;
import com.ysmilec.testserver.fragment.ProcessFragment;
import com.ysmilec.testserver.interfaces.Callback;
import com.ysmilec.testserver.ssh.SSHUtils;
import com.ysmilec.testserver.ssh.ServerManager;

import java.util.ArrayList;
import java.util.List;

public class ServerDetailsActivity extends AppCompatActivity {
    //当前显示的fragment
    private static final String CURRENT_FRAGMENT = "STATE_FRAGMENT_SHOW";
    private TextView tv_username;
    private TextView tv_server_detail;
    private SSHUtils utils = new SSHUtils();
    private DiskFragment diskFragment;
    private InternetFragment internet_fragment;
    private ProcessFragment process_fragment;
    private CPUFragment cpu_fragment;
    private FragmentManager fragmentManager;
    private Fragment currentFragment;
    private List<Fragment> fragments;
    public int currentIndex = 0;
    private BottomNavigationView bottomNavigationView;
    Session session = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_details);
        fragmentManager = getSupportFragmentManager();
        currentFragment = new Fragment();
        fragments = new ArrayList<>();
        diskFragment = new DiskFragment();
        cpu_fragment = new CPUFragment();
        internet_fragment = new InternetFragment();
        process_fragment = new ProcessFragment();
        tv_username = findViewById(R.id.username);
        tv_server_detail = findViewById(R.id.tv_server_detail);
        bottomNavigationView = findViewById(R.id.bottom_navigation_serverdetail);
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
//        接收由跳转过来的fragment传过来的服务器信息
        Intent intent = this.getIntent();
        final String username = intent.getStringExtra("username");
        final String pwd = intent.getStringExtra("pwd");
        final String host = intent.getStringExtra("host");
        tv_username.setText(username+"@"+host);

//        使用SSH连接linux主机
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    session = utils.getSession(host,username,pwd);
//                    }
                } catch (JSchException e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(session!=null){
                            Toast.makeText(ServerDetailsActivity.this,"连接成功！",Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(ServerDetailsActivity.this,"连接失败！",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }).start();


        if (savedInstanceState != null) { // “内存重启”时调用

            //获取“内存重启”时保存的索引下标
            currentIndex = savedInstanceState.getInt(CURRENT_FRAGMENT,0);

            //注意，添加顺序要跟下面添加的顺序一样！！！！
            fragments.removeAll(fragments);
            boolean add = fragments.add(fragmentManager.findFragmentByTag(0 + ""));
            fragments.add(fragmentManager.findFragmentByTag(1+""));
            fragments.add(fragmentManager.findFragmentByTag(2+""));
            fragments.add(fragmentManager.findFragmentByTag(3+""));
            //恢复fragment页面
            restoreFragment();
        }else{      //正常启动时调用
            fragments.add(diskFragment);
            fragments.add(cpu_fragment);
            fragments.add(internet_fragment);
            fragments.add(process_fragment);
            showFragment();
        }
//       建立ssh连接
        try {
            ServerManager sm = new ServerManager(host, username, pwd);
            sm.connect(new Callback[]{
                    diskFragment,
                    cpu_fragment,
                    internet_fragment,
                    process_fragment});
        } catch (JSchException e) {
            e.printStackTrace();
        }
    }

    /**
     * 使用show() hide()切换页面
     * 显示fragment
     */
    public void showFragment(){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        //如果之前没有添加过
        if(!fragments.get(currentIndex).isAdded()){
            transaction
                    .hide(currentFragment)
                    .add(R.id.content,fragments.get(currentIndex),""+currentIndex);  //第三个参数为添加当前的fragment时绑定一个tag

        }else{
            transaction
                    .hide(currentFragment)
                    .show(fragments.get(currentIndex));
        }
        currentFragment = fragments.get(currentIndex);
        transaction.commit();
    }


    /**
     * 恢复fragment
     */
    private void restoreFragment(){
        FragmentTransaction mBeginTreansaction = fragmentManager.beginTransaction();

        for (int i = 0; i < fragments.size(); i++) {

            if(i == currentIndex){
                mBeginTreansaction.show(fragments.get(i));
            }else{
                mBeginTreansaction.hide(fragments.get(i));
            }
        }
        mBeginTreansaction.commit();
        //把当前显示的fragment记录下来
        currentFragment = fragments.get(currentIndex);

    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new
            BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.hardpan:
                            currentIndex = 0;
                            showFragment();
                            tv_server_detail.setText("硬盘信息");
                            return true;
                        case R.id.CPU:
                            currentIndex = 1;
                            showFragment();
                            tv_server_detail.setText("CPU信息");
                            return true;
                        case R.id.internet:
                            currentIndex = 2;
                            showFragment();
                            tv_server_detail.setText("网络信息");
                            return true;
                        case R.id.process:
                            currentIndex = 3;
                            showFragment();
                            tv_server_detail.setText("进程信息");
                        default:
                            break;
                    }
                    return false;
                }
            };
}
