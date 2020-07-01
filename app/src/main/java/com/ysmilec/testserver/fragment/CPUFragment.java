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

public class CPUFragment extends Fragment implements Callback {
    List<Map<String, Object>> listitem = new ArrayList<Map<String, Object>>(); //存储数据的数组列表
    private ListView listView;
    private View view;
    public SimpleAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_listview, container, false);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("cpu_name", "名称");
        map.put("cpu_content", "内容");
        listitem.add(map);
        adapter = new SimpleAdapter(getActivity()
                , listitem
                , R.layout.cpu_item
                , new String[]{"cpu_name", "cpu_content"}
                , new int[]{R.id.tv_cpu_name, R.id.tv_cpu_content});
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
                map1 = listitem.get(position);
                String[] cpu_name = new String[]{"CPU编号", "CPU制造商", "CPU产品系列代号", "CPU属于其系列中的哪一代的代号", "CPU属于的名字及其编号、标称主频","CPU属于制作更新版本", "微代码", "CPU的实际使用主频"
                        , "CPU二级缓存大小", "物理CPU的标号", "单个物理CPU内封装的逻辑核数", "当前物理核在其所处CPU中的编号，这个编号不一定连续","位于相同物理封装中的内核数量"
                        ,"用来区分不同逻辑核的编号，系统中每个逻辑核的此编号必然不同，此编号不一定连续","初始化的用于区分不同逻辑核的编号，系统中每个逻辑核的此编号必然不同，此编号不一定连续","是否具有浮点运算单元（Floating Point Unit）"
                        , "是否支持浮点计算异常", "执行cpuid指令前，eax寄存器中的值，根据不同的值cpuid指令会返回不同的内容"
                        , "表明当前CPU是否在内核态支持对用户空间的写保护（Write Protection）", "bugs","在系统内核启动时粗略测算的CPU速度（Million Instructions Per Second）"
                        , "每次刷新缓存的大小单位", "缓存地址对齐单位", "可访问地址空间位数"};
                if(position!=0)
                    Toast.makeText(getActivity(), cpu_name[position-1]+":"+map1.get("cpu_content"),Toast.LENGTH_LONG).show();

            }
        });
        return view;
    }

    @Override
    public void onConnect(final ServerManager sm) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final String cpu = sm.getCpuUsage();
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
                        String[] cpuline = cpu.split("\n");
                        for(int i=0; i<cpuline.length-1; i++){
                            Map<String, Object> map = new HashMap<String, Object>();
                            String[] cpukey = cpuline[i].split(":");
                            map.put("cpu_name", cpukey[0]);
                            map.put("cpu_content", cpukey[1]);
                            if(i != 19){
                                listitem.add(map);
                            }
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
