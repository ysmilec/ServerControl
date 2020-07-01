package com.ysmilec.testserver.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.ysmilec.testserver.R;
import com.ysmilec.testserver.interfaces.Callback;
import com.ysmilec.testserver.ssh.ServerManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.fragment.app.Fragment;

public class InternetFragment extends Fragment implements Callback {
    List<Map<String, Object>> listitem = new ArrayList<Map<String, Object>>(); //存储数据的数组列表
    private ListView listView;
    private View view;
    public SimpleAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_listview, container, false);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("internet_name", "网络接口");
        map.put("internet_content","接口信息");
        listitem.add(map);
        adapter = new SimpleAdapter(getActivity()
                , listitem
                , R.layout.internet_item
                , new String[]{"internet_name", "internet_content"}
                , new int[]{R.id.internet_name, R.id.internet_content});
        // 第一个参数是上下文对象
        // 第二个是listitem
        // 第三个是指定每个列表项的布局文件
        // 第四个是指定Map对象中定义的两个键（这里通过字符串数组来指定）
        // 第五个是用于指定在布局文件中定义的id（也是用数组来指定）
        listView = view.findViewById(R.id.server_listview);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Map<String, Object> map1 = new HashMap<String, Object>();
                if(position!=0){
                    map1 = listitem.get(position);
                    String toast = "网络接口名称："+map1.get("internet_name")+" 网络接口信息："+map1.get("internet_content");
                    Toast.makeText(getActivity(), toast,Toast.LENGTH_LONG).show();
                }


            }
        });
        return view;
    }

    @Override
    public void onConnect(final ServerManager sm) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final String internet = sm.getInternetUsage();
                while (getActivity()==null){
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String[] internetline = internet.split("\n\n");
                        for(String i: internetline) {
                            Map<String, Object> map = new HashMap<String, Object>();
                            String[] internetkey = i.split(":");
                            map.put("internet_name", internetkey[0]);
                            map.put("internet_content",internetkey[1]);
                            listitem.add(map);
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        }).start();

    }

    @Override
    public void onDisconnect() {

    }

}
