package com.ysmilec.testserver.fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ysmilec.testserver.R;

import androidx.fragment.app.Fragment;

public class SettingsFragment extends Fragment {
    /**
     * 初始化Fragment
     */
    private View view;
    private TextView textView;
    private Spinner spinner;
    private Button button;
    private TextView tv_about;
    public String SSH_PORT;

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_setting,container, false);
        textView = view.findViewById(R.id.textView2);
        spinner = view.findViewById(R.id.spinner2);
        button = view.findViewById(R.id.button);
        tv_about = view.findViewById(R.id.tv_about);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),R.array.sshport,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setPrompt("ssh端口选择");
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String select = parent.getItemAtPosition(position).toString();
                if(select.equals("默认(22)")){

                }else {
                    sshDialog();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        tv_about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aboutDialog();
            }
        });
        return view;
    }

    public void aboutDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final AlertDialog dialog = builder.create();
        View dialogView = View.inflate(getActivity(), R.layout.about_dialog, null);
        dialog.setView(dialogView);
        dialog.show();
        TextView tv_about = dialogView.findViewById(R.id.textView_about);
        tv_about.setText("开发者：ysmilec\n开发版本：1.0\n功能：监控服务器");
        Button button = dialogView.findViewById(R.id.button_about);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }


    public void sshDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final AlertDialog dialog = builder.create();
        View dialogView = View.inflate(getActivity(), R.layout.ssh_port_dislog, null);
        dialog.setView(dialogView);
        dialog.show();

        final EditText et_port = dialogView.findViewById(R.id.et_ssh_port);

        final Button btn_define = dialogView.findViewById(R.id.btn_def);
        final Button btn_cancel = dialogView.findViewById(R.id.btn_can);

        btn_define.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String port = et_port.getText().toString();
                if (TextUtils.isEmpty(port)) {
                    Toast.makeText(getActivity(), "端口信息不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                SSH_PORT = port;
                dialog.dismiss();
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

}
