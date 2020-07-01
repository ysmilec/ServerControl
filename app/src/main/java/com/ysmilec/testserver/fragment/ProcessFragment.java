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

public class ProcessFragment extends Fragment implements Callback {
    List<Map<String, Object>> listitem = new ArrayList<Map<String, Object>>(); //存储数据的数组列表
    private ListView listView;
    private View view;
    public SimpleAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_listview, container, false);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("process_user", "用户名");
        map.put("process_pid", "id");
        map.put("process_cpu", "CPU");
        map.put("process_mem", "内存");
        map.put("process_time", "开始时间");
        map.put("process_command", "命令");
        listitem.add(map);
        adapter = new SimpleAdapter(getActivity()
                , listitem
                , R.layout.process_item
                , new String[]{"process_user", "process_pid", "process_cpu", "process_mem", "process_time", "process_command"}
                , new int[]{R.id.process_user, R.id.process_pid, R.id.process_cpu, R.id.process_mem,R.id.process_time, R.id.process_command});
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
                    String toast = "用户名："+map1.get("process_user")+" 进程id："+map1.get("process_pid")+" 进程占用CPU："+map1.get("process_cpu")+" 进程占用内存："+map1.get("process_mem")+" 进程启动时间："+map1.get("process_time")+" 启动进程的命令："+map1.get("process_command");
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
                final String process = sm.getProcessUsage();
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
                        String[] processline = process.split("\n");
                        int length = processline.length;
                        for(int i=1; i<length; i++){
                            Map<String, Object> map = new HashMap<String, Object>();
                            String pkey = processline[i].replaceAll(" {2,}", " ");
                            String[] key = pkey.split(" ");
                            map.put("process_user", key[0]);
                            map.put("process_pid", key[1]);
                            map.put("process_cpu", key[2]);
                            map.put("process_mem", key[3]);
                            map.put("process_time", key[9]);
                            map.put("process_command", key[10]);
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
