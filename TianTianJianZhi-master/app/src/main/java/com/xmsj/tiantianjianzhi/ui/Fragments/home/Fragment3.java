package com.xmsj.tiantianjianzhi.ui.Fragments.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.xmsj.tiantianjianzhi.R;
import com.xmsj.tiantianjianzhi.adapter.SelectionAdapter;
import com.xmsj.tiantianjianzhi.bean.SelectionBean;
import com.xmsj.tiantianjianzhi.ui.Activity.JobWebDetailsActivity;
import com.xmsj.tiantianjianzhi.utils.DividerItemDecoration;
import com.xmsj.tiantianjianzhi.utils.ToastUtils;
import com.longsh.optionframelibrary.OptionCenterDialog;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by SuHongJin on 2018/7/16.
 */

public class Fragment3 extends Fragment implements View.OnClickListener {
    private RecyclerView rRecyclerview;
    private List<SelectionBean>  datalist = new ArrayList<>();
    private RelativeLayout rl_load_view3;
    private Button btn_load3;
    private RelativeLayout rl_network_error3;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment3, container, false);
        initView(view);
        loadList();
        return view;
    }

    private void initView(View view) {
        rRecyclerview = (RecyclerView) view.findViewById(R.id.sRecyclerview);
        rl_load_view3 = (RelativeLayout) view.findViewById(R.id.rl_load_view3);
        btn_load3 = (Button) view.findViewById(R.id.btn_load3);
        btn_load3.setOnClickListener(this);
        rl_network_error3 = (RelativeLayout) view.findViewById(R.id.rl_network_error3);
    }

    //加载列表数据
    private void loadList() {
        //获取后台数据
        BmobQuery<SelectionBean> query = new BmobQuery<SelectionBean>();
        query.findObjects(new FindListener<SelectionBean>() {
            @Override
            public void done(final List<SelectionBean> list, BmobException e) {
                if (e == null) {
                    //添加前清除集合数据先，防止数据添加重复
                    datalist.clear();
                    //添加数据到集合
                    datalist.addAll(list);
                    rRecyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
                    SelectionAdapter adapter = new SelectionAdapter(getContext(), datalist);
                    rRecyclerview.setItemAnimator(new DefaultItemAnimator());
                    rRecyclerview.addItemDecoration(new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL_LIST));
                    rRecyclerview.setAdapter(adapter);
                    //隐藏加载view
                    rl_load_view3.setVisibility(View.GONE);
                    rl_network_error3.setVisibility(View.GONE);
                    adapter.setOnItemClickLitener(new SelectionAdapter.OnItemClickLitener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            //点击事件
                            Intent intent=new Intent(getContext(), JobWebDetailsActivity.class);
                            intent.putExtra("url",datalist.get(position).getUrl_selection());
                            startActivity(intent);
                        }
                        @Override
                        public void onItemLongClick(View view, int position) {
                            //长按弹出列表提示框
                            final ArrayList<String> list = new ArrayList<>();
                            list.add("分享兼职");
                            list.add("报名兼职");
                            list.add("取消操作");
                            final OptionCenterDialog optionCenterDialog = new OptionCenterDialog();
                            optionCenterDialog.show(getContext(), list);
                            optionCenterDialog.setItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    switch (position){
                                        case 0:
                                            ToastUtils.showImageToast(getContext(),"分享成功");
                                            break;
                                        case 1:
                                            ToastUtils.showImageToast(getContext(),"报名成功");
                                            break;
                                        default:
                                            break;
                                    }
                                    optionCenterDialog.dismiss();
                                }
                            });
                        }
                    });
                } else {
                    rl_load_view3.setVisibility(View.GONE);
                    rl_network_error3.setVisibility(View.VISIBLE);
                    btn_load3.setText("重新加载");
                    ToastUtils.showTextToast(getContext(), "出故障啦~请检查网络");
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_load3:
                loadList();
                btn_load3.setText("加载中...");
                break;
        }
    }
}
