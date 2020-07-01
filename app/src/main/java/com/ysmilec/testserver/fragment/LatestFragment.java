package com.ysmilec.testserver.fragment;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.ysmilec.testserver.MainActivity;
import com.ysmilec.testserver.R;
import com.ysmilec.testserver.ServerDetailsActivity;
import com.ysmilec.testserver.dao.DBOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.fragment.app.Fragment;

public class LatestFragment extends Fragment {
    /**
     * 初始化Fragment
     */
    public List<Map<String, Object>> listitem = new ArrayList<Map<String, Object>>(); //存储数据的数组列表
    //    listview与数据库id对应的列表
    public List<Map<Integer, String>> listId = new ArrayList<Map<Integer, String>>();
    int image_server = R.mipmap.server; //存储图片
    public ListView listView;
    private View view;
    public SimpleAdapter adapter;
    MainActivity main = new MainActivity();



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_listview, container, false);
        update();
        adapter = new SimpleAdapter(getActivity()
                , listitem
                , R.layout.fragment_server_item
                , new String[]{"server_status", "server_name", "image_server"}
                , new int[]{R.id.tv_server_status, R.id.tv_server_name, R.id.image_server});
        // 第一个参数是上下文对象
        // 第二个是listitem
        // 第三个是指定每个列表项的布局文件
        // 第四个是指定Map对象中定义的两个键（这里通过字符串数组来指定）
        // 第五个是用于指定在布局文件中定义的id（也是用数组来指定）

        listView = view.findViewById(R.id.server_listview);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            //设置监听器
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                获取当前点击的列表uuid
                String uuid = null;
                Map<Integer,String> map = new HashMap<>();
                for(int i =0; i<listId.size();i++){
                    map = listId.get(i);
                    for(int key: map.keySet()){
                        if(key == position)
                            uuid = map.get(position);
                    }
                }
                DBOpenHelper dbOpenHelper = new DBOpenHelper(getActivity(),"server_db",null,1);
                SQLiteDatabase sqLiteDatabase = dbOpenHelper.getReadableDatabase();
                Cursor cursor = sqLiteDatabase.query("latest_server_tb", new String[]{"username","pwd","host"}, "id=?", new String[]{uuid}, null, null, null, null);
                cursor.moveToNext();
                String username = cursor.getString(cursor.getColumnIndex("username"));
                String pwd = cursor.getString(cursor.getColumnIndex("pwd"));
                String host = cursor.getString(cursor.getColumnIndex("host"));
//                启动新的Activity,并把数据传过去
                Intent intent = new Intent(getActivity(), ServerDetailsActivity.class);
                intent.putExtra("username",username);
                intent.putExtra("pwd",pwd);
                intent.putExtra("host",host);
                startActivity(intent);
            }
        });
        ItemOnLongClick();
        return view;
    }

    public void update() {
        listitem.clear();
        listId.clear();
        DBOpenHelper dbOpenHelper = new DBOpenHelper(getActivity(),"server_db",null,1);
        SQLiteDatabase sqLiteDatabase = dbOpenHelper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query("latest_server_tb",null,null,null,null,null,null);
        int FirstPos = listId.size();
        while (cursor.moveToNext()){
            String uuid = cursor.getString(cursor.getColumnIndex("id"));
            String username = cursor.getString(cursor.getColumnIndex("username"));
            String passwd = cursor.getString(cursor.getColumnIndex("pwd"));
            String host = cursor.getString(cursor.getColumnIndex("host"));
            addServer(username, host);
            Map<Integer, String> map = new HashMap<Integer, String>();
            map.put(FirstPos, uuid);
            listId.add(map);
            FirstPos++;
            Log.i("Latest_Server","uuid:"+uuid+"  username:"+username+"  passwd:"+passwd+" host:"+host);
        }
    }

    private void ItemOnLongClick() {
        listView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                menu.add(0,0,0,"删除");
            }
        });
    }

    // 长按菜单响应函数
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int pos = info.position;
        switch(item.getItemId()) {
            case 0:
                // 删除操作
                if(listitem.remove(pos)!=null){
                    adapter.notifyDataSetChanged();
                    DBOpenHelper dbOpenHelper = new DBOpenHelper(getActivity(),"server_db",null,1);
                    SQLiteDatabase sqLiteDatabase = dbOpenHelper.getWritableDatabase();
                    Map<Integer,String> map = new HashMap<>();
                    String uuid = null;
                    for(int i =0; i<listId.size();i++){
                        map = listId.get(i);
                        for(int key: map.keySet()){
                            Log.i("KKey","key:"+String.valueOf(key));
                            if(key == pos)
                                uuid = map.get(pos);
                        }
                    }
                    sqLiteDatabase.delete("latest_server_tb", "id=?", new String[]{uuid});
                    //将该listview与数据库绑定的条目删除
                    listId.remove(pos);
                }else
                    Toast.makeText(getActivity(), "Failed~pos:"+String.valueOf(pos), Toast.LENGTH_LONG).show();
                break;

            default:
                break;
        }
        return super.onContextItemSelected(item);

    }

    public void addServer(String username, String host){
        //写死的数据，用于测试
        String server_name = username+"@"+host;
        String server_status = "设置完成";
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("image_server", image_server);
        map.put("server_name", server_name);
        map.put("server_status", server_status);
        listitem.add(map);
    }

}