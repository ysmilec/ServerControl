package com.ysmilec.testserver;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.ysmilec.testserver.dao.DBOpenHelper;
import com.ysmilec.testserver.fragment.LatestFragment;
import com.ysmilec.testserver.fragment.AllFrament;
import com.ysmilec.testserver.fragment.SettingsFragment;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity{
    //当前显示的fragment
    private static final String CURRENT_FRAGMENT = "STATE_FRAGMENT_SHOW";
    private TextView server_list;
    private Toolbar toolbar;
    private FragmentManager fragmentManager;
    private Fragment currentFragment;
    private List<Fragment> fragments;
    private AllFrament all_frament;
    private LatestFragment latest_fragment;
    private BottomNavigationView bottomNavigationView;
    private SettingsFragment settings_fragment;
    public int currentIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fragmentManager = getSupportFragmentManager();
        currentFragment = new Fragment();
        fragments = new ArrayList<>();
        latest_fragment = new LatestFragment();
        all_frament = new AllFrament();
        settings_fragment = new SettingsFragment();
        initViews();
        server_list.setTextColor(Color.parseColor("#1ba0e1"));

        if (savedInstanceState != null) { // “内存重启”时调用

            //获取“内存重启”时保存的索引下标
            currentIndex = savedInstanceState.getInt(CURRENT_FRAGMENT,0);

            //注意，添加顺序要跟下面添加的顺序一样！！！！
            fragments.removeAll(fragments);
            fragments.add(fragmentManager.findFragmentByTag(0+""));
            fragments.add(fragmentManager.findFragmentByTag(1+""));
            fragments.add(fragmentManager.findFragmentByTag(2+""));
            //恢复fragment页面
            restoreFragment();
        }else{      //正常启动时调用
            fragments.add(latest_fragment);
            fragments.add(all_frament);
            fragments.add(settings_fragment);
            showFragment();
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

    /**
     * 初始化布局View
     */
    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        server_list = findViewById(R.id.server_list);
        bottomNavigationView = findViewById(R.id.bottom_navigation_server);
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new
            BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.latest_server:
//                           每次进入最近使用页面时都需要更新一下页面
//                           在全部服务器页面会对最近使用的数据库进行增删，但是不会改变，
//                           只有在点击最近使用页面时从当前最近使用的数据库获取数据并更新页面
                            latest_fragment.update();
                            latest_fragment.adapter.notifyDataSetChanged();
                            currentIndex = 0;
                            showFragment();
                            server_list.setText("最近使用服务器列表");
                            return true;
                        case R.id.all_server:
                            currentIndex = 1;
                            showFragment();
                            server_list.setText("全部服务器列表");
                            return true;
                        case R.id.settings:
                            currentIndex = 2;
                            showFragment();
                            server_list.setText("设置页面");
                            return true;
                        default:
                            break;
                    }
                    return false;
                }
            };



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add_server) {
            Toast.makeText(this,"增加服务器",Toast.LENGTH_LONG).show();
            int pos = all_frament.listId.size();
            all_frament.customDialog(pos);
            return true;
        }else if(id == R.id.action_clear_server){
            Toast.makeText(this,"清空服务器",Toast.LENGTH_LONG).show();
            all_frament.clearServer();
            all_frament.adapter.notifyDataSetChanged();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
