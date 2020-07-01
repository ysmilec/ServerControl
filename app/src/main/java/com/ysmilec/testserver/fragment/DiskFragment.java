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

public class DiskFragment extends Fragment implements Callback{
    List<Map<String, Object>> listitem = new ArrayList<Map<String, Object>>(); //存储数据的数组列表
    private ListView listView;
    private View view;
    public SimpleAdapter adapter;
    private String disk;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_listview, container, false);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("filesystem", "文件系统");
        map.put("size", "总容量");
        map.put("used", "已使用");
        map.put("avail", "剩余容量");
        map.put("used_percentage", "已使用百分比");
        map.put("mounted", "路径地址");
        listitem.add(map);
        adapter = new SimpleAdapter(getActivity()
                , listitem
                , R.layout.disk_item
                , new String[]{"filesystem", "size", "used", "avail", "used_percentage", "mounted"}
                , new int[]{R.id.disk_filesystem, R.id.disk_size, R.id.disk_used, R.id.disk_avail,R.id.disk_used_percentage, R.id.disk_mounted});
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
                if(position!=0) {
                    map1 = listitem.get(position);
                    String toast = "文件系统："+map1.get("filesystem")+" 硬盘总容量："+map1.get("size")+" 硬盘已使用大小："+map1.get("used")+" 硬盘剩剩余容量："+map1.get("avail")+" 硬盘已使用容量所占百分比："+map1.get("used_percentage")+" 硬盘挂载路径地址："+map1.get("mounted");
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
                final String disk = sm.getDiskUsage();
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
                        String[] diskline = disk.split("\n");
                        for(int i=1; i<diskline.length; i++){
                            Map<String, Object> map = new HashMap<String, Object>();
                            String d = diskline[i].replaceAll(" {2,}", " ");
                            String[] diskkey = d.split(" ");
                            map.put("filesystem", diskkey[0]);
                            map.put("size", diskkey[1]);
                            map.put("used", diskkey[2]);
                            map.put("avail", diskkey[3]);
                            map.put("used_percentage", diskkey[4]);
                            map.put("mounted", diskkey[5]);
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
